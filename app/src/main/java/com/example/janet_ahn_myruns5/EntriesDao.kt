package com.example.janet_ahn_myruns5

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EntriesDao {

    @Query("SELECT * FROM Entries")
    fun getAllEntries(): Flow<List<Entries>>

    @Insert
    suspend fun insert(entry: Entries)

    @Query("DELETE FROM Entries")
    suspend fun deleteAll()

    @Query("DELETE FROM Entries WHERE id = :key") //":" indicates that it is a Bind variable
    suspend fun deleteEntry(key: Long)
}