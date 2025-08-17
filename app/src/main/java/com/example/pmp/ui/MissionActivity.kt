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
import com.example.pmp.databinding.ActivityDesignateBinding
import com.example.pmp.ui.adapter.MissionAdapter
import com.example.pmp.viewModel.MissionsVM
import com.example.pmp.viewModel.MissionWebSocketListener
import com.example.pmp.data.model.WebSocketModel
import kotlin.text.any
import kotlin.text.orEmpty

class MissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDesignateBinding
    private lateinit var missionsVM: MissionsVM
    private lateinit var adapter: MissionAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDesignateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        missionsVM = ViewModelProvider(this)[MissionsVM::class.java]
        val webSocketModel = WebSocketModel()
        val listener = MissionWebSocketListener(missionsVM)
        webSocketModel.connect(listener,this)

        missionsVM = ViewModelProvider(this)[MissionsVM::class.java]
        adapter = MissionAdapter(mutableListOf(), missionsVM)
        binding.missionRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.missionRecyclerView.adapter = adapter

        missionsVM.missions.observe(this) { missions ->
            adapter.updateData(missions)
        }

        missionsVM.loadMissions()

        binding.missionDeleteAllButton.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("确认清空所有任务吗？")
                setMessage("清空后无法恢复，请谨慎操作！")
                setPositiveButton("确认") { _, _ ->
                    val missions = missionsVM.missions.value.orEmpty()
                    val hasUnhandled = missions.any { !it.isHandled }
                    if (hasUnhandled) {
                        Toast.makeText(context, "还有未处理的任务，不能清空！", Toast.LENGTH_SHORT).show()
                    } else {

                        missionsVM.deleteAllMissions(context)
                        adapter.updateData(emptyList())
                    }
                }
                setNegativeButton("取消", null)
            }.show()
        }
    }
}