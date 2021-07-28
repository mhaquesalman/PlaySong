package com.salman.playsong

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_album.view.*

class AlbumAdapter(
        val context: Context?,
        val musicFiles: ArrayList<MusicFiles>
) : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    private var listener: ((musicFiles: MusicFiles) -> Unit)? = null
    fun setListenerAction(listener: (MusicFiles) -> Unit) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_album, parent, false)
        return AlbumViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {

        holder.albumName?.text = musicFiles[position].title
        val image = Utils.getAlbumArt(musicFiles[position].path)

        if (image != null) {
            Glide.with(context!!).asBitmap().load(image).into(holder.albumImage!!)
        } else {
            Glide.with(context!!).load(R.drawable.music_placeholder).into(holder.albumImage!!)
        }

        holder.view.setOnClickListener {
            listener?.let {
                it(musicFiles[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return musicFiles.size
    }



    inner class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var albumImage: ImageView? = null
        var albumName: TextView? = null
        val view: View = itemView

        init {
            albumImage = itemView.album_image
            albumName = itemView.album_name
        }
    }

}