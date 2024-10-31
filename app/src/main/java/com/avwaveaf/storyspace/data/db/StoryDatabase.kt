package com.avwaveaf.storyspace.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.avwaveaf.storyspace.data.model.ListStoryItem
import com.avwaveaf.storyspace.domain.dao.RemoteKeysDao
import com.avwaveaf.storyspace.domain.dao.StoryDao

@Database(entities = [ListStoryItem::class, RemoteKeys::class], version = 2, exportSchema = false)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}