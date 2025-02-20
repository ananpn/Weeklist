package com.weeklist.database.Daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.weeklist.LogicAndData.Constants
import com.weeklist.database.CountOfDate
import com.weeklist.database.TaskCount
import com.weeklist.database.TotalOfTask
import kotlinx.coroutines.flow.Flow

@Dao
interface CountDao {
    // TaskCount methods ******************************

    @Query("UPDATE ${Constants.COUNT_TABLE} " +
            "SET count = (count*:newClickStep/:gcd) " +
            "WHERE parentId = :id")
    suspend fun updateTaskCountsClickStep(newClickStep : Int,
                                          gcd : Int,
                                          id :Int)

    @Query("UPDATE ${Constants.COUNT_TABLE} " +
            "SET count = (count+:increment) " +
            "WHERE parentId = :parentId AND date = :date")
    suspend fun plusOneCountDao(parentId : Int, increment : Int, date : String)

    @Insert(entity = TaskCount::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNew(taskCount: TaskCount)

    @Transaction
    suspend fun plusOneCount(parentId : Int, increment : Int, date : String){
        insertNew(TaskCount(parentId = parentId, date = date, count = 0))
        plusOneCountDao(parentId, increment, date)
    }

    @Insert(entity = TaskCount::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskCount(taskCount: TaskCount)

    @Query("SELECT count FROM ${Constants.COUNT_TABLE} WHERE parentId = :parentId AND date = :date LIMIT 1")
    fun getCount(parentId : Int, date : String): Flow<Int>

    @Query("SELECT date, count FROM ${Constants.COUNT_TABLE} WHERE parentId = :parentId AND date IN (:dispDates) GROUP BY date")
    fun getCountWeek(parentId : Int, dispDates : List<String>) : Flow<List<CountOfDate>>

    @Query("SELECT parentId as id, SUM(CAST (count as float) / CAST( ${Constants.TASK_TABLE}.denominator as float)) as total FROM ${Constants.COUNT_TABLE} " +
            "INNER JOIN ${Constants.TASK_TABLE} ON ${Constants.COUNT_TABLE}.parentId = ${Constants.TASK_TABLE}.id " +
            "WHERE date IN (:dispDatesStrings) " +
            "AND parentId = ${Constants.TASK_TABLE}.id AND groupId = :groupId GROUP BY parentId")
    fun getAllRowTotals(groupId : Int,
                        dispDatesStrings: List<String>) : Flow<List<TotalOfTask>>

    @Query(
        "SELECT SUM(CAST (count as float) / CAST( ${Constants.TASK_TABLE}.denominator as float)) FROM ${Constants.COUNT_TABLE} " +
                "INNER JOIN ${Constants.TASK_TABLE} ON ${Constants.COUNT_TABLE}.parentId = ${Constants.TASK_TABLE}.id " +
                "WHERE date = :date " +
                "AND parentId = ${Constants.TASK_TABLE}.id AND groupId = :groupId " +
                "LIMIT 1")
    fun getColumnTotal(groupId : Int, date: String): Flow<Float>

}