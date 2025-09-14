package dev.sixdev.sghost.util

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import android.util.Base64

private val Context.ds by preferencesDataStore("sghost_prefs")

class Prefs(private val ctx: Context) {
    private val ksAlias = "sghost.master"
    private fun getOrCreateKey(): SecretKey {
        val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (ks.getEntry(ksAlias, null) as? KeyStore.SecretKeyEntry)?.secretKey?.let { return it }
        val kg = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val spec = KeyGenParameterSpec.Builder(
            ksAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setUserAuthenticationRequired(false)
            .build()
        kg.init(spec)
        return kg.generateKey()
    }

    fun encryptForPrefs(plain: ByteArray): String {
        val key = getOrCreateKey()
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val iv = cipher.iv
        val enc = cipher.doFinal(plain)
        val pack = iv + enc
        return Base64.encodeToString(pack, Base64.NO_WRAP)
    }

    fun decryptFromPrefs(b64: String): ByteArray {
        val pack = Base64.decode(b64, Base64.NO_WRAP)
        val iv = pack.copyOfRange(0, 12)
        val enc = pack.copyOfRange(12, pack.size)
        val key = getOrCreateKey()
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))
        return cipher.doFinal(enc)
    }

    suspend fun put(key: String, value: String) {
        val k = stringPreferencesKey(key)
        ctx.ds.edit { it[k] = value }
    }

    suspend fun get(key: String): String? {
        val k = stringPreferencesKey(key)
        return ctx.ds.data.first()[k]
    }

    fun getSync(key: String): String? = runBlocking { get(key) }
    fun putSync(key: String, value: String) = runBlocking { put(key, value) }
}
