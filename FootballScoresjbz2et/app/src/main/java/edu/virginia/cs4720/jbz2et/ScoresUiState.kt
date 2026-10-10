package edu.virginia.cs4720.jbz2et

const val SEASON_YEAR = 2026

sealed interface ScoresUiState {
    data object Loading : ScoresUiState
    data class Success(val games: List<GameEntity>) : ScoresUiState
    data object Error : ScoresUiState
}

data class ScoresScreenState(
    val division: String = "fbs",
    val week: Int = 1,
    val isRefreshing: Boolean = false,
    val uiState: ScoresUiState = ScoresUiState.Loading
)