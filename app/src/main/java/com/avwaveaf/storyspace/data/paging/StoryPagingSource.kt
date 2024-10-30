package com.avwaveaf.storyspace.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.avwaveaf.storyspace.data.model.ListStoryItem
import com.avwaveaf.storyspace.network.ApiService
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class StoryPagingSource @AssistedInject constructor(private val apiService: ApiService) :
    PagingSource<Int, ListStoryItem>() {
    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getStories(position, params.loadSize)
            val storyData = responseData.listStory

            LoadResult.Page(
                data = storyData,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (storyData.isEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(): StoryPagingSource
    }
}