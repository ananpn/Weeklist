package com.weeklist.screens.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weeklist.LogicAndData.Constants
import com.weeklist.LogicAndData.Constants.Companion.EMPTY_STRING
import com.weeklist.LogicAndData.Constants.Companion.currentDateStringConst
import com.weeklist.LogicAndData.Constants.Companion.emptyGroup
import com.weeklist.LogicAndData.Constants.Companion.emptyTask
import com.weeklist.LogicAndData.TimeFunctions.Companion.generateWeek
import com.weeklist.LogicAndData.TimeFunctions.Companion.obtainCurrentDate
import com.weeklist.database.CountOfDate
import com.weeklist.database.LastDoneDate
import com.weeklist.database.Task
import com.weeklist.database.TaskCount
import com.weeklist.database.TaskGroup
import com.weeklist.database.TaskRepository
import com.weeklist.database.TotalOfTask
import com.weeklist.di.PrefsImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo: TaskRepository,
    private val prefs: PrefsImpl,
) : ViewModel() {

    var uiState by mutableStateOf(UiState())


    //Displayed group *********************************************************************

    fun setDispGroup(dispGroupId: Int) {
        viewModelScope.launch {
            prefs.saveDispGroupId(dispGroupId)
            setUiState(dispGroupId = dispGroupId)
        }
    }

    private val _dispGroupIdView = MutableStateFlow(prefs.dispGroupId)
    val dispGroupId = _dispGroupIdView.value

    fun updateDispGroupId() {
        _dispGroupIdView.value = prefs.dispGroupId
        viewModelScope.launch {
            try {
                setUiState(dispGroupId = dispGroupId.first())
            } catch (e: Exception) {
                setUiState(dispGroupId = 0)
            }
        }
    }

    //Flows from DB ********************************************************************************

    val tasks = dispGroupId.flatMapLatest { dispGroupId -> repo.getAllTasksGroupRep(dispGroupId) }

    val groups = repo.getAllGroups()

    val groupTitle = dispGroupId.flatMapLatest { dispGroupId -> repo.getGroupTitle(dispGroupId) }


    // Task methods ************************************************************

    fun insertTask(task: Task) = viewModelScope.launch {
        repo.insertTask(task)
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        repo.updateTask(task)
    }

    suspend fun updateTasksSequentially(direction: Int, id: Int, groupId: Int) : Int {
        var output = -1
        viewModelScope.launch {
            output = repo.updateTasksSequentially(direction, id, groupId)
        }
        while(output<0){
            delay(30)
        }
        return output
    }


    suspend fun deleteTask(task: Task) = viewModelScope.launch {
        repo.deleteTask(task)
    }

    fun lastDone(groupId : Int, currentDate : String) : Flow<List<LastDoneDate>> {
        return repo.lastDone(groupId, currentDate)
    }

    //TaskCount methods ******************************************************************

    fun insertTaskCount(taskCount: TaskCount) = viewModelScope.launch {
        repo.insertTaskCount(taskCount)
    }

    fun getCountWeek(parentId: Int, dispDates: List<String>): Flow<List<CountOfDate>> {
        return repo.getCountWeek(parentId, dispDates)
    }

    fun getAllRowTotals(
        groupId: Int,
        dispDates: List<String>
    ): Flow<List<TotalOfTask>> {
        return repo.getAllRowTotals(groupId, dispDates)
    }

    fun getColumnTotal(groupId: Int, date: String): Flow<Float> {
        return repo.getColumnTotal(groupId, date)
    }

    fun updateTaskCountsClickStep(
        newClickStep: Int,
        gcd: Int,
        id: Int
    ) = viewModelScope.launch {
        repo.updateTaskCountsClickStep(newClickStep, gcd, id)
    }

    fun plusOneCount(parentId: Int, increment: Int, date: String) = viewModelScope.launch {
        repo.plusOneCount(parentId, increment, date)
    }

    //Group methods **************************

    fun setFirstGroupId() = viewModelScope.launch {
        val firstGroupId: Int? = repo.getFirstGroupId()
        if (firstGroupId != null) {
            setDispGroup(firstGroupId)
        } else setDispGroup(0)
    }

    fun insertGroup(group: TaskGroup) = viewModelScope.launch {
        repo.insertGroup(group)
    }

    suspend fun updateGroup(group: TaskGroup) = viewModelScope.launch {
        repo.updateGroup(group)
    }

    suspend fun updateGroupsSequentially(direction: Int, groupId: Int): Int {
        var output = -1
        viewModelScope.launch{
            output = repo.updateGroupsSequentially(direction, groupId)
        }
        while(output<0){
            delay(30)
        }
        return output
    }

    fun getGroupTitle(groupId : Int) : Flow<String> {
        return repo.getGroupTitle(groupId)
    }

    fun getGroupDefaultClickStep(groupId : Int) : Flow<Int> {
        return repo.getGroupDefaultCS(groupId)
    }

    fun deleteGroup(group : TaskGroup) = viewModelScope.launch{
        repo.deleteGroupTotally(group)
    }



    //Global booleans******************************************************************************

    var openAddNewDialog by mutableStateOf(false)

    var openUpdateDialog by mutableStateOf(false)

    var openModifyCount by mutableStateOf(false)

    var openNewGroupDialog by mutableStateOf(false)

    fun openAddNewDialog() {
        openAddNewDialog = true
    }

    fun closeAddNewDialog() {
        openAddNewDialog = false
    }


    fun openUpdateDialog(task : Task = emptyTask, group : TaskGroup = emptyGroup) {
        viewModelScope.launch {
            if (task != emptyTask){
                setUiState(
                    upTask = task
                )
                openUpdateDialog = true
            }
            if (group != emptyGroup) {
                setUiState(
                    upGroup = group
                )
                openUpdateDialog = true
            }
        }
    }

    fun closeUpdateDialog() {
        openUpdateDialog = false
    }

    fun openModifyCount() {
        openModifyCount = true
    }

    fun closeModifyCount() {
        openModifyCount = false
    }

    fun openNewGroupDia() {
        openNewGroupDialog = true
    }

    fun closeNewGroupDia() {
        openNewGroupDialog = false
    }

    var userAction : Boolean = false

    var noGroups : Boolean = false
    fun letNoGroups(newNoGroups : Boolean){
        noGroups = newNoGroups
    }

    var noTasks : Boolean = false
    fun letNoTasks(newNoTasks : Boolean){
        noTasks = newNoTasks
    }

    //Theme and colors ****************************************************************************

    var defaultBGColor = Color.Transparent
    var modifiedBGColor = Color.Transparent

    fun setBGColors(newDef : Color, newMod : Color){
        defaultBGColor = newDef
        modifiedBGColor = newMod
    }

    private val _darkTheme = MutableStateFlow(prefs.darkTheme)
    val darkTheme = _darkTheme.value
    fun updateDarkThemeState(){
        _darkTheme.value = prefs.darkTheme
    }

    fun setDarkMode(isNightMode: Boolean) =viewModelScope.launch {
        prefs.saveDarkTheme(isNightMode)
        delay(30)
        updateDarkThemeState()
    }

    fun darkThemeGet() : Boolean {
        updateDarkThemeState()
        var output = false
        viewModelScope.launch {
            val darkTheme2 = _darkTheme.value
            try {
                output = darkTheme2.first()
            }
            catch (e : Exception){
            }
        }
        return output
    }

    private val _seedColorSlider = MutableStateFlow(prefs.seedColorData)
    val seedColorSlider = _seedColorSlider.value

    fun updateSeedColorState(){
        _seedColorSlider.value = prefs.seedColorData
    }

    fun setSeedColor(newSliderFloat : Float) = viewModelScope.launch {
        prefs.saveSeedColorData(newSliderFloat)
    }

    fun seedColorGet() : Float {
        updateSeedColorState()
        var output = 0f
        viewModelScope.launch {
            val seedColor2 = _seedColorSlider.value

            try {
                output = seedColor2.first()
            }
            catch (e : Exception){
            }
        }
        return output
    }

    private val _paletteIn = MutableStateFlow(prefs.paletteData)
    val paletteIn = _paletteIn.value

    fun updatePaletteState(){
        _paletteIn.value = prefs.paletteData
    }

    fun setPalette(newPaletteIn : Int) = viewModelScope.launch {
        prefs.savePaletteData(newPaletteIn)
    }

    fun paletteGet() : Int {
        updatePaletteState()
        var output = 0
        viewModelScope.launch {
            val palette2 = _paletteIn.value

            try {
                output = palette2.first()
            }
            catch (e : Exception){
            }
        }
        return output
    }

    //Global variables *********************************************************************

    var screenWidthDp = 0.dp
    var screenHeightDp = 0.dp
    var boxWidth8 = (screenWidthDp/(Constants.firstColumnWeight+7f))
    var screenWidthPx = 0f

    fun updateScreenSize(screenWidthInput : Dp, screenWidthInputPx : Float, screenHeightInput : Dp){
        screenWidthDp = screenWidthInput
        screenHeightDp = screenHeightInput
        screenWidthPx = screenWidthInputPx
        boxWidth8 = (screenWidthDp/(Constants.firstColumnWeight +7f))
    }

    //Preferences *********************************************************************
/*

    private val _rowTapMode = MutableStateFlow(prefs.rowTapMode)
    val rowTapMode = _rowTapMode.value
    fun updateRowTapMode(){
        _rowTapMode.value = prefs.rowTapMode
    }

    fun setRowTapMode(isNightMode: Boolean) =viewModelScope.launch {
        prefs.saveRowTapMode(isNightMode)
    }

    fun rowTapModeGet() : Boolean {
        updateRowTapMode()
        var output = false
        viewModelScope.launch {
            val rowTapMode2 = _rowTapMode.value
            try {
                output = rowTapMode2.first()
            }
            catch (e : Exception){
            }
        }
        return output
    }
*/

    private val _editPast = MutableStateFlow(prefs.editPast)
    val editPast = _editPast.value
    fun updateEditPastState(){
        _editPast.value = prefs.editPast
    }

    fun setEditPast(isNightMode: Boolean) =viewModelScope.launch {
        prefs.saveEditPast(isNightMode)
    }

    fun editPastGet() : Boolean {
        updateEditPastState()
        var output = false
        viewModelScope.launch {
            val editPast2 = _editPast.value
            try {
                output = editPast2.first()
            }
            catch (e : Exception){
            }
        }
        return output
    }


//Time *******************************************************************************
    var currentDate : String by mutableStateOf(currentDateStringConst)
    fun currentDateSet(){
        currentDate = obtainCurrentDate()
    }

//UiState ***************************************************************************************

fun setUiState(orderId : Int = uiState.parentId,
               dispGroupId : Int = uiState.dispGroupId,
               groupTitle : String = uiState.groupTitle,
               dispDates : List<LocalDate> = uiState.dispDates,
               upTask : Task = uiState.upTask,
               upGroup : TaskGroup = uiState.upGroup,
               mcDate : String = uiState.mcDate,
               mcCount :Int = uiState.mcCount,
               xIndex : Int = uiState.xIndex,
               selectGroup : Boolean = uiState.selectGroup,
               deleteDialogOpen : Boolean = uiState.deleteDialogOpen,) {
    uiState = uiState.copy(
        parentId = orderId,
        dispGroupId = dispGroupId,
        groupTitle = groupTitle,
        dispDates =  dispDates,
        upTask = upTask,
        upGroup = upGroup,
        mcDate = mcDate,
        mcCount = mcCount,
        xIndex = xIndex,
        selectGroup = selectGroup,
        deleteDialogOpen = deleteDialogOpen,
    )
}


fun nextWeek(){
    setUiState(dispDates = generateWeek(uiState.dispDates[0].plusDays(7)))
}

fun prevWeek(){
    setUiState(dispDates = generateWeek(uiState.dispDates[0].minusDays(7)))
}

data class UiState(val dispDates : List<LocalDate> = Constants.currentWeek,
                   val dispGroupId : Int = 0,
                   val groupTitle : String = EMPTY_STRING,
                   val parentId : Int = 0,
                   val upTask : Task = emptyTask,
                   val upGroup : TaskGroup = emptyGroup,
                   val mcDate : String = currentDateStringConst,
                   val mcCount :Int = 0,
                   val xIndex : Int = 0,
                   val selectGroup : Boolean = false,
                   val deleteDialogOpen : Boolean = false,)
}

