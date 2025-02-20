package com.weeklist.screens.Components

import android.widget.Toast
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.weeklist.LogicAndData.Constants
import com.weeklist.LogicAndData.Constants.Companion.GIVE_GROUP_CLICK_STEP
import com.weeklist.LogicAndData.Constants.Companion.GIVE_GROUP_TITLE
import com.weeklist.LogicAndData.Constants.Companion.GROUP_EDIT_FAIL
import com.weeklist.LogicAndData.Constants.Companion.whiteSpaceAnyWherePattern
import com.weeklist.screens.uiStuff.DeleteButton
import com.weeklist.screens.uiStuff.integerInputField
import com.weeklist.screens.uiStuff.stringInputField
import com.weeklist.screens.utils.MainViewModel
import com.weeklist.ui.theme.getTextButtonColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun editGroupDialog(
    viewModel: MainViewModel
)
{ if (viewModel.openUpdateDialog) {
    val coroutineScope = rememberCoroutineScope()
    var group by remember{ mutableStateOf(Constants.emptyGroup) }
    group = viewModel.uiState.upGroup
    val context = LocalContext.current
    var title by remember { mutableStateOf(group.groupTitle) }
    var clickStep by remember { mutableStateOf(group.defaultClickStep.toString()) }

    var reOrderPress = remember { false }

    AlertDialog(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .offset(x = 30.dp),
        onDismissRequest = { viewModel.closeUpdateDialog() },
        title = { Text(text = Constants.MODIFY_GROUP) },
        text = {
            Column {
                Spacer(
                    modifier = Modifier.height(9.dp)
                )
                Box(){
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Reorder:")
                    Button(modifier = Modifier.offset(10.dp),
                        onClick = {
                            if (!reOrderPress) coroutineScope.launch {
                                reOrderPress = true
                                val newOrder = viewModel.updateGroupsSequentially(
                                    direction = -1,
                                    groupId = group.idG ?: 0
                                )
                                viewModel.setUiState(
                                    upGroup = viewModel.uiState.upGroup.copy(
                                        groupOrder = newOrder
                                    )
                                )
                                delay(80)
                                reOrderPress = false
                            }
                        }) {
                        Icon(Icons.Outlined.KeyboardArrowUp, "")
                    }
                    Button(modifier = Modifier.offset(10.dp),
                        onClick = {
                            if (!reOrderPress) coroutineScope.launch {
                                reOrderPress = true
                                val newOrder = viewModel.updateGroupsSequentially(
                                    direction = 1,
                                    groupId = group.idG ?:0
                                )
                                viewModel.setUiState(
                                    upGroup = viewModel.uiState.upGroup.copy(
                                        groupOrder = newOrder
                                    )
                                )
                                delay(80)
                                reOrderPress = false
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
                label = GIVE_GROUP_TITLE,
                onInput = {title = it}
            )
            Spacer(modifier = Modifier.height(14.dp))
            integerInputField(
                inVal = clickStep,
                label = GIVE_GROUP_CLICK_STEP,
                onInput = {clickStep = it}
            )
            }
            //Delete dialog
            YesNoDialog(
                enabled = viewModel.uiState.deleteDialogOpen,
                titleText = Constants.CONFIRM_DELETE_GROUP,
                yesText = Constants.DELETE_BUTTON,
                cancelText = Constants.CANCEL_BUTTON,
                onConfirm = {
                    coroutineScope.launch {
                        viewModel.deleteGroup(viewModel.uiState.upGroup)
                        delay(50)
                        viewModel.setUiState(deleteDialogOpen = false)
                    }
                },
                onDismiss = {
                    viewModel.setUiState(deleteDialogOpen = false)
                }
            )
        },
        confirmButton = {
            TextButton(
                colors = getTextButtonColors(),
                onClick = {
                    coroutineScope.launch{
                        try{
                            if (clickStep.isEmpty()) clickStep = group.defaultClickStep.toString()
                            val csInt = clickStep.toInt()
                            if (csInt<=0) clickStep = group.defaultClickStep.toString()
                            var output = viewModel.uiState.upGroup.copy(
                                groupTitle = title.replace(whiteSpaceAnyWherePattern, " "),
                                defaultClickStep = clickStep.toInt()
                            )
                            viewModel.setUiState(upGroup = output)
                            viewModel.updateGroup(output)
                            delay(50)
                            viewModel.closeUpdateDialog()
                        }
                        catch(e : Exception){
                            clickStep = group.defaultClickStep.toString()
                            val toast =
                                Toast.makeText(context, GROUP_EDIT_FAIL, Toast.LENGTH_SHORT) // in Activity
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