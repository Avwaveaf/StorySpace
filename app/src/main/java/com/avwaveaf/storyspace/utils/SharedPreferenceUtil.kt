package com.avwaveaf.storyspace.utils


import android.content.Context
import android.content.SharedPreferences
import com.avwaveaf.storyspace.data.model.ListStoryItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SharedPreferencesUtil {
    private const val PREFS_NAME = "StorySpacePrefs"
    private const val KEY_STORIES = "stories"

    private fun getSharedPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveStories(context: Context, stories: List<ListStoryItem>) {
        val jsonString = Gson().toJson(stories)
        getSharedPrefs(context).edit().putString(KEY_STORIES, jsonString).apply()
    }

    fun loadStories(context: Context): List<ListStoryItem> {
        val jsonString = getSharedPrefs(context).getString(KEY_STORIES, null)
        return if (jsonString != null) {
            val type = object : TypeToken<List<ListStoryItem>>() {}.type
            Gson().fromJson(jsonString, type)
        } else {
            emptyList()
        }
    }
}