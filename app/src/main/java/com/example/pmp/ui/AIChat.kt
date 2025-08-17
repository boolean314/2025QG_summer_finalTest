package com.example.pmp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.pmp.data.model.ChatMessage
import com.example.pmp.data.model.GlobalData
import com.example.pmp.databinding.FragmentAiChatBinding
import com.example.pmp.databinding.FragmentHomepageBinding
import com.example.pmp.ui.adapter.ChatAdapter
import com.example.pmp.viewModel.AIChatVM
import com.example.pmp.viewModel.HomepageVM
import com.example.pmp.viewModel.PersonalProjectVM
import kotlin.getValue
import androidx.recyclerview.widget.LinearLayoutManager


class AIChat : Fragment(){

    private lateinit var binding: FragmentAiChatBinding
    private val viewModel: AIChatVM by viewModels()
    private val projectVM: PersonalProjectVM by viewModels()
    private var selectedProjectId: String? = null
    private val chatList = mutableListOf<ChatMessage>()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAiChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.aiChatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        chatAdapter = ChatAdapter(chatList)
        binding.aiChatRecyclerView.adapter = chatAdapter
        val userId = GlobalData.userInfo?.id
        if (userId != null) {
            projectVM.loadProjects(userId)
        } else {
            //处理 userInfo 为 null 的情况，可以显示一个错误信息或者跳转到登录页面
            Log.e("AIChat", "User info is null")
        }
            projectVM.projects.observe(viewLifecycleOwner, Observer { projects ->
            val names = projects.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, names)
            binding.createProjectPermission.setAdapter(adapter)
            binding.createProjectPermission.setOnItemClickListener { _, _, pos, _ ->
                selectedProjectId = projects[pos].uuid
            }
        })

        chatAdapter = ChatAdapter(chatList)
        binding.aiChatRecyclerView.adapter = chatAdapter

        //发送消息
        binding.sendButton.setOnClickListener {
            val msg = binding.msgInputBar.editText?.text.toString()
            val projectId = selectedProjectId ?: return@setOnClickListener
            if (msg.isNotBlank()) {
                chatList.add(ChatMessage(msg, true))
                chatAdapter.notifyItemInserted(chatList.size - 1)
                binding.aiChatRecyclerView.scrollToPosition(chatList.size - 1)
                viewModel.sendMsg(msg, projectId)
                binding.msgInputBar.editText?.setText("")
            }
        }

        //监听AI回复
        viewModel.receiveMsg.observe(viewLifecycleOwner) { aiMsg ->
            chatList.add(ChatMessage(aiMsg, false))
            chatAdapter.notifyItemInserted(chatList.size - 1)
            binding.aiChatRecyclerView.scrollToPosition(chatList.size - 1)
        }
    }
}