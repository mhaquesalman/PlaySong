package com.salman.playsong

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "musics")
data class MusicFiles(
        @PrimaryKey(autoGenerate = false)
        val id: String,
        val title: String? = null,
        val artist: String? = null,
        val album: String? = null,
        val duration: String? = null,
        val path: String? = null,
        var isFav: Boolean = false
)
