package com.weeklist.screens.Components


import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.weeklist.LogicAndData.Constants.Companion.ADD
import com.weeklist.LogicAndData.Constants.Companion.emptyGroup
import com.weeklist.screens.utils.MainViewModel
import kotlinx.coroutines.delay

@Composable
fun SelectGroupMenu(viewModel : MainViewModel,
                    groupChanged : () -> Unit
) {
    val groups by viewModel.groups.collectAsStateWithLifecycle(initialValue = listOf())
    var buttonPressed by rememberSaveable{ mutableStateOf(false)}
    LaunchedEffect(buttonPressed){
        if (buttonPressed) {
            delay(50)
            viewModel.setUiState(
                upGroup = groups.find { it.idG == viewModel.uiState.dispGroupId } ?: emptyGroup
            )
            delay(50)
            buttonPressed=false
        }
    }
    DropdownMenu(
        modifier = Modifier,
        expanded = viewModel.uiState.selectGroup,
        onDismissRequest = {
            viewModel.setUiState(selectGroup = false)
        })
    {
        groups.forEach { group ->
            DropdownMenuItem(
                modifier = Modifier.background(
                    color = when(group.idG == viewModel.uiState.dispGroupId){
                        false -> MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                        true -> MaterialTheme.colorScheme.surfaceColorAtElevation(15.dp)
                    }
                ),
                text = { Text(group.groupTitle) },
                onClick = {
                    if (group.idG != viewModel.uiState.dispGroupId) {
                        buttonPressed=true
                        viewModel.setUiState(
                            upGroup = group,
                            dispGroupId = group.idG ?:0,
                            groupTitle = group.groupTitle,
                            selectGroup = false
                        )
                        viewModel.setDispGroup(group.idG ?:0)
                        viewModel.updateDispGroupId()
                        groupChanged()
                    }
                }
            )
        }
        DropdownMenuItem(
            text = {Text(ADD)},
            leadingIcon = { Icon(Icons.Filled.Add, "")},
            onClick = {
                buttonPressed = true
                viewModel.openNewGroupDia()
            }
        )
    }

    AddGroupDialog(viewModel)
}