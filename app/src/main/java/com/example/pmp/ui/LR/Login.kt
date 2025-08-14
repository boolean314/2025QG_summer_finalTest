package com.example.pmp.ui.LR

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import com.example.pmp.R
import com.example.pmp.databinding.LoginBinding
import com.example.pmp.viewModel.DialogFindPasswordVM
import com.example.pmp.viewModel.DialogPasswordVM
import com.example.pmp.viewModel.account.LoginVM
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class Login : AppCompatActivity() {

    private lateinit var binding: LoginBinding
    private val viewModel: LoginVM by viewModels()
    private val viewModelFindPassword : DialogFindPasswordVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.registerEntrance.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }
        binding.forgetEntrance.setOnClickListener {
            Toast.makeText(this, "马上完成", Toast.LENGTH_SHORT).show()
        }
        binding.LoginProgressButton.isIndeterminateProgressMode = true
        binding.LoginProgressButton.setOnClickListener {
            when {
                viewModel.account.value.isNullOrBlank() -> {
                    Toast.makeText(this, "请输入邮箱", Toast.LENGTH_SHORT).show()
                }
                viewModel.password.value.isNullOrBlank() -> {
                    Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    viewModel.login(this, binding.LoginProgressButton)
                }
            }
        }
        binding.forgetEntrance.setOnClickListener { showEditPasswordDialog() }
    }

    fun showEditPasswordDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_find_password, null)
        val email = dialogView.findViewById<EditText>(R.id.edit_email)
        val verifyCode = dialogView.findViewById<EditText>(R.id.edit_verify_code)
        val getVerifyCodeButton = dialogView.findViewById<Button>(R.id.GetVerifyCodeButton)
        val passwordEdit = dialogView.findViewById<EditText>(R.id.edit_password)
        val passwordAgainEdit = dialogView.findViewById<EditText>(R.id.edit_password_again)
        val ensureButton = dialogView.findViewById<Button>(R.id.ensure_button)

        viewModelFindPassword.email.observe(this as LifecycleOwner) { email ->
            getVerifyCodeButton.isEnabled = isEmailValid(email ?: "")
        }
        ensureButton.isEnabled = false

        val watcher = {
            val email = email.text.toString()
            val verifyCodeText = verifyCode.text.toString()
            val password = passwordEdit.text.toString()
            val passwordAgain = passwordAgainEdit.text.toString()
            ensureButton.isEnabled = email.isNotBlank() && verifyCodeText.isNotBlank() && password.isNotBlank() && passwordAgain.isNotBlank() && password == passwordAgain
        }
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { watcher() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        email.addTextChangedListener(textWatcher)
        verifyCode.addTextChangedListener(textWatcher)
        passwordEdit.addTextChangedListener(textWatcher)
        passwordAgainEdit.addTextChangedListener(textWatcher)

        val dialog = MaterialAlertDialogBuilder(this).setView(dialogView).create()

        getVerifyCodeButton.setOnClickListener {
            viewModelFindPassword.email.value = email.text.toString()
            viewModelFindPassword.verifyCode(this, getVerifyCodeButton)
        }

        ensureButton.setOnClickListener {
            viewModelFindPassword.email.value = email.text.toString()
            viewModelFindPassword.verifyCode.value = verifyCode.text.toString()
            viewModelFindPassword.newPassword.value = passwordEdit.text.toString()
            viewModelFindPassword.newPasswordAgain.value = passwordAgainEdit.text.toString()
            viewModelFindPassword.modify(
                this,
                onSuccess = { dialog.dismiss() },
                onError = { msg -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }
            )
        }

        adaptUI(dialog)

        dialog.show()
    }

    fun adaptUI(dialog : AlertDialog){  //Dialog适配器
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val window = dialog.window
        window?.setLayout(
            (screenWidth * 0.888888).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
