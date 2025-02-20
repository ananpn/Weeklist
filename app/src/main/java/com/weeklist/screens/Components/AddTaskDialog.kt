package com.weeklist.screens.Components

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.weeklist.LogicAndData.Constants.Companion.ADD_BUTTON
import com.weeklist.LogicAndData.Constants.Companion.ADD_TASK
import com.weeklist.LogicAndData.Constants.Companion.CANCEL_BUTTON
import com.weeklist.LogicAndData.Constants.Companion.EMPTY_STRING
import com.weeklist.LogicAndData.Constants.Companion.GIVE_TASK_CLICK_STEP
import com.weeklist.LogicAndData.Constants.Companion.GIVE_TASK_TITLE
import com.weeklist.LogicAndData.Constants.Companion.TASK_ADD_FAIL
import com.weeklist.LogicAndData.Constants.Companion.whiteSpaceAnyWherePattern
import com.weeklist.database.Task
import com.weeklist.screens.uiStuff.integerInputField
import com.weeklist.screens.uiStuff.stringInputField
import com.weeklist.screens.utils.MainViewModel
import com.weeklist.ui.theme.getTextButtonColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun AddTaskDialog(
    viewModel : MainViewModel,
)
{ if (viewModel.openAddNewDialog) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var title by remember { mutableStateOf(EMPTY_STRING) }
    val defaultClickStepFlow = viewModel.getGroupDefaultClickStep(viewModel.uiState.dispGroupId)
    val defaultClickStep = defaultClickStepFlow.collectAsState(1)
    var clickStep by remember{mutableStateOf(EMPTY_STRING)}
    val focusRequester = FocusRequester()
    LaunchedEffect(Unit){
        delay(30)
        clickStep = defaultClickStep.value.toString()
        delay(30)
    }
    AlertDialog(
        onDismissRequest = {
            viewModel.closeAddNewDialog()
        },
        title = {
            Text(
                text = ADD_TASK
            )
        },
        text = {
            Column() {
                stringInputField(
                    inVal = title,
                    label = GIVE_TASK_TITLE,
                    shouldFocus = true,
                    onInput = {title = it}
                )
                Spacer(
                    modifier = Modifier.height(16.dp)
                )
                integerInputField(
                    clickStep,
                    label = GIVE_TASK_CLICK_STEP,
                    onInput = {clickStep = it}
                )
            }
        },
        confirmButton = {
            TextButton(
                colors = getTextButtonColors(),
                onClick = {
                    coroutineScope.launch {
                        try {
                            if (clickStep.isEmpty()) clickStep =
                                defaultClickStep.value.toString()
                            val task = Task(
                                title = title.replace(whiteSpaceAnyWherePattern, " "),
                                groupId = viewModel.uiState.dispGroupId,
                                clickStep = clickStep.toInt(),
                                denominator = clickStep.toInt()
                            )
                            viewModel.insertTask(task)
                            delay(30)
                            viewModel.closeAddNewDialog()
                        }
                        catch(e :Exception){
                            clickStep = defaultClickStep.value.toString()
                            val toast =
                                Toast.makeText(context, TASK_ADD_FAIL, Toast.LENGTH_LONG) // in Activity
                            toast.show()

                        }
                    }
                }
            ) {
                Text(
                    text = ADD_BUTTON
                )
            }
        },
        dismissButton = {
            TextButton(
                colors = getTextButtonColors(),
                onClick = {
                    viewModel.closeAddNewDialog()
                }
            ) {
                Text(
                    text = CANCEL_BUTTON
                )
            }
        }
    )
}
}
