package com.salman.playsong

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_player.*
import java.util.*
import kotlin.collections.ArrayList

class PlayerActivity : AppCompatActivity(),
        OnActionPlaying,
        ServiceConnection {
    companion object {
        var listSongs: ArrayList<MusicFiles>? = ArrayList()
        var uri: Uri? = null
//        var mediaPlayer: MediaPlayer? = null
        var shuffleBtn = false
        var repeatBtn = false
        var isComplete = false
        var isFavSong = false

        var onFavAdd: ((musicFiles: MusicFiles) -> Unit)? = null
        var onFavRemv: ((musicId: String) -> Unit)? = null

        fun setOnFavAddAction(onFavAddAction: (musicFiles: MusicFiles) -> Unit) {
            this.onFavAdd = onFavAddAction
        }

        fun setOnRemvAction(onFavRemvAction: (musicId: String) -> Unit) {
            this.onFavRemv = onFavRemvAction
        }
    }

    var position = 0
    var sender: String? = null
    var sendFrom: String? = null
    val handler = Handler()
    var musicService: MusicService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullscreen()
        setContentView(R.layout.activity_player)
        supportActionBar?.hide()



        intent?.let {
            getDataFromIntent(it)
        }

//        updateSeekBar()

        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (musicService != null && fromUser) {
                    musicService!!.seekTo(progress * 1000)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
//                musicService?.seekTo(seekBar!!.progress)
            }
        })

        //updateSeekBar()

        shuffle.setOnClickListener {
            if (shuffleBtn) {
                shuffleBtn = false
                shuffle.setImageResource(R.drawable.ic_shuffle)
            } else {
                shuffleBtn = true
                shuffle.setImageResource(R.drawable.ic_shuffle_on)
            }
        }


        repeat.setOnClickListener {
            if (repeatBtn) {
                repeatBtn = false
                repeat.setImageResource(R.drawable.ic_repeat)
            } else {
                repeatBtn = true
                repeat.setImageResource(R.drawable.ic_repeat_on)
            }
        }


        favourite.setOnClickListener {
            if (!isFavSong) {
                onFavAdd?.let {
                    it(listSongs!!.get(position))
                }
                isFavSong = true
                favourite.setImageResource(R.drawable.ic_favorite_on)
            } else {
                onFavRemv?.let {
                    it(listSongs!!.get(position).id)
                }
                isFavSong = false
                favourite.setImageResource(R.drawable.ic_favorite)
            }
        }

/*        runOnUiThread(object : Runnable {
            override fun run() {
                if (musicService != null) {
                    val currentPosition = musicService!!.currentPosition / 1000
                    seekbar.progress = currentPosition
                    play_duration.text = formattedTime(currentPosition)
                }
                handler.postDelayed(this, 1000)
            }
        })*/


        back_btn.setOnClickListener {
            super.onBackPressed()
        }

    }

    override fun onResume() {
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        playThread()
        prevThread()
        nextThread()
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        unbindService(this)
    }

    private val updater: Runnable = Runnable {
        updateSeekBar()
        val currentPosition = musicService!!.getCurrentPosition() / 1000
        play_duration.text = formattedTime(currentPosition)
    }


    private fun updateSeekBar() {
        if (musicService != null && musicService!!.isPlaying()) {
//            val currentPosition = ((musicService!!.currentPosition) / (musicService!!.duration)) * 100
            val currentPosition = musicService!!.getCurrentPosition() / 1000
            seekbar.progress = currentPosition
            handler.postDelayed(updater, 1000)
        }

/*        seekbar.max = musicService!!.duration / 1000
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (musicService != null) {
                    val currentPosition = musicService!!.currentPosition / 1000
                    seekbar.progress = currentPosition
                    play_duration.text = formattedTime(currentPosition)
                }
            }
        }, 0, 1000)*/
    }

    private fun formattedTime(currentPosition: Int): String {
        val formatTime: String
        val seconds = (currentPosition % 60).toString()
        val minutes = (currentPosition / 60).toString()
        formatTime = if (seconds.length == 1) {
            minutes + ":" + "0" + seconds
        } else {
            minutes + ":" + seconds
        }
        return formatTime
    }

    private fun getDataFromIntent(it: Intent) {
        position = it.getIntExtra("position", 0)
        sender = it.getStringExtra("sender")

//        listSongs?.clear()
        if (sender != null && sender.equals("album")) {
            sendFrom = "album"
            listSongs = AlbumDetailsFragment.getAlbumSongs
        } else if (sender != null && sender.equals("favorite")) {
            sendFrom = "favorite"
            listSongs = FavouriteFragment.favMusicList
        } else {
            listSongs = ViewpagerFragment.getMusicsFiles()
        }

        if (listSongs != null) {
            play_pause.setImageResource(R.drawable.ic_pause)
            uri = Uri.parse(listSongs!![position].path)

/*            if (musicService != null) {
                musicService!!.stop()
                musicService!!.release()
                musicService = MediaPlayer.create(applicationContext, uri)
                musicService!!.start()
            } else {
                musicService = MediaPlayer.create(applicationContext, uri)
                musicService!!.start()
            }*/
            
            val intent = Intent(this, MusicService::class.java)
            intent.putExtra("startPosition", position)
            intent.putExtra("sendFrom", sendFrom)
            startService(intent)

        }
    }

    private fun setFullscreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    private fun playThread() {
        Thread {
            play_pause.setOnClickListener {
                playPauseBtnClicked()
            }
        }.start()
    }

    override fun playPauseBtnClicked() {
        if (musicService!!.isPlaying()) {
            musicService!!.showNotification(R.drawable.ic_play)
            play_pause.setImageResource(R.drawable.ic_play)
            musicService!!.pause()
            updateSeekBar()
        } else {
            musicService!!.showNotification(R.drawable.ic_pause)
            play_pause.setImageResource(R.drawable.ic_pause)
            musicService!!.start()
            updateSeekBar()
        }
    }

    private fun prevThread() {
        Thread {
            prev.setOnClickListener {
                prevBtnClicked()
            }
        }.start()
    }

    override fun prevBtnClicked() {
        if (musicService!!.isPlaying()) {
            musicService!!.stop()
            musicService!!.release()
            if (shuffleBtn && !repeatBtn) {
                position = getRandom(listSongs!!.size)
            } else if (!shuffleBtn && !repeatBtn) {
                position = if ((position - 1) < 0) listSongs!!.size - 1 else position - 1
            }
            uri = Uri.parse(listSongs!![position].path)
            musicService!!.createMediaPlayer(position)
            metaData()
            updateSeekBar()
            musicService!!.showNotification(R.drawable.ic_pause)
            play_pause.setImageResource(R.drawable.ic_pause)
            musicService!!.start()
        } else {
            musicService!!.stop()
            musicService!!.release()
            if (shuffleBtn && !repeatBtn) {
                position = getRandom(listSongs!!.size)
            } else if (!shuffleBtn && !repeatBtn) {
                position = if ((position - 1) < 0) listSongs!!.size - 1 else position - 1
            }
            uri = Uri.parse(listSongs!![position].path)
            musicService!!.createMediaPlayer(position)
            metaData()
            updateSeekBar()
            musicService!!.showNotification(R.drawable.ic_play)
            play_pause.setImageResource(R.drawable.ic_play)
        }
    }

    private fun nextThread() {
        Thread {
            next.setOnClickListener {
                nextBtnClicked()
            }
        }.start()
    }

    override fun nextBtnClicked() {
        if (musicService!!.isPlaying()) {
            musicService!!.stop()
            musicService!!.release()
            if (shuffleBtn && !repeatBtn) {
                position = getRandom(listSongs!!.size)
            } else if (!shuffleBtn && !repeatBtn) {
                position = ((position + 1) % listSongs!!.size)
            }
            uri = Uri.parse(listSongs!![position].path)
            musicService!!.createMediaPlayer(position)
            metaData()
            updateSeekBar()
            musicService!!.showNotification(R.drawable.ic_pause)
            play_pause.setImageResource(R.drawable.ic_pause)
            musicService!!.start()
        } else {
            musicService!!.stop()
            musicService!!.release()
            if (shuffleBtn && !repeatBtn) {
                position = getRandom(listSongs!!.size)
            } else if (!shuffleBtn && !repeatBtn) {
                position = ((position + 1) % listSongs!!.size)
            }
            uri = Uri.parse(listSongs!![position].path)
            musicService!!.createMediaPlayer(position)
            metaData()
            updateSeekBar()
            if (!isComplete) {
                musicService!!.showNotification(R.drawable.ic_play)
                play_pause.setImageResource(R.drawable.ic_play)
            }
            else {
                musicService!!.showNotification(R.drawable.ic_pause)
                play_pause.setImageResource(R.drawable.ic_pause)
            }
        }
    }

    private fun getRandom(i: Int): Int {
        return Random().nextInt(i)
    }

    private fun metaData() {
        song_name.text = listSongs!!.get(position).title
        song_artist.text = listSongs!![position].artist
        isFavSong = listSongs!!.get(position).isFav
        if (isFavSong) {
            favourite.setImageResource(R.drawable.ic_favorite_on)
        } else {
            favourite.setImageResource(R.drawable.ic_favorite)
        }

        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri.toString())
        val totalDuration = Integer.parseInt(listSongs!![position].duration!!) / 1000 // java approach
        total_duration.text = formattedTime(totalDuration)
        val image = retriever.embeddedPicture
        retriever.release()
        val bitmap: Bitmap
        if (image != null) {
//            Glide.with(this).asBitmap().load(image).into(cover_art)
            bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
            imageAnimation(this, cover_art, bitmap)
            Palette.from(bitmap).generate { palette ->
                val swatch = palette?.dominantSwatch
                if (swatch != null) {
                    imgView_gradient.setBackgroundResource(R.drawable.gradient_bg)
                    container.setBackgroundResource(R.drawable.main_bg)
                    val gradientDrawable = GradientDrawable(
                            GradientDrawable.Orientation.BOTTOM_TOP,
                            intArrayOf(swatch.rgb, 0x000000)
                    )
                    imgView_gradient.background = gradientDrawable
                    val gradientDrawableBg = GradientDrawable(
                            GradientDrawable.Orientation.BOTTOM_TOP,
                            intArrayOf(swatch.rgb, swatch.rgb)
                    )
                    container.background = gradientDrawableBg
                    val drawable = ResourcesCompat.getDrawable(resources, R.drawable.main_bg, null)
                    layout_top.background = drawable
                    song_name.setTextColor(swatch.titleTextColor)
                    song_artist.setTextColor(swatch.bodyTextColor)

                } else {
                    imgView_gradient.setBackgroundResource(R.drawable.gradient_bg)
                    container.setBackgroundResource(R.drawable.main_bg)
                    val gradientDrawable = GradientDrawable(
                            GradientDrawable.Orientation.BOTTOM_TOP,
                            intArrayOf(0xff0000, 0x000000)
                    )
                    imgView_gradient.background = gradientDrawable
                    val gradientDrawableBg = GradientDrawable(
                            GradientDrawable.Orientation.BOTTOM_TOP,
                            intArrayOf(0xff0000, 0x000000)
                    )
                    container.background = gradientDrawableBg
                    val drawable = ResourcesCompat.getDrawable(resources, R.drawable.main_bg, null)
                    layout_top.background = drawable
                    song_name.setTextColor(Color.WHITE)
                    song_artist.setTextColor(Color.DKGRAY)
                }
            }

        } else {
//            Glide.with(this).load(R.drawable.cover_art_placeholder).into(cover_art)
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.cover_art_placeholder)
            imageAnimation(this, cover_art, bitmap)
            imgView_gradient.setBackgroundResource(R.drawable.gradient_bg)
            container.setBackgroundResource(R.drawable.main_bg)
            val drawable = ResourcesCompat.getDrawable(resources, R.drawable.gradient_bg, null)
            layout_top.background = drawable
            song_name.setTextColor(Color.WHITE)
            song_artist.setTextColor(Color.DKGRAY)
        }

    }


    private fun imageAnimation(context: Context, imageView: ImageView, bitmap: Bitmap) {
        val animOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out)
        val animIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
        animOut.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(p0: Animation?) {}

            override fun onAnimationEnd(p0: Animation?) {
                Glide.with(context).load(bitmap).into(imageView)
                animIn.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(p0: Animation?) {}

                    override fun onAnimationEnd(p0: Animation?) {}

                    override fun onAnimationRepeat(p0: Animation?) {}
                })
                imageView.startAnimation(animIn)
            }

            override fun onAnimationRepeat(p0: Animation?) {}
        })
        imageView.startAnimation(animOut)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        Toast.makeText(this, "service connected", Toast.LENGTH_SHORT).show()
        val myBinder: MusicService.MyBinder = service as MusicService.MyBinder
        musicService = myBinder.getService()
        musicService?.setActionCallback(this)
        musicService?.let {
            seekbar.max = it.getDuration() / 1000
        }
        updateSeekBar()
        metaData()
        musicService!!.showNotification(R.drawable.ic_pause)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        Toast.makeText(this, "service disconnected", Toast.LENGTH_SHORT).show()
        musicService = null
    }

}