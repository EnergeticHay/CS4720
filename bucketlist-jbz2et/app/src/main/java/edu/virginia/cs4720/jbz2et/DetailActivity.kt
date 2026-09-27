package edu.virginia.cs4720.jbz2et

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import edu.virginia.cs4720.jbz2et.ui.theme.Bucketlistjbz2etTheme

class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val id = intent.getStringExtra("ITEM_ID") ?: ""
        setContent {
            Bucketlistjbz2etTheme {
                DetailScreen(id = id, onDone = { finish() })
            }
        }
    }
}