package com.example.janet_ahn_myruns5

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class EntryViewModelFactory (private val repository: EntryRepository) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>) : T{ //create() creates a new instance of the modelClass, which is CommentViewModel in this case.
        if(modelClass.isAssignableFrom(RoomViewModel::class.java))
            return RoomViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}