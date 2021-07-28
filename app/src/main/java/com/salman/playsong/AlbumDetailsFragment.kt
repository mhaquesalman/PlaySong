package com.salman.playsong

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_album_details.*
import kotlinx.android.synthetic.main.fragment_album_details.view.*


class AlbumDetailsFragment : Fragment() {
    companion object {
        const val ARG_ALBUM_NAME = "album_name"
        private var albumSongs: ArrayList<MusicFiles>? = null
        val getAlbumSongs
            get() = albumSongs

        fun newInstance(param: String?) =
                AlbumDetailsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_ALBUM_NAME, param)
                    }
                }
    }
    private var mView: View? = null
    private var albumName: String? = null
    private var musicAdapter: MusicAdapter? = null
    lateinit var mContext: Context
    lateinit var mActivity: AppCompatActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as AppCompatActivity

        albumSongs = ArrayList()

        arguments?.let {
            albumName = it.getString(ARG_ALBUM_NAME)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_album_details, container, false)

        val bundle = arguments
        bundle?.let {
            albumName = it.getString(ARG_ALBUM_NAME)
        }

        val musicsLists = ViewpagerFragment.getMusicsFiles()
        if (musicsLists != null) {
            var j = 0
            for (i in musicsLists.indices) {
                if (albumName!!.equals(musicsLists[i].album)) {
                    albumSongs!!.add(j, musicsLists[i])
                    j++
                }
            }
            val image = Utils.getAlbumArt(albumSongs!![0].path)
            if (image != null) {
                Glide.with(requireContext()).load(image).into(mView!!.album_photo)
            } else {
                Glide.with(requireContext()).load(R.drawable.cover_art_placeholder).into(mView!!.album_photo)
            }
        }

        initViews()

        return mView
    }

    private fun initViews() {
        if (albumSongs!!.size != 0) {
            musicAdapter = MusicAdapter(albumSongs!!, mContext, "album")
            mView!!.recycler.adapter = musicAdapter
            mView!!.recycler.layoutManager = LinearLayoutManager(
                    mContext,
                    RecyclerView.VERTICAL,
                    false
            )
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

/*    fun newInstance(param: String): AlbumDetailsFragment {
        val fragment = AlbumDetailsFragment()
        val bundle = Bundle()
        bundle.putString(ARG_ALBUM_NAME, param)
        fragment.arguments = bundle
        return fragment
    }*/

}