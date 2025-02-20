package com.weeklist.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.weeklist.database.Task
import com.weeklist.screens.utils.MainViewModel
import com.weeklist.screens.utils.dayTotalConstructor
import com.weeklist.screens.utils.taskTotalConstructor

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TotalsScreen(
    tasks : List<Task>,
    dispDates : List<String>,
    viewModel: MainViewModel,
                 //sharedPreferences : SharedPreferences
) {
    Column {
        Row(modifier = Modifier.weight(1.1f)) {
            dayTotalConstructor(
                viewModel = viewModel,
                dispDates = dispDates
                )
        }
        Spacer(
            modifier = Modifier.height(16.dp)
        )
        Row(modifier = Modifier.weight(5f)) {
            taskTotalConstructor(
                viewModel = viewModel,
                tasks = tasks,
                dispDates = dispDates,
            )
        }
    }
}