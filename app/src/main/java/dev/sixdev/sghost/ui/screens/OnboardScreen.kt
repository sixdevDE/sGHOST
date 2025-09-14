package dev.sixdev.sghost.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.sixdev.sghost.App
import dev.sixdev.sghost.data.MyProfile
import dev.sixdev.sghost.nav.Routes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import android.app.Application

@Composable
fun OnboardScreen(nav: NavController) {
    val ctx = LocalContext.current
    val app = ctx.applicationContext as Application as App
    var name by remember { mutableStateOf("") }
    var espUrl by remember { mutableStateOf("http://192.168.4.1") }
    var master by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Dein Anzeigename") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = espUrl,
            onValueChange = { espUrl = it },
            label = { Text("ESP Basis-URL") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = master,
            onValueChange = { master = it },
            label = { Text("Neues Master-Passwort setzen") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            scope.launch {
                try {
                    withContext(Dispatchers.IO) {
                        val kp = app.crypto.genKeyPair()
                        val myId = UUID.randomUUID().toString()
                        val skEnc =
                            app.prefs.encryptForPrefs(app.crypto.secretKeyB64(kp).toByteArray())
                        app.db.profile().save(
                            MyProfile(
                                id = myId,
                                displayName = name.ifBlank { "User-${myId.take(6)}" },
                                nodeBinding = null,
                                publicKey = app.crypto.publicKeyB64(kp),
                                secretKeyEnc = skEnc
                            )
                        )
                        val prov = dev.sixdev.sghost.esp.Provisioner(app)
                        prov.pair(espUrl, master)
                    }
                    Toast.makeText(
                        ctx,
                        "Paired successfully! Now configure Wi-Fi.",
                        Toast.LENGTH_LONG
                    ).show()
                    nav.navigate(Routes.WifiConfig)
                } catch (e: dev.sixdev.sghost.esp.PairingFailedException) {
                    Toast.makeText(
                        ctx,
                        "Pairing failed. HTTP Code: ${e.code}",
                        Toast.LENGTH_LONG
                    ).show()
                } catch (e: dev.sixdev.sghost.esp.EmptyBodyException) {
                    Toast.makeText(
                        ctx,
                        "Pairing failed. Empty response from device.",
                        Toast.LENGTH_LONG
                    ).show()
                } catch (e: dev.sixdev.sghost.esp.BadJsonException) {
                    Toast.makeText(
                        ctx,
                        "Pairing failed. Invalid data from device.",
                        Toast.LENGTH_LONG
                    ).show()
                } catch (e: Exception) {
                    Toast.makeText(
                        ctx,
                        "Error: ${e.message ?: "Unknown error"}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Koppeln & Fortfahren")
        }
    }
}
