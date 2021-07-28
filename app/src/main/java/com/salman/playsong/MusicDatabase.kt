package com.salman.playsong

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [MusicFiles::class],
    version = 2,
    exportSchema = false
)
abstract class MusicDatabase : RoomDatabase() {

    abstract fun musicDao(): MusicDao

    companion object {
        const val DATABASE = "music_favorites"
        @Volatile
        private var INSTANCE: MusicDatabase? = null

        fun getMusicDatabase(context: Context): MusicDatabase {
            val instance = INSTANCE
            if (instance != null) {
                return instance
            }
            synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    MusicDatabase::class.java,
                    DATABASE
                ).fallbackToDestructiveMigration().build()
                INSTANCE = newInstance
                return newInstance
            }
        }
    }

}