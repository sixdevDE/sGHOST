package dev.sixdev.sghost

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import dev.sixdev.sghost.nav.NavGraph
import dev.sixdev.sghost.nav.Routes
import dev.sixdev.sghost.ui.theme.SGhostTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContent {
            var startRoute by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(Unit) {
                val profile = (application as App).db.profile().get()
                startRoute = if (profile?.nodeBinding != null) {
                    Routes.Home
                } else {
                    Routes.Onboard
                }
            }

            SGhostTheme {
                if (startRoute != null) {
                    NavGraph(startDestination = startRoute!!)
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}
