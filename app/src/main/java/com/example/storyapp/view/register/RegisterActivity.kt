package com.example.storyapp.view.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.utils.ViewModelFactory
import com.example.storyapp.view.login.LoginActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var factory: ViewModelFactory
    private val registerViewModel: RegisterViewModel by viewModels { factory }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)



        setupView()
        setupViewModel()
        playAnimation()
        setupAction()
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
            title = getString(R.string.sign_up_bar)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupViewModel() {
        factory = ViewModelFactory.getInstance(this)
    }

    private fun setupAction() {
        binding.btnSignUp.setOnClickListener {
            if (isEditTextEmpty(binding.edtNameText) || isEditTextEmpty(binding.edtEmailText) || isEditTextEmpty(
                    binding.edtPasswordText
                )
            ) {
                // Validasi jika EditText kosong
                showAlert("Error", "Semua kolom harus diisi")
            } else if (hasValidationError()) {
                // Validasi jika EditText masih memiliki Error
                showAlert("Error", "Semua kolom harus diisi dengan benar.")
            } else {
                // Semua EditText sudah diisi, lanjutkan ke aktivitas selanjutnya
                showLoading()
                postText()
                registerViewModel.registerResponse.observe(this@RegisterActivity) { response ->
                    if (!response.error) {
                        showAlert("Berhasil", "E-mail anda telah terdaftar silakan klik lanjut") { _, _ ->
                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                            startActivity(intent)
                            registerViewModel.registerResponse.value?.error = false
                            finish()
                        }
                    } else {
                        // Jika terjadi error bahwa email sudah terdaftar
                        showAlert("Error", "Email sudah terdaftar")
                    }
                }
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivRegister, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val message =
            ObjectAnimator.ofFloat(binding.tvMessageRegister, View.ALPHA, 1f).setDuration(500)
        val name = ObjectAnimator.ofFloat(binding.tvName, View.ALPHA, 1f).setDuration(500)
        val nameBox = ObjectAnimator.ofFloat(binding.edtName, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(500)
        val emailBox =
            ObjectAnimator.ofFloat(binding.edtEmailLayout, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(500)
        val passwordBox =
            ObjectAnimator.ofFloat(binding.edtPasswordLayout, View.ALPHA, 1f).setDuration(500)
        val signup = ObjectAnimator.ofFloat(binding.btnSignUp, View.ALPHA, 1f).setDuration(500)


        AnimatorSet().apply {
            playSequentially(message, name, nameBox, email, emailBox, password, passwordBox, signup)
            start()
        }
    }

    private fun postText() {
        binding.apply {
            registerViewModel.register(
                edtNameText.text.toString(),
                edtEmailText.text.toString(),
                edtPasswordText.text.toString()
            )
        }
    }

    private fun showAlert(title: String, message: String, onPositiveClick: ((dialog: DialogInterface, which: Int) -> Unit)? = null) {
        AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("OK", onPositiveClick)
            create()
            show()
        }
    }

    private fun showLoading() {
        registerViewModel.isLoading.observe(this@RegisterActivity) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private fun isEditTextEmpty(editText: EditText): Boolean {
        return editText.text.toString().trim().isEmpty()
    }

    private fun hasValidationError(): Boolean {
        val nameError = binding.edtNameText.isError
        val emailError = binding.edtEmailText.isError
        val passwordError = binding.edtPasswordText.isError

        return nameError || emailError || passwordError
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }


}