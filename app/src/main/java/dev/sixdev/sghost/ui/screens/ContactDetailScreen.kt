package dev.sixdev.sghost.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.sixdev.sghost.App
import dev.sixdev.sghost.data.Contact
import kotlinx.coroutines.launch

@Composable
fun ContactDetailScreen(nav: NavController, id: String) {
    val app = LocalContext.current.applicationContext as App
    val scope = rememberCoroutineScope()
    var contact by remember { mutableStateOf<Contact?>(null) }
    var nick by remember { mutableStateOf("") }

    LaunchedEffect(id) {
        contact = app.db.contacts().byId(id)
        nick = contact?.nickname ?: ""
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Kontakt Details", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text(contact?.displayName ?: "")
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = nick, onValueChange = { nick = it }, label = { Text("Nickname") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            scope.launch {
                contact?.let {
                    app.db.contacts().setNickname(it.id, nick.ifBlank { null })
                    Toast.makeText(app, "Gespeichert", Toast.LENGTH_SHORT).show()
                    nav.popBackStack()
                }
            }
        }, modifier = Modifier.fillMaxWidth()) { Text("Speichern") }
    }
}
