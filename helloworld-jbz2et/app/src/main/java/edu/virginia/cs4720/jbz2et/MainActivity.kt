package edu.virginia.cs4720.jbz2et

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.virginia.cs4720.jbz2et.ui.theme.Helloworldjbz2etTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Helloworldjbz2etTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(modifier = Modifier.fillMaxSize().padding(innerPadding)
                    )
                }
            }
        }
    }
}

private val salutations = listOf("Hello", "Hola", "Bonjour")
private const val name_placeholder = "Your name here"

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    var name by rememberSaveable {mutableStateOf("")}
    var salutation by rememberSaveable {mutableStateOf(salutations.first())}
    val message = "$salutation, ${name.ifBlank {name_placeholder}}!"

    Column(modifier = modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = message, style = MaterialTheme.typography.headlineMedium)

        HorizontalDivider()

        OutlinedTextField(value = name, onValueChange = {name = it}, label = {Text("Your name")},
            singleLine = true, modifier = Modifier.fillMaxWidth())

        Text(text = "Salutation", style = MaterialTheme.typography.titleMedium)
        Column {
            for (option in salutations) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = (option == salutation),
                        onClick = { salutation = option }
                    )
                    Text(text = option)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Helloworldjbz2etTheme {
        Greeting()
    }
}