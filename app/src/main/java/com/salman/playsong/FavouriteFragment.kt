package com.salman.playsong

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_favourite.*
import kotlinx.android.synthetic.main.fragment_favourite.view.*


class FavouriteFragment : Fragment() {

    private val musicViewModel: MusicViewModel by viewModels<MusicViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.d("Song", "onCreateView Fav: called")
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)

        initViews(view)

        return view
    }

    override fun onResume() {
        super.onResume()
        Log.d("Song", "onResume: fav")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("Song", "onDestroyView: fav")
    }

    private fun initViews(v: View) {
        v.recycler_favourite.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        v.recycler_favourite.setHasFixedSize(true)
        musicViewModel.readMusicFromFav().observe(viewLifecycleOwner, Observer {
            list.clear()
            list.addAll(it)
            val musicAdapter = MusicAdapter(list, context, "favorite", true)
            v.recycler_favourite.adapter = musicAdapter
            // Kotlin way of creating ArrayList from any Collection
//            val musicAdapter = MusicAdapter(ArrayList(it), context)
        })
    }

    companion object {
        private val list = arrayListOf<MusicFiles>()
        val favMusicList
            get() = list

        fun newInstance() = FavouriteFragment()
    }

}