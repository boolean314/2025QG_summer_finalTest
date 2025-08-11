package com.example.pmp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pmp.R
import com.example.pmp.data.model.Project
import com.example.pmp.ui.adapter.ProjectItemAdapter

class PublicEventFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_public_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val testProjects = listOf(
            Project(
                name = "FocusLife",
                platform = "移动",
                status = "公开",
                description = "FocusLife是一款能和智能底座联动的手机APP，用户可以在手机上设置喝水目标、设置待办事项......",
                createdDate = "2025-08-11 16:29:45",
                shareCode = "dho3c4cjsb8194",
                botUrl = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=xxxx"
            ),
            Project(
                name = "CS:GO",
                platform = "前端",
                status = "公开",
                description = "风靡全球的多人射击游戏，玩家可以在这里组队竞技。",
                createdDate = "2025-07-21 10:15:30",
                shareCode = "csgo7355608",
                botUrl = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=yyyy"
            ),
            Project(
                name = "HealthTracker",
                platform = "后台",
                status = "公开",
                description = "HealthTracker记录你的健康数据，生成可视化报告。",
                createdDate = "2025-06-05 09:00:00",
                shareCode = "health2025",
                botUrl = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=zzzz"
            )
        )

        //设置 RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.public_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = ProjectItemAdapter(testProjects)
    }

}