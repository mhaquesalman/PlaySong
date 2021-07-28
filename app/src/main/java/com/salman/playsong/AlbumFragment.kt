package com.salman.playsong

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.salman.playsong.AlbumDetailsFragment.Companion.ARG_ALBUM_NAME
import kotlinx.android.synthetic.main.fragment_album.view.*

class AlbumFragment : Fragment() {

    var albumAdapter: AlbumAdapter? = null
    var recyclerView: RecyclerView? = null
    var musicFiles: ArrayList<MusicFiles>? = null
    lateinit var mContext: Context
    lateinit var mActivity: MainActivity

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
        Log.d("Song", "onResume: album")
        onHideRecentPlayFrag?.let {
//            it()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("Song", "onCreateView Album: called")
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_album, container, false)

        val fm = fragmentManager
        val viewPagerFragment = fragmentManager?.findFragmentById(R.id.fragment)
                as ViewpagerFragment



        recyclerView = view.recycler_album

        recyclerView!!.layoutManager = GridLayoutManager(context, 2)
        recyclerView!!.setHasFixedSize(true)
        musicFiles = ViewpagerFragment.getAlbumFiles()
        if (musicFiles != null && !musicFiles!!.isEmpty()) {
            albumAdapter = AlbumAdapter(context, musicFiles!!)
            recyclerView!!.adapter = albumAdapter
            albumAdapter!!.setListenerAction {
                goToAlbumDetails(it)
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("Song", "onDestroyView: album")
    }

    private fun goToAlbumDetails(musicFiles: MusicFiles) {
/*        val fragment = AlbumDetailsFragment()
        val bundle = Bundle()
        bundle.putString(ARG_ALBUM_NAME, musicFiles.album)
        fragment.arguments = bundle*/
        val fragment = AlbumDetailsFragment.newInstance(musicFiles.album)
        val transaction = mActivity.supportFragmentManager.beginTransaction()
//        transaction.hide(this)
        transaction.replace(R.id.fragment_album_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
        needReload = true
//        mActivity.hideTabLayout()
    }

    companion object {
        var needReload = false
        fun newInstance() = AlbumFragment()

        private var onHideRecentPlayFrag: (() -> Unit)? =null
        fun setOnHideRecentPlayFragAction(onHideRecentPlayFrag: () -> Unit) {
            Log.d("BottomFrag", "bottomfrag: initialized")
            this.onHideRecentPlayFrag = onHideRecentPlayFrag
        }
    }
}