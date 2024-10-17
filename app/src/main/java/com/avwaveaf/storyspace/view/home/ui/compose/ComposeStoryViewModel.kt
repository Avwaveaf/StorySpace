package com.avwaveaf.storyspace.view.home.ui.compose

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avwaveaf.storyspace.data.repository.story.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class ComposeStoryViewModel @Inject constructor(
    private val storyRepository: StoryRepository
) : ViewModel() {

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _isUploading = MutableLiveData(false)
    val isUploading: LiveData<Boolean> get() = _isUploading

    private val _uploadSuccess = MutableLiveData<Boolean>()
    val uploadSuccess: LiveData<Boolean> get() = _uploadSuccess

    fun uploadImage(
        file: MultipartBody.Part,
        description: RequestBody,
    ) {
        _isUploading.value = true
        viewModelScope.launch {
            val result = storyRepository.uploadImage(file, description)
            result.onSuccess {
                _errorMessage.value = null
                _uploadSuccess.value = true
            }.onFailure {
                _errorMessage.value = it.message
                _uploadSuccess.value = false
            }
            _isUploading.value = false
        }
    }
}