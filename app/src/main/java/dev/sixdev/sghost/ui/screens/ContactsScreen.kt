package dev.sixdev.sghost.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.sixdev.sghost.App
import dev.sixdev.sghost.data.Contact
import dev.sixdev.sghost.nav.Routes
import kotlinx.coroutines.launch

@Composable
fun ContactsScreen(nav: NavController) {
    val app = androidx.compose.ui.platform.LocalContext.current.applicationContext as App
    var contacts by remember { mutableStateOf(listOf<Contact>()) }

    LaunchedEffect(Unit) {
        contacts = app.db.contacts().getAll()
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Kontakte", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        LazyColumn {
            items(contacts.size) { i ->
                val c = contacts[i]
                ListItem(
                    headlineText = { Text(c.displayName + (c.nickname?.let { " ($it)" } ?: "")) },
                    supportingText = { Text("Node: ${c.nodeAddress}") },
                    trailingContent = {
                        Row {
                            Button(onClick = { nav.navigate("chat/${c.id}") }) { Text("Chat") }
                            Spacer(Modifier.width(8.dp))
                            OutlinedButton(onClick = { nav.navigate("contact_detail/${c.id}") }) { Text("Details") }
                        }
                    }
                )
                Divider()
            }
        }
    }
}
