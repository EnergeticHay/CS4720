package edu.virginia.cs4720.jbz2et

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import edu.virginia.cs4720.jbz2et.ui.theme.Bucketlistjbz2etTheme

class CreateActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Bucketlistjbz2etTheme {
                CreateScreen(onDone = { finish() })
            }
        }
    }
}