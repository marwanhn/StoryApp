package com.example.storyapp.view.main

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.adapter.StoryUserAdapter
import com.example.storyapp.data.retrofit.response.ListStoryItem
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.utils.ViewModelFactory
import com.example.storyapp.view.intro.IntroActivity
import com.example.storyapp.view.upload.UploadStoryActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var factory: ViewModelFactory
    private lateinit var storyAdapter: StoryUserAdapter
    private val mainViewModel: MainViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()



        val layoutManager = LinearLayoutManager(this)
        binding.rvStories.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvStories.addItemDecoration(itemDecoration)
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.apply {
            title = getString(R.string.app_name)
        }
    }

    private fun setupViewModel() {
        factory = ViewModelFactory.getInstance(this)

        mainViewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, IntroActivity::class.java))
                finish()
            } else {
                getStory(user.token)
            }
        }
    }

    private fun setupAction () {
        binding.btnUpload.setOnClickListener {
            startActivity(Intent(this,UploadStoryActivity::class.java))
        }
    }

    private fun getStory(token: String) {
        showLoading()
        mainViewModel.getListStory(token)
        mainViewModel.listStoryUser.observe(this) {
            setupAdapter(it.listStory)
            hideLoading()
        }
    }



    private fun setupAdapter(listStoryUser: List<ListStoryItem>) {
        storyAdapter = StoryUserAdapter(listStoryUser)
        binding.rvStories.adapter = storyAdapter
    }


    private fun showLoading() {
        mainViewModel.isLoading.observe(this@MainActivity) {
            binding.progressBar.visibility = View.VISIBLE
        }
    }

    private fun hideLoading() {
        mainViewModel.isLoading.observe(this@MainActivity) {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                mainViewModel.logout()
                true
            }
            R.id.action_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }


}