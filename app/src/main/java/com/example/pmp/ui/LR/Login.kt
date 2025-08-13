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
import com.example.pmp.databinding.LoginBinding
import com.example.pmp.viewModel.account.LoginVM

class Login : AppCompatActivity() {

    private lateinit var binding: LoginBinding
    private val viewModel: LoginVM by viewModels()

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
    }
}
