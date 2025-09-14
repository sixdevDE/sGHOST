package dev.sixdev.sghost.qrcode

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.journeyapps.barcodescanner.BarcodeEncoder

data class QRContact(val id: String, val displayName: String, val nodeAddress: String, val publicKey: String)

object QR {
    fun makeBitmap(content: String, size: Int = 800): Bitmap {
        val enc = BarcodeEncoder()
        val hints = mapOf(EncodeHintType.MARGIN to 1)
        val matrix = enc.createMatrix(content, BarcodeFormat.QR_CODE, size, size, hints)
        return enc.createBitmap(matrix)
    }
}
