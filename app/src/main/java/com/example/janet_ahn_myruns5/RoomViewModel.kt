package com.example.janet_ahn_myruns5

import androidx.lifecycle.*

class RoomViewModel(private val repository: EntryRepository) : ViewModel() {
    val allEntriesLiveData: LiveData<List<Entries>> = repository.allEntries.asLiveData()

    fun insert(entry: Entries) {
        repository.insert(entry)
    }

    fun delete(id: Long) {
        repository.delete(id)
    }
}

