package com.avwaveaf.storyspace.view.home.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.avwaveaf.storyspace.data.model.ListStoryItem
import com.avwaveaf.storyspace.data.repository.story.StoryRepository
import com.avwaveaf.storyspace.utils.DataDummy
import com.avwaveaf.storyspace.utils.LoggingRule
import com.avwaveaf.storyspace.utils.MainDispatcherRule
import com.avwaveaf.storyspace.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val loggingRule = LoggingRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var differ: AsyncPagingDataDiffer<ListStoryItem>

    @Before
    fun setUp() {
        homeViewModel = HomeViewModel(storyRepository)
        differ = AsyncPagingDataDiffer(
            diffCallback = DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

    }

    @Test
    fun `when Get Stories Should Not Null and Return Success`() = runTest {
        // Arrange
        val dummyStories = DataDummy.generateDummyStoriesResponse()
        val data: PagingData<ListStoryItem> = StoryPagingSource.snapshot(dummyStories)
        val expectedStories: Flow<PagingData<ListStoryItem>> = flowOf(data)

        // Mock the repository method
        Mockito.`when`(storyRepository.getPagedStories()).thenReturn(expectedStories)

        // Act
        homeViewModel.requestRefresh() // Trigger refresh
        val actualStories = homeViewModel.pagedStories.getOrAwaitValue()
        differ.submitData(actualStories)

        // Assert
        // Case 1: Memastikan data tidak null.
        assertNotNull(differ.snapshot())
        // Case 2: Memastikan jumlah data sesuai dengan yang diharapkan.
        assertEquals(dummyStories.size, differ.snapshot().size)
        // Case 3: Memastikan data pertama yang dikembalikan sesuai.
        assertEquals(dummyStories[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Stories Empty Should Return No Data`() = runTest {
        // Arrange
        val emptyData: PagingData<ListStoryItem> = PagingData.empty()
        val expectedStories: Flow<PagingData<ListStoryItem>> = flowOf(emptyData)

        // Mock the repository method
        Mockito.`when`(storyRepository.getPagedStories()).thenReturn(expectedStories)

        // Act
        homeViewModel.requestRefresh() // Trigger refresh
        val actualStories = homeViewModel.pagedStories.getOrAwaitValue()
        differ.submitData(actualStories)

        // Assert
        // Case 1: Memastikan jumlah data yang dikembalikan nol.
        assertEquals(0, differ.snapshot().size)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}

class StoryPagingSource : PagingSource<Int, ListStoryItem>() {
    companion object {
        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return LoadResult.Page(emptyList(), null, null)
    }
}
