package com.salman.playsong

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_music.view.*

class MusicAdapter(
    val musicFiles: ArrayList<MusicFiles>,
    val context: Context?,
    var sender: String = "",
    var isFromFavFrag: Boolean = false
) : RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

    private var songAction: OnSongAction? = null
    fun setOnSongAction(songAction: OnSongAction) {
        this.songAction = songAction
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_music, parent, false)
        return MusicViewHolder(view)
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        holder.fileName?.text = musicFiles[position].title
        val image = Utils.getAlbumArt(musicFiles[position].path)

        if (image != null) {
            Glide.with(context!!).asBitmap().load(image).into(holder.fileImg!!)
        } else {
            Glide.with(context!!).load(R.drawable.music_placeholder).into(holder.fileImg!!)
        }

        holder.view.setOnClickListener {
            val i = Intent(context, PlayerActivity::class.java)
            if (!sender.equals(""))
                i.putExtra("sender", sender)
            i.putExtra("position", position)
            context.startActivity(i)
        }

        holder.menuBtn!!.setOnClickListener {
            val popupMenu = PopupMenu(context, it)
            popupMenu.menuInflater.inflate(R.menu.popup, popupMenu.menu)
            val menuOps = popupMenu.menu
            if (musicFiles.get(position).isFav) {
                menuOps.findItem(R.id.favourite).title = "Remove from favourite"
            } else {
                menuOps.findItem(R.id.favourite).title = "Add to favourite"
            }
            if (isFromFavFrag) {
                menuOps.findItem(R.id.delete).isVisible = false
            }
            popupMenu.setOnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.delete -> {
                        if (position != RecyclerView.NO_POSITION)
                            songAction?.delete(musicFiles[position].id, position, it)
                    }
                    R.id.favourite -> {
                        if (position != RecyclerView.NO_POSITION) {
                            if (!musicFiles.get(position).isFav) {
                                songAction?.addToFavorite(position, musicFiles.get(position))
                            } else {
                                songAction?.removeFromFavorite(musicFiles.get(position).id)
                            }
                        }
                    }
                }
                true
            }
            popupMenu.show()
        }

    }

    override fun getItemCount(): Int {
        return musicFiles.size
    }

    fun updateMusic(position: Int, music: MusicFiles) {
        Log.d("Adapter", "isFav: ${music.isFav} ")
        musicFiles.get(position).apply {
            isFav = music.isFav
            musicFiles.set(position, this)
            notifyItemChanged(position, this)
        }
    }

    inner class MusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var fileName: TextView? = null
        var fileImg: ImageView? = null
        var menuBtn: ImageView? = null
        val view: View = itemView

        init {
            fileName = itemView.music_name
            fileImg = itemView.music_img
            menuBtn = itemView.menu
        }
    }

    interface OnSongAction {
        fun delete(musicId: String, position: Int, view: View)
        fun addToFavorite(position: Int, musicFiles: MusicFiles)
        fun removeFromFavorite(musicId: String)
    }

}