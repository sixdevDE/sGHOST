package dev.sixdev.sghost.esp

import com.squareup.moshi.adapter
import dev.sixdev.sghost.App
import dev.sixdev.sghost.data.Message
import okhttp3.Request
import android.util.Base64
import java.io.File
import java.util.UUID

class Puller(private val app: App) {
    @OptIn(ExperimentalStdlibApi::class)
    suspend fun pull(baseUrl: String, since: Long): Int {
        val req = Request.Builder().url("$baseUrl/msg/pull?since=$since").get().build()
        var count = 0
        Api.ok.newCall(req).execute().use { rsp ->
            if (!rsp.isSuccessful) return 0
            val body = rsp.body?.string() ?: return 0
            val adapter = Api.moshi().adapter<List<PullResItem>>()
            val list = adapter.fromJson(body) ?: emptyList()
            val me = app.db.profile().get() ?: return 0
            val mySk = String(app.prefs.decryptFromPrefs(me.secretKeyEnc))
            for (it in list) {
                val from = app.db.contacts().byId(it.fromId) ?: continue
                val shared = app.crypto.sharedKeyB64(mySk, from.publicKey)
                val cipherBytes = Base64.decode(it.blobB64, Base64.NO_WRAP)
                // already xchacha encrypted, store as is
                val file = File(app.filesDir, "rx_${UUID.randomUUID()}.bin").apply { writeBytes(cipherBytes) }
                val msg = Message(
                    uid = UUID.randomUUID().toString(),
                    peerId = it.fromId,
                    kind = it.mediaKind,
                    cipherPath = file.absolutePath,
                    mediaMime = null,
                    state = "received",
                    createdAt = it.createdAt
                )
                app.db.messages().upsert(msg)
                count++
            }
        }
        return count
    }
}
