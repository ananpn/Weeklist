package com.weeklist.database.Daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.weeklist.LogicAndData.Constants.Companion.COUNT_TABLE
import com.weeklist.LogicAndData.Constants.Companion.TASK_TABLE
import com.weeklist.database.LastDoneDate
import com.weeklist.database.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    // Task methods ******************************
    @Insert(entity = Task::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskDao(task: Task)

    @Transaction
    suspend fun insertTask(task: Task) {
        val lastOrder = neatifyTaskOrdersSize(task.groupId)
        insertTaskDao(task.copy(orderId = lastOrder))
    }

    @Query("SELECT orderId FROM $TASK_TABLE WHERE id = :id LIMIT 1")
    fun getTaskOrder(id : Int) : Int

    @Query("SELECT * FROM $TASK_TABLE WHERE groupId = :groupId ORDER BY orderId ASC")
    fun getAllTasksGroupDao(groupId: Int): Flow<List<Task>>

    @Update(entity = Task::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTaskDao(task : Task)

    @Transaction
    suspend fun updateTask(task : Task){
        updateTaskDao(task)
        neatifyTaskOrders(task.groupId)
    }

    @Delete(entity = Task::class)
    suspend fun deleteTaskDao(task : Task)

    @Transaction
    suspend fun deleteTask(task : Task){
        deleteTaskDao(task)
        neatifyTaskOrders(task.groupId)
    }

    @Query("UPDATE $TASK_TABLE " +
            "SET orderId = CASE orderId " +
            "WHEN :order THEN -:order-:direction-1 " +
            "WHEN :order+:direction THEN -:order-1 END " +
            "WHERE orderId IN (:order+:direction, :order) AND groupId = :groupId")
    fun updateOrderIds(direction : Int, order : Int, groupId : Int)

    @Query("UPDATE $TASK_TABLE " +
            "SET orderId = -orderId-1 " +
            "WHERE orderId < 0")
    fun updateNegativeOrderIds()

    @Transaction
    suspend fun updateTasksSequentially(direction : Int, id : Int, groupId : Int) : Int {
        val lastOrder = neatifyTaskOrdersSize(groupId)
        val order = getTaskOrder(id)
        if (order+direction in 0..lastOrder) {
            updateOrderIds(direction, order, groupId)
            updateNegativeOrderIds()
            return order+direction
        }
        else return order
    }

    @Query("SELECT * FROM $TASK_TABLE WHERE groupId = :groupId ORDER BY orderId")
    fun getTasksByGroupId(groupId: Int): List<Task>

    @Update
    suspend fun updateTasks(tasks: List<Task>)

    @Transaction
    suspend fun neatifyTaskOrdersSize(groupId: Int) : Int {
        val tasks = getTasksByGroupId(groupId)
        val output = mutableListOf<Task>()
        tasks.forEachIndexed { index : Int, task : Task ->
            output.add(task.copy(orderId = index))
        }
        updateTasks(output)
        return output.size
    }

    @Transaction
    suspend fun neatifyTaskOrders(groupId: Int) {
        val tasks = getTasksByGroupId(groupId)
        val output = mutableListOf<Task>()
        tasks.forEachIndexed { index : Int, task : Task ->
            output.add(task.copy(orderId = index))
        }
        updateTasks(output)
    }

    @Query("SELECT parentId as id, MAX(date) as lastDone FROM $COUNT_TABLE " +
            "INNER JOIN $TASK_TABLE ON $COUNT_TABLE.parentId = $TASK_TABLE.id " +
            "WHERE parentId = $TASK_TABLE.id AND $TASK_TABLE.groupId = :groupId AND date<=:currentDate AND count>0 GROUP BY parentId")
    fun lastDone(groupId : Int, currentDate : String) : Flow<List<LastDoneDate>>





}