package dev.sixdev.sghost.ui.screens

import android.graphics.asImageBitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.squareup.moshi.adapter
import dev.sixdev.sghost.App
import dev.sixdev.sghost.qrcode.QR
import dev.sixdev.sghost.qrcode.QRContact

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun QRShowScreen(nav: NavController) {
    val app = androidx.compose.ui.platform.LocalContext.current.applicationContext as App
    var bmp by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    LaunchedEffect(Unit) {
        val p = app.db.profile().get()!!
        val qr = QRContact(
            id = p.id, displayName = p.displayName,
            nodeAddress = p.nodeBinding ?: "",
            publicKey = p.publicKey
        )
        val payload = dev.sixdev.sghost.esp.Api.moshi().adapter<QRContact>().toJson(qr)
        bmp = QR.makeBitmap(payload, 900)
    }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Mein QR", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        bmp?.let { Image(bitmap = it.asImageBitmap(), contentDescription = "QR", modifier = Modifier.fillMaxWidth()) }
    }
}
