package edu.virginia.cs4720.jbz2et

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import java.util.Calendar
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ScoresViewModel(private val repository: ScoresRepository) : ViewModel() {

    private val _state = MutableStateFlow(ScoresScreenState(week = currentWeek()))
    val state: StateFlow<ScoresScreenState> = _state.asStateFlow()

    private var observeJob: Job? = null

    init {
        load()
    }

    fun selectWeek(week: Int) {
        if (week == _state.value.week) return
        _state.update { it.copy(week = week) }
        load()
    }

    fun selectDivision(division: String) {
        if (division == _state.value.division) return
        _state.update { it.copy(division = division) }
        load()
    }

    fun refresh() {
        val division = _state.value.division
        val week = _state.value.week
        _state.update {
            it.copy(
                isRefreshing = true,
                uiState = if (it.uiState is ScoresUiState.Error) ScoresUiState.Loading else it.uiState
            )
        }
        viewModelScope.launch {
            val count = repository.refresh(division, SEASON_YEAR, week)
            _state.update { current ->
                if (current.division != division || current.week != week) {
                    current
                } else {
                    current.copy(
                        isRefreshing = false,
                        uiState = when {
                            count == 0 -> ScoresUiState.Success(emptyList())
                            count == null && current.uiState is ScoresUiState.Loading -> ScoresUiState.Error
                            else -> current.uiState
                        }
                    )
                }
            }
        }
    }

    private fun load() {
        val division = _state.value.division
        val week = _state.value.week
        _state.update { it.copy(uiState = ScoresUiState.Loading) }
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            var firstEmission = true
            repository.observeGames(division, SEASON_YEAR, week).collect { games ->
                if (games.isNotEmpty()) {
                    _state.update { it.copy(uiState = ScoresUiState.Success(games)) }
                }
                if (firstEmission) {
                    firstEmission = false
                    refresh()
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY])
                ScoresViewModel(
                    ScoresRepository(
                        NetworkModule.api,
                        AppDatabase.getInstance(app).gameDao()
                    )
                )
            }
        }
    }
}

fun currentWeek(): Int {
    val seasonStart = Calendar.getInstance().apply {
        clear()
        set(2026, Calendar.SEPTEMBER, 1)
    }
    val days = (System.currentTimeMillis() - seasonStart.timeInMillis) / (24L * 60 * 60 * 1000)
    return (days / 7 + 1).toInt().coerceIn(1, 14)
}