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
import dev.sixdev.sghost.esp.Provisioner
import dev.sixdev.sghost.nav.Routes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun WifiConfigScreen(nav: NavController) {
    val ctx = LocalContext.current
    val app = ctx.applicationContext as App
    var ssid by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Enter the Wi-Fi credentials for your network. The ESP32 will connect to this network.")
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = ssid,
            onValueChange = { ssid = it },
            label = { Text("SSID") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    try {
                        withContext(Dispatchers.IO) {
                            val profile = app.db.profile().get()
                            val baseUrl = profile?.nodeBinding?.split("|")?.get(0)
                                ?: throw dev.sixdev.sghost.esp.WifiConfigFailedException("Device not paired. Please restart setup.")
                            val provisioner = Provisioner(app)
                            provisioner.wifiConfig(baseUrl, ssid, pass)
                        }
                        Toast.makeText(
                            ctx,
                            "Wi-Fi configured! The ESP32 will now connect to your network.",
                            Toast.LENGTH_LONG
                        ).show()
                        nav.navigate(Routes.Home) {
                            popUpTo(Routes.Onboard) { inclusive = true }
                        }
                    } catch (e: dev.sixdev.sghost.esp.WifiConfigFailedException) {
                        Toast.makeText(
                            ctx,
                            "Failed to configure Wi-Fi: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(
                            ctx,
                            "An unknown error occurred: ${e.toString()}",
                            Toast.LENGTH_LONG
                        ).show()
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Connect")
            }
        }
    }
}
