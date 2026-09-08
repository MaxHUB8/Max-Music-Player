package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        FavoriteEntity::class,
        PlaylistEntity::class,
        PlaylistSongCrossRef::class,
        PlayHistoryEntity::class,
        EqualizerPresetEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class MaxDatabase : RoomDatabase() {
    abstract fun maxDao(): MaxDao

    companion object {
        private const val DB_NAME = "max_music_player.db"

        @Volatile
        private var INSTANCE: MaxDatabase? = null

        /**
         * Migration from version 1 to version 2:
         * Adds indices for instant queries without thread contention (prevents ANRs),
         * adds playlist description, play history duration and Dolby attributes,
         * and preserves all existing user favorites, playlists, and audio settings.
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. Update playlists table
                try {
                    db.execSQL("ALTER TABLE playlists ADD COLUMN description TEXT NOT NULL DEFAULT ''")
                } catch (ignored: Exception) {}

                // 2. Update playlist_songs table
                try {
                    db.execSQL("ALTER TABLE playlist_songs ADD COLUMN addedAt INTEGER NOT NULL DEFAULT 0")
                } catch (ignored: Exception) {}

                // 3. Update play_history table
                try {
                    db.execSQL("ALTER TABLE play_history ADD COLUMN durationMs INTEGER NOT NULL DEFAULT 0")
                } catch (ignored: Exception) {}
                try {
                    db.execSQL("ALTER TABLE play_history ADD COLUMN formatBadge TEXT NOT NULL DEFAULT 'LOSSLESS'")
                } catch (ignored: Exception) {}
                try {
                    db.execSQL("ALTER TABLE play_history ADD COLUMN isDolby INTEGER NOT NULL DEFAULT 0")
                } catch (ignored: Exception) {}

                // 4. Update favorites table
                try {
                    db.execSQL("ALTER TABLE favorites ADD COLUMN mediaUri TEXT")
                } catch (ignored: Exception) {}
                try {
                    db.execSQL("ALTER TABLE favorites ADD COLUMN isDolby INTEGER NOT NULL DEFAULT 0")
                } catch (ignored: Exception) {}

                // 5. Update equalizer_presets table
                try {
                    db.execSQL("ALTER TABLE equalizer_presets ADD COLUMN isDolbyEnabled INTEGER NOT NULL DEFAULT 0")
                } catch (ignored: Exception) {}
                try {
                    db.execSQL("ALTER TABLE equalizer_presets ADD COLUMN dolbyProfile TEXT NOT NULL DEFAULT 'OFF'")
                } catch (ignored: Exception) {}
                try {
                    db.execSQL("ALTER TABLE equalizer_presets ADD COLUMN dolbySurroundPct INTEGER NOT NULL DEFAULT 50")
                } catch (ignored: Exception) {}

                // 6. Non-destructive indices for rapid UI lookups
                try {
                    db.execSQL("CREATE INDEX IF NOT EXISTS index_favorites_addedAt ON favorites(addedAt)")
                    db.execSQL("CREATE INDEX IF NOT EXISTS index_playlists_createdAt ON playlists(createdAt)")
                    db.execSQL("CREATE INDEX IF NOT EXISTS index_playlist_songs_playlistId ON playlist_songs(playlistId)")
                    db.execSQL("CREATE INDEX IF NOT EXISTS index_playlist_songs_songId ON playlist_songs(songId)")
                    db.execSQL("CREATE INDEX IF NOT EXISTS index_play_history_playedAt ON play_history(playedAt)")
                    db.execSQL("CREATE INDEX IF NOT EXISTS index_play_history_songId ON play_history(songId)")
                } catch (ignored: Exception) {}
            }
        }

        /**
         * Migration template for future version 2 -> 3 schema extensions
         */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Prepared non-destructive hook for future schema updates
            }
        }

        fun getInstance(context: Context): MaxDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MaxDatabase::class.java,
                    DB_NAME
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .fallbackToDestructiveMigrationOnDowngrade(true) // Safe fallback on downgrade only, never on upgrade
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

