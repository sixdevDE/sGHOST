package dev.sixdev.sghost

import android.app.Application
import dev.sixdev.sghost.crypto.Crypto
import dev.sixdev.sghost.data.AppDatabase
import dev.sixdev.sghost.util.Prefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class App : Application() {
    lateinit var db: AppDatabase
        private set
    lateinit var prefs: Prefs
        private set
    lateinit var crypto: Crypto
        private set

    val appScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        db = AppDatabase.get(this)
        prefs = Prefs(this)
        crypto = Crypto(this, prefs)
    }
}
