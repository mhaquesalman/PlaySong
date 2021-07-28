package com.salman.playsong

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_now_playing.*


class NowPlayingFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_now_playing, container, false)

        return view
    }

    override fun onResume() {
        super.onResume()
        if (ViewpagerFragment.showMiniPlayer) {
            if (ViewpagerFragment.getPathOfLastSong != null) {
                val art = Utils.getAlbumArt(ViewpagerFragment.getPathOfLastSong)
                if (art != null) {
                    Glide.with(requireContext()).load(art).into(bottom_album)
                } else {
                    Glide.with(requireContext()).load(R.drawable.cover_art_placeholder).into(bottom_album)
                }
                bototm_title.text = ViewpagerFragment.getTitleOfLastSong
                bottom_artist.text = ViewpagerFragment.getArtistOfLastSong
            }
        }
    }

}