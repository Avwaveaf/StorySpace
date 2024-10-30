package com.avwaveaf.storyspace.data.repository.story

import com.avwaveaf.storyspace.data.model.NewStoryResponse
import com.avwaveaf.storyspace.data.model.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface StoryRepository {
    suspend fun getStories():Result<StoryResponse>
    suspend fun getStoriesWithLocation():Result<StoryResponse>
    suspend fun uploadImage(file: MultipartBody.Part, description: RequestBody): Result<NewStoryResponse>
    suspend fun uploadDataWithLocation(file: MultipartBody.Part, description: RequestBody, lat: RequestBody, lon: RequestBody): Result<NewStoryResponse>
}