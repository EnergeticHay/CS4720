package edu.virginia.cs4720.jbz2et

import androidx.room.Entity

@Entity(tableName = "games", primaryKeys = ["gameId", "division"])
data class GameEntity(
    val gameId: String,
    val division: String,
    val year: Int,
    val week: Int,
    val homeName: String,
    val awayName: String,
    val homeScore: String,
    val awayScore: String,
    val homeWinner: Boolean,
    val awayWinner: Boolean,
    val gameState: String,
    val currentPeriod: String,
    val contestClock: String,
    val startDate: String,
    val startTime: String,
    val startTimeEpoch: Long
)