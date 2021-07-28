package com.salman.playsong

import android.content.ContentUris
import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_favourite.view.*
import kotlinx.android.synthetic.main.fragment_songs.*
import kotlinx.android.synthetic.main.fragment_songs.view.*
import java.io.File

class SongsFragment : Fragment(), MusicAdapter.OnSongAction {
    companion object {
        fun newInstance() = SongsFragment()
        private var onShowRecentPlayFrag: (() -> Unit)? =null
        fun setOnShowRecentPlayFragAction(onShowRecentPlayFrag: () -> Unit) {
            Log.d("BottomFrag", "bottomfrag: initialized")
            this.onShowRecentPlayFrag = onShowRecentPlayFrag
        }
    }

    var musicAdapter: MusicAdapter? = null
    var recyclerView: RecyclerView? = null
    var musicLists: ArrayList<MusicFiles>? = null
    var position = 0
    lateinit var mContext: Context
    lateinit var mActivity: MainActivity
    private val musicViewModel: MusicViewModel by viewModels<MusicViewModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = mContext as MainActivity
    }

    override fun onResume() {
        super.onResume()
        mActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        Log.d("Song", "onResume: song")
        onShowRecentPlayFrag?.let {
//            it()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("Song", "onCreateView Song: called")
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_songs, container, false)

        recyclerView = view.recycler

        initViews()

        setFavFrmPlayerActivity()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("Song", "onDestroyView: song")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Song", "onDestroy: song")
    }

    private fun initViews() {
        recyclerView!!.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            setHasFixedSize(true)
            musicLists = ViewpagerFragment.getMusicsFiles()
/*            if (musicLists != null && !musicLists!!.isEmpty()) {
                musicAdapter = MusicAdapter(musicLists!!, context)
                adapter = musicAdapter
                musicAdapter?.setOnSongAction(this@SongsFragment)
            }*/
        }
        fetchFavFromDB()
    }

    private fun fetchFavFromDB() {
        musicViewModel.readMusicFromFav().observe(viewLifecycleOwner, Observer {
            val list = arrayListOf<MusicFiles>()
            list.clear()
            list.addAll(it)
            for (i in 0 until musicLists!!.size) {
                for (j in 0 until list.size) {
                    if (list[j].id == musicLists!![i].id) {
                        musicLists!![i].isFav = true
//                        Log.d("Song", "common[$j] / music[$i]: ${list[j].title} / ${musicLists!![i].title}")
                        break
                    } else {
                        musicLists!![i].isFav = false
                    }
                }
            }
            if (musicLists != null && !musicLists!!.isEmpty()) {
                musicAdapter = MusicAdapter(musicLists!!, context)
                recyclerView?.adapter = musicAdapter
                musicAdapter?.setOnSongAction(this@SongsFragment)
            }
        })
    }

    private fun setFavFrmPlayerActivity() {

        PlayerActivity.setOnFavAddAction{ musicFiles ->
            addToFavorite(position, musicFiles)
        }

        PlayerActivity.setOnRemvAction { musicId ->
            removeFromFavorite(musicId)
        }
    }

    override fun delete(musicId: String, position: Int, view: View) {
        val snackBar = Snackbar.make(view, "Confirm delete ? Or swipe to cancel", Snackbar.LENGTH_LONG)
        snackBar.setAction("YES") {
           val contentUri = ContentUris.withAppendedId(
                   MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                   (musicLists!![position].id).toLong()
           )
            val file = File(musicLists!![position].path!!)
            val isDeleted = file.delete()
            if (isDeleted) {
                context?.contentResolver?.delete(contentUri, null, null)
                musicLists!!.removeAt(position)
                musicAdapter?.notifyItemRemoved(position)
                musicAdapter?.notifyItemRangeChanged(position, musicLists!!.size)
                Toast.makeText(context, "File Deleted!", Toast.LENGTH_SHORT).show()
            }
        }
        snackBar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
        snackBar.show()
    }

    override fun addToFavorite(position: Int, musicFiles: MusicFiles) {
        this.position = position
        val music = with(musicFiles) {
            MusicFiles(
                    id, title,
                    artist, album,
                    duration, path,
                    true
            )
        }
        musicViewModel.addMusicToFav(music)
        Toast.makeText(mContext, "Added to favorite..", Toast.LENGTH_SHORT).show()
//        musicAdapter?.updateMusic(position, music)
    }


    override fun removeFromFavorite(musicId: String) {
        musicViewModel.removeMusicFromFav(musicId)
        Toast.makeText(mContext, "Remove from favorite..", Toast.LENGTH_SHORT).show()
    }
}