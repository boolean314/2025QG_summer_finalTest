package com.example.pmp.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.pmp.data.model.Project

class AllEventFragmentVM: ViewModel() {

    private val _projects = MutableLiveData<List<Project>>()
    val projects: LiveData<List<Project>> = _projects

    fun loadProjects() {
        _projects.value = listOf(
            Project(
                projectId = "1",
                name = "FocusLife",
                platform = "移动",
                status = "公开",
                description = "FocusLife是一款能和智能底座联动的手机APP，用户可以在手机上设置喝水目标、设置待办事项......",
                createdDate = "2025-08-11 16:29:45",
                shareCode = "dho3c4cjsb8194",
                botUrl = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=xxxx"
            ),
            Project(
                projectId = "1",
                name = "TaskMaster",
                platform = "前端",
                status = "私有",
                description = "TaskMaster帮助团队高效管理任务，支持多平台同步。",
                createdDate = "2025-07-21 10:15:30",
                shareCode = "abc123xyz",
                botUrl = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=yyyy"
            ),
            Project(
                projectId = "1",
                name = "HealthTracker",
                platform = "后台",
                status = "公开",
                description = "HealthTracker记录你的健康数据，生成可视化报告。",
                createdDate = "2025-06-05 09:00:00",
                shareCode = "health2025",
                botUrl = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=zzzz"
            )
        )
    }
}