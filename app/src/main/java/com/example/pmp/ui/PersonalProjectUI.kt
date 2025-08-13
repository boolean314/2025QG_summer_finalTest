package com.example.pmp.ui

import android.content.Intent
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pmp.R
import com.example.pmp.data.model.GlobalData
import com.example.pmp.ui.adapter.PersonalProjectAdapter
import com.example.pmp.viewModel.PersonalProjectVM
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList
import com.wangjie.rapidfloatingactionbutton.util.RFABShape
import com.wangjie.rapidfloatingactionbutton.util.RFABTextUtil

class PersonalProjectUI : Fragment(), RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener<Int> {

    private lateinit var rfaLayout: RapidFloatingActionLayout
    private lateinit var rfaButton: RapidFloatingActionButton
    private lateinit var rfabHelper: RapidFloatingActionHelper
    private val viewModel: PersonalProjectVM by viewModels()

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
        val userId = GlobalData.userInfo?.id?.toLong() ?: return
        viewModel.loadProjects(userId)
        viewModel.projects.observe(viewLifecycleOwner) { projects ->
            val adapter = PersonalProjectAdapter(projects.toMutableList()) { uuid ->
                viewModel.deleteProject(uuid) { success ->
                    if (success) {
                        Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            recyclerView.adapter = adapter
        }

        val searchEditText = view.findViewById<TextInputLayout>(R.id.search_bar).editText
        val searchButton = view.findViewById<android.widget.ImageButton>(R.id.search_button)

        searchEditText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    viewModel.filterProjectsByName("")
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        searchButton.setOnClickListener {
            val query = searchEditText?.text?.toString() ?: ""
            viewModel.filterProjectsByName(query)
        }
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