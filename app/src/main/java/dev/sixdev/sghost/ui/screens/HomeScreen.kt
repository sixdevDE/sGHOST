package dev.sixdev.sghost.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.sixdev.sghost.nav.Routes

@Composable
fun HomeScreen(nav: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(onClick = { nav.navigate(Routes.MyContact) }, modifier = Modifier.fillMaxWidth()) { Text("Mein Kontakt") }
        Button(onClick = { nav.navigate(Routes.Contacts) }, modifier = Modifier.fillMaxWidth()) { Text("Kontakte") }
        Button(onClick = { nav.navigate(Routes.QRShow) }, modifier = Modifier.fillMaxWidth()) { Text("QR – Meinen Kontakt zeigen") }
        Button(onClick = { nav.navigate(Routes.QRScan) }, modifier = Modifier.fillMaxWidth()) { Text("QR – Kontakt scannen") }
    }
}
