package com.example.basic_ch04_calculator

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.basic_ch04_calculator.dao.HistoryDao
import com.example.basic_ch04_calculator.model.History

@Database(entities = [History::class],version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun historyDao(): HistoryDao

}