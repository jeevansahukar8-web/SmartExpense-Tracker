import os
import json
import base64
import redis
import jwt
import datetime
import re
import torch
from fastapi import FastAPI, HTTPException, Header
from pydantic import BaseModel
from transformers import pipeline
from cryptography.hazmat.primitives.ciphers.aead import AESGCM

# ==========================================
# 1. CONFIGURATION
# ==========================================
app = FastAPI(title="Smart Expenser Backend")

try:
    redis_client = redis.Redis(host='localhost', port=6379, db=0)
    redis_client.ping()
except redis.ConnectionError:
    print("Warning: Redis is not running. Caching will fail.")

SECRET_KEY = b"0123456789abcdef0123456789abcdef"
JWT_SECRET = "my_super_secure_jwt_secret"
JWT_ALGORITHM = "HS256"

# ==========================================
# 2. AI MODEL SETUP
# ==========================================
print("Loading AI Model...")
device = 0 if torch.cuda.is_available() else -1
classifier = pipeline("zero-shot-classification",
                      model="facebook/bart-large-mnli",
                      device=device)

EXPENSE_CATEGORIES = ["Food", "Recharge", "Travel", "Health", "Shopping", "Bills", "Education", "Entertainment", "Investment"]

class EncryptedPayload(BaseModel):
    iv: str
    ciphertext: str

class LoginRequest(BaseModel):
    username: str
    password: str

# ==========================================
# 3. CORE LOGIC
# ==========================================

def decrypt_data(iv_b64: str, ciphertext_b64: str) -> dict:
    try:
        iv = base64.b64decode(iv_b64)
        ciphertext_with_tag = base64.b64decode(ciphertext_b64)
        aesgcm = AESGCM(SECRET_KEY)
        plaintext_bytes = aesgcm.decrypt(iv, ciphertext_with_tag, None)
        return json.loads(plaintext_bytes.decode("utf-8"))
    except Exception as e:
        raise HTTPException(status_code=400, detail="Decryption Failed")

def clean_merchant_name(raw_merchant: str) -> str:
    """
    Strips VPA suffixes (e.g., blinkit3.payu@hdfcbank -> Blinkit)
    """
    # 1. Remove everything after the @ symbol
    name = raw_merchant.split('@')[0]
    # 2. Remove payment gateway intermediaries
    name = re.sub(r'\.(payu|razorpay|ccavenue|billdesk|pinelabs|s1hcjzo).*', '', name, flags=re.IGNORECASE)
    # 3. Remove trailing numbers (e.g., blinkit3 -> blinkit)
    name = re.sub(r'\d+$', '', name)
    return name.capitalize()

def hybrid_classification(merchant: str, full_text: str):
    full_text_lower = full_text.lower()
    cleaned_merchant = clean_merchant_name(merchant)
    merchant_lower = cleaned_merchant.lower()

    # --- STAGE 1: Transaction State ---
    if any(word in full_text_lower for word in ["failed", "declined", "insufficient", "rejected"]):
        txn_state = "Failed"
    elif any(word in full_text_lower for word in ["credited", "received", "deposited"]) and "debited" not in full_text_lower:
        txn_state = "Credit"
    else:
        txn_state = "Debit"

    category = "Unknown"
    confidence = 1.0

    # --- STAGE 2: Category Assignment (Only for Debits) ---
    if txn_state == "Debit":
        # Fast Deterministic Rules
        if any(x in merchant_lower for x in ["swiggy", "zomato", "kfc", "blinkit", "zepto", "instamart", "mcdonald"]):
            category = "Food"
        elif any(x in merchant_lower for x in ["jio", "airtel", "vi", "bsnl"]):
            category = "Recharge"
        elif any(x in merchant_lower for x in ["uber", "ola", "rapido", "irctc", "redbus"]):
            category = "Travel"
        elif any(x in merchant_lower for x in ["netflix", "prime", "hotstar", "spotify"]):
            category = "Entertainment"
        else:
            # AI Fallback Contextual Understanding
            sequence = f"Transaction at {cleaned_merchant}. SMS: {full_text}"
            prediction = classifier(sequence, EXPENSE_CATEGORIES)
            category = prediction['labels'][0]
            confidence = prediction['scores'][0]

    return txn_state, category, confidence, cleaned_merchant

# ==========================================
# 4. API ENDPOINTS
# ==========================================

@app.post("/login")
def login(creds: LoginRequest):
    if creds.username == "admin" and creds.password == "password":
        expiration = datetime.datetime.utcnow() + datetime.timedelta(hours=24)
        encoded_jwt = jwt.encode({"sub": creds.username, "exp": expiration}, JWT_SECRET, algorithm=JWT_ALGORITHM)
        return {"token": encoded_jwt}
    raise HTTPException(status_code=401, detail="Invalid Credentials")

@app.post("/classify")
def classify_expense(payload: EncryptedPayload, authorization: str = Header(None)):
    if not authorization or not authorization.startswith("Bearer "):
        raise HTTPException(status_code=401, detail="Missing Token")

    token = authorization.split(" ")[1]
    try:
        jwt.decode(token, JWT_SECRET, algorithms=[JWT_ALGORITHM])
    except Exception:
        raise HTTPException(status_code=401, detail="Invalid Token")

    data = decrypt_data(payload.iv, payload.ciphertext)
    merchant = data.get("merchant", "Unknown")
    amount = data.get("amount", 0.0)
    full_text = data.get("text", "")
    timestamp = data.get("timestamp", 0)

    txn_state, category, confidence, cleaned_merchant = hybrid_classification(merchant, full_text)

    # Return the beautifully cleaned data back to Android
    return {
        "transaction_state": txn_state,
        "category": category,
        "confidence": confidence,
        "merchant": cleaned_merchant
    }
