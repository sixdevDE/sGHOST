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
import kotlinx.coroutines.launch

@Composable
fun MyContactScreen(nav: NavController) {
    val app = LocalContext.current.applicationContext as App
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var pub by remember { mutableStateOf("") }
    var node by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val p = app.db.profile().get()
        name = p?.displayName ?: ""
        pub = p?.publicKey ?: ""
        node = p?.nodeBinding ?: ""
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Mein Kontakt", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Anzeigename") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = pub, onValueChange = {}, label = { Text("Public Key (readonly)") }, readOnly = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = node, onValueChange = {}, label = { Text("Node Bindung (readonly)") }, readOnly = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            scope.launch {
                val p = app.db.profile().get() ?: return@launch
                app.db.profile().save(p.copy(displayName = name))
                Toast.makeText(app, "Gespeichert", Toast.LENGTH_SHORT).show()
            }
        }, modifier = Modifier.fillMaxWidth()) { Text("Speichern") }
    }
}
