package com.example.storyapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.data.retrofit.response.ListStoryItem
import com.example.storyapp.databinding.ItemRowStoryBinding
import com.example.storyapp.view.detail.DetailStoryActivity
import com.example.storyapp.view.detail.DetailStoryActivity.Companion.EXTRA_DATA

class StoryUserAdapter:
    PagingDataAdapter<ListStoryItem, StoryUserAdapter.ViewHolder>(DIFF_ITEM_CALLBACK) {

   class ViewHolder(private val binding: ItemRowStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            with(binding) {
                Glide.with(root.context)
                    .load(story.photoUrl)
                    .fitCenter()
                    .into(ivImage)
                tvUsername.text = story.name
                tvDescription.text = story.description


                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                    intent.putExtra(EXTRA_DATA, story)

                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(ivImage, "story"),
                            Pair(tvUsername, "name"),
                            Pair(tvDescription, "description"),
                        )
                    itemView.context.startActivity(intent, optionsCompat.toBundle())
                }
            }


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
    }



    companion object {
        val DIFF_ITEM_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }

    }
}