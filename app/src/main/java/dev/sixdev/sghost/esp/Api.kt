package dev.sixdev.sghost.esp

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.*
import java.util.concurrent.TimeUnit

object Api {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val ok = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    val json = "application/json; charset=utf-8".toMediaType()
    fun moshi() = moshi
}
