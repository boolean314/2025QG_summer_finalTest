package com.example.pmp.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pmp.R
import com.example.pmp.data.model.WebSocketModel
import com.example.pmp.databinding.ActivityMailBinding
import com.example.pmp.ui.adapter.MailAdapter
import com.example.pmp.ui.adapter.MissionAdapter
import com.example.pmp.viewModel.MailsVM
import com.example.pmp.viewModel.MailsWebSocketListener
import com.example.pmp.viewModel.MissionWebSocketListener
import com.example.pmp.viewModel.MissionsVM
import kotlin.collections.any
import kotlin.collections.orEmpty

class SystemMail : AppCompatActivity() {

    private lateinit var binding: ActivityMailBinding
    private lateinit var mailsVM: MailsVM
    private lateinit var adapter: MailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mailsVM = ViewModelProvider(this)[MailsVM::class.java]
        val webSocketModel = WebSocketModel()
        val listener = MailsWebSocketListener(mailsVM)
        webSocketModel.connect(listener,this)

        mailsVM = ViewModelProvider(this)[MailsVM::class.java]
        adapter = MailAdapter(mutableListOf(), mailsVM)
        binding.mailRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.mailRecyclerView.adapter = adapter

        mailsVM.mails.observe(this) { mails ->
            adapter.updateData(mails)
        }

        mailsVM.loadMails()

        binding.mailDeleteAllButton.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("确认清空所有通知吗？")
                setMessage("清空后无法恢复，请谨慎操作！")
                setPositiveButton("确认") { _, _ ->
                        mailsVM.deleteAllMails(context)
                        adapter.updateData(emptyList())
                }
                setNegativeButton("取消", null)
            }.show()
        }
    }
}