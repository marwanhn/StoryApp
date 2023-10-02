package com.example.storyapp.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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
import com.example.storyapp.data.pref.UserModel
import com.example.storyapp.view.main.MainActivity
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.utils.ViewModelFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var factory: ViewModelFactory
    private val loginViewModel: LoginViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
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
        supportActionBar?.hide()
    }

    private fun setupViewModel() {
        factory = ViewModelFactory.getInstance(this)
    }

    private fun setupAction() {
        binding.btnSignIn.setOnClickListener {
            val email = binding.edtEmailText.text.toString()
            if (isEditTextEmpty(binding.edtEmailText) || isEditTextEmpty(binding.edtPasswordText)) {
                // Validasi jika EditText kosong
                AlertDialog.Builder(this).apply {
                    setTitle("Error")
                    setMessage("Semua kolom harus diisi")
                    setPositiveButton("OK", null)
                    create()
                    show()
                }
            } else if (hasValidationError()) {
                // Validasi jika EditText masih memiliki Error
                AlertDialog.Builder(this).apply {
                    setTitle("Error")
                    setMessage("Semua kolom harus diisi dengan benar.")
                    setPositiveButton("OK", null)
                    create()
                    show()
                }
            } else {
                // Semua EditText sudah diisi, lanjutkan ke aktivitas selanjutnya
                showLoading()
                postText()
                showAlert()
                loginViewModel.login()
                loginViewModel.loginResponse.observe(this@LoginActivity) { response ->
                    if (!response.error) {
                        AlertDialog.Builder(this).apply {
                            setTitle("Berhasil")
                            setMessage("Anda telah login dengan $email, Silakan klik lanjut")
                            setPositiveButton("Lanjut") { _, _ ->
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                                loginViewModel.loginResponse.value?.error = false
                                finish()
                            }
                            create()
                            show()
                        }
                    }
                }
            }

        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.tvTitleLogin, View.ALPHA, 1f).setDuration(500)
        val message =
            ObjectAnimator.ofFloat(binding.tvMessageLogin, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(500)
        val emailBox =
            ObjectAnimator.ofFloat(binding.edtEmailLayout, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(500)
        val passwordBox =
            ObjectAnimator.ofFloat(binding.edtPasswordLayout, View.ALPHA, 1f).setDuration(500)
        val signin = ObjectAnimator.ofFloat(binding.btnSignIn, View.ALPHA, 1f).setDuration(500)


        AnimatorSet().apply {
            playSequentially(title, message, email, emailBox, password, passwordBox, signin)
            start()
        }
    }

    private fun showLoading() {
        loginViewModel.isLoading.observe(this@LoginActivity) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private fun showAlert(){
        loginViewModel.toastText.observe(this@LoginActivity) {
            AlertDialog.Builder(this).apply {
                setTitle("Error")
                setMessage("User not found")
                setPositiveButton("OK", null)
                create()
                show()
            }
        }

    }

    private fun postText() {
        binding.apply {
            loginViewModel.getLogin(
                edtEmailText.text.toString(),
                edtPasswordText.text.toString()
            )
        }

        loginViewModel.loginResponse.observe(this@LoginActivity) { response ->
                saveSession(
                    UserModel(
                        response.loginResult?.name.toString(),
                        AUTH_KEY + (response.loginResult?.token.toString()),
                        true,
                    )
                )
        }
    }

    private fun saveSession(user: UserModel) {
        loginViewModel.saveSession(user)
    }

    private fun isEditTextEmpty(editText: EditText): Boolean {
        return editText.text.toString().trim().isEmpty()
    }

    companion object {
        private const val AUTH_KEY = "Bearer "
    }

    private fun hasValidationError(): Boolean {
        val emailError = binding.edtEmailText.isError
        val passwordError = binding.edtPasswordText.isError

        return emailError || passwordError
    }
}