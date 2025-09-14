package dev.sixdev.sghost.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.squareup.moshi.adapter
import dev.sixdev.sghost.App
import dev.sixdev.sghost.data.Contact
import dev.sixdev.sghost.qrcode.QRContact
import java.util.UUID

@Composable
fun QRScanScreen(nav: NavController) {
    val app = androidx.compose.ui.platform.LocalContext.current.applicationContext as App
    var status by remember { mutableStateOf("Bereit") }
    val scope = rememberCoroutineScope()

    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(ScanContract()) { res ->
        val txt = res?.contents
        if (!txt.isNullOrBlank()) {
            try {
                val qr = dev.sixdev.sghost.esp.Api.moshi().adapter<QRContact>().fromJson(txt)!!
                val c = Contact(
                    id = qr.id.ifBlank { UUID.randomUUID().toString() },
                    displayName = qr.displayName,
                    nickname = null,
                    nodeAddress = qr.nodeAddress,
                    publicKey = qr.publicKey
                )
                scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                    app.db.contacts().upsert(c)
                    status = "Kontakt hinzugefügt"
                }
            } catch (e: Exception) {
                status = "Ungültiger QR"
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(onClick = {
            val options =
                ScanOptions().setDesiredBarcodeFormats(ScanOptions.QR_CODE).setPrompt("QR scannen")
            launcher.launch(options)
        }, modifier = Modifier.fillMaxWidth()) { Text("Scanner starten") }
        Spacer(Modifier.height(12.dp))
        Text(status)
    }
}
