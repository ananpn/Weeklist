package com.weeklist.screens.Components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.weeklist.LogicAndData.Constants.Companion.CANCEL_BUTTON
import com.weeklist.LogicAndData.Constants.Companion.CONFIRM_DELETE_TASK
import com.weeklist.LogicAndData.Constants.Companion.DELETE_BUTTON
import com.weeklist.screens.utils.MainViewModel
import com.weeklist.ui.theme.getTextButtonColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DeleteTaskDialog(
    viewModel: MainViewModel,
    onDelete: () -> Unit
)
{ if (viewModel.uiState.deleteDialogOpen) {
    val coroutineScope = rememberCoroutineScope()
    AlertDialog(
        onDismissRequest = { viewModel.setUiState(deleteDialogOpen = false) },
        title = {
            Text(
                text = CONFIRM_DELETE_TASK
            )
        },
        confirmButton = {
            TextButton(
                colors = getTextButtonColors(),
                onClick = {
                    coroutineScope.launch {
                        viewModel.deleteTask(viewModel.uiState.upTask)
                        delay(50)
                        onDelete()
                        viewModel.setUiState(deleteDialogOpen = false)
                    }
                }
            ) {
                Text(
                    text = DELETE_BUTTON
                )
            }
        },
        dismissButton = {
            TextButton(
                colors = getTextButtonColors(),
                onClick = {
                    viewModel.setUiState(deleteDialogOpen = false)
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