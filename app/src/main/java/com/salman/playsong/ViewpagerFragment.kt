package com.salman.playsong

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_viewpager.*
import kotlinx.android.synthetic.main.fragment_viewpager.view.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class ViewpagerFragment : Fragment(), EasyPermissions.PermissionCallbacks {
    companion object {
        var showMiniPlayer = true
        private var pathOfLastSong: String? = null
        val getPathOfLastSong
            get() = pathOfLastSong
        private var titleOfLastSong: String? = null
        val getTitleOfLastSong
            get() = titleOfLastSong
        private var artistOfLastSong: String? = null
        val getArtistOfLastSong
            get() = artistOfLastSong
        const val REQ_CODE = 1
        private var musicFiles: ArrayList<MusicFiles>? = null
        fun getMusicsFiles() = musicFiles
        private var albumFiles: ArrayList<MusicFiles>? = ArrayList()
        fun getAlbumFiles() = albumFiles
    }
    private lateinit var mContext: Context
    private var mView: View? = null
    private var viewPagerAdapter: ViewPagerAdapter? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_viewpager, container, false)

        permission()

//        recentPlayFragActionVisibility()

        return mView
    }

    fun recentPlayFragActionVisibility() {
        AlbumFragment.setOnHideRecentPlayFragAction {
            Log.d("BottomFrag", "bottomfrag hide: called")
            fragment_bottom.visibility = View.INVISIBLE
        }

        SongsFragment.setOnShowRecentPlayFragAction {
            Log.d("BottomFrag", "bottomfrag show: called")
            fragment_bottom.visibility = View.VISIBLE
        }
    }

    fun check() = Toast.makeText(mContext, "loaded", Toast.LENGTH_SHORT).show()

    override fun onResume() {
        super.onResume()
        val path = Utils.pathOfLastMusicPlayed(mContext)
        val title = Utils.titleOfLastMusicPlayed(mContext)
        val artist =  Utils.artistOfLastMusicPlayed(mContext)
        if (path != null) {
            showMiniPlayer = true
            pathOfLastSong = path
            titleOfLastSong = title
            artistOfLastSong = artist
        } else {
            fragment_bottom.visibility = View.GONE
            showMiniPlayer = false
            pathOfLastSong = null
            titleOfLastSong = null
            artistOfLastSong = null
        }
    }

    private fun permission() {
        if (EasyPermissions.hasPermissions(
                mContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )) {
            musicFiles = getAllAudio(mContext)
            initViewPager()
        } else {
            EasyPermissions.requestPermissions(
                this,
                "Need permission to run this app",
                REQ_CODE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    fun hideTabLayout() {
        mView!!.tab_layout.visibility = View.GONE
    }

    fun showTabLayout() {
        mView!!.tab_layout.visibility = View.VISIBLE
    }

    fun refresh() {
        //musicFiles = getAllAudio(mContext)
        initViewPager()
    }

    private fun initViewPager() {
        viewPagerAdapter = ViewPagerAdapter(requireActivity().supportFragmentManager)
        viewPagerAdapter!!.apply {
            addFragments(SongsFragment.newInstance(), "Songs")
            addFragments(AlbumFragment.newInstance(), "Albums")
            addFragments(FavouriteFragment.newInstance(), "Favorites")
            mView!!.viewpager.adapter = viewPagerAdapter
            mView!!.tab_layout.setupWithViewPager(mView!!.viewpager)
        }
    }

    private fun getAllAudio(context: Context): ArrayList<MusicFiles> {
        val duplicate = arrayListOf<String>()
        val tempAudios = arrayListOf<MusicFiles>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA, // path
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media._ID,
        )

        val cursor = context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            null
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val album = cursor.getString(0)
                val title = cursor.getString(1)
                val duration = cursor.getString(2)
                val path = cursor.getString(3)
                val artist = cursor.getString(4)
                val id = cursor.getString(5)

                val musicFiles = MusicFiles(
                    id, title,
                    artist, album,
                    duration, path
                )
                tempAudios.add(musicFiles)
                if (!duplicate.contains(album)) {
                    albumFiles!!.add(musicFiles)
                    duplicate.add(album)
                }
            }
            cursor.close()
        }
        return tempAudios
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            musicFiles = getAllAudio(mContext)
            initViewPager()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Toast.makeText(context, "permission granted successfully", Toast.LENGTH_SHORT).show()
        if (requestCode == REQ_CODE && perms.isNotEmpty()) {
            musicFiles = getAllAudio(mContext)
            initViewPager()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            permission()
        }
    }

}