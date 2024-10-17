package com.avwaveaf.storyspace.workers

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.avwaveaf.storyspace.data.model.ListStoryItem
import com.avwaveaf.storyspace.data.repository.story.StoryRepository
import com.avwaveaf.storyspace.utils.SharedPreferencesUtil
import com.avwaveaf.storyspace.view.widget.StorySpaceWidget
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@HiltWorker
class StoryUpdateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: StoryRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val stories = repository.getStories()
            Log.d("worker_story", "Worker instantiated")
            if (stories.isSuccess) {
                stories.onSuccess { response ->
                    saveStories(response.listStory)
                    Log.d("worker_story", "Saved stories: ${response.listStory}")
                    updateWidget()
                }
                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Log.e("worker_story", "Error in worker", e)
            Result.failure()
        }
    }

    private fun saveStories(stories: List<ListStoryItem>) {
        SharedPreferencesUtil.saveStories(applicationContext, stories)
    }

    private fun updateWidget() {
        val intent = Intent(applicationContext, StorySpaceWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = AppWidgetManager.getInstance(applicationContext)
            .getAppWidgetIds(ComponentName(applicationContext, StorySpaceWidget::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        applicationContext.sendBroadcast(intent)
        Log.d("worker_story", "Widget update broadcast sent")
    }
}