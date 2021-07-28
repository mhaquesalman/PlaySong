package com.salman.playsong

import androidx.lifecycle.LiveData

class MusicRepository(private val musicDao: MusicDao) {

    suspend fun addMusicToFav(musicFiles: MusicFiles) {
        musicDao.addMusicToFav(musicFiles)
    }

    fun readMusicFromFav(): LiveData<List<MusicFiles>> = musicDao.readMusicFromFav()

    suspend fun removeMusicFromFav(musicId: String) {
        musicDao.removeMusicFromFav(musicId)
    }
}