package app.expense.domain.utils

import java.util.Locale

fun String.cleanMerchantName(): String {
    var name = this.substringBefore("@")
    
    val gateways = listOf(".payu", ".razorpay", ".ccavenue", ".billdesk", ".pinelabs", ".s1hcjzo")
    for (gateway in gateways) {
        name = name.substringBefore(gateway)
    }
    
    // Remove trailing numbers (e.g. blinkit3 -> blinkit)
    name = name.replace(Regex("\\d+$"), "")
    
    return name.replaceFirstChar { 
        if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
    }
}
