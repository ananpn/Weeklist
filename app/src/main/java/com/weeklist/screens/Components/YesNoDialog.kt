package com.weeklist.screens.Components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.weeklist.ui.theme.getTextButtonColors

@Composable
fun YesNoDialog(
    enabled : Boolean,
    titleText : String,
    yesText : String,
    cancelText : String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
)
{ if (enabled) {
    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        title = {
            Text(
                text = titleText
            )
        },
        confirmButton = {
            TextButton(
                colors = getTextButtonColors(),
                onClick = {
                    onConfirm()
                }
            ) {
                Text(
                    text = yesText
                )
            }
        },
        dismissButton = {
            TextButton(
                colors = getTextButtonColors(),
                onClick = {
                    onDismiss()
                }
            ) {
                Text(
                    text = cancelText
                )
            }
        }
    )
}
}