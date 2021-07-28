package com.salman.playsong

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationCompat


private const val TAG = "MusicService"
class MusicService : Service(), MediaPlayer.OnCompletionListener {
    val mBinder: IBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    var uri: Uri? = null
    var position = -1
    private var musicList: ArrayList<MusicFiles>? = ArrayList()
    private var actionPlaying: OnActionPlaying? = null
    var mediaSessionCompat: MediaSessionCompat? = null

    override fun onCreate() {
        super.onCreate()

        mediaSessionCompat = MediaSessionCompat(baseContext, "PLAY-AUDIO")
        Log.d(TAG, "onCreate: ${mediaSessionCompat?.sessionToken}")
    }

    override fun onBind(intent: Intent?): IBinder {
        return mBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val startPosition = intent?.getIntExtra("startPosition", -1)
        val sendFrom = intent?.getStringExtra("sendFrom")
        val actionName = intent?.getStringExtra("actionName")
        if (startPosition != null && startPosition != -1) {
            playMedia(startPosition, sendFrom)
        }
        if (actionName != null) {
            when (actionName) {
                Constants.S_ACTION_PLAY -> {
                    if (actionPlaying != null) {
                        actionPlaying!!.playPauseBtnClicked()
                    }
                }
                Constants.S_ACTION_NEXT -> {
                    if (actionPlaying != null) {
                        actionPlaying!!.nextBtnClicked()
                    }
                }
                Constants.S_ACTION_PREV -> {
                    if (actionPlaying != null) {
                        actionPlaying!!.prevBtnClicked()
                    }
                }
            }
        }
        return START_STICKY
    }

    fun setActionCallback(onActionPlaying: OnActionPlaying) {
        actionPlaying = onActionPlaying
    }

    private fun playMedia(startPosition: Int, sendFrom: String?) {
        when {
            sendFrom.equals("favorite") -> {
                musicList = FavouriteFragment.favMusicList
            }
            sendFrom.equals("album") -> {
                musicList = AlbumDetailsFragment.getAlbumSongs
            }
            else -> {
                musicList = ViewpagerFragment.getMusicsFiles()
            }
        }
        position = startPosition
        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            if (musicList != null) {
                createMediaPlayer(position)
                mediaPlayer!!.start()
            }
        } else {
            createMediaPlayer(position)
            mediaPlayer!!.start()
        }
        onComplete()
    }

    fun createMediaPlayer(innerPosition: Int) {
        position = innerPosition
        uri = Uri.parse(musicList!!.get(position).path)
        Utils.saveLastMusicPlayed(this, uri!!, musicList!![position])
        mediaPlayer = MediaPlayer.create(applicationContext, uri)
    }

    fun start() = mediaPlayer?.start()

    fun isPlaying() = mediaPlayer!!.isPlaying

    fun pause() = mediaPlayer!!.pause()

    fun stop() = mediaPlayer!!.stop()

    fun release() = mediaPlayer!!.release()

    fun getDuration() = mediaPlayer!!.duration

    fun getCurrentPosition() = mediaPlayer!!.currentPosition

    fun seekTo(position: Int) = mediaPlayer!!.seekTo(position)

    fun onComplete() = mediaPlayer!!.setOnCompletionListener(this)

    override fun onCompletion(mediaplayer: MediaPlayer?) {
        PlayerActivity.isComplete = true
        if (actionPlaying != null) {
            actionPlaying!!.nextBtnClicked()
            if (mediaplayer != null) {
                createMediaPlayer(position)
                mediaplayer.start()
                PlayerActivity.isComplete = false
            }
        }
    }

    fun showNotification(playPauseBtn: Int) {
        val intent = Intent(this, PlayerActivity::class.java)
        val contentPending = PendingIntent.getActivity(this, 0, intent, 0)

        val prevIntent = Intent(this, NotificationReceiver::class.java)
        prevIntent.action = Constants.ACTION_PREV
        val prevPending = PendingIntent.getBroadcast(this, 0,
                prevIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val nextIntent = Intent(this, NotificationReceiver::class.java)
        nextIntent.action = Constants.ACTION_NEXT
        val nextPending = PendingIntent.getBroadcast(this, 0,
                nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val pauseIntent = Intent(this, NotificationReceiver::class.java)
        pauseIntent.action = Constants.ACTION_PLAY
        val pausePending = PendingIntent.getBroadcast(this, 0,
                pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val stopIntent = Intent(this, NotificationReceiver::class.java)
        stopIntent.action = "action_stop"
        val stopPending = PendingIntent.getBroadcast(this, 0,
                stopIntent, PendingIntent.FLAG_UPDATE_CURRENT)


        val picture: ByteArray? = Utils.getAlbumArt(musicList!![position].path)
        val thumb: Bitmap? = if (picture != null) {
            BitmapFactory.decodeByteArray(picture, 0, picture.size)
        } else {
            BitmapFactory.decodeResource(resources, R.drawable.cover_art_placeholder)
        }

//        add below lines to work MediaStyle notifications in android 11
//        also add dependency "androidx.media:media:1.2.0"

        mediaSessionCompat!!.setMetadata(MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, thumb)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, musicList!![position].title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, musicList!![position].artist)
                .build()
        )

        val notification = NotificationCompat.Builder(this, Constants.CHANNEL_ID)
                .setSmallIcon(playPauseBtn)
                .setLargeIcon(thumb)
                .setContentTitle(musicList!![position].title)
                .setContentText(musicList!![position].artist)
                .addAction(R.drawable.ic_prev, "Previous", prevPending) //0 position for compactView
                .addAction(playPauseBtn, "Pause", pausePending) //1 position for compactView
                .addAction(R.drawable.ic_next, "Next", nextPending) //2 position for compactView
                .addAction(R.drawable.ic_close, "Close", stopPending) //3 position for compactView
                .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(1)
                    .setMediaSession(mediaSessionCompat!!.sessionToken))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)


/*        val notification = NotificationCompat.Builder(this, Constants.CHANNEL_ID)
                .setSmallIcon(playPauseBtn)
                .setContentTitle(musicList!![position].title)
                .setContentText(musicList!![position].artist)
                .addAction(R.drawable.ic_prev, "Previous", prevPending)
                .addAction(playPauseBtn, "Pause", pausePending)
                .addAction(R.drawable.ic_next, "Stop", stopPending)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionCompat!!.sessionToken))
                .setLargeIcon(thumb)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)*/


/*        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification.build())*/

        startForeground(1, notification.build())
    }

    inner class MyBinder : Binder() {
        fun getService() = this@MusicService
    }

}