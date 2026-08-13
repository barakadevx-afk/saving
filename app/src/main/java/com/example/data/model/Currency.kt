package com.example.data.model

enum class AppCurrency(
    val code: String,
    val symbol: String,
    val flag: String,
    val rwfRate: Double
) {
    RWF("RWF", "RWF", "🇷🇼", 1.0),
    USD("USD", "$", "🇺🇸", 1380.0);

    fun format(amountInRwf: Double): String {
        return when (this) {
            RWF -> "%,d RWF".format(amountInRwf.toLong())
            USD -> "$%,.2f".format(amountInRwf / rwfRate)
        }
    }

    fun formatCompact(amountInRwf: Double): String {
        return when (this) {
            RWF -> if (amountInRwf >= 1_000_000) "%.1fM RWF".format(amountInRwf / 1_000_000)
                   else if (amountInRwf >= 1_000) "%.0fk RWF".format(amountInRwf / 1_000)
                   else "%,d RWF".format(amountInRwf.toLong())
            USD -> {
                val usdVal = amountInRwf / rwfRate
                if (usdVal >= 1_000_000) "$%.1fM".format(usdVal)
                else if (usdVal >= 1_000) "$%.1fk".format(usdVal)
                else "$%.2f".format(usdVal)
            }
        }
    }
}
