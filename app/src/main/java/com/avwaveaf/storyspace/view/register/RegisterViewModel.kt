package com.avwaveaf.storyspace.view.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avwaveaf.storyspace.data.repository.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _registrationResult = MutableLiveData<String>()
    val registrationResult: LiveData<String> get() = _registrationResult
    private val _registrationResultError = MutableLiveData<Boolean>()
    val registrationResultError: LiveData<Boolean> get() = _registrationResultError

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            val result = authRepository.registerUser(name, email, password)
            result.onSuccess {
                _registrationResultError.value = false
                _registrationResult.value = it.message
            }.onFailure {
                _registrationResultError.value = true
                _registrationResult.value = it.message.toString()
            }
        }
    }
}