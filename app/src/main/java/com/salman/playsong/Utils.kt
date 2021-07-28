package com.salman.playsong

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.preference.PreferenceManager

class Utils {

    companion object {

        fun getAlbumArt(uri: String?): ByteArray? {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(uri)
            val art = retriever.embeddedPicture
            retriever.release()
            return art
        }

        fun saveLastMusicPlayed(context: Context, uri: Uri, musicFiles: MusicFiles) {
            val sharedPref = context.getSharedPreferences(Constants.MUSIC_LAST_PLAYED, Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString(Constants.MUSIC_FILE, uri.toString())
            editor.putString(Constants.SONG_NAME, musicFiles.title)
            editor.putString(Constants.ARTIST_NAME, musicFiles.artist)
            editor.apply()
        }

        fun pathOfLastMusicPlayed(context: Context): String? {
            val sharedPref = context.getSharedPreferences(Constants.MUSIC_LAST_PLAYED, Context.MODE_PRIVATE)
            val path = sharedPref.getString(Constants.MUSIC_FILE, null)
            return path
        }

        fun titleOfLastMusicPlayed(context: Context): String? {
            val sharedPref = context.getSharedPreferences(Constants.MUSIC_LAST_PLAYED, Context.MODE_PRIVATE)
            val title = sharedPref.getString(Constants.SONG_NAME, null)
            return title
        }

        fun artistOfLastMusicPlayed(context: Context): String? {
            val sharedPref = context.getSharedPreferences(Constants.MUSIC_LAST_PLAYED, Context.MODE_PRIVATE)
            val artist = sharedPref.getString(Constants.ARTIST_NAME, null)
            return artist
        }
    }

}