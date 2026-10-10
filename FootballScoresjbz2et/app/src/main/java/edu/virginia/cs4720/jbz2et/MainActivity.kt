package edu.virginia.cs4720.jbz2et

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import edu.virginia.cs4720.jbz2et.ui.theme.FootballScoresjbz2etTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FootballScoresjbz2etTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ScoresDebug(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FootballScoresjbz2etTheme {
        Greeting("Android")
    }
}

@Composable
fun ScoresDebug(modifier: Modifier = Modifier) {
    val vm: ScoresViewModel = viewModel(factory = ScoresViewModel.Factory)
    val state by vm.state.collectAsStateWithLifecycle()
    val summary = when (val ui = state.uiState) {
        ScoresUiState.Loading -> "Loading"
        is ScoresUiState.Success -> "Success: ${ui.games.size} games"
        ScoresUiState.Error -> "Error"
    }
    Text(text = "Week ${state.week} ${state.division}: $summary", modifier = modifier)
}