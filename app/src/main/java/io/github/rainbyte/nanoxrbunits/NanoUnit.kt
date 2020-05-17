package io.github.rainbyte.nanoxrbunits

import android.icu.text.DecimalFormatSymbols

enum class NanoUnit(val decimals : Int) {
    Nano(30),
    mNano(27),
    uNano(24),
    Raw(0);

    val integers = 39 - decimals

    fun toRaws(input: String) : String {
        val parts = input.split(delimiters = *charArrayOf('.'), limit = 2)
        val lpart = parts[0].dropWhile { it == '0' }.padStart(integers, '0')
        val rpart = (if (parts.size == 2) parts[1] else "").padEnd(decimals, '0')
        return lpart + rpart
    }

    fun fromRaws(raws: String): String {
        val padded = raws.padStart(39, '0')
        val lpart = padded.take(integers).dropWhile { it == '0' }
        val rpart = padded.takeLast(decimals).dropLastWhile { it == '0' }
        // DecimalFormatSymbols.getInstance().decimalSeparator
        return lpart + if (decimals > 0 && rpart.isNotBlank()) ".$rpart" else ""
    }
}
