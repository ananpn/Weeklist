package com.weeklist.database

import com.weeklist.database.Daos.CountDao
import com.weeklist.database.Daos.GroupDao
import com.weeklist.database.Daos.TaskDao
import kotlinx.coroutines.flow.Flow

class OfflineRepository(private val taskDao: TaskDao,
                        private val countDao: CountDao,
                        private val groupDao: GroupDao,
) : TaskRepository {
    // Task methods ************************************************************
    override suspend fun insertTask(task: Task) = taskDao.insertTask(task)

    override suspend fun updateTask(task : Task) = taskDao.updateTask(task)

    override fun getAllTasksGroupRep(groupId : Int): Flow<List<Task>>
        = taskDao.getAllTasksGroupDao(groupId)


    override suspend fun updateTasksSequentially(direction : Int, id : Int, groupId : Int) : Int
        = taskDao.updateTasksSequentially(direction, id, groupId)

    override suspend fun deleteTask(task : Task) = taskDao.deleteTask(task)

    override fun lastDone(groupId : Int, currentDate : String) : Flow<List<LastDoneDate>> =
        taskDao.lastDone(groupId, currentDate)

    // TaskCount methods ************************************************************

    override suspend fun insertTaskCount(taskCount: TaskCount) = countDao.insertTaskCount(taskCount)

    override suspend fun updateTaskCountsClickStep(
        newClickStep : Int,
        gcd : Int,
        id :Int)
        =countDao.updateTaskCountsClickStep(newClickStep, gcd, id)

    override fun getCount(parentId : Int, date : String): Flow<Int>
        = countDao.getCount(parentId, date)

    override fun getCountWeek(parentId : Int, dispDates : List<String>)  : Flow<List<CountOfDate>>
        =countDao.getCountWeek(parentId, dispDates)

    override fun getAllRowTotals(groupId : Int,
                                 dispDatesStrings: List<String>) : Flow<List<TotalOfTask>>
            = countDao.getAllRowTotals(groupId, dispDatesStrings)

    override fun getColumnTotal(groupId : Int, date : String) : Flow<Float>
        = countDao.getColumnTotal(groupId, date)

    override suspend fun plusOneCount(parentId : Int, increment : Int, date : String)
        = countDao.plusOneCount(parentId, increment, date)

    //TaskGroup methods **************************************************************

    override suspend fun getFirstGroupId(): Int? = groupDao.getFirstGroupId()

    override fun getGroupTitle(groupId : Int) : Flow<String> = groupDao.getGroupTitle(groupId)

    override suspend fun insertGroup(group : TaskGroup)
        = groupDao.insertGroup(group)

    override suspend fun deleteGroupTotally(group : TaskGroup)
        = groupDao.deleteGroupTotally(group)

    override fun getAllGroups() : Flow<List<TaskGroup>> = groupDao.getAllGroups()

    override suspend fun updateGroup(group : TaskGroup) = groupDao.updateGroup(group)

    override suspend fun updateGroupsSequentially(direction : Int, groupId : Int) : Int
        = groupDao.updateGroupsSequentially(direction, groupId)

    override suspend fun neatifyGroupOrders() =
        groupDao.neatifyGroupOrders()

    override fun getGroupDefaultCS(groupId : Int) : Flow<Int> =
        groupDao.getGroupDefaultCS(groupId)
}