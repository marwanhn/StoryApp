package com.example.storyapp.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.storyapp.R

class MyEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs), View.OnTouchListener {
    private lateinit var editTextBackground: Drawable
    private lateinit var editTextErrorBackground: Drawable
    var isError: Boolean = false

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                validateInput()
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateInput()
            }

            override fun afterTextChanged(s: Editable?) {
                validateInput()
            }
        })

        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = if (isError) {
            editTextErrorBackground
        } else {
            editTextBackground
        }
    }

    private fun init() {
        editTextBackground = ContextCompat.getDrawable(context, R.drawable.bg_edit_text)!!
        editTextErrorBackground =
            ContextCompat.getDrawable(context, R.drawable.bg_edit_text_error)!!
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        return false
    }

    private fun validateInput() {
        val validationType = getValidationType()
        val inputText = text.toString()
        var errorMessage: String? = null

        isError = when (validationType) {
            ValidationType.Name -> {
                if (inputText.isEmpty()) {
                    errorMessage = context.getString(R.string.field_error_null)
                    true
                } else {
                    false
                }
            }

            ValidationType.Email -> {
                if (inputText.isEmpty()) {
                    errorMessage = context.getString(R.string.field_error_null)
                    true
                } else if (!Patterns.EMAIL_ADDRESS.matcher(inputText).matches()) {
                    errorMessage = context.getString(R.string.email_error_validation)
                    true
                } else false
            }

            ValidationType.Password -> {
                if(inputText.isEmpty()) {
                    errorMessage = context.getString(R.string.field_error_null)
                    true
                } else if (inputText.length < 8) {
                    errorMessage = context.getString(R.string.password_error_validation)
                    true
                } else {
                    false
                }
            }

            else -> false
        }

        setErrorIf(isError, errorMessage)
    }

    private fun setErrorIf(error: Boolean, errorMessage: String?) {
        if (error) {
            setError(errorMessage, null)
        }
    }

    private fun getValidationType(): ValidationType {
        return when (this.id) {
            R.id.edt_name_text -> ValidationType.Name
            R.id.edt_email_text -> ValidationType.Email
            R.id.edt_password_text -> ValidationType.Password
            else -> ValidationType.None
        }
    }

    private enum class ValidationType {
        None,
        Name,
        Email,
        Password
    }
}
