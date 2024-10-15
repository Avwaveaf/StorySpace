package com.avwaveaf.storyspace.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avwaveaf.storyspace.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun login(email: String, password: String, onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.login(email, password)
            onResult(result)
        }
    }
}