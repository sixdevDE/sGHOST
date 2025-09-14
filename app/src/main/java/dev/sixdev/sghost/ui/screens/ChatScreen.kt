package dev.sixdev.sghost.ui.screens

import android.content.Intent
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.sixdev.sghost.App
import dev.sixdev.sghost.data.Message
import dev.sixdev.sghost.media.AudioRecorder
import dev.sixdev.sghost.media.Media
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

@Composable
fun ChatScreen(nav: NavController, peerId: String) {
    val app = androidx.compose.ui.platform.LocalContext.current.applicationContext as App
    val scope = rememberCoroutineScope()
    var input by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<Message>()) }
    var peer by remember { mutableStateOf(app.db.contacts().byId(peerId)) }

    // Video capture launcher
    val videoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
        val uri = res.data?.data
        if (uri != null) {
            scope.launch {
                try {
                    val my = app.db.profile().get()!!
                    val sk = String(app.prefs.decryptFromPrefs(my.secretKeyEnc))
                    val shared = app.crypto.sharedKeyB64(sk, peer!!.publicKey)
                    val raw = app.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: ByteArray(0)
                    val cipher = app.crypto.encryptXChaCha(shared, raw)
                    val f = File(app.filesDir, "vid_${UUID.randomUUID()}.bin").apply { writeBytes(cipher) }
                    val msg = Message(UUID.randomUUID().toString(), peerId, "video", f.absolutePath, "video/mp4", "queued")
                    app.db.messages().upsert(msg)
                    messages = app.db.messages().forPeer(peerId)
                    Toast.makeText(app, "Video verschlüsselt & gequeued", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(app, "Video-Fehler: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Audio recorder
    var rec by remember { mutableStateOf<AudioRecorder?>(null) }
    var recFile by remember { mutableStateOf<File?>(null) }
    var recording by remember { mutableStateOf(false) }

    LaunchedEffect(peerId) {
        peer = app.db.contacts().byId(peerId)
        messages = app.db.messages().forPeer(peerId)
    }

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Text(peer?.displayName ?: "Chat", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        LazyColumn(Modifier.weight(1f)) {
            items(messages.size) { i ->
                val m = messages[i]
                ListItem(
                    headlineText = { Text(m.kind.uppercase()) },
                    supportingText = { Text("${m.state}  •  ${m.createdAt}") }
                )
                Divider()
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = input, onValueChange = { input = it }, modifier = Modifier.weight(1f), label = { Text("Nachricht") })
            Button(onClick = {
                scope.launch {
                    try {
                        val my = app.db.profile().get()!!
                        val sk = String(app.prefs.decryptFromPrefs(my.secretKeyEnc))
                        val shared = app.crypto.sharedKeyB64(sk, peer!!.publicKey)
                        val cipher = app.crypto.encryptXChaCha(shared, input.toByteArray())
                        val f = File(app.filesDir, "msg_${UUID.randomUUID()}.bin").apply { writeBytes(cipher) }
                        val msg = Message(UUID.randomUUID().toString(), peerId, "text", f.absolutePath, "text/plain", "queued")
                        app.db.messages().upsert(msg)
                        messages = app.db.messages().forPeer(peerId)
                        input = ""
                        Toast.makeText(app, "Verschlüsselt & gequeued", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(app, "Fehler: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }) { Text("Senden") }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                if (!recording) {
                    val file = Media.createPrivateFile(app, "aud_${UUID.randomUUID()}", "m4a")
                    val r = AudioRecorder(file); r.start()
                    rec = r; recFile = file; recording = true
                } else {
                    rec?.stop()
                    recording = false
                    scope.launch {
                        try {
                            val my = app.db.profile().get()!!
                            val sk = String(app.prefs.decryptFromPrefs(my.secretKeyEnc))
                            val shared = app.crypto.sharedKeyB64(sk, peer!!.publicKey)
                            val raw = recFile!!.readBytes()
                            val cipher = app.crypto.encryptXChaCha(shared, raw)
                            val f = File(app.filesDir, "aud_${UUID.randomUUID()}.bin").apply { writeBytes(cipher) }
                            val msg = Message(UUID.randomUUID().toString(), peerId, "audio", f.absolutePath, "audio/mp4", "queued")
                            app.db.messages().upsert(msg)
                            messages = app.db.messages().forPeer(peerId)
                            Toast.makeText(app, "Audio verschlüsselt & gequeued", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(app, "Audio-Fehler: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }, modifier = Modifier.weight(1f)) {
                Text(if (!recording) "Audio aufnehmen" else "Aufnahme stoppen")
            }
            Button(onClick = {
                val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                videoLauncher.launch(intent)
            }, modifier = Modifier.weight(1f)) {
                Text("Video aufnehmen")
            }
        }
    }
}
