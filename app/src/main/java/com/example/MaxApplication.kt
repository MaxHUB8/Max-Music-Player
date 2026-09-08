package com.example

import android.app.Application
import com.example.data.MusicRepository
import com.example.data.local.MaxDatabase
import com.example.data.local.UserPreferencesRepository
import com.example.playback.PlaybackManager

class MaxApplication : Application() {

    lateinit var database: MaxDatabase
        private set

    lateinit var repository: MusicRepository
        private set

    lateinit var userPreferences: UserPreferencesRepository
        private set

    val playbackManager: PlaybackManager by lazy {
        PlaybackManager(this, repository, database.maxDao(), userPreferences)
    }

    companion object {
        lateinit var instance: MaxApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = MaxDatabase.getInstance(this)
        repository = MusicRepository(this, database.maxDao())
        userPreferences = UserPreferencesRepository(this)
    }
}
