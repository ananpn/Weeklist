package com.weeklist.LogicAndData

import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.weeklist.LogicAndData.TimeFunctions.Companion.formatToString
import com.weeklist.LogicAndData.TimeFunctions.Companion.generateWeek
import com.weeklist.database.Task
import com.weeklist.database.TaskGroup


class Constants {
    companion object {

        //animation durations, delays ***********************************************
        const val appInitializedDelay : Long = 50 //must be long since this is in delay()
        const val appDarkThemeGetDelay : Long = 70 //must be long since this is in delay()
        const val mainGridGroupChangeDelay : Long = 120 //must be long since this is in delay()
        const val mainGridGroupChangeAnimationDur : Int = 150
        const val navigationGraphEnterDelay : Int = 170
        const val navigationGraphEnterDur: Int = 200
        const val navigationGraphExitDur: Int = 130
        const val settingsEnterDur : Int = 150


        //Placeholders ************************************************************
        val emptyTask = Task(
            id=0,
            groupId = 0,
            orderId = 0,
            title = "",
            clickStep = 0,
            denominator = 1
        )

        val emptyGroup = TaskGroup(
            idG=0,
            groupOrder = 0,
            groupTitle = ""
        )

        //Other Constants******************************************************

        const val firstColumnWeight = 1.5f //weight in maingrid for title box
        const val taskTotalsFirstWeight = 1.1f
        const val taskTotalsWeight = 1f
        const val lastDoneWeight = 0.5f

        const val normalFontSize = 10.5
        const val rowTotalsFontSize = 11.5
        const val lastDoneFontSize = 10

        const val defaultColorFloat = 345f
        val defaultSeedColor = transformFloatToColor(float = defaultColorFloat)

        val dateBoxHeight = 55.dp
        val boxHeight = 50.dp

        val pressZero = PressInteraction.Press(Offset(0.5f, 0.5f))

        val paletteItems = listOf(
            "PaletteStyle.TonalSpot",
            //"PaletteStyle.Expressive",
            "PaletteStyle.Neutral",
            //"PaletteStyle.Content",
            "PaletteStyle.FruitSalad",
            //"PaletteStyle.Fidelity",
            "PaletteStyle.Rainbow",
            "PaletteStyle.Vibrant"
        )

        const val dateRowHeight = 55

        val dateRowModifier = Modifier
            .height(dateRowHeight.dp)

        val onlyNumbersPattern = Regex("^\\d+\$")
        val decimalPattern = Regex("^\\d+\\.?\\d*\$")
        val noSpecialCharPattern = Regex("^\\d+\\.?\\d*\$")
        //val startSpacePattern = Regex("^\\s+")
        val whiteSpaceAnyWherePattern = Regex("\\s+")
        val spaceAnyWherePattern = Regex("\" \"+")
        val newLinePattern = Regex("\\v")
        val textInputNegativePattern = Regex("^\\v+\$")



        //Strings *********************************************************************

        //time
        val currentDateStringConst = TimeFunctions.getTimeNow("yyyyMMdd")
        //val currentDate = LocalDate.now()
        private val lastMonday = TimeFunctions.lastMonday()
        val currentWeek = generateWeek(lastMonday)
        val currentWeekStrings = currentWeek.map{date -> formatToString(date, "yyyyMMdd") }

        //Room
        const val TASK_TABLE = "tasks"
        const val COUNT_TABLE = "task_counts"
        const val GROUP_TABLE = "groups"
        const val POSITION_TABLE = "positions"

        //Titles
        const val WEEK_TOTAL_TITLE = "All tasks"
        const val LAST_DONE = "Last Done"

        //Actions
        const val CONFIRM_DELETE = "Really delete?"
        const val DAY_TOTAL = "Day total"

        //Task Strings
        const val ADD_TASK = "Add a task."
        const val DELETE_TASK = "Delete a task."
        const val MODIFY_TASK = "Edit task:"
        const val CONFIRM_DELETE_TASK = "Really delete this task? It's history cannot be recovered."
        const val GIVE_TASK_TITLE = "Name of task:"
        //const val MAXIMUM = "Maximum value..."
        const val GIVE_TASK_CLICK_STEP = "How many taps to add 1:"

        //Count Strings
        const val MODIFY_COUNT = "Modify count:"

        //Group Strings
        const val GROUP_OF = "Group:"
        const val ADD_GROUP = "Add a group."
        const val MODIFY_GROUP = "Edit group:"
        const val DELETE_GROUP = "Delete group."
        const val CONFIRM_DELETE_GROUP = "Really delete this group? It cannot be recovered."
        const val GIVE_GROUP_TITLE = "Name group:"
        const val GIVE_GROUP_CLICK_STEP = "Taps needed to add 1:"
        const val GROUP_CLICK_STEP_INFO = "Applies to new tasks."

        //Buttons
        const val PREVIOUS_WEEK = "Previous Week"
        const val NEXT_WEEK = "Next Week"
        const val ADD_BUTTON = "Add new"
        const val ADD = "Add"
        const val ADD_GROUP_BUTTON = "Add a group"
        const val ADD_TASK_BUTTON = "Add a task"
        const val CANCEL_BUTTON = "Cancel"
        const val UPDATE_BUTTON = "Update"
        const val SETTINGS_BUTTON = "Settings"
        const val DONE_BUTTON = "Done"
        const val DELETE_BUTTON = "Delete"

        //Settings
        const val SETTINGS_TITLE = "Settings"
        const val GROUPS_SETTINGS = "Groups"
        const val COLORS_SETTINGS = "Colors"
        const val TAP_SETTINGS = "Tap Behaviour"
        const val DARK_THEME_SETTING = "Dark Theme"
        const val HUE_SETTING = "Base Color"
        const val DEFAULT_HUE_BUTTON = "Default"
        const val PALETTE_SETTING = "Palette:"
        const val EDIT_PAST_SETTING = "Tapping only affects current day"
        const val EDIT_PAST_SUBTITLE = "Other dates can still be edited by long tapping"
        const val ROW_TAP_SETTING = "Tap on row to add to current day"
        const val ROW_TAP_SUBTITLE = "Other dates can still be edited by long tapping"


        //Placeholders
        const val EMPTY_STRING = ""

        //Toasts
        const val TASK_ADD_FAIL = "Something went wrong, nothing was saved"
        const val TASK_EDIT_FAIL = "Something went wrong, no changes saved"
        const val GROUP_ADD_FAIL = "Inputted values invalid, group not created"
        const val GROUP_EDIT_FAIL = "Inputted values invalid, no changes"


    }
}


