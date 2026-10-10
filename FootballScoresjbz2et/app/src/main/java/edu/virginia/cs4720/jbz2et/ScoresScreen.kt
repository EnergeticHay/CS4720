package edu.virginia.cs4720.jbz2et

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ScoresRoute(modifier: Modifier = Modifier) {
    val vm: ScoresViewModel = viewModel(factory = ScoresViewModel.Factory)
    val state by vm.state.collectAsStateWithLifecycle()
    ScoresScreen(state = state, modifier = modifier)
}

@Composable
fun ScoresScreen(state: ScoresScreenState, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (val ui = state.uiState) {
            ScoresUiState.Loading -> CircularProgressIndicator()
            ScoresUiState.Error -> Text("Couldn't load scores")
            is ScoresUiState.Success -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(ui.games, key = { it.gameId }) { game ->
                    GameRow(game = game)
                }
            }
        }
    }
}

@Composable
fun GameRow(game: GameEntity, modifier: Modifier = Modifier) {
    val isFinal = game.gameState == "final"
    val isLive = game.gameState == "live"
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = statusText(game),
                style = MaterialTheme.typography.labelLarge,
                color = if (isLive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            TeamLine(
                label = "Away",
                name = game.awayName,
                score = game.awayScore,
                isWinner = isFinal && game.awayWinner
            )
            Spacer(modifier = Modifier.height(4.dp))
            TeamLine(
                label = "Home",
                name = game.homeName,
                score = game.homeScore,
                isWinner = isFinal && game.homeWinner
            )
        }
    }
}

@Composable
fun TeamLine(label: String, name: String, score: String, isWinner: Boolean) {
    val weight = if (isWinner) FontWeight.Bold else FontWeight.Normal
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(48.dp)
        )
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = weight,
            modifier = Modifier.weight(1f)
        )
        if (isWinner) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Winner",
                modifier = Modifier.size(18.dp)
            )
        }
        Text(
            text = score,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = weight,
            textAlign = TextAlign.End,
            modifier = Modifier.width(44.dp)
        )
    }
}

fun statusText(game: GameEntity): String = when (game.gameState) {
    "final" -> if (game.currentPeriod.contains("OT", ignoreCase = true)) "Final (OT)" else "Final"
    "pre" -> "${formatStartDay(game.startDate)} · ${game.startTime}"
    "live" -> {
        val period = periodLabel(game.currentPeriod)
        if (period == "Halftime" || game.contestClock.isBlank()) period else "$period · ${game.contestClock}"
    }
    else -> game.gameState.replaceFirstChar { it.uppercase() }
}

fun periodLabel(period: String): String = when (period.trim().uppercase()) {
    "1ST" -> "1st Quarter"
    "2ND" -> "2nd Quarter"
    "3RD" -> "3rd Quarter"
    "4TH" -> "4th Quarter"
    "HALF", "HALFTIME" -> "Halftime"
    else -> period
}

fun formatStartDay(startDate: String): String {
    return try {
        val parsed = SimpleDateFormat("MM/dd/yyyy", Locale.US).parse(startDate)
        if (parsed == null) startDate else SimpleDateFormat("EEE, MMM d", Locale.US).format(parsed)
    } catch (e: ParseException) {
        startDate
    }
}

@Preview(showBackground = true)
@Composable
fun GameRowPreview() {
    val base = GameEntity(
        "1", "fbs", 2026, 6, "San Jose St.", "Wyoming", "13", "16",
        false, true, "final", "FINAL (OT)", "0:00", "10/09/2026", "9:00 PM ET", 0L
    )
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        GameRow(game = base)
        GameRow(
            game = base.copy(
                gameId = "2", homeName = "Virginia", awayName = "Syracuse",
                homeScore = "24", awayScore = "17", awayWinner = false,
                gameState = "live", currentPeriod = "3rd", contestClock = "8:42"
            )
        )
        GameRow(
            game = base.copy(
                gameId = "3", homeName = "Alabama", awayName = "Georgia",
                homeScore = "", awayScore = "", awayWinner = false,
                gameState = "pre", currentPeriod = "", startDate = "10/10/2026", startTime = "7:30 PM ET"
            )
        )
    }
}