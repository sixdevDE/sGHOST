package dev.sixdev.sghost.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Contact(
    @PrimaryKey val id: String,
    val displayName: String,
    val nickname: String?,
    val nodeAddress: String,
    val publicKey: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity
data class MyProfile(
    @PrimaryKey val single: Int = 0,
    val id: String,
    val displayName: String,
    val nodeBinding: String?,
    val publicKey: String,
    val secretKeyEnc: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity
data class Message(
    @PrimaryKey val uid: String,
    val peerId: String,
    val kind: String,
    val cipherPath: String,
    val mediaMime: String?,
    val state: String,
    val createdAt: Long = System.currentTimeMillis()
)
