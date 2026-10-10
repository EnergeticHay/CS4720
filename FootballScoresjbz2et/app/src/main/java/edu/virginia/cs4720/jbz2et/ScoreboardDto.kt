package edu.virginia.cs4720.jbz2et

import kotlinx.serialization.Serializable

@Serializable
data class ScoreboardResponse(
    val games: List<GameWrapper> = emptyList()
)

@Serializable
data class GameWrapper(
    val game: GameDto
)

@Serializable
data class GameDto(
    val gameID: String,
    val home: TeamDto,
    val away: TeamDto,
    val gameState: String = "",
    val currentPeriod: String = "",
    val contestClock: String = "",
    val startDate: String = "",
    val startTime: String = "",
    val startTimeEpoch: String = ""
)

@Serializable
data class TeamDto(
    val score: String = "",
    val names: TeamNamesDto,
    val winner: Boolean = false
)

@Serializable
data class TeamNamesDto(
    val short: String = ""
)