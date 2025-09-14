package dev.sixdev.sghost.crypto

import android.content.Context
import android.util.Base64
import com.goterl.lazysodium.LazySodiumAndroid
import com.goterl.lazysodium.SodiumAndroid
import com.goterl.lazysodium.interfaces.AEAD
import com.goterl.lazysodium.interfaces.Box
import com.goterl.lazysodium.utils.KeyPair
import com.goterl.lazysodium.utils.Key
import dev.sixdev.sghost.util.Prefs
import java.security.SecureRandom

class Crypto(ctx: Context, private val prefs: Prefs) {
    private val sodium = SodiumAndroid()
    private val ls = LazySodiumAndroid(sodium)
    private val rnd = SecureRandom()

    fun genKeyPair(): KeyPair {
        val pk = ByteArray(Box.PUBLICKEYBYTES)
        val sk = ByteArray(Box.SECRETKEYBYTES)
        sodium.crypto_box_keypair(pk, sk)
        return KeyPair(Key.fromBytes(pk), Key.fromBytes(sk))
    }

    fun publicKeyB64(kp: KeyPair) = Base64.encodeToString(kp.publicKey.asBytes, Base64.NO_WRAP)
    fun secretKeyB64(kp: KeyPair) = Base64.encodeToString(kp.secretKey.asBytes, Base64.NO_WRAP)

    fun sharedKeyB64(mySkB64: String, peerPkB64: String): String {
        val shared = ByteArray(Box.BEFORENMBYTES)
        val mySk = Base64.decode(mySkB64, Base64.NO_WRAP)
        val peerPk = Base64.decode(peerPkB64, Base64.NO_WRAP)
        sodium.crypto_box_beforenm(shared, peerPk, mySk)
        return Base64.encodeToString(shared, Base64.NO_WRAP)
    }

    fun encryptXChaCha(sharedKeyB64: String, plaintext: ByteArray, aad: ByteArray? = null): ByteArray {
        val key = Base64.decode(sharedKeyB64, Base64.NO_WRAP)
        val nonce = ByteArray(AEAD.XCHACHA20POLY1305_IETF_NPUBBYTES)
        rnd.nextBytes(nonce)
        val macLen = AEAD.XCHACHA20POLY1305_IETF_ABYTES
        val out = ByteArray(nonce.size + plaintext.size + macLen)
        System.arraycopy(nonce, 0, out, 0, nonce.size)
        val ok = sodium.crypto_aead_xchacha20poly1305_ietf_encrypt(
            out, nonce.size, plaintext, plaintext.size,
            aad, aad?.size ?: 0, null, nonce, key
        )
        check(ok == 0)
        return out
    }

    fun decryptXChaCha(sharedKeyB64: String, cipherWithNonce: ByteArray, aad: ByteArray? = null): ByteArray {
        val key = Base64.decode(sharedKeyB64, Base64.NO_WRAP)
        val nonceLen = AEAD.XCHACHA20POLY1305_IETF_NPUBBYTES
        val nonce = cipherWithNonce.copyOfRange(0, nonceLen)
        val cipher = cipherWithNonce.copyOfRange(nonceLen, cipherWithNonce.size)
        val out = ByteArray(cipher.size - AEAD.XCHACHA20POLY1305_IETF_ABYTES)
        val ok = sodium.crypto_aead_xchacha20poly1305_ietf_decrypt(
            out, null, null, cipher, cipher.size,
            aad, aad?.size ?: 0, nonce, key
        )
        check(ok == 0)
        return out
    }
}
