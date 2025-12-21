package com.example.gameproject.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "game_results")
data class GameResult(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val level: Int = 1,
    val completionTime: Long, // время в миллисекундах
    val date: Date = Date()
)