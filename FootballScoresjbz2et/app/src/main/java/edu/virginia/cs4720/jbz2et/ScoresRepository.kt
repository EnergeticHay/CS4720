package edu.virginia.cs4720.jbz2et

import kotlinx.coroutines.flow.Flow

class ScoresRepository(
    private val api: ScoreboardApi,
    private val dao: GameDao
) {
    fun observeGames(division: String, year: Int, week: Int): Flow<List<GameEntity>> =
        dao.observeGames(division, year, week)

    suspend fun refresh(division: String, year: Int, week: Int): Int? {
        return try {
            val weekPath = week.toString().padStart(2, '0')
            val response = api.getScoreboard(division, year, weekPath)
            val entities = response.games.map { it.game.toEntity(division, year, week) }
            dao.upsertGames(entities)
            entities.size
        } catch (e: Exception) {
            null
        }
    }
}

fun GameDto.toEntity(division: String, year: Int, week: Int) = GameEntity(
    gameId = gameID,
    division = division,
    year = year,
    week = week,
    homeName = home.names.short,
    awayName = away.names.short,
    homeScore = home.score,
    awayScore = away.score,
    homeWinner = home.winner,
    awayWinner = away.winner,
    gameState = gameState,
    currentPeriod = currentPeriod,
    contestClock = contestClock,
    startDate = startDate,
    startTime = startTime,
    startTimeEpoch = startTimeEpoch.toLongOrNull() ?: 0L
)