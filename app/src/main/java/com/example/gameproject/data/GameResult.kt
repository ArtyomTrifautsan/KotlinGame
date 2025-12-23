// GameResult.kt
package com.example.gameproject.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "game_results")
data class GameResult(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val level: Int = 1,
    val completionTime: Long,
    val date: Long = System.currentTimeMillis() // Измените на Long
)

@Dao
interface GameResultDao {
    @Insert
    suspend fun insertResult(result: GameResult)

    @Query("SELECT * FROM game_results ORDER BY date DESC")
    fun getAllResults(): Flow<List<GameResult>>

    @Query("DELETE FROM game_results")
    suspend fun deleteAll()
}