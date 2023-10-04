package com.example.storyapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.data.retrofit.response.ListStoryItem
import com.example.storyapp.databinding.ItemRowStoryBinding
import com.example.storyapp.view.detail.DetailStoryActivity
import com.example.storyapp.view.detail.DetailStoryActivity.Companion.EXTRA_DATA

class StoryUserAdapter(private val listStory: List<ListStoryItem>) :
    RecyclerView.Adapter<StoryUserAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemRowStoryBinding) :
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

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val user = listStory[position]
        viewHolder.bind(user)
    }

    override fun getItemCount(): Int {
        return listStory.size
    }
}