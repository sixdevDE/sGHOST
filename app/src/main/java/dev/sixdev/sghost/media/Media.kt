package dev.sixdev.sghost.media

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import java.io.File

object Media {
    fun createPrivateFile(ctx: Context, name: String, ext: String): File {
        val dir = File(ctx.filesDir, "media")
        if (!dir.exists()) dir.mkdirs()
        return File(dir, "$name.$ext")
    }
    fun addToGallery(ctx: Context, file: File, mime: String) {
        val cv = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, file.name)
            put(MediaStore.MediaColumns.MIME_TYPE, mime)
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Movies/sGHOST")
        }
        ctx.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, cv)?.let { uri ->
            ctx.contentResolver.openOutputStream(uri)?.use { out ->
                file.inputStream().use { it.copyTo(out) }
            }
        }
    }
}
