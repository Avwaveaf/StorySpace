package com.avwaveaf.storyspace.view.storymaps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avwaveaf.storyspace.data.model.ListStoryItem
import com.avwaveaf.storyspace.data.repository.story.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val storyRepository: StoryRepository
) : ViewModel() {
    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>> get() = _stories

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage


    fun fetchStoriesWithLocation() {
        viewModelScope.launch {
            val result = storyRepository.getStoriesWithLocation()
            result.onSuccess {
                _stories.value = it.listStory
            }.onFailure {
                _errorMessage.value = it.message
            }

        }
    }
}