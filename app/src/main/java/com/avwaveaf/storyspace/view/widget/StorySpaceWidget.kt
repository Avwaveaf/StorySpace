package com.avwaveaf.storyspace.view.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.net.toUri
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.avwaveaf.storyspace.R
import com.avwaveaf.storyspace.data.model.ListStoryItem
import com.avwaveaf.storyspace.view.home.ui.detail.DetailActivity
import com.avwaveaf.storyspace.workers.StoryUpdateWorker

class StorySpaceWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {

        val workRequest = OneTimeWorkRequestBuilder<StoryUpdateWorker>().build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Log.d("StorySpaceWidget", "onReceive called with action: ${intent.action}")
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
            if (appWidgetIds != null) {
                onUpdate(context, appWidgetManager, appWidgetIds)
            }
        } else if (intent.action == STORY_CLICK_ACTION) {
            val story = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(EXTRA_STORY, ListStoryItem::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(EXTRA_STORY)
            }

            if (story != null) {
                val detailIntent = Intent(context, DetailActivity::class.java)
                detailIntent.putExtra("story_data", story)
                detailIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(detailIntent)
            }
        }
    }

    companion object {

        private const val STORY_CLICK_ACTION = "com.avwaveaf.storyspace.STORY_CLICK_ACTION"
        const val EXTRA_STORY = "com.avwaveaf.storyspace.EXTRA_STORY"

        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            Log.d("StorySpaceWidget", "Updating widget: $appWidgetId")
            val intent = Intent(context, StoryRemoteViewsService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.data = intent.toUri(Intent.URI_INTENT_SCHEME).toUri()

            val views = RemoteViews(context.packageName, R.layout.story_space_widget)
            views.setRemoteAdapter(R.id.stack_view, intent)
            views.setEmptyView(R.id.stack_view, R.id.empty_view)


            val storyClickIntent = Intent(context, StorySpaceWidget::class.java)
            storyClickIntent.action = STORY_CLICK_ACTION
            storyClickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

            val storyClickPendingIntent = PendingIntent.getBroadcast(
                context, 0, storyClickIntent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                else 0
            )

            views.setPendingIntentTemplate(R.id.stack_view, storyClickPendingIntent)


            appWidgetManager.updateAppWidget(appWidgetId, views)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.stack_view)
        }
    }
}
