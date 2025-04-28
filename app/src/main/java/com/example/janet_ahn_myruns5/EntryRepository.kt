package com.example.janet_ahn_myruns5

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class EntryRepository(private val entriesDao: EntriesDao) {
    val allEntries: Flow<List<Entries>> = entriesDao.getAllEntries()

    fun insert(entry: Entries){
        CoroutineScope(Dispatchers.IO).launch{
            entriesDao.insert(entry)
        }
    }

    fun delete(id: Long){
        CoroutineScope(Dispatchers.IO).launch {
            entriesDao.deleteEntry(id)
        }
    }
}