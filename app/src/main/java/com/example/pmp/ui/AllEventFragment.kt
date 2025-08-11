package com.example.pmp.ui

import android.content.Intent
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pmp.R
import com.example.pmp.data.model.Project
import com.example.pmp.ui.adapter.ProjectItemAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList
import com.wangjie.rapidfloatingactionbutton.util.RFABShape
import com.wangjie.rapidfloatingactionbutton.util.RFABTextUtil

class AllEventFragment : Fragment(), RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener<Int> {

    private lateinit var rfaLayout: RapidFloatingActionLayout
    private lateinit var rfaButton: RapidFloatingActionButton
    private lateinit var rfabHelper: RapidFloatingActionHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_all_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rfaLayout = view.findViewById(R.id.rfaLayout)
        rfaButton = view.findViewById(R.id.rfaBtn)
        initRFAB()

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
                name = "TaskMaster",
                platform = "前端",
                status = "私有",
                description = "TaskMaster帮助团队高效管理任务，支持多平台同步。",
                createdDate = "2025-07-21 10:15:30",
                shareCode = "abc123xyz",
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
        val recyclerView = view.findViewById<RecyclerView>(R.id.all_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = ProjectItemAdapter(testProjects)
    }

    private fun initRFAB() {
        val context = requireContext()
        val content = RapidFloatingActionContentLabelList(context)
        content.setOnRapidFloatingActionContentLabelListListener(this)

        val items = listOf(
            RFACLabelItem<Int>()
                .setLabel("创建项目")
                .setResId(R.drawable.ic_action_add_circle_outline)
                .setLabelSizeSp(16)
                .setIconNormalColor(0xff00cc99.toInt())
                .setIconPressedColor(0xff00cc66.toInt())
                .setLabelBackgroundDrawable(
                    RFABShape.generateCornerShapeDrawable(
                        0xffffffff.toInt(),
                        RFABTextUtil.dip2px(context, 4f)
                    )
                ),
            RFACLabelItem<Int>()
                .setLabel("加入项目")
                .setResId(R.drawable.ic_action_arrow_forward_light)
                .setIconNormalColor(0xff00cccc.toInt())
                .setIconPressedColor(0xff0099cc.toInt())
                .setLabelSizeSp(16)
                .setLabelBackgroundDrawable(
                    RFABShape.generateCornerShapeDrawable(
                        0xffffffff.toInt(),
                        RFABTextUtil.dip2px(context, 4f)
                    )
                )
        )
        content.setItems(items)
            .setIconShadowRadius(RFABTextUtil.dip2px(context, 5f))
            .setIconShadowColor(0xff888888.toInt())
            .setIconShadowDy(RFABTextUtil.dip2px(context, 1f))

        rfabHelper = RapidFloatingActionHelper(
            context,
            rfaLayout,
            rfaButton,
            content
        ).build()
    }

    override fun onRFACItemLabelClick(position: Int, item: RFACLabelItem<Int>) {
        when(position) {
            0 -> {
                startActivity(Intent(context, CreateProject::class.java))
            }
            1 -> {
                val joinDialog = layoutInflater.inflate(R.layout.dialog_join_project, null)
                MaterialAlertDialogBuilder(this.requireContext())
                    .setView(joinDialog)
                    .create()
                    .show()
            }
        }
        rfabHelper.collapseContent()
    }

    override fun onRFACItemIconClick(position: Int, item: RFACLabelItem<Int>) {
        when(position) {
            0 -> {
                startActivity(Intent(context, CreateProject::class.java))
            }
            1 -> {
                val joinDialog = layoutInflater.inflate(R.layout.dialog_join_project, null)
                MaterialAlertDialogBuilder(this.requireContext())
                    .setView(joinDialog)
                    .create()
                    .show()
            }
        }
        rfabHelper.collapseContent()
    }

}