package com.weeklist.screens.uiStuff

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.weeklist.LogicAndData.Constants.Companion.newLinePattern
import com.weeklist.LogicAndData.Constants.Companion.whiteSpaceAnyWherePattern
import com.weeklist.LogicAndData.breakWordAtCenter
import com.weeklist.LogicAndData.countMatches
import com.weeklist.LogicAndData.newLineAtCenter
import kotlinx.coroutines.delay
import kotlin.math.max

@Composable
fun autoFitTextBox(textIn : String,
                   targetFontSize : Double,
                   minimumFontSize : Double = targetFontSize*0.62,
                   maxIndexOfRow : Int = 1000,
                   softWrap : Boolean = false,
                   maxLines : Int = 1,
                   animationOn : Boolean = false,
                   animationKey : Any = 0,
                   fontWeight : FontWeight = FontWeight.Normal){
    var fontSize by remember(textIn) { mutableStateOf(targetFontSize+3.0) }
    var targetFontSize by rememberSaveable { mutableStateOf(targetFontSize) }
    var readyToDraw by remember(textIn) { mutableStateOf(false) }
    var text by rememberSaveable(textIn, animationKey) { mutableStateOf(textIn) }
    var inflated by rememberSaveable(textIn) { mutableStateOf(false) }
    LaunchedEffect(animationKey){
        if (animationOn) {
            fontSize = 1.06*fontSize
            targetFontSize = 1.06*targetFontSize
            inflated = true
            delay(80)
            fontSize = fontSize/1.06
            targetFontSize = targetFontSize/1.06
            inflated = false
        }
    }
    Text(
        text = text,
        fontWeight = fontWeight,
        fontSize = fontSize.sp,
        maxLines = maxLines,
        softWrap = softWrap,
        textAlign = TextAlign.Center,
        lineHeight = lineHeightFromFontSize(fontSize).sp,
        //overFlow = TextOverflow.Ellipsis,
        modifier = Modifier
            .drawWithContent {
                if (readyToDraw) drawContent()
            },
        onTextLayout = { textLayoutResult ->
            if (fontSize>targetFontSize+.5) {
                fontSize = targetFontSize
            }
            readyToDraw = false
            if ((textLayoutResult.didOverflowWidth||textLayoutResult.didOverflowHeight) && !readyToDraw) {
                if (textLayoutResult.didOverflowWidth
                    && !softWrap
                    && countMatches(text, newLinePattern)<maxLines-1
                    && text.contains(" ")) {
                    text = newLineAtCenter(text)
                    fontSize = targetFontSize
                }
                else if (
                    textLayoutResult.didOverflowWidth
                    && (fontSize < minimumFontSize*1.2)
                    && !softWrap
                    && countMatches(text, whiteSpaceAnyWherePattern) == 0) {
                    text = breakWordAtCenter(text, maxIndexOfRow)
                    fontSize = targetFontSize
                }
                if (fontSize>(minimumFontSize*1.06)*1.16 && inflated) {
                    fontSize = fontSize * 0.95
                }
                if (fontSize>minimumFontSize*1.16 && !inflated) {
                    fontSize = fontSize * 0.85
                }
            }
            else {
                readyToDraw = true
            }
            if (fontSize<=minimumFontSize*1.16) readyToDraw = true
        },
    )
}

private fun lineHeightFromFontSize(size : Double) : Double{
    val coefficient = max(1.1, 2.0-(size)/(18.0))
    return coefficient*size
}
