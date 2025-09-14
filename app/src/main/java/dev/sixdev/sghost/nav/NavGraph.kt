package dev.sixdev.sghost.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import dev.sixdev.sghost.ui.screens.*

object Routes {
    const val Onboard = "onboard"
    const val Home = "home"
    const val MyContact = "my_contact"
    const val Contacts = "contacts"
    const val ContactDetail = "contact_detail/{id}"
    const val Chat = "chat/{peerId}"
    const val QRShow = "qr_show"
    const val QRScan = "qr_scan"
}

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.Onboard) {
        composable(Routes.Onboard) { OnboardScreen(navController) }
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
