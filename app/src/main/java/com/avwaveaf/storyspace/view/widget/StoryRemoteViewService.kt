package com.avwaveaf.storyspace.view.widget

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.avwaveaf.storyspace.R
import com.avwaveaf.storyspace.data.model.ListStoryItem
import com.avwaveaf.storyspace.helper.loadBitmapFromUrl
import com.avwaveaf.storyspace.utils.SharedPreferencesUtil

class StoryRemoteViewsService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return StoryRemoteViewsFactory(applicationContext)
    }
}

internal class StoryRemoteViewsFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {
    private var stories = emptyList<ListStoryItem>()


    override fun onCreate() {
    }

    override fun onDataSetChanged() {
        stories = SharedPreferencesUtil.loadStories(context)
    }

    override fun onDestroy() {
    }

    override fun getCount(): Int = stories.size

    override fun getViewAt(position: Int): RemoteViews {
        val story = stories[position]
         val rv = RemoteViews(context.packageName, R.layout.widget_item).apply {
            setTextViewText(R.id.story_title, story.name)
             setImageViewBitmap(R.id.story_image, story.photoUrl?.let { loadBitmapFromUrl(it) })
        }


        val fillInIntent = Intent().apply {
            putExtra(StorySpaceWidget.EXTRA_STORY, story)
        }
        rv.setOnClickFillInIntent(R.id.widget_item_layout, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = true

}