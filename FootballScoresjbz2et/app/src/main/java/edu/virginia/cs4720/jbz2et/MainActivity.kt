package edu.virginia.cs4720.jbz2et

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FootballScoresjbz2etTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ScoresRoute(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}