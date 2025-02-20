package com.weeklist.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import com.weeklist.database.Task
import com.weeklist.screens.utils.MainViewModel
import com.weeklist.screens.utils.mainGridConstructor

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GridScreen(viewModel: MainViewModel,
               tasks : List<Task>,
               dispDates : List<String>
) {
    if (viewModel.noGroups){
        mainGridConstructor(
            tasks = emptyList(),
            viewModel = viewModel,
            dispDates = dispDates,
        )
    }
    else {
        mainGridConstructor(
            tasks = tasks,
            viewModel = viewModel,
            dispDates = dispDates
        )
    }
}