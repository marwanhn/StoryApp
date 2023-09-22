package com.example.storyapp.view.register

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
import androidx.appcompat.app.AlertDialog
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.view.login.LoginActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)



        setupView()
        setupAction()
        playAnimation()
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

    private fun setupAction() {
        binding.btnSignUp.setOnClickListener {
            if (isEditTextEmpty(binding.edtNameText) || isEditTextEmpty(binding.edtEmailText) || isEditTextEmpty(binding.edtPasswordText)) {
                // Validasi jika EditText kosong
                AlertDialog.Builder(this).apply {
                    setTitle("Error")
                    setMessage("Semua kolom harus diisi")
                    setPositiveButton("OK", null)
                    create()
                    show()
                }
            } else if(hasValidationError()) {
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
                AlertDialog.Builder(this).apply {
                    setTitle("Yeah!")
                    setMessage("Akun anda telah berhasil dibuat.")
                    setPositiveButton("Lanjut") { _, _ ->
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    }
                    create()
                    show()
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

        val message = ObjectAnimator.ofFloat(binding.tvMessageRegister, View.ALPHA, 1f).setDuration(500)
        val name = ObjectAnimator.ofFloat(binding.tvName, View.ALPHA, 1f).setDuration(500)
        val nameBox = ObjectAnimator.ofFloat(binding.edtName, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(500)
        val emailBox = ObjectAnimator.ofFloat(binding.edtEmailLayout,View.ALPHA,1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.tvPassword,View.ALPHA,1f).setDuration(500)
        val passwordBox = ObjectAnimator.ofFloat(binding.edtPasswordLayout,View.ALPHA,1f).setDuration(500)
        val signup = ObjectAnimator.ofFloat(binding.btnSignUp, View.ALPHA, 1f).setDuration(500)


        AnimatorSet().apply {
            playSequentially(message, name, nameBox, email, emailBox, password, passwordBox, signup)
            start()
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


}