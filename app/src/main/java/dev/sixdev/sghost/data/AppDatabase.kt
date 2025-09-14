package dev.sixdev.sghost.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Contact::class, MyProfile::class, Message::class],
    version = 1, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contacts(): ContactDao
    abstract fun profile(): ProfileDao
    abstract fun messages(): MessageDao

    companion object {
        @Volatile private var I: AppDatabase? = null
        fun get(ctx: Context): AppDatabase =
            I ?: synchronized(this) {
                I ?: Room.databaseBuilder(ctx, AppDatabase::class.java, "sghost.db")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { I = it }
            }
    }
}
