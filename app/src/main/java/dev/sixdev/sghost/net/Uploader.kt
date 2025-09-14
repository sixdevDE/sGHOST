package dev.sixdev.sghost.net

import dev.sixdev.sghost.App
import dev.sixdev.sghost.data.Message
import dev.sixdev.sghost.esp.UploadReq
import dev.sixdev.sghost.esp.Api
import com.squareup.moshi.adapter
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import android.util.Base64

class Uploader(private val app: App) {
    private inline fun <reified T> toJson(d: T) = Api.moshi().adapter<T>().toJson(d)

    suspend fun tryUpload(baseUrl: String, msg: Message, myId: String, peerId: String): Boolean {
        val cipher = File(msg.cipherPath)
        if (!cipher.exists()) return false
        val blob = Base64.encodeToString(cipher.readBytes(), Base64.NO_WRAP)
        val body = toJson(UploadReq(peerId, myId, msg.kind, msg.createdAt, blob)).toRequestBody(Api.json)
        val req = Request.Builder().url("$baseUrl/msg/upload").post(body).build()
        Api.ok.newCall(req).execute().use { rsp ->
            if (!rsp.isSuccessful) return false
        }
        app.db.messages().setState(msg.uid, "sent")
        return true
    }
}
