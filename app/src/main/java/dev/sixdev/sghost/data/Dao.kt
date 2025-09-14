package dev.sixdev.sghost.data

import androidx.room.*

@Dao
interface ContactDao {
    @Query("SELECT * FROM Contact ORDER BY displayName")
    suspend fun getAll(): List<Contact>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(c: Contact)
    @Query("SELECT * FROM Contact WHERE id=:id")
    suspend fun byId(id: String): Contact?
    @Query("UPDATE Contact SET nickname=:nick WHERE id=:id")
    suspend fun setNickname(id: String, nick: String?)
    @Delete suspend fun delete(c: Contact)
}

@Dao
interface ProfileDao {
    @Query("SELECT * FROM MyProfile WHERE single=0")
    suspend fun get(): MyProfile?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(p: MyProfile)
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM Message WHERE peerId=:peer ORDER BY createdAt ASC")
    suspend fun forPeer(peer: String): List<Message>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(m: Message)
    @Query("UPDATE Message SET state=:state WHERE uid=:uid")
    suspend fun setState(uid: String, state: String)
    @Query("SELECT * FROM Message WHERE state='queued' ORDER BY createdAt ASC LIMIT 50")
    suspend fun queued(): List<Message>
}
