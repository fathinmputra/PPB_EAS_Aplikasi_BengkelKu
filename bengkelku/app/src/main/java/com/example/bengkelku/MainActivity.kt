package com.example.bengkelku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.bengkelku.navigation.BengkelKuNavGraph
import com.example.bengkelku.ui.theme.BengkelkuTheme
import com.example.bengkelku.data.local.PreferenceManager
import com.example.bengkelku.data.local.MockDataGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private lateinit var prefsManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize local storage
        prefsManager = PreferenceManager(this)

        enableEdgeToEdge()
        setContent {
            BengkelkuTheme {
                BengkelKuApp(prefsManager = prefsManager)
            }
        }
    }
}

@Composable
fun BengkelKuApp(prefsManager: PreferenceManager) {
    val navController = rememberNavController()

    // Initialize app data on first launch
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            // Setup initial data if needed
            MockDataGenerator.initializeAllDemoData(prefsManager)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        BengkelKuNavGraph(navController = navController)
    }
}

@Preview(showBackground = true)
@Composable
fun BengkelKuAppPreview() {
    BengkelkuTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            BengkelKuNavGraph(navController = rememberNavController())
        }
    }
}