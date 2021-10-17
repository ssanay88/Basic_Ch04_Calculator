package com.example.basic_ch04_calculator.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.basic_ch04_calculator.model.History

@Dao
// 룸에 연결된 Dao
interface HistoryDao {

    // 쿼리문으로 작성
    @Query("SELECT * FROM history")
    fun getAll(): List<History>

    @Insert
    fun insertHistory(history: History)

    @Query("DELETE FROM history")
    fun deleteAll()


}