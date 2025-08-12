package com.example.pmp.ui

import android.content.Intent
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pmp.R
import com.example.pmp.data.model.Project
import com.example.pmp.ui.adapter.ProjectItemAdapter
import com.example.pmp.viewModel.AllEventFragmentVM
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
    private val viewModel: AllEventFragmentVM by viewModels()

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

        val recyclerView = view.findViewById<RecyclerView>(R.id.all_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewModel.projects.observe(viewLifecycleOwner) { projects ->
            recyclerView.adapter = ProjectItemAdapter(projects)
        }
        viewModel.loadProjects()
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