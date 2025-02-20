package com.weeklist.screens.utils

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.weeklist.LogicAndData.Constants.Companion.boxHeight
import com.weeklist.LogicAndData.Constants.Companion.firstColumnWeight
import com.weeklist.LogicAndData.Constants.Companion.normalFontSize
import com.weeklist.LogicAndData.Constants.Companion.pressZero
import com.weeklist.LogicAndData.formatCountToDisp
import com.weeklist.LogicAndData.xIndexFromOffset
import com.weeklist.database.StringOfDate
import com.weeklist.database.Task
import com.weeklist.screens.uiStuff.autoFitTextBox
import com.weeklist.screens.uiStuff.boxShadows
import kotlinx.coroutines.delay
import kotlin.math.max

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun mainGridRow(columnAmount : Int = 8,
                task : Task,
                dispDates : List<String>,
                viewModel : MainViewModel,
                rowTapped : (Int) -> Unit,
                rowLongPressed : (Int, Int) -> Unit,
){
    //Log.v("maingridrow launched", "task = $task")
    var pressedX by remember{ mutableStateOf(0) }
    val interactionSource = remember { MutableInteractionSource()}
    val countsOfRow = viewModel.getCountWeek(task.id, dispDates)
                .collectAsStateWithLifecycle(
                    initialValue = listOf()
                )
    val stringsOfRow =
        countsOfRow
            .value
            .map{StringOfDate(it.date, formatCountToDisp(it.count, task.denominator)) }
    LaunchedEffect(viewModel.openModifyCount||viewModel.openUpdateDialog){
        if (!(viewModel.openModifyCount||viewModel.openUpdateDialog)) {
            interactionSource.emit(PressInteraction.Release(pressZero))
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(task, dispDates) {
                detectTapGestures(
                    onTap = { offset ->
                        viewModel.userAction = true
                        val xIndex = xIndexFromOffset(offset.x, viewModel.screenWidthPx)
                        rowTapped(xIndex)
                    },
                    onLongPress = { offset ->
                        //if (gridScreenOpen) {
                        val xIndex = xIndexFromOffset(offset.x, viewModel.screenWidthPx)
                        val xDate = dispDates[max(xIndex - 1, 0)]
                        val mcCount: Int =
                            countsOfRow.value.find { it.date == xDate }?.count ?: 0
                        rowLongPressed(xIndex, mcCount)
                        //}
                    },
                    onPress = { offset ->
                        //if (gridScreenOpen) {
                        pressedX = xIndexFromOffset(offset.x, viewModel.screenWidthPx)
                        delay(30)
                        interactionSource.emit(pressZero)
                        tryAwaitRelease()
                        while (viewModel.openModifyCount || viewModel.openUpdateDialog) {
                            delay(100)
                        }
                        interactionSource.emit(PressInteraction.Release(pressZero))

                        //(viewModel.openModifyCount||viewModel.openUpdateDialog)
                        //}
                    }
                )
            }
            .height(50.dp)
    ) {
        //name column
        var nameBoxModifier = Modifier
            .weight(firstColumnWeight)
            .fillMaxSize()
        if (pressedX == 0){
            nameBoxModifier = nameBoxModifier.indication(
                interactionSource = interactionSource,
                indication = LocalIndication.current
            )
        }
        Box(
            modifier = nameBoxModifier
            ,
            contentAlignment = Alignment.Center
        ) {
            var textIn by rememberSaveable { mutableStateOf(task?.title.toString() ?: "") }

            LaunchedEffect(task){
                textIn = task?.title.toString() ?: ""
            }

            autoFitTextBox(
                textIn = textIn,
                targetFontSize = normalFontSize,
                maxLines = 2,
                animationOn = true,
                animationKey = task.orderId,
                maxIndexOfRow = 15

            )
            //mainTextBox(textIn, task.orderId)
        }
        //Task Count columns
        repeat(7) { xIndex ->
            val xDate = dispDates[xIndex]

            var taskCountBoxModifier =
                Modifier.background(color =
                    when(xDate == viewModel.currentDate) {
                        false -> viewModel.defaultBGColor
                        true -> viewModel.modifiedBGColor
                    }
                )
            if (pressedX == xIndex+1){
                taskCountBoxModifier = taskCountBoxModifier.indication(
                    interactionSource = interactionSource,
                    indication = LocalIndication.current
                )
            }
            //taskcounts grid
            Box(
                contentAlignment = Alignment.Center,
                modifier =
                taskCountBoxModifier
                    .weight(1f)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(5))
            ) {
                if (xIndex == 0) {
                    boxShadows(
                        boxWidth = viewModel.boxWidth8,
                        boxHeight = boxHeight,
                        leftWidth = 1.dp,
                        leftAlpha = 0.08f,
                        rightWidth = 2.dp,
                        rightAlpha = 0.4f,
                        downHeight = 1.5.dp,
                        downAlpha = 0.28f,
                    )
                } else {
                    boxShadows(
                        boxWidth = viewModel.boxWidth8,
                        boxHeight = boxHeight,
                        rightWidth = 2.dp,
                        rightAlpha = 0.4f,
                        downHeight = 1.5.dp,
                        downAlpha = 0.28f,
                    )
                }
                var fontSize by rememberSaveable { mutableStateOf(15.0) }
                val dispText = stringsOfRow.find { it.date == xDate }?.dispCount ?: ""
                LaunchedEffect(dispText, task.orderId) {
                    if (viewModel.userAction) {
                        fontSize = 15.0
                        delay(80)
                        if (fontSize > 14.5) {
                            fontSize = 14.0
                        }
                        delay(100)
                        viewModel.userAction = false
                    }
                }

                Text(
                    maxLines = 1,
                    softWrap = false,
                    text = dispText,
                    fontSize = fontSize.sp,
                    lineHeight = (1.8 * fontSize).sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}