package dev.sixdev.sghost.esp

import com.squareup.moshi.adapter
import dev.sixdev.sghost.App
import dev.sixdev.sghost.data.MyProfile
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class Provisioner(private val app: App) {
    private inline fun <reified T> toJson(data: T) =
        Api.moshi().adapter<T>().toJson(data)

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun pair(baseUrl: String, masterPassword: String): PairStartRes {
        val profile = app.db.profile().get() ?: error("Profile not initialized")
        val body = toJson(PairStartReq(masterPassword, profile.publicKey)).toRequestBody(Api.json)
        val req = Request.Builder().url("$baseUrl/pair/start").post(body).build()
        Api.ok.newCall(req).execute().use { rsp ->
            if (!rsp.isSuccessful) throw PairingFailedException(rsp.code)
            val txt = rsp.body?.string() ?: throw EmptyBodyException()
            val res = Api.moshi().adapter<PairStartRes>().fromJson(txt) ?: throw BadJsonException()
            app.db.profile().save(profile.copy(nodeBinding = "$baseUrl|${res.nodeId}"))
            return res
        }
    }

    suspend fun wifiConfig(baseUrl: String, ssid: String, pass: String) {
        try {
            val body = toJson(WifiConfigReq(ssid, pass)).toRequestBody(Api.json)
            val req = Request.Builder().url("$baseUrl/wifi/config").post(body).build()
            Api.ok.newCall(req).execute().use { rsp ->
                if (!rsp.isSuccessful) {
                    throw WifiConfigFailedException("HTTP Error: ${rsp.code}")
                }
            }
        } catch (e: java.io.IOException) {
            throw WifiConfigFailedException("Connection failed. Is the device still reachable?", e)
        }
    }
}
