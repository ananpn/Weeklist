package com.weeklist.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.weeklist.LogicAndData.Constants.Companion.COUNT_TABLE
import com.weeklist.LogicAndData.Constants.Companion.GROUP_TABLE
import com.weeklist.LogicAndData.Constants.Companion.TASK_TABLE

@Entity(tableName = GROUP_TABLE, indices = [Index(
    value = ["groupOrder"],
    unique = true)])
data class TaskGroup(
    @PrimaryKey
    val idG: Int? = null,
    val groupOrder: Int = 0,
    val groupTitle: String,
    @ColumnInfo(name = "defaultClickStep", defaultValue = "1")
    val defaultClickStep : Int = 1,
)

@Entity(tableName = TASK_TABLE,
    foreignKeys = [
        ForeignKey(
            entity = TaskGroup::class,
            parentColumns = ["idG"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index(
                    value = ["groupId", "orderId"],
                    unique = true)])
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val groupId: Int,
    val orderId: Int = 0,
    val title: String,
    val clickStep: Int,
    val denominator: Int
)

@Entity(
    tableName = COUNT_TABLE,
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["parentId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index(
        value = ["parentId", "date"], unique = true)]
)
data class TaskCount(
    @PrimaryKey
    val idTC: Int? = null, //TaskCount column identifier
    val parentId : Int,
    val date: String,
    val count: Int
)

data class CountOfDate(
    val date : String,
    val count : Int
)

data class StringOfDate(
    val date : String,
    val dispCount : String
)


data class TotalOfTask(
    val id : Int?,
    val total: Float?
)

data class LastDoneDate(
    val id : Int?,
    val lastDone: String?
)

