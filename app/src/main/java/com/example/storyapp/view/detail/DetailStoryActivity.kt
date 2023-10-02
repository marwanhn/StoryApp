package com.example.storyapp.view.detail

import android.nfc.NfcAdapter.EXTRA_DATA
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.storyapp.R
import com.example.storyapp.adapter.StoryUserAdapter
import com.example.storyapp.data.retrofit.response.ListStoryItem
import com.example.storyapp.databinding.ActivityDetailStoryBinding
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.utils.ViewModelFactory
import com.example.storyapp.view.main.MainViewModel

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
            Glide.with(this@DetailStoryActivity)
                .load(currentStory.photoUrl)
                .fitCenter()
                .into(ivStory)
            tvName.text = currentStory.name
            tvDesc.text = currentStory.description

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