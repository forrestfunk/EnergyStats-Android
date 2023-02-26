package com.alpriest.energystats.models

import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToLong

fun Double.asPercent(): String {
    return String.format("%.0f%%", (this * 100))
}

fun Int.kW(): String {
    val divided = this.toDouble().rounded(2)
    val dec = DecimalFormat("#,###,###")
    return dec.format(divided) + " kW"
}

fun Double.kW(): String {
    val divided = this.rounded(2)
    val dec = DecimalFormat("#,###,###.##")
    return dec.format(divided) + " kW"
}

fun Double.w(): String {
    val divided = (this * 1000.0).roundToLong()

    val dec = DecimalFormat("#,###,###.##")
    return dec.format(divided) + " W"
}

fun Double.rounded(decimalPlaces: Int): Double {
    val power = 10.0.pow(decimalPlaces.toDouble())
    return (this * power).roundToLong() / power
}

fun Double.sameValueAs(other: Double): Boolean {
    return abs(this - other) < 0.0000001
}