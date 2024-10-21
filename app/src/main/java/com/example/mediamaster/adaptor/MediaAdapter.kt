package com.example.mediamaster.adaptor.MediaAdapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mediamaster.R
import com.example.mediamaster.databinding.ContactItemsBinding
import com.example.mediamaster.databinding.ItemImageBinding

enum class MediaType {
    IMAGE, VIDEO, CONTACT, AUDIO, DOCUMENT
}

data class MediaRow(
    val mediaItems: List<MediaItem>
)

data class MediaItem(
    val uri: Uri? = null,
    val type: MediaType,
    val contactName: String? = null,
    val contactNumber: String? = null,
    val audioFileName: String? = null,
    val audioFileSize: Long? = null,
    val documentFileName: String? = null,
    val documentFileSize: Long? = null
)

class MediaAdapter(
    private val mediaRows: List<MediaRow>
) : RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {

    companion object {
        const val VIEW_TYPE_IMAGE_VIDEO = 1
        const val VIEW_TYPE_CONTACT = 2
        const val VIEW_TYPE_AUDIO = 3
        const val VIEW_TYPE_DOCUMENT = 4
    }

    sealed class MediaViewHolder(binding: androidx.viewbinding.ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        class ImageVideoViewHolder(val binding: ItemImageBinding) : MediaViewHolder(binding)
        class ContactViewHolder(val binding: ContactItemsBinding) : MediaViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int {
        val mediaRow = mediaRows[position]
        return when {
            mediaRow.mediaItems.isNotEmpty() -> {
                when (mediaRow.mediaItems[0].type) {
                    MediaType.CONTACT -> VIEW_TYPE_CONTACT
                    MediaType.AUDIO -> VIEW_TYPE_AUDIO
                    MediaType.DOCUMENT -> VIEW_TYPE_DOCUMENT
                    else -> VIEW_TYPE_IMAGE_VIDEO
                }
            }
            else -> VIEW_TYPE_IMAGE_VIDEO
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        return when (viewType) {
            VIEW_TYPE_CONTACT, VIEW_TYPE_AUDIO, VIEW_TYPE_DOCUMENT -> {
                val binding = ContactItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MediaViewHolder.ContactViewHolder(binding)
            }
            else -> {
                val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MediaViewHolder.ImageVideoViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val mediaRow = mediaRows[position]
        when (holder) {
            is MediaViewHolder.ImageVideoViewHolder -> loadMedia(holder, mediaRow)
            is MediaViewHolder.ContactViewHolder -> {
                val mediaItem = mediaRow.mediaItems[0]
                when (mediaItem.type) {
                    MediaType.CONTACT -> loadContact(holder, mediaItem)
                    MediaType.AUDIO -> loadAudio(holder, mediaItem)
                    MediaType.DOCUMENT -> loadDocument(holder, mediaItem)
                    else -> VIEW_TYPE_IMAGE_VIDEO
                }
            }
        }
    }

    private fun loadMedia(holder: MediaViewHolder.ImageVideoViewHolder, mediaRow: MediaRow) {
        holder.binding.apply {
            imageView1.setImageDrawable(null)
            imageView2.setImageDrawable(null)
            imageView3.setImageDrawable(null)
            imageView4.setImageDrawable(null)

            video.visibility = View.GONE
            video2.visibility = View.GONE
            video3.visibility = View.GONE
            video4.visibility = View.GONE
        }

        val imageViews = listOf(holder.binding.imageView1, holder.binding.imageView2, holder.binding.imageView3, holder.binding.imageView4)
        val videoIcons = listOf(holder.binding.video, holder.binding.video2, holder.binding.video3, holder.binding.video4)

        mediaRow.mediaItems.take(4).forEachIndexed { index, mediaItem ->
            Glide.with(holder.itemView.context)
                .load(mediaItem.uri)
                .placeholder(R.drawable.loading1)
                .error(R.drawable.img_1)
                .into(imageViews[index])

            if (mediaItem.type == MediaType.VIDEO) {
                videoIcons[index].visibility = View.VISIBLE
            } else {
                videoIcons[index].visibility = View.GONE
            }
        }
    }


    private fun loadAudio(holder: MediaViewHolder.ContactViewHolder, mediaItem: MediaItem) {
        holder.binding.contactNameTextView.text = holder.itemView.context.getString(R.string.audio_file_name_label, mediaItem.audioFileName)
        holder.binding.contactNumberTextView.text = holder.itemView.context.getString(R.string.audio_file_size_label, formatFileSize(mediaItem.audioFileSize))
        holder.binding.mediaImageView.setImageResource(R.drawable.audio)
    }

    private fun loadContact(holder: MediaViewHolder.ContactViewHolder, mediaItem: MediaItem) {
        holder.binding.contactNameTextView.text = holder.itemView.context.getString(R.string.contact_name_label, mediaItem.contactName)
        holder.binding.contactNumberTextView.text = holder.itemView.context.getString(R.string.contact_number_label, mediaItem.contactNumber)
        holder.binding.mediaImageView.setImageResource(R.drawable.man)
    }

    private fun loadDocument(holder: MediaViewHolder.ContactViewHolder, mediaItem: MediaItem) {
        holder.binding.contactNameTextView.text = holder.itemView.context.getString(R.string.audio_file_name_label, mediaItem.documentFileName)
        holder.binding.contactNumberTextView.text = holder.itemView.context.getString(R.string.audio_file_size_label, formatFileSize(mediaItem.documentFileSize))
        holder.binding.mediaImageView.setImageResource(R.drawable.folder)
    }


    private fun formatFileSize(size: Long?): String {
        return when {
            size == null -> "Unknown size"
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> "${size / 1024} KB"
            else -> "${size / (1024 * 1024)} MB"
        }
    }

    override fun getItemCount(): Int = mediaRows.size
}
