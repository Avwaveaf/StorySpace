package com.avwaveaf.storyspace.di

import android.content.Context
import androidx.room.Room
import com.avwaveaf.storyspace.data.db.StoryDatabase
import com.avwaveaf.storyspace.domain.dao.StoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): StoryDatabase {
        return Room.databaseBuilder(
            context,
            StoryDatabase::class.java,
            "story_database"
        ).build()
    }

    @Provides
    fun provideStoryDao(database: StoryDatabase): StoryDao {
        return database.storyDao()
    }
}