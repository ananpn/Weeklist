package com.weeklist.screens.uiStuff

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInSine
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.weeklist.LogicAndData.Constants
import com.weeklist.LogicAndData.booleanToInt
import com.weeklist.ui.theme.getDeleteButtonColors
import com.weeklist.ui.theme.getTextButtonColors
import com.weeklist.ui.theme.getTransparentButtonColors

@Composable
fun AddButton(noGroups : Boolean,
              noTasks : Boolean,
              onClicked : () -> Unit,
              modifier : Modifier = Modifier){
    if (noGroups){
        Button(
            modifier = modifier
                .height(50.dp)
                .fillMaxWidth(0.4f)
            ,
            onClick = { onClicked() },
            colors = getTextButtonColors()
        )
        {
            Icon(Icons.Outlined.Add, contentDescription = "")
            Text(Constants.ADD_GROUP_BUTTON, maxLines = 1, overflow = TextOverflow.Visible)
        }
    }
    else{
        Button(
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth(when(noTasks){
                    false -> 0.34f
                    true -> 0.4f
                }
                )
            ,
            onClick = { onClicked() },
            colors = getTextButtonColors()
        )
        {
            Icon(Icons.Outlined.Add, contentDescription = "")
            Text(
                text = when(noTasks){
                    false -> Constants.ADD_BUTTON
                    true -> Constants.ADD_TASK_BUTTON
                },
                maxLines = 1,
                overflow = TextOverflow.Visible
            )
        }
    }
}

@Composable
fun BoxScope.DeleteButton(
    modifier : Modifier,
    onDelete: () -> Unit)
{
    var activateDelete by remember{mutableStateOf(false)}
    AnimatedVisibility(
        visible = activateDelete,
        modifier = modifier,
        enter = fadeIn(
            animationSpec = TweenSpec(
                durationMillis = 250,
                delay = 20,
                easing = EaseInSine
            ),
            initialAlpha = 0f,
        )
    )
    {
        Button(modifier = Modifier,
            contentPadding = PaddingValues(5.dp),
            shape = RoundedCornerShape(corner = CornerSize(15)),
            colors = getDeleteButtonColors(),
            onClick = {
                if (activateDelete) onDelete()
            }
        ) {
            Icon(
                Icons.Outlined.Delete,
                contentDescription ="",
                modifier = Modifier
                    .size(20.dp)
                    .offset(0.dp, 0.dp)
            )
        }
    }
    //fake delete button
    AnimatedVisibility(
        visible = !activateDelete,
        modifier = modifier,
        exit = fadeOut(
            animationSpec = TweenSpec(
                durationMillis = 250,
                delay = 20,
                easing = EaseInSine
            ),
        )
    )
    {
        Button(modifier = Modifier
            ,
            contentPadding = PaddingValues(5.dp),
            enabled = false,
            shape = RoundedCornerShape(corner = CornerSize(15)),
            colors =  getDeleteButtonColors()
            ,
            onClick = {
            }
        ) {
            Icon(
                Icons.Outlined.Delete,
                contentDescription = "",
                modifier = Modifier
                    .size(20.dp)
                    .offset(0.dp, 0.dp)
            )
        }
    }
    Button(modifier = modifier
            .offset(y = (- 300 * booleanToInt(activateDelete)).dp),
        contentPadding = PaddingValues(5.dp),
        enabled = !activateDelete,
        shape = RoundedCornerShape(corner = CornerSize(15)),
        colors = getTransparentButtonColors(),
        onClick = {
            activateDelete = true
        }
    ) {
        Icon(
            Icons.Outlined.Delete,
            contentDescription ="",
            modifier = Modifier
                .size(20.dp)
                .offset(0.dp, 0.dp)
        )
    }
}