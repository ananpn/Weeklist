package com.weeklist.screens.Components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.weeklist.LogicAndData.Constants
import com.weeklist.screens.utils.MainViewModel
import com.weeklist.ui.theme.getTextButtonColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DeleteGroupDialog(
    viewModel: MainViewModel,
    onDelete: () -> Unit
)
{ if (viewModel.uiState.deleteDialogOpen) {
    val coroutineScope = rememberCoroutineScope()
    AlertDialog(
        onDismissRequest = { viewModel.setUiState(deleteDialogOpen = false) },
        title = {
            Text(
                text = Constants.CONFIRM_DELETE_GROUP
            )
        },
        confirmButton = {
            TextButton(
                colors = getTextButtonColors(),
                onClick = {
                    coroutineScope.launch{
                        viewModel.deleteGroup(viewModel.uiState.upGroup)
                        delay(50)
                        onDelete()
                        viewModel.setUiState(deleteDialogOpen = false)

                    }
                }
            ) {
                Text(
                    text = Constants.DELETE_BUTTON
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
                    text = Constants.CANCEL_BUTTON
                )
            }
        }
    )
}
}
