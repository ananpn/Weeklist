package com.weeklist.database.Daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.weeklist.LogicAndData.Constants
import com.weeklist.database.TaskGroup
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {
    //Group Methods*************************************

    @Query("SELECT idG FROM ${Constants.GROUP_TABLE} ORDER BY groupOrder LIMIT 1")
    suspend fun getFirstGroupId() : Int?

    @Query("SELECT groupTitle FROM ${Constants.GROUP_TABLE} WHERE idG = :idG LIMIT 1")
    fun getGroupTitle(idG : Int) : Flow<String>

    @Query("SELECT groupOrder FROM ${Constants.GROUP_TABLE} WHERE idG = :idG LIMIT 1")
    fun getGroupOrder(idG : Int) : Int

    @Insert(entity = TaskGroup::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupDao(group : TaskGroup)

    @Transaction
    suspend fun insertGroup(group : TaskGroup) {
        val lastOrder = neatifyGroupOrdersSize()
        insertGroupDao(group.copy(groupOrder = lastOrder))
    }

    @Delete(entity = TaskGroup::class)
    suspend fun deleteGroup(group : TaskGroup)

    @Query("SELECT * FROM ${Constants.GROUP_TABLE} ORDER BY groupOrder ASC")
    suspend fun getAllGroupsAsList() : List<TaskGroup>

    @Query("SELECT * FROM ${Constants.GROUP_TABLE} ORDER BY groupOrder ASC")
    fun getAllGroups() : Flow<List<TaskGroup>>

    @Update(entity = TaskGroup::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateGroupDao(group : TaskGroup)

    @Transaction
    suspend fun updateGroup(group : TaskGroup) {
        updateGroupDao(group)
        neatifyGroupOrders()
    }

    @Query("UPDATE ${Constants.GROUP_TABLE} " +
            "SET groupOrder = CASE groupOrder " +
            "WHEN :order THEN -:order-:direction-1 WHEN :order+:direction THEN -:order-1 END " +
            "WHERE groupOrder IN (:order+:direction, :order)")
    fun updateGroupOrders(order : Int, direction : Int)

    @Query("UPDATE ${Constants.GROUP_TABLE} " +
            "SET groupOrder = -groupOrder-1 " +
            "WHERE groupOrder < 0")
    fun updateNegativegroupOrders()

    @Transaction
    suspend fun updateGroupsSequentially(direction : Int, groupId : Int) : Int {
        val lastOrder = neatifyGroupOrdersSize()
        val groupOrder = getGroupOrder(groupId)
        if (groupOrder+direction in 0..lastOrder) {
            updateGroupOrders(groupOrder, direction)
            updateNegativegroupOrders()
            return groupOrder+direction
        }
        else return groupOrder
    }

    @Query("DELETE FROM ${Constants.TASK_TABLE} WHERE groupId = :groupId")
    suspend fun deleteTasksOfGroup(groupId : Int)

    @Query("DELETE FROM ${Constants.COUNT_TABLE} WHERE parentId IN " +
            "(SELECT id FROM ${Constants.TASK_TABLE} WHERE groupId = :groupId)")
    suspend fun deleteTaskCountsOfGroup(groupId : Int)

    @Transaction
    suspend fun deleteGroupTotally(group : TaskGroup)  {
        deleteGroup(group)
        if (group.idG != null) {
            deleteTasksOfGroup(group.idG)
            deleteTaskCountsOfGroup(group.idG)
        }
        neatifyGroupOrders()
    }

    @Update
    suspend fun updateGroups(groups: List<TaskGroup>)

    @Transaction
    suspend fun neatifyGroupOrdersSize() : Int {
        val groups = getAllGroupsAsList()
        val output = mutableListOf<TaskGroup>()
        groups.forEachIndexed { index : Int, group : TaskGroup ->
            output.add(group.copy(groupOrder = index))
        }
        updateGroups(output)
        return output.size
    }

    @Transaction
    suspend fun neatifyGroupOrders() {
        val groups = getAllGroupsAsList()
        val output = mutableListOf<TaskGroup>()
        groups.forEachIndexed { index : Int, group : TaskGroup ->
            output.add(group.copy(groupOrder = index))
        }
        updateGroups(output)
    }

    @Query("SELECT defaultClickStep FROM ${Constants.GROUP_TABLE} WHERE idG = :groupId LIMIT 1")
    fun getGroupDefaultCS(groupId : Int) : Flow<Int>
}