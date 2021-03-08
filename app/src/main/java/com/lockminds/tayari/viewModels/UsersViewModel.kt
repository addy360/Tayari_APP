package com.lockminds.tayari.viewModels

import androidx.lifecycle.*
import com.lockminds.tayari.datasource.repository.AppRepository
import com.lockminds.tayari.datasource.tables.Users
import kotlinx.coroutines.launch

class UsersViewModel(private val repository: AppRepository) : ViewModel() {

    val user: LiveData<Users> = repository.user.asLiveData()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    suspend fun insertUser(user: Users) = viewModelScope.launch {
        repository.insertUser(user)
    }

    suspend fun deleteAllUsers() = viewModelScope.launch {
        repository.deleteAllUsers()
    }

}

class UsersViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UsersViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}