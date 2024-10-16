package com.avwaveaf.storyspace.data.repository.story

import com.avwaveaf.storyspace.data.model.StoryResponse

interface StoryRepository {
    suspend fun getStories():Result<StoryResponse>
}