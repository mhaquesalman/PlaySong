package com.salman.playsong

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MusicDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addMusicToFav(musicFiles: MusicFiles)

    @Query("SELECT * FROM musics ORDER BY title ASC")
    fun readMusicFromFav(): LiveData<List<MusicFiles>>

//    @Delete
//    fun removeMusicFromFav(musicFiles: MusicFiles)

    @Query("DELETE FROM musics WHERE id = :musicId")
    suspend fun removeMusicFromFav(musicId: String)

}