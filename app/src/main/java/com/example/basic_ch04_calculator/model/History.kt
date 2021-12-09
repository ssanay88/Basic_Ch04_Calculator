package com.example.basic_ch04_calculator.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// gradle 에서 Room을 사용한다고 선언해줘야한다.
@Entity
data class History(
    @PrimaryKey val uid:Int?,    // 유일키
    // ColumnInfo : 테이블안에서 열 이름을 원하는 것으로 지정하고 싶을 때 설정
    @ColumnInfo(name = "expression") val expression: String?,    // 계산식
    @ColumnInfo(name = "result") val result: String?    // 결과값
)