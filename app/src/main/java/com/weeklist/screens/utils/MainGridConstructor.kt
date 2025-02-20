package com.weeklist.screens.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseInSine
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weeklist.LogicAndData.Constants
import com.weeklist.LogicAndData.Constants.Companion.dateRowModifier
import com.weeklist.LogicAndData.Constants.Companion.mainGridGroupChangeAnimationDur
import com.weeklist.LogicAndData.Constants.Companion.mainGridGroupChangeDelay
import com.weeklist.LogicAndData.TimeFunctions
import com.weeklist.database.Task
import com.weeklist.screens.Components.AddTaskDialog
import com.weeklist.screens.Components.EditTaskDialog
import com.weeklist.screens.Components.ModifyCountDialog
import com.weeklist.screens.uiStuff.AddButton
import com.weeklist.screens.uiStuff.boxShadows
import com.weeklist.screens.uiStuff.groupTitleBox
import kotlinx.coroutines.delay
import kotlin.math.max

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun mainGridConstructor(tasks : List<Task>,
                        dispDates : List<String>,
                        viewModel: MainViewModel
) {
    var visibilityAddButton by remember { mutableStateOf(false) }
    var columnAmount : Int = 8
    var groupIdChanging by remember { mutableStateOf(false) }

    LaunchedEffect(groupIdChanging){
        if (groupIdChanging) {
            visibilityAddButton =false
            delay(mainGridGroupChangeDelay)
            groupIdChanging = false
        }
        else{
            delay(mainGridGroupChangeDelay/2)
            visibilityAddButton=true
        }
    }

    LaunchedEffect(Unit) {
        delay(400)
        visibilityAddButton = true
    }

    /*
    val currentDate by remember(viewModel.currentDate){
        mutableStateOf( viewModel.currentDate)
    }
    */

    Column(
        Modifier.fillMaxSize()
    )
    {
        Row(
            modifier = dateRowModifier
                .fillMaxWidth()
        ) {
            //Group Title
            groupTitleBox(
                modifier = Modifier.weight(1.5f),
                viewModel = viewModel,
                groupChanged = {groupIdChanging = true}
            )
            //dates
            repeat(7) {xIndex ->
                Box(
                    modifier = Modifier
                        .background(
                            color = when (dispDates[xIndex] == viewModel.currentDate) {
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
                        boxHeight = Constants.dateBoxHeight,
                        leftWidth = 1.dp,
                        leftAlpha = 0.1f,
                        rightWidth = 2.dp,
                        rightAlpha = 0.44f,
                        downHeight = 3.dp,
                        downAlpha = 0.4f,
                    )
                    DatesBox(dispDates[xIndex])

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
                userScrollEnabled = true,
            ) {
                items(
                    items = tasks,
                    key = { task -> task.id })
                { task ->
                    //if (gridScreenOpen){
                    mainGridRow(
                        columnAmount = columnAmount,
                        viewModel = viewModel,
                        task = task,
                        dispDates = dispDates,
                        rowTapped = {xIndex ->
                            var addDate = ""
                            if (xIndex>0) {
                                addDate = TimeFunctions.getTimeNow("yyyyMMdd")
                                addDate = if ((addDate != dispDates[xIndex-1]) && viewModel.editPastGet()){
                                    ""
                                } else {
                                    dispDates[xIndex-1]
                                }
                            }
                            if (addDate in dispDates) {
                                viewModel.plusOneCount(
                                    parentId = task.id,
                                    increment = task.denominator / task.clickStep,
                                    date = addDate
                                )
                            }
                            /*
                            if (xIndex > 0){
                                val modDate = when(viewModel.editPastGet()){
                                    true-> when(TimeFunctions.getTimeNow("yyyyMMdd") in dispDates){
                                        true -> TimeFunctions.getTimeNow("yyyyMMdd")
                                        false -> null
                                    }
                                    false -> dispDates[max(xIndex-1,0)]
                                }
                                viewModel.plusOneCount(
                                    parentId = task.id,
                                    increment = task.denominator/task.clickStep,
                                    date = dispDates[max(xIndex - 1, 0)]
                                )
                            }
                            */
                        },
                        rowLongPressed = {xIndex, mcCount ->
                            if (xIndex == 0) {
                                viewModel.openUpdateDialog(task = task)
                            }
                            else if (xIndex<9){
                                viewModel.setUiState(
                                    upTask = task,
                                    dispGroupId = task.groupId,
                                    mcDate = dispDates[max(xIndex - 1, 0)],
                                    mcCount = mcCount
                                )
                                viewModel.openModifyCount()
                            }
                        },
                    )
                }

                item {Spacer(modifier = Modifier.height(25.dp))}
                item {
                    Box(modifier = Modifier
                        .offset(x = 30.dp)
                        .fillMaxWidth()) {
                        this@Column.AnimatedVisibility(
                            visible = (visibilityAddButton),
                            modifier = Modifier
                            ,
                            enter = fadeIn(
                                animationSpec = TweenSpec(
                                    durationMillis = 300,
                                    delay = 100,
                                    easing = EaseInSine
                                ),
                                initialAlpha = 0f,
                            ),
                            exit = fadeOut(
                                animationSpec = TweenSpec(
                                    durationMillis = 80,
                                    delay = 0,
                                    easing = EaseInOut
                                )
                            )
                        ) {
                            AddButton(
                                viewModel.noGroups,
                                viewModel.noTasks,
                                onClicked = {when(viewModel.noGroups){
                                        false -> viewModel.openAddNewDialog()
                                        true -> viewModel.openNewGroupDia()
                                    }
                                }
                            )
                        }
                    }
                }
                item {Spacer(modifier = Modifier.height(30.dp))}
            }
        }

        ModifyCountDialog(
            viewModel
        )
        AddTaskDialog(
            viewModel
        )

        EditTaskDialog(
            viewModel
        )
    }

}




@Composable
fun DatesBox(date: String) {
    Text(text = TimeFunctions.formatStringToDisplay(date),
        textAlign = TextAlign.Center,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 1
    )
}