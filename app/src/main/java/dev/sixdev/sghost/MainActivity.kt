package dev.sixdev.sghost

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import dev.sixdev.sghost.nav.NavGraph
import dev.sixdev.sghost.ui.theme.SGhostTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContent {
            SGhostTheme { NavGraph() }
        }
    }
}
