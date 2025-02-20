package com.weeklist.screens.Components

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.weeklist.LogicAndData.Constants
import com.weeklist.LogicAndData.Constants.Companion.GIVE_GROUP_CLICK_STEP
import com.weeklist.LogicAndData.Constants.Companion.GROUP_ADD_FAIL
import com.weeklist.database.TaskGroup
import com.weeklist.screens.uiStuff.integerInputField
import com.weeklist.screens.uiStuff.stringInputField
import com.weeklist.screens.utils.MainViewModel
import com.weeklist.ui.theme.getTextButtonColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AddGroupDialog(
    viewModel : MainViewModel
)
{ if (viewModel.openNewGroupDialog) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var title by remember { mutableStateOf(Constants.EMPTY_STRING) }
    var clickStep by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = {
            viewModel.closeNewGroupDia()
        },
        title = {
            Text(
                text = Constants.ADD_GROUP
            )
        },
        text = {
            Column {
                stringInputField(
                    inVal = title,
                    label = Constants.GIVE_GROUP_TITLE,
                    shouldFocus = true,
                    onInput = {title = it }
                )
                Spacer(modifier = Modifier.height(14.dp))
                integerInputField(
                    inVal = clickStep,
                    label = GIVE_GROUP_CLICK_STEP,
                    onInput = {clickStep = it}
                )
            }
        },
        confirmButton = {
            TextButton(
                colors = getTextButtonColors(),
                onClick = {
                    coroutineScope.launch {
                        try{
                            if (clickStep.isEmpty()) clickStep = "1"
                            val csInt = clickStep.toInt()
                            if (csInt<=0) clickStep = "1"
                            val group= TaskGroup(
                                groupTitle = title.replace(Constants.whiteSpaceAnyWherePattern, " "),
                                defaultClickStep = clickStep.toInt())
                            viewModel.insertGroup(group)
                            delay(20)
                            viewModel.closeNewGroupDia()
                        }
                        catch(e : Exception){
                            clickStep = ""
                            val toast =
                                Toast.makeText(context, GROUP_ADD_FAIL, Toast.LENGTH_SHORT) // in Activity
                            toast.show()
                        }

                    }

                }
            ) {
                Text(
                    text = Constants.ADD_BUTTON
                )
            }
        },
        dismissButton = {
            TextButton(
                colors = getTextButtonColors(),
                onClick = {
                    viewModel.closeNewGroupDia()
                }
            ) {
                Text(
                    text = Constants.CANCEL_BUTTON
                )
            }
        }
    )
}
}