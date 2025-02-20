package com.weeklist.LogicAndData

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.ColorUtils.HSLToColor
import com.materialkolor.PaletteStyle
import com.weeklist.LogicAndData.Constants.Companion.paletteItems
import com.weeklist.LogicAndData.Constants.Companion.spaceAnyWherePattern
import com.weeklist.LogicAndData.Constants.Companion.whiteSpaceAnyWherePattern
import com.weeklist.LogicAndData.TimeFunctions.Companion.formatToLocalDate
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.abs
import kotlin.math.min

fun transformFloatToColor(float : Float, defaultColorFloat : Float = 36f) : Color {
    val float2 = (180f+defaultColorFloat-float).mod(360f)
    val output = Color(HSLToColor(floatArrayOf(float2, 1f, 0.5f)))
    return output
}

@Composable
fun transformIntToPaletteStyle(input : Int) : PaletteStyle {
    val string = paletteItems[input]
    if (string == "PaletteStyle.TonalSpot")
        return PaletteStyle.TonalSpot
    if (string == "PaletteStyle.Neutral")
        return PaletteStyle.Neutral
    if (string == "PaletteStyle.Vibrant")
        return PaletteStyle.Vibrant
    if (string == "PaletteStyle.FruitSalad")
        return PaletteStyle.FruitSalad
    return PaletteStyle.Rainbow
}


@Composable
fun transformBGColor(color : Color) : Color{
    val output = color.copy(
        red = (color.red+0.1f)/1.1f,
        green = calculateTransform(color.green),
        blue = calculateTransform(color.blue)
    )
    return output
}

fun calculateTransform(x: Float): Float {
    var result : Float = 0f
    if (x<=0.5) {
        result = (x + 0.04f) / 0.96f
    }
    if (x>0.5){
        result = (x-0.07f)
    }
    return result
}

fun selectedColor(color : Color) : Color{
    val output = color.copy(
        red = calculateTransform(color.red),
        green = calculateTransform(color.green),
        blue = calculateTransform(color.blue)
    )
    return output
}

fun formatCountToDisp(count : Int, denominator : Int) : String{
    var drawnCount : Float = 0f
    try{
        drawnCount = count.toFloat() / denominator.toFloat()
    }
    catch (e : Exception){
        drawnCount = count.toFloat()
    }
    val decimals = drawnCount.toString()
        .replace(
            Regex("(^-?\\d+\\.\\d*[1-9])(0+\$)|(\\.0+\$)"),
            "$1"
        )
        .substringAfterLast(".", "").length
    val decimals2 = min(decimals, 2)
    val format: String = "%.${decimals2}f"
    return String
        .format(format, drawnCount)
        .replace(
            Regex("(^-?\\d+\\.\\d*[1-9])(0+\$)|(\\.0+\$)"),
            "$1"
        )
        .replace(Regex("^0{1}$"), "")
        .take(5)
        .dropLastWhile { it.toString() == "." }
}

fun formatCountToFloat(count : Int, denominator: Int) : Float{
    val drawnCount = count.toFloat() / denominator.toFloat()
    return drawnCount
}


fun formatTotalToDisp(total : Float) : String{
    val decimals = total.toString()
        .replace(
            Regex("(^-?\\d+\\.\\d*[1-9])(0+\$)|(\\.0+\$)"),
            "$1"
        ) //trims end zeroes
        .substringAfterLast(".", "").length
    val decimals2 = min(decimals, 2)
    val format: String = "%.${decimals2}f"
    return String
        .format(format, total)
        .replace(
            Regex("(^-?\\d+\\.\\d*[1-9])(0+\$)|(\\.0+\$)"),
            "$1"
        )
        .replace(Regex("^0{1}$"), "")
        .take(5)
        .dropLastWhile { it.toString() == "." }
}

fun booleanToInt(boolean : Boolean) : Int{
    if (boolean) return 1
    return 0
}

fun gcd(a: Int, b: Int): Int {
    if (b == 0) return a
    return gcd(b, a % b)
}

fun lcm(a: Int, b: Int): Int {
    return a / gcd(a, b) * b
}

fun newDenominator(oldDenominator : Int, clickStep : Int) : Int {
    return (oldDenominator*clickStep)/gcd(oldDenominator, clickStep)
}

fun xIndexFromOffset(xOffset : Float, screenWidthPx : Float) : Int{
    var index = 0
    while (xOffset > (screenWidthPx*(1.5f+index.toFloat()))/8.5f){
        index+=1
    }
    return index
}

fun newLineAtCenter(input : String) : String{
    if (!whiteSpaceAnyWherePattern.containsMatchIn(input)) {
        return input
    }
    //replaces multiple spaces " " with one space
    val text = input.replace(spaceAnyWherePattern, " ")
    var centerSpaceInd = 0
    var minVal = text.lastIndex*2
    for(i in 1..text.lastIndex-1) {
        if (text.substring(i..i) == " "){
            if (abs(text.lastIndex-2*i) < minVal) {
                centerSpaceInd = i
                minVal = abs(text.lastIndex - 2 * i)
            }
        }
    }
    if (centerSpaceInd >0 ) {
        return text.replaceRange(centerSpaceInd..centerSpaceInd,
            "\n")
    }
    else return text

}

fun breakWordAtCenter(text : String, maxIndexOnRow : Int = 1000) : String{
    val indexToInject = min(text.lastIndex/2, maxIndexOnRow)
    return text.replaceRange(indexToInject..< indexToInject,
            "-\n")

}

fun formatLastDateToDisp(lastDateString : String) : String{
    if (lastDateString.isEmpty() || lastDateString == "0") return ""
    val lastDate = formatToLocalDate(lastDateString+"0000", "yyyyMMddHHmm")
    val currentDate = LocalDateTime.now()
    val days = Duration.between(lastDate, currentDate).toDays().toInt()
    if (days == 0) return "Today"
    if (days == 1) return "Yesterday"
    else return "$days days ago"

}

fun countMatches(text : String, pattern : Regex) : Int{
    return pattern.split(text).count()-1
}
