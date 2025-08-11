package com.example.pmp.ui.account

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dd.CircularProgressButton
import com.example.pmp.R
import com.example.pmp.databinding.LoginBinding
import com.example.pmp.ui.Container

class LoginUI : AppCompatActivity() {

    private lateinit var binding: LoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.registerEntrance.setOnClickListener {
            startActivity(Intent(this, RegisterUI::class.java))
        }
        binding.forgetEntrance.setOnClickListener {
            Toast.makeText(this, "马上完成", Toast.LENGTH_SHORT).show()
        }
        binding.LoginProgressButton.isIndeterminateProgressMode = true
        binding.LoginProgressButton.setOnClickListener {
            when (binding.LoginProgressButton.progress) {
                //初始状态：点击后开始加载
                CircularProgressButton.IDLE_STATE_PROGRESS -> {
                    binding.LoginProgressButton.progress = CircularProgressButton.INDETERMINATE_STATE_PROGRESS
                    simulateNetworkRequest()
                }
                //成功状态：点击后重置
                CircularProgressButton.SUCCESS_STATE_PROGRESS -> {
                    binding.LoginProgressButton.progress = CircularProgressButton.IDLE_STATE_PROGRESS
                }
                //失败状态：点击后重置
                CircularProgressButton.ERROR_STATE_PROGRESS -> {
                    binding.LoginProgressButton.progress = CircularProgressButton.IDLE_STATE_PROGRESS
                }
            }
        }
    }

    private fun simulateNetworkRequest() {
        Handler(Looper.getMainLooper()).postDelayed({
            val isSuccess = true

            if (isSuccess) {
                //成功状态：设置进度为100，延迟后跳转页面
                binding.LoginProgressButton.progress = CircularProgressButton.SUCCESS_STATE_PROGRESS
                Handler(Looper.getMainLooper()).postDelayed({
                    //跳转到目标活动
                    startActivity(Intent(this, Container::class.java))
                    //跳转后重置按钮状态（可选）
                }, 1000) // 显示成功状态1秒后跳转
            } else {
                //失败状态：设置进度为-1（错误状态）
                binding.LoginProgressButton.progress = CircularProgressButton.ERROR_STATE_PROGRESS
                //失败状态3秒后自动重置（可选）
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.LoginProgressButton.progress = CircularProgressButton.IDLE_STATE_PROGRESS
                }, 2000)
            }
        }, 2000) //模拟2秒网络请求耗时
    }
}