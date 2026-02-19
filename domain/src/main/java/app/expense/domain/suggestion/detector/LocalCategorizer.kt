package app.expense.domain.suggestion.detector

object LocalCategorizer {
    
    fun predictCategory(merchant: String, fullText: String): String {
        val textLower = fullText.lowercase()
        val merchantLower = merchant.lowercase()

        // Lightning-fast local rule engine based on your Python script
        if (listOf("swiggy", "zomato", "kfc", "blinkit", "zepto", "instamart", "mcdonald", "dominos").any { merchantLower.contains(it) }) return "Food"
        if (listOf("jio", "airtel", "vi", "bsnl", "recharge").any { merchantLower.contains(it) }) return "Recharge"
        if (listOf("uber", "ola", "rapido", "irctc", "redbus", "makemytrip").any { merchantLower.contains(it) }) return "Travel"
        if (listOf("netflix", "prime", "hotstar", "spotify", "bookmyshow", "pvrcinemas").any { merchantLower.contains(it) }) return "Entertainment"
        if (listOf("apollo", "pharmacy", "hospital", "clinic", "medplus", "netmeds").any { merchantLower.contains(it) || textLower.contains(it) }) return "Health"
        if (listOf("amazon", "flipkart", "myntra", "ajio", "meesho", "reliance", "zudio").any { merchantLower.contains(it) }) return "Shopping"
        if (listOf("bescom", "electricity", "water", "bill", "broadband", "act").any { merchantLower.contains(it) || textLower.contains(it) }) return "Bills"
        if (listOf("college", "school", "university", "udemy", "coursera", "byjus").any { merchantLower.contains(it) }) return "Education"
        if (listOf("groww", "zerodha", "upstox", "mutual fund", "sip", "stocks").any { merchantLower.contains(it) || textLower.contains(it) }) return "Investment"

        // If local rules fail, it remains Unknown until the Add Expense screen calls the Python AI
        return "Unknown" 
    }
}
