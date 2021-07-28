package com.salman.playsong

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MusicViewModel(application: Application) : AndroidViewModel(application) {

    private val musicRepository: MusicRepository

    init {
        val musicDao = MusicDatabase.getMusicDatabase(application).musicDao()
        musicRepository = MusicRepository(musicDao)
    }


    fun addMusicToFav(musicFiles: MusicFiles) {
        viewModelScope.launch(Dispatchers.IO) {
            musicRepository.addMusicToFav(musicFiles)
        }
    }

    fun readMusicFromFav(): LiveData<List<MusicFiles>> {
        return musicRepository.readMusicFromFav()
    }

    fun removeMusicFromFav(musicId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            musicRepository.removeMusicFromFav(musicId)
        }
    }

}