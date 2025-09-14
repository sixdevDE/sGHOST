package dev.sixdev.sghost.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.sixdev.sghost.ui.screens.*

object Routes {
    const val Onboard = "onboard"
    const val WifiConfig = "wifi_config"
    const val Home = "home"
    const val MyContact = "my_contact"
    const val Contacts = "contacts"
    const val ContactDetail = "contact_detail/{id}"
    const val Chat = "chat/{peerId}"
    const val QRShow = "qr_show"
    const val QRScan = "qr_scan"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.Onboard
) {
    val screenTitles = mapOf(
        Routes.Home to "sGHOST",
        Routes.Onboard to "Setup",
        Routes.WifiConfig to "Configure Wi-Fi",
        Routes.QRScan to "Scan Contact",
        Routes.QRShow to "My QR Code",
        Routes.Contacts to "Contacts",
        Routes.MyContact to "My Contact",
        Routes.ContactDetail to "Contact Details",
        Routes.Chat to "Chat"
    )

    Scaffold(
        topBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val title = screenTitles[currentRoute] ?: "sGHOST"

            // Do not show TopAppBar on screens that should be fullscreen or have custom headers
            if (currentRoute != null) {
                 TopAppBar(title = { Text(title) })
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.Onboard) { OnboardScreen(navController) }
            composable(Routes.WifiConfig) { WifiConfigScreen(navController) }
            composable(Routes.Home) { HomeScreen(navController) }
            composable(Routes.MyContact) { MyContactScreen(navController) }
            composable(Routes.Contacts) { ContactsScreen(navController) }
            composable(Routes.QRShow) { QRShowScreen(navController) }
            composable(Routes.QRScan) { QRScanScreen(navController) }
            composable(Routes.ContactDetail) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: ""
                ContactDetailScreen(navController, id)
            }
            composable(Routes.Chat) { backStackEntry ->
                val peerId = backStackEntry.arguments?.getString("peerId") ?: ""
                ChatScreen(navController, peerId)
            }
        }
    }
}
