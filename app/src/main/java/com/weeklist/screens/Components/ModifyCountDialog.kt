package com.weeklist.screens.Components


import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.weeklist.LogicAndData.Constants
import com.weeklist.database.Task
import com.weeklist.database.TaskCount
import com.weeklist.screens.utils.MainViewModel
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifyCountDialog(
    viewModel: MainViewModel
) {if (viewModel.openModifyCount) {
    val task = viewModel.uiState.upTask
    val context = LocalContext.current
    var inputDone by remember{ mutableStateOf(false) }
    var textFieldFocused by remember{ mutableStateOf(false) }
    Dialog(
        onDismissRequest = {
            if (!textFieldFocused) {
                viewModel.closeModifyCount()
            }
            else {
                inputDone = true
            }

        }
    )
    {
        var countInsert by remember{ mutableStateOf("") }
        val focusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current
        if (inputDone){
            inputDone=false
            focusManager.clearFocus()
            viewModel.setUiState(
                mcCount = valueToCount(countInsert, task)
            )
            val countInsert2 = countToDispValue(viewModel.uiState.mcCount, task)
            //Toast näkyviin vain jos hyvä input. Huonolla inputilla arvo -> 0
            try {
                countInsert.toFloat() //failaa jos huono input
                if (countInsert2 != countInsert) {
                    val toast =
                        Toast.makeText(context, "Value rounded", Toast.LENGTH_SHORT) // in Activity
                    toast.show()
                }
            }
            catch(e : Exception){ }
            countInsert = countInsert2
            viewModel.insertTaskCount(
                TaskCount(
                    parentId = task.id,
                    date = viewModel.uiState.mcDate,
                    count = viewModel.uiState.mcCount
                )
            )

        }
        if (!textFieldFocused){
            countInsert = countToDispValue(viewModel.uiState.mcCount, task)
        }
        Row(
            modifier = Modifier
                .offset(20.dp)
                .fillMaxWidth(0.6f),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Column(modifier = Modifier
                .width(70.dp)
                //.weight(2.5f)
            )
            {
                Button(
                    enabled = !textFieldFocused,
                    onClick = {
                        viewModel.userAction=true
                        val newCount = viewModel.uiState.mcCount + task.denominator/task.clickStep
                        viewModel.plusOneCount(
                            parentId = task.id,
                            increment = task.denominator/task.clickStep,
                            date = viewModel.uiState.mcDate
                        )
                        viewModel.setUiState(
                            mcCount = newCount
                        )
                    }
                ) {
                    Icon(Icons.Outlined.KeyboardArrowUp, "")
                }
                Button(enabled = !textFieldFocused,
                    onClick = {
                        viewModel.userAction=true
                        val newCount = viewModel.uiState.mcCount - task.denominator/task.clickStep
                        if (newCount >= 0) {
                            viewModel.plusOneCount(
                                parentId = task.id,
                                increment = - task.denominator / task.clickStep,
                                date = viewModel.uiState.mcDate
                            )
                            viewModel.setUiState(
                                mcCount = newCount
                            )
                        }
                    }
                ) {
                    Icon(Icons.Outlined.KeyboardArrowDown, "")

                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            TextField(modifier = Modifier
                .onFocusChanged { textFieldFocused = it.isFocused }
                .fillMaxHeight(0.07f)
                .width(39.dp + (max(countInsert.length, 3) * 7).dp)
                .focusRequester(focusRequester)
                    ,
                value = countInsert,
                onValueChange = {
                    if ((it.isEmpty() || it.matches(Constants.decimalPattern))
                                && it.length < 8) {
                        countInsert = it
                    }
                },
                shape = RoundedCornerShape(28),
                textStyle = TextStyle.Default.copy(
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        inputDone = true
                    }
                ),
                maxLines  = 1,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )
        }
    }
}
}

private fun valueToCount(input : String, task : Task) : Int{
    var first : Float = 0f
    try {first = input.toFloat()*task.clickStep.toFloat()}
    catch (e: Exception){}
    val rawCount : Int = first.roundToInt()
    var count = 0
    count = ((rawCount.toFloat()*task.denominator.toFloat())/task.clickStep.toFloat()).roundToInt()
    return count
}

private fun countToDispValue(count : Int, task : Task) : String{
    //val rawCount = count*task.clickStep
    val drawnCount = count.toFloat()/task.denominator.toFloat()
    val decimals = drawnCount.toString()
        .replace(Regex("(^-?\\d+\\.\\d*[1-9])(0+\$)|(\\.0+\$)"), "$1") //trims end zeroes
        .substringAfterLast(".", "").length
    val decimals2 = min(decimals, 2)
    val format : String = "%.${decimals2}f"
    val output = String
        .format(format, drawnCount)
        .replace(Regex("(^-?\\d+\\.\\d*[1-9])(0+\$)|(\\.0+\$)"), "$1")
        .replace(Regex("^0{1}$"), "")
    return output
}

