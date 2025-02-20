package com.weeklist.screens.Components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.weeklist.LogicAndData.Constants
import com.weeklist.LogicAndData.Constants.Companion.CANCEL_BUTTON
import com.weeklist.LogicAndData.Constants.Companion.CONFIRM_DELETE_TASK
import com.weeklist.LogicAndData.Constants.Companion.DELETE_BUTTON
import com.weeklist.LogicAndData.Constants.Companion.GIVE_TASK_CLICK_STEP
import com.weeklist.LogicAndData.Constants.Companion.GIVE_TASK_TITLE
import com.weeklist.LogicAndData.Constants.Companion.GROUP_OF
import com.weeklist.LogicAndData.Constants.Companion.TASK_EDIT_FAIL
import com.weeklist.LogicAndData.Constants.Companion.emptyTask
import com.weeklist.LogicAndData.Constants.Companion.whiteSpaceAnyWherePattern
import com.weeklist.LogicAndData.gcd
import com.weeklist.LogicAndData.newDenominator
import com.weeklist.screens.uiStuff.DeleteButton
import com.weeklist.screens.uiStuff.integerInputField
import com.weeklist.screens.uiStuff.stringInputField
import com.weeklist.screens.utils.MainViewModel
import com.weeklist.ui.theme.getTextButtonColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskDialog(
    viewModel: MainViewModel
)
{ if (viewModel.openUpdateDialog) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var task by remember { mutableStateOf(emptyTask) }
    task = viewModel.uiState.upTask
    var title by rememberSaveable { mutableStateOf(task.title) }
    var clickStep by rememberSaveable { mutableStateOf(task.clickStep.toString()) }

    //group menu dropdown
    var expanded by remember { mutableStateOf(false) }
    var selectedGroupId by rememberSaveable { mutableStateOf(task.groupId) }
    //Not from viewModel.groupTitle, since otherwise the menu would always display the opened group
    var dispGroup by rememberSaveable { mutableStateOf("") }
    dispGroup = viewModel.getGroupTitle(task.groupId).collectAsState(initial = "").value

    val groups by viewModel.groups.collectAsState(initial = listOf())
    var defaultClickStep by rememberSaveable{
        mutableStateOf(groups.find{it.idG == task.groupId}?.defaultClickStep ?:1)
    }
    defaultClickStep = groups.find{it.idG == task.groupId}?.defaultClickStep ?:1


    var dialogVisible by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(80)
        dialogVisible = true
    }
    var reOrderPress = remember { false }
    AnimatedVisibility(
        visible = dialogVisible,
        modifier = Modifier,
        enter = EnterTransition.None,
        exit = ExitTransition.None

    ) {
        AlertDialog(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .offset(x = 30.dp),
            onDismissRequest = { viewModel.closeUpdateDialog() },
            title = { Text(text = Constants.MODIFY_TASK) },
            text = {
                Column {
                    Spacer(
                        modifier = Modifier.height(9.dp)
                    )
                    Box() {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Reorder:")
                            Button(modifier = Modifier.offset(10.dp),
                                onClick = {
                                    if (!reOrderPress) {
                                        coroutineScope.launch {
                                            reOrderPress = true
                                            val newOrder = viewModel.updateTasksSequentially(
                                                direction = -1,
                                                id = task.id,
                                                groupId = task.groupId
                                            )
                                            viewModel.setUiState(
                                                upTask = viewModel.uiState.upTask.copy(
                                                    orderId = newOrder
                                                )
                                            )
                                            viewModel.userAction = true
                                            delay(80)
                                            reOrderPress = false
                                        }
                                    }
                                }) {
                                Icon(Icons.Outlined.KeyboardArrowUp, "")
                            }
                            Button(modifier = Modifier.offset(10.dp),
                                onClick = {
                                    if (!reOrderPress) {
                                        coroutineScope.launch {
                                            reOrderPress = true
                                            val newOrder = viewModel.updateTasksSequentially(
                                                direction = 1,
                                                id = task.id,
                                                groupId = task.groupId
                                            )
                                            viewModel.setUiState(
                                                upTask = viewModel.uiState.upTask.copy(
                                                    orderId = newOrder
                                                )
                                            )
                                            viewModel.userAction = true
                                            delay(80)
                                            reOrderPress = false
                                        }
                                    }
                                }) {
                                Icon(Icons.Outlined.KeyboardArrowDown, "")
                            }
                        }
                        //delete button
                        DeleteButton(
                            modifier = Modifier
                                .align(alignment = Alignment.TopEnd)
                                .offset(x = 10.dp, y = -65.dp),
                            onDelete = {
                                viewModel.setUiState(deleteDialogOpen = true)
                            }
                        )
                    }
                    Spacer(
                        modifier = Modifier.height(9.dp)
                    )
                    stringInputField(
                        inVal = title,
                        label = GIVE_TASK_TITLE,
                        onInput = {title = it }
                    )
                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )
                    integerInputField(
                        inVal = clickStep,
                        label = GIVE_TASK_CLICK_STEP,
                        onInput = {clickStep = it}
                    )

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                    ) {
                        TextField(
                            // The `menuAnchor` modifier must be passed to the text field for correctness.
                            modifier = Modifier.menuAnchor(),
                            readOnly = true,
                            value = dispGroup,
                            onValueChange = {},
                            label = { Text(GROUP_OF) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                        ) {
                            groups.forEach { group ->
                                DropdownMenuItem(
                                    text = { Text(group.groupTitle) },
                                    onClick = {
                                        dispGroup = group.groupTitle
                                        selectedGroupId = group.idG ?: 0
                                        expanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                )
                            }
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )
                }
                //Delete dialog
                YesNoDialog(
                    enabled = viewModel.uiState.deleteDialogOpen,
                    titleText = CONFIRM_DELETE_TASK,
                    yesText = DELETE_BUTTON,
                    cancelText = CANCEL_BUTTON,
                    onConfirm = {
                        coroutineScope.launch {
                            viewModel.deleteTask(viewModel.uiState.upTask)
                            delay(50)
                            viewModel.setUiState(deleteDialogOpen = false)
                        }
                    },
                    onDismiss = {
                        viewModel.setUiState(deleteDialogOpen = false)
                    }
                )
/*
                DeleteTaskDialog(
                    viewModel = viewModel,
                    onDelete = { viewModel.closeUpdateDialog() }
                )
                */
            },
            confirmButton = {
                TextButton(
                    //border = BorderStroke(width = 2.dp, color = buttonBorderColor),
                    colors = getTextButtonColors(),
                    onClick = {
                        coroutineScope.launch{
                            try{
                                if (clickStep.isEmpty()) clickStep = defaultClickStep.toString()
                                val csInt = when(clickStep.toInt()<=0){
                                    true -> defaultClickStep
                                    false -> clickStep.toInt()
                                }
                                var output = viewModel.uiState.upTask.copy(
                                    title = title.replace(whiteSpaceAnyWherePattern, " "),
                                    groupId = selectedGroupId,
                                    orderId = 9000,
                                    clickStep = csInt,
                                    denominator = newDenominator(
                                        viewModel.uiState.upTask.denominator,
                                        csInt
                                    )
                                )
                                if (task.groupId == selectedGroupId) {
                                    output = output.copy(
                                        orderId = viewModel.uiState.upTask.orderId
                                    )
                                }
                                viewModel.updateTask(output)
                                if (clickStep.toInt() != viewModel.uiState.upTask.clickStep) {
                                    viewModel.updateTaskCountsClickStep(
                                        id = output.id,
                                        newClickStep = csInt,
                                        gcd = gcd(
                                            viewModel.uiState.upTask.denominator,
                                            csInt
                                        )
                                    )
                                }
                                viewModel.setUiState(upTask = output)
                                viewModel.closeUpdateDialog()
                            }
                            catch(e : Exception){
                                clickStep = task.clickStep.toString()
                                val toast =
                                    Toast.makeText(context, TASK_EDIT_FAIL, Toast.LENGTH_LONG) // in Activity
                                toast.show()
                            }
                        }
                    }
                ) {
                    Text(
                        text = Constants.UPDATE_BUTTON
                    )
                }
            },
            dismissButton = {
                TextButton(
                    colors = getTextButtonColors(),
                    onClick = { viewModel.closeUpdateDialog() }
                ) {
                    Text(
                        text = Constants.CANCEL_BUTTON
                    )
                }
            }
        )
    }
    }
}