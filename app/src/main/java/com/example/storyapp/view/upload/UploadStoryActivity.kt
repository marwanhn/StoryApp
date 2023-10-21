package com.example.storyapp.view.upload

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityUploadStoryBinding
import com.example.storyapp.utils.ViewModelFactory
import com.example.storyapp.utils.getImageUri
import com.example.storyapp.utils.reduceFileImage
import com.example.storyapp.utils.uriToFile
import com.example.storyapp.view.main.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody


class UploadStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadStoryBinding
    private var currentImageUri: Uri? = null
    private lateinit var factory: ViewModelFactory
    private val uploadStoryViewModel: UploadStoryViewModel by viewModels { factory }
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lat = 0.0
    private var lon = 0.0





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupView()
        setupViewModel()
        setupAction()

    }

    private fun setupView() {
        binding = ActivityUploadStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        supportActionBar?.apply {
            title = getString(R.string.title_upload_story)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupViewModel() {
        factory = ViewModelFactory.getInstance(this)
    }


    private fun setupAction() {
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener { uploadImage() }
        binding.cbLocation.setOnCheckedChangeListener { _, isChecked ->
            getMyLastLocation()
        }

    }

    private fun startGallery() {
        launcherGallery.launch("image/*")
    }


    private val launcherGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("File Picker", "No file selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getMyLastLocation()
                }
                else -> {
                    // No location access granted.
                }
            }
        }
    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
    private fun getMyLastLocation() {
        if     (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ){
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    lat = location.latitude
                    lon = location.longitude
                    Log.d("UploadStoryActivity","$lat + $lon")
                } else {
                    Toast.makeText(
                        this@UploadStoryActivity,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun uploadImage() {
        if (currentImageUri != null) {
            val file = uriToFile(currentImageUri!!, this)
            val reducedFile = reduceFileImage(file)
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                reducedFile.name,
                requestImageFile
            )

            val description = binding.edtDescriptionText.text.toString()
            if (description.isBlank()) {

                Toast.makeText(
                    this@UploadStoryActivity,
                    getString(R.string.empty_description_warning),
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            val latitude = if(binding.cbLocation.isChecked)  {
                lat
            } else {
                0.0
            }
            val longitude = if(binding.cbLocation.isChecked)  {
                lon
            } else {
                0.0
            }


            showLoading()

            uploadStoryViewModel.getSession().observe(this@UploadStoryActivity) {
                uploadStoryResponse(
                    it.token,
                    imageMultipart,
                    description.toRequestBody("text/plain".toMediaType()),
                    latitude.toString().toRequestBody("text/plain".toMediaType()),
                    longitude.toString().toRequestBody("text/plain".toMediaType())
                )
            }
        } else {
            Toast.makeText(
                this@UploadStoryActivity,
                getString(R.string.empty_image_warning),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun uploadStoryResponse(
        token: String,
        photo: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody,
        lon: RequestBody
    ) {
        uploadStoryViewModel.uploadStory(token, photo, description, lat, lon)
        uploadStoryViewModel.uploadStoryResponse.observe(this@UploadStoryActivity) {
            if (!it.error!!) {
                moveActivity()
            }
        }
    }

    private fun moveActivity() {
        val intent = Intent(this@UploadStoryActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun showLoading() {
        uploadStoryViewModel.isLoading.observe(this@UploadStoryActivity) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}