package com.example.basic_ch04_calculator.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.basic_ch04_calculator.model.History

@Dao
// 룸에 연결된 Dao
// Data Access Object , 데이터베이스에 접근가능한 쿼리를 제공
// DB에서 원하는 정보를 찾는 쿼리문을 함수로 접근가능하도록 해준다.
interface HistoryDao {

    // 쿼리문으로 작성
    @Query("SELECT * FROM history")
    fun getAll(): List<History>

    // 기록을 추가 (Insert)
    @Insert
    fun insertHistory(history: History)

    @Query("DELETE FROM history")
    fun deleteAll()


}