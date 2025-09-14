package dev.sixdev.sghost.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.sixdev.sghost.App
import dev.sixdev.sghost.esp.Puller
import dev.sixdev.sghost.net.Uploader

class UploadWorker(ctx: Context, params: WorkerParameters): CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val app = applicationContext as App
        val me = app.db.profile().get() ?: return Result.success()
        val queued = app.db.messages().queued()
        val uploader = Uploader(app)
        var okAny = false
        for (m in queued) {
            val contact = app.db.contacts().byId(m.peerId) ?: continue
            val baseUrl = contact.nodeAddress
            if (uploader.tryUpload(baseUrl, m, me.id, contact.id)) okAny = true
        }
        return if (okAny) Result.success() else Result.success()
    }
}

class PullWorker(ctx: Context, params: WorkerParameters): CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val app = applicationContext as App
        val me = app.db.profile().get() ?: return Result.success()
        // simple since value: last hour
        val since = System.currentTimeMillis() - 60_000L * 60
        val puller = Puller(app)
        // Pull from my bound node if any
        val base = me.nodeBinding?.substringBefore("|") ?: return Result.success()
        puller.pull(base, since)
        return Result.success()
    }
}
