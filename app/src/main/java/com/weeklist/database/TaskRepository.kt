package com.weeklist.database

import kotlinx.coroutines.flow.Flow


interface TaskRepository {
    // Task methods ******************************
    suspend fun insertTask(task: Task)

    fun getAllTasksGroupRep(groupId : Int): Flow<List<Task>>

    suspend fun updateTask(task : Task)

    suspend fun updateTasksSequentially(direction : Int, id : Int, groupId : Int) : Int

    suspend fun deleteTask(task : Task)

    fun lastDone(groupId : Int, currentDate : String) : Flow<List<LastDoneDate>>

    // TaskCount methods ******************************

    suspend fun insertTaskCount(taskCount: TaskCount)

    suspend fun updateTaskCountsClickStep(
        newClickStep : Int,
        gcd : Int,
        id :Int)

    fun getCount(parentId : Int, date : String) : Flow<Int>

    fun getCountWeek(parentId : Int, dispDates : List<String>) : Flow<List<CountOfDate>>

    fun getAllRowTotals(groupId : Int,
                        dispDatesStrings: List<String>) : Flow<List<TotalOfTask>>

    fun getColumnTotal(groupId : Int, date : String) : Flow<Float>

    suspend fun plusOneCount(parentId : Int, increment :Int, date : String)

    //TaskGroup methods ********************************

    suspend fun getFirstGroupId() : Int?

    fun getGroupTitle(groupId : Int) : Flow<String>

    suspend fun insertGroup(group : TaskGroup)

    suspend fun deleteGroupTotally(group : TaskGroup)

    fun getAllGroups() : Flow<List<TaskGroup>>

    suspend fun updateGroup(group : TaskGroup)

    suspend fun updateGroupsSequentially(direction : Int, groupId : Int) : Int

    suspend fun neatifyGroupOrders()

    fun getGroupDefaultCS(groupId : Int) : Flow<Int>
}