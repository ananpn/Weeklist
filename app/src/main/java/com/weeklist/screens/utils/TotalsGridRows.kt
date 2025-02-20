package com.weeklist.screens.utils

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.weeklist.LogicAndData.Constants.Companion.WEEK_TOTAL_TITLE
import com.weeklist.LogicAndData.Constants.Companion.boxHeight
import com.weeklist.LogicAndData.Constants.Companion.firstColumnWeight
import com.weeklist.LogicAndData.Constants.Companion.lastDoneFontSize
import com.weeklist.LogicAndData.Constants.Companion.lastDoneWeight
import com.weeklist.LogicAndData.Constants.Companion.normalFontSize
import com.weeklist.LogicAndData.Constants.Companion.rowTotalsFontSize
import com.weeklist.LogicAndData.Constants.Companion.taskTotalsFirstWeight
import com.weeklist.LogicAndData.Constants.Companion.taskTotalsWeight
import com.weeklist.LogicAndData.formatLastDateToDisp
import com.weeklist.LogicAndData.formatTotalToDisp
import com.weeklist.database.Task
import com.weeklist.screens.uiStuff.boxShadows

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun dayTotalGridRow(
    task : Task,
                dispDates : List<String>,
                viewModel : MainViewModel
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {

        //name column
        val nameBoxModifier = Modifier
            .weight(firstColumnWeight)
            .fillMaxSize()
        Box(
            modifier = nameBoxModifier
            ,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = task?.title.toString() ?: "",
                fontSize = normalFontSize.sp,
                maxLines = 1
            )
        }
        //task count columns
        repeat(7) { xIndex ->
            val xDate = dispDates[xIndex]
            //taskcounts grid

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(color = when(xDate == viewModel.currentDate){
                        false -> viewModel.defaultBGColor
                        true-> viewModel.modifiedBGColor
                    })
                    .weight(1f)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(5))
            ) {
                boxShadows(
                    boxWidth = viewModel.boxWidth8,
                    boxHeight = boxHeight,
                    upHeight = 1.dp,
                    upAlpha = 0.1f,
                    leftWidth = 1.dp,
                    leftAlpha = 0.1f,
                    rightWidth = 1.dp,
                    rightAlpha = 0.1f,
                    downHeight = 1.5.dp,
                    downAlpha = 0.1f,
                )
                val columnTotal = viewModel.getColumnTotal(
                    task.groupId,
                    xDate
                ).collectAsStateWithLifecycle(initialValue = 0f).value
                Text(
                    text = formatTotalToDisp(columnTotal),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun taskTotalGridRow(id : Int,
                     title : String,
                     lastDate : String,
                     total : Float
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        var nameBoxModifier = Modifier
            .weight(taskTotalsFirstWeight)
            .fillMaxSize()
        Box(
            modifier = nameBoxModifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                fontSize = rowTotalsFontSize.sp,
                maxLines = 1
            )

        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(taskTotalsWeight)
                .fillMaxSize()
                .clip(RoundedCornerShape(5))
        ) {
            Text(
                text = formatTotalToDisp(total),
                textAlign = TextAlign.Center
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(lastDoneWeight)
                .fillMaxSize()
                .clip(RoundedCornerShape(5))
        ) {
            Text(
                text = formatLastDateToDisp(lastDate),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(
                    alpha=0.7f
                ),
                fontSize = lastDoneFontSize.sp
            )
        }
    }
}

@Composable
fun weekTotalGridRow(total: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        var nameBoxModifier = Modifier
            .weight(taskTotalsFirstWeight)
            .fillMaxSize()
        Box(
            modifier = nameBoxModifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = WEEK_TOTAL_TITLE,
                fontWeight = FontWeight.Bold,
                fontSize = rowTotalsFontSize.sp,
                maxLines = 1
            )

        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(taskTotalsWeight)
                .fillMaxSize()
                .clip(RoundedCornerShape(5))
        ) {

            Text(
                text = formatTotalToDisp(total),
                textAlign = TextAlign.Center
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(lastDoneWeight)
                .fillMaxSize()
                .clip(RoundedCornerShape(5))
        ) {
            Text(
                text = "",
                textAlign = TextAlign.Center
            )
        }
    }
}
