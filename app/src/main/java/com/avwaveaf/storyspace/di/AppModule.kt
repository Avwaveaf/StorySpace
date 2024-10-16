package com.avwaveaf.storyspace.di

import android.app.Application
import android.content.Context
import com.avwaveaf.storyspace.data.repository.auth.AuthRepository
import com.avwaveaf.storyspace.data.repository.auth.AuthRepositoryImpl
import com.avwaveaf.storyspace.data.repository.story.StoryRepository
import com.avwaveaf.storyspace.data.repository.story.StoryRepositoryImpl
import com.avwaveaf.storyspace.utils.SessionManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    abstract fun bindStoryRepository(
        storyRepositoryImpl: StoryRepositoryImpl
    ): StoryRepository


    companion object {
        // Provide the application context
        @Provides
        fun provideContext(application: Application): Context {
            return application.applicationContext
        }

        @Provides
        fun provideSessionManager(@ApplicationContext context: Context): SessionManager {
            return SessionManager(context)
        }
    }
}