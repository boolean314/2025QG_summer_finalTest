package com.example.pmp.ui.LR

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pmp.R
import com.example.pmp.databinding.RegisterBinding
import com.example.pmp.viewModel.account.RegisterVM

class Register : AppCompatActivity() {

    private lateinit var binding: RegisterBinding
    private val viewModel: RegisterVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = RegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.GetVerifyCodeButton.isEnabled = false
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.loginEntrance.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }
        binding.RegisterProgressButton.isIndeterminateProgressMode = true
        binding.GetVerifyCodeButton.setOnClickListener {
            viewModel.verifyCode(this, binding.GetVerifyCodeButton)
        }
        viewModel.email.observe(this) { email ->
            binding.GetVerifyCodeButton.isEnabled = isEmailValid(email ?: "")
        }
        binding.RegisterProgressButton.setOnClickListener {
            when {
                viewModel.email.value.isNullOrBlank() -> {
                    Toast.makeText(this, "请输入邮箱", Toast.LENGTH_SHORT).show()
                }
                viewModel.verifyCode.value.isNullOrBlank() -> {
                    Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show()
                }
                viewModel.password.value.isNullOrBlank() -> {
                    Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show()
                }
                viewModel.phone.value.isNullOrBlank() -> {
                    Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    viewModel.register(this, binding.RegisterProgressButton)
                }
            }
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}