package com.weeklist.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alorma.compose.settings.storage.base.rememberBooleanSettingState
import com.alorma.compose.settings.storage.base.rememberFloatSettingState
import com.alorma.compose.settings.storage.base.rememberIntSettingState
import com.alorma.compose.settings.ui.SettingsGroup
import com.alorma.compose.settings.ui.SettingsListDropdown
import com.alorma.compose.settings.ui.SettingsSlider
import com.alorma.compose.settings.ui.SettingsSwitch
import com.weeklist.LogicAndData.Constants.Companion.COLORS_SETTINGS
import com.weeklist.LogicAndData.Constants.Companion.DARK_THEME_SETTING
import com.weeklist.LogicAndData.Constants.Companion.DEFAULT_HUE_BUTTON
import com.weeklist.LogicAndData.Constants.Companion.EDIT_PAST_SETTING
import com.weeklist.LogicAndData.Constants.Companion.EDIT_PAST_SUBTITLE
import com.weeklist.LogicAndData.Constants.Companion.GROUPS_SETTINGS
import com.weeklist.LogicAndData.Constants.Companion.HUE_SETTING
import com.weeklist.LogicAndData.Constants.Companion.PALETTE_SETTING
import com.weeklist.LogicAndData.Constants.Companion.SETTINGS_TITLE
import com.weeklist.LogicAndData.Constants.Companion.TAP_SETTINGS
import com.weeklist.LogicAndData.Constants.Companion.paletteItems
import com.weeklist.database.TaskGroup
import com.weeklist.screens.Components.AddGroupDialog
import com.weeklist.screens.Components.editGroupDialog
import com.weeklist.screens.uiStuff.AddButton
import com.weeklist.screens.utils.MainViewModel
import com.weeklist.ui.theme.getTextButtonColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel : MainViewModel,
                   onDarkThemeChanged: (Boolean) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var initialSliderValue by remember{ mutableStateOf(180f) }

    var initialDarkState by remember{ mutableStateOf(false) }

    var initialPaletteState by remember{ mutableStateOf(0) }

    var initialEditPast by remember{ mutableStateOf(false) }

    LaunchedEffect(Unit){
        initialPaletteState = viewModel.paletteGet()
        initialDarkState = viewModel.darkThemeGet()
        initialSliderValue = viewModel.seedColorGet()
        initialEditPast = viewModel.editPastGet()
    }
    /*
    Log.v("SettingsScreen",
        "initialSliderValue = ${initialSliderValue}")
    */
    val hueState = rememberFloatSettingState(
        defaultValue = initialSliderValue
    )


    val darkState = rememberBooleanSettingState(
        defaultValue = initialDarkState
    )

    val paletteState = rememberIntSettingState(defaultValue = initialPaletteState)

    val editPast = rememberBooleanSettingState(
        defaultValue = initialEditPast
    )
/*
    val rowTapMode = rememberBooleanSettingState(
        defaultValue = initialRowTapMode
    )
    */

    val groups by viewModel.groups.collectAsState(initial = listOf())

    LaunchedEffect(initialDarkState){
        darkState.value = initialDarkState
        delay(50)
    }
    LaunchedEffect(initialPaletteState){
        paletteState.value = initialPaletteState
        delay(50)
    }
    LaunchedEffect(initialSliderValue){
        hueState.value = initialSliderValue
        delay(50)
    }

    LaunchedEffect(initialEditPast){
        editPast.value = initialEditPast
        delay(50)
    }
/*
    LaunchedEffect(initialRowTapMode){
        rowTapMode.value = initialRowTapMode
        delay(50)
    }
    */

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(SETTINGS_TITLE)
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(state = rememberScrollState())
        ) {
            SettingsGroup(title = { Text(GROUPS_SETTINGS) }) {
                for (group in groups.sortedBy { it.groupOrder }
                ) {
                    Spacer(modifier = Modifier.height(5.dp))
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .fillMaxWidth()
                            .clip(shape = RoundedCornerShape(20))
                            .height(40.dp)
                            .clickable(
                                onClick = {
                                    viewModel.openUpdateDialog(group = group)
                                }
                            )
                            .background(
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                            .shadow(elevation = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {
                        Spacer(modifier = Modifier.width(20.dp))
                        groupCard(group)
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                }
                Spacer(modifier = Modifier.height(20.dp))
                AddButton(
                    noGroups = true,
                    noTasks = false,
                    onClicked = {viewModel.openNewGroupDia()},
                    modifier = Modifier.offset(x = 30.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            SettingsGroup(title = { Text(COLORS_SETTINGS) }) {
                SettingsSwitch(
                    state = darkState,
                    title = { Text(DARK_THEME_SETTING) },
                    onCheckedChange = {
                        coroutineScope.launch{
                            onDarkThemeChanged(it)
                            darkState.value = it
                            viewModel.setDarkMode(it)
                        }
                    }
                )

                SettingsSlider(
                    valueRange = (0f..360f),
                    state = hueState,
                    onValueChange = {
                        hueState.value = it
                        viewModel.setSeedColor(hueState.value)
                        viewModel.updateSeedColorState()
                        //Log.v("HueSlider", hueState.value.toString())
                    },
                    onValueChangeFinished = {

                    },
                    title = { Text(text = HUE_SETTING) })
                Button(
                    modifier = Modifier.align(Alignment.End),
                    onClick = {
                        hueState.value = 180f
                        viewModel.setSeedColor(hueState.value)
                        viewModel.updateSeedColorState()
                    },
                    colors = getTextButtonColors()
                ) {
                    Icon(Icons.Filled.Refresh, "")
                    Text(
                        text = DEFAULT_HUE_BUTTON,
                        fontSize = 10.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                var paletteItems2 : MutableList<String> = mutableListOf()
                paletteItems.onEach {paletteItems2.add(it.drop(13)) }
                SettingsListDropdown(
                    title = {Text(PALETTE_SETTING)},
                    state = paletteState,
                    items = paletteItems2,
                    onItemSelected = {index, item ->
                        paletteState.value = index
                        viewModel.setPalette(index)
                    },
                    menuItem = {index, item ->
                        Text(item)
                    },
                )

            }
            Spacer(modifier = Modifier.height(10.dp))

            SettingsGroup(title = { Text(TAP_SETTINGS) }) {
                SettingsSwitch(
                    state = editPast,
                    title = { Text(EDIT_PAST_SETTING) },
                    subtitle = { Text(EDIT_PAST_SUBTITLE) },
                    onCheckedChange = {
                        editPast.value = it
                        viewModel.setEditPast(it)
                        viewModel.updateEditPastState()
                    }
                )
/*
                SettingsSwitch(
                    state = rowTapMode,
                    title = { Text(ROW_TAP_SETTING) },
                    subtitle = { Text(ROW_TAP_SUBTITLE) },
                    onCheckedChange = {
                        rowTapMode.value = it
                        viewModel.setRowTapMode(it)
                        viewModel.updateRowTapMode()
                    }
                )
                */


            }
            Spacer(modifier = Modifier.height(36.dp))



        }


    }
    AddGroupDialog(viewModel)

    editGroupDialog(viewModel)

}

@Composable
fun groupCard(group : TaskGroup){
    Text(
        text = group.groupTitle,
        textAlign = TextAlign.Center
    )
}