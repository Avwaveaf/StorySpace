package com.avwaveaf.storyspace.view.home.ui.home.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.avwaveaf.storyspace.R
import com.avwaveaf.storyspace.data.model.ListStoryItem
import com.avwaveaf.storyspace.databinding.ItemStoryBinding
import com.avwaveaf.storyspace.helper.formatDate
import com.bumptech.glide.Glide

class StoryAdapter(private val onItemClick: (ListStoryItem, ItemStoryBinding) -> Unit) :
    PagingDataAdapter<ListStoryItem, StoryAdapter.StoryViewHolder>(StoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it, position == itemCount - 1) }
    }

    inner class StoryViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem, isLast: Boolean) {
            binding.textStoryName.text = story.name
            binding.textStoryDescription.text = story.description
            binding.textStoryDate.text = formatDate(dateString = story.createdAt.toString())
            // Load image using Glide
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .into(binding.imageStory)

            binding.root.setOnClickListener {
                onItemClick(story, binding)
            }

            // Adjust bottom margin if this is the last item
            val layoutParams = binding.root.layoutParams as ViewGroup.MarginLayoutParams
            if (isLast) {
                layoutParams.bottomMargin =
                    itemView.context.resources.getDimensionPixelSize(R.dimen.last_listitem_margin)
            } else {
                layoutParams.bottomMargin =
                    itemView.context.resources.getDimensionPixelSize(R.dimen.default_listitem_margin)
            }
            binding.root.layoutParams = layoutParams

        }
    }

    class StoryDiffCallback : DiffUtil.ItemCallback<ListStoryItem>() {
        override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
            return oldItem == newItem
        }
    }
}
