package com.weeklist.screens.uiStuff

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.weeklist.screens.Components.SelectGroupMenu
import com.weeklist.screens.utils.MainViewModel

@Composable
fun groupTitleBox(viewModel : MainViewModel,
                  groupChanged : () -> Unit,
                  modifier : Modifier = Modifier,
                  clickable : Boolean = true){
    val groupTitle by viewModel.groupTitle.collectAsStateWithLifecycle(initialValue = "")
    Box(modifier = modifier
        .fillMaxSize()
        .clickable(enabled = clickable,
            onClick = {
                viewModel.setUiState(selectGroup = true)
            }
        ),
        contentAlignment = Alignment.Center) {
        if (groupTitle != null) {
            //Text(text = groupTitle)
            autoFitTextBox(
                textIn = groupTitle,
                targetFontSize = 16.0,
                minimumFontSize = 9.5,
                maxLines = 3,
                softWrap = false,
                fontWeight = FontWeight.SemiBold,
                maxIndexOfRow = 13
            )
        }
        else  {
            Text("")
            viewModel.setFirstGroupId()
        }
        if(clickable) {
            SelectGroupMenu(
                viewModel,
                groupChanged = { groupChanged() }
            )
        }
    }
}