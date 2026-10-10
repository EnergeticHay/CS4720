package edu.virginia.cs4720.jbz2et

import retrofit2.http.GET
import retrofit2.http.Path

interface ScoreboardApi {
    @GET("scoreboard/football/{division}/{year}/{week}")
    suspend fun getScoreboard(
        @Path("division") division: String,
        @Path("year") year: Int,
        @Path("week") week: String
    ): ScoreboardResponse
}