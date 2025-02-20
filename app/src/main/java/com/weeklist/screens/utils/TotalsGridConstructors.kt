package com.weeklist.screens.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.weeklist.LogicAndData.Constants.Companion.DAY_TOTAL
import com.weeklist.LogicAndData.Constants.Companion.LAST_DONE
import com.weeklist.LogicAndData.Constants.Companion.dateBoxHeight
import com.weeklist.LogicAndData.Constants.Companion.dateRowHeight
import com.weeklist.LogicAndData.Constants.Companion.dateRowModifier
import com.weeklist.LogicAndData.Constants.Companion.emptyTask
import com.weeklist.LogicAndData.Constants.Companion.lastDoneWeight
import com.weeklist.LogicAndData.Constants.Companion.mainGridGroupChangeAnimationDur
import com.weeklist.LogicAndData.Constants.Companion.mainGridGroupChangeDelay
import com.weeklist.LogicAndData.Constants.Companion.taskTotalsFirstWeight
import com.weeklist.LogicAndData.Constants.Companion.taskTotalsWeight
import com.weeklist.LogicAndData.TimeFunctions
import com.weeklist.database.Task
import com.weeklist.screens.uiStuff.boxShadows
import com.weeklist.screens.uiStuff.groupTitleBox
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun dayTotalConstructor(
    dispDates : List<String>,
    viewModel: MainViewModel,
) {
    val groupId by viewModel.dispGroupId.collectAsStateWithLifecycle(initialValue = 0)
    var groupIdChanging by remember { mutableStateOf(false) }


    LaunchedEffect(groupIdChanging){
        if (groupIdChanging) {
            delay(mainGridGroupChangeDelay)
            groupIdChanging = false
        }
    }
    val tasksWeekTotal = listOf(
        emptyTask.copy(
            title = DAY_TOTAL,
            groupId = groupId
        )
    )

    Column(
        Modifier.fillMaxSize()
    )
    {
        Row(
            modifier = dateRowModifier
                .fillMaxWidth()
        ) {
            //group name
            groupTitleBox(
                modifier = Modifier.weight(1.5f),
                viewModel = viewModel,
                groupChanged = {groupIdChanging = true}
            )
        //dates
        repeat(7) {
                Box(
                    modifier = Modifier
                        .background(color =
                            when(dispDates[it] == viewModel.currentDate){
                                false -> viewModel.defaultBGColor
                                true -> viewModel.modifiedBGColor
                            }
                        )
                        .weight(1f)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(7))
                    ,
                    contentAlignment = Alignment.Center
                ) {
                    boxShadows(
                        boxWidth = viewModel.boxWidth8,
                        boxHeight = dateBoxHeight,
                        leftWidth = 1.dp,
                        leftAlpha = 0.1f,
                        rightWidth = 2.dp,
                        rightAlpha = 0.44f,
                        downHeight = 3.dp,
                        downAlpha = 0.4f,
                    )
                    DatesBox(dispDates[it])
                }
            }
        }

        AnimatedVisibility(
            visible = !groupIdChanging,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(0.9f)
            ,
            enter = fadeIn (
                animationSpec = TweenSpec(mainGridGroupChangeAnimationDur, 0, EaseInOut),
                initialAlpha = 0f,
            ),
            exit = ExitTransition.None
        ) {
            LazyColumn(
                modifier = Modifier,
                userScrollEnabled = false
            ) {
                items(
                    items = tasksWeekTotal,
                    key = { task -> task.id })
                { task ->
                    dayTotalGridRow(
                        viewModel = viewModel,
                        task = task,
                        dispDates = dispDates
                    )
                }
                item(){
                Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

}


@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun taskTotalConstructor(tasks : List<Task>,
                    dispDates : List<String>,
                    viewModel: MainViewModel
) {
    val groupId by viewModel.dispGroupId.collectAsStateWithLifecycle(initialValue = 0)
    val groupTitle by viewModel.groupTitle.collectAsStateWithLifecycle(initialValue = "")
    var groupIdChanging by remember { mutableStateOf(false) }
    val totalsListState = viewModel.getAllRowTotals(groupId, dispDates).collectAsStateWithLifecycle(
        initialValue = listOf()
    )
    val totalsList = totalsListState.value
    //add stuff to viewmodel, repositories, dao. Also format last date 
    val lastDoneListState = viewModel.lastDone(groupId, TimeFunctions.getTimeNow("yyyyMMdd")).collectAsStateWithLifecycle(
        initialValue = listOf()
    )
    val lastDoneList = lastDoneListState.value

    LaunchedEffect(groupIdChanging) {
        if (groupIdChanging) {
            delay(mainGridGroupChangeDelay)
            groupIdChanging = false
        }
    }

    Column(
        Modifier.fillMaxSize()
    )
    {
        Row(
            modifier = dateRowModifier
                .fillMaxWidth()
        ) {
            //Group title
            groupTitleBox(
                modifier = Modifier.weight(taskTotalsFirstWeight),
                viewModel = viewModel,
                groupChanged = {},
                clickable = false
            )
            //dates

            var dateBoxModifier = Modifier
                .background(color = viewModel.defaultBGColor)
            Box(
                modifier = dateBoxModifier
                    .weight(taskTotalsWeight)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(7)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Total on week \n" +
                            "${TimeFunctions.formatStringToDisplay(dispDates[0])} - " +
                            "${TimeFunctions.formatStringToDisplay(dispDates[6])}",
                    textAlign = TextAlign.Center
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .offset(y = (dateRowHeight / 2 - 2).dp)
                        .height(1.dp)
                        .background(
                            color = MaterialTheme
                                .colorScheme
                                .outline.copy(alpha = 0.34f)
                        )
                )
            }
            Box(modifier = Modifier
                    .weight(lastDoneWeight)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(7)),
                contentAlignment = Alignment.Center){
                Text(
                    text = LAST_DONE,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground.copy(
                        alpha=0.7f
                    ),
                    fontSize = 16.sp
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .offset(y = (dateRowHeight / 2 - 2).dp)
                        .height(1.dp)
                        .background(
                            color = MaterialTheme
                                .colorScheme
                                .outline.copy(alpha = 0.34f)
                        )
                )
            }
        }
        AnimatedVisibility(
            visible = !groupIdChanging,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(0.9f),
            enter = fadeIn(
                animationSpec = TweenSpec(mainGridGroupChangeAnimationDur, 0, EaseInOut),
                initialAlpha = 0f,
            ),
            exit = ExitTransition.None
        ) {
            Column(modifier = Modifier
                .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally)
            {
                tasks.forEach{task ->
                    val lastDateToDisp = try{
                        lastDoneList.find{it.id == task.id}?.lastDone ?:""
                    }
                    catch (e : Exception){
                        ""
                    }
                    val totalToDisp = try{
                        totalsList.find{it.id == task.id}?.total ?:0f
                    }
                    catch (e : Exception){
                        0f
                    }
                    taskTotalGridRow(
                        id = task.id,
                        title = task.title,
                        lastDate = lastDateToDisp,
                        total = totalToDisp)
                }
                Divider(
                    modifier = Modifier.fillMaxWidth(0.92f),
                    thickness = 1.dp)
                weekTotalGridRow(total = totalsList.map{it.total ?:0f}.sum())
                Spacer(modifier = Modifier.height(9.dp))
            }
        }
    }
}