package com.example.storyapp.view.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.retrofit.response.ListStoryItem
import com.example.storyapp.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupView()
        setupDetail()
    }

    private fun setupView() {
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = getString(R.string.title_detail)
            setDisplayHomeAsUpEnabled(true)
        }


    }

    private fun setupDetail() {
        val currentStory = intent.getParcelableExtra<ListStoryItem>(EXTRA_DATA) as ListStoryItem
        binding.apply {
            ivStory.loadImage(url = currentStory.photoUrl)
            tvName.text = currentStory.name
            tvDesc.text = currentStory.description
        }
    }

    private fun ImageView.loadImage(url: String?, @DrawableRes placeholderResId: Int = R.drawable.baseline_place_holder) {
        if (!url.isNullOrBlank()) {
            Glide.with(this.context)
                .load(url)
                .fitCenter()
                .placeholder(placeholderResId)
                .error(placeholderResId) // Menetapkan placeholder jika ada kesalahan dalam memuat gambar
                .into(this)
        } else {
            // Handle case where URL is empty or null
            this.setImageResource(placeholderResId)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
    }


}