package edu.virginia.cs4720.steploggerjbz2et

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlin.math.abs

private const val MAX_AXIS = 20f

@Composable
fun LiveScreen(viewModel: StepViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Step Logger", style = MaterialTheme.typography.headlineMedium)
        AccelerometerCard(state)
    }
}

@Composable
private fun AccelerometerCard(state: StepUiState) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Accelerometer (m/s²)", style = MaterialTheme.typography.titleMedium)
            if (state.hasAccelerometer) {
                AxisRow("X", state.x)
                AxisRow("Y", state.y)
                AxisRow("Z", state.z)
            } else {
                Text("This device has no accelerometer, so live motion values are not available.")
            }
        }
    }
}

@Composable
private fun AxisRow(label: String, value: Float) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium, modifier = Modifier.width(20.dp))
        LinearProgressIndicator(
            progress = { (abs(value) / MAX_AXIS).coerceIn(0f, 1f) },
            modifier = Modifier.weight(1f)
        )
        Text(
            "%7.2f".format(value),
            style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Monospace)
        )
    }
}