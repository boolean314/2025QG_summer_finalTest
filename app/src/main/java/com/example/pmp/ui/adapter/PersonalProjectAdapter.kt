package com.example.pmp.ui.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.graphics.BlurEffect
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pmp.R
import com.example.pmp.data.model.GlobalData
import com.example.pmp.data.model.PersonalProject
import com.example.pmp.ui.PersonalProjectUI
import com.example.pmp.ui.detail.BackendDetail
import com.example.pmp.ui.detail.FrontendDetail
import com.example.pmp.ui.detail.MobileDetail
import com.example.pmp.viewModel.PersonalProjectVM
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.DelicateCoroutinesApi
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class PersonalProjectAdapter(
    val dataList: MutableList<PersonalProject>,
    private val onDelete: (String) -> Unit,
    private val onExit: (String, Long) -> Unit,
    private val viewModel: PersonalProjectVM,
    private val onAuthenticate: suspend (Long, String, (Boolean) -> Unit) -> Unit
) :
    RecyclerView.Adapter<PersonalProjectAdapter.ProjectViewHolder>() {

    private val expandedIndexes = HashSet<Int>()

    class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val titleLayout: View = itemView.findViewById(R.id.titleViewCard)
        val detailLayout: View = itemView.findViewById(R.id.contentViewCard)
        val projectName: TextView = itemView.findViewById(R.id.projectName)
        val statusText: TextView = itemView.findViewById(R.id.statusText)
        val projectDescription: TextView = itemView.findViewById(R.id.projectDescription)
        val projectCreatedTime: TextView = itemView.findViewById(R.id.projectCreatedDate)
        val statusCard: MaterialCardView = itemView.findViewById(R.id.statusCard)
        val enterBtn: Button = itemView.findViewById(R.id.enterBtn)
        val deleteBtn: Button = itemView.findViewById(R.id.deleteBtn)
        val exitBtn: Button = itemView.findViewById(R.id.exitBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.personal_project_item, parent, false)
        return ProjectViewHolder(view)
    }

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = dataList[position]
        holder.projectName.text = project.name
        holder.statusText.text = if (project.isPublic) "公开" else "私有"
        val color = if (project.isPublic) R.color.stroke_green else R.color.red_deeper
        holder.statusText.setTextColor(ContextCompat.getColor(holder.itemView.context, color))
        holder.statusCard.strokeColor = ContextCompat.getColor(holder.itemView.context, color)
        holder.projectDescription.text = project.description

        val formattedDate = convertDateFormat(project.createdTime)
        holder.projectCreatedTime.text = formattedDate


        holder.detailLayout.visibility = if (expandedIndexes.contains(position)) View.VISIBLE else View.GONE

        holder.deleteBtn.visibility = View.GONE
        holder.exitBtn.visibility = View.GONE

        holder.titleLayout.setOnClickListener {
            if (expandedIndexes.contains(position)) {
                expandedIndexes.remove(position)
            } else {
                expandedIndexes.add(position)
            }
            notifyItemChanged(position)
        }

        if(expandedIndexes.contains(position)) {
            val userId = GlobalData.userInfo?.id ?: return
            val projectId = project.uuid
            kotlinx.coroutines.GlobalScope.launch {
                onAuthenticate(userId, projectId) { canDelete ->
                    if (canDelete) {
                        holder.deleteBtn.post {
                            holder.deleteBtn.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

        if (expandedIndexes.contains(position)) {
            val userId = GlobalData.userInfo?.id ?: return
            val projectId = project.uuid
            kotlinx.coroutines.GlobalScope.launch {
                val result = viewModel.authenticateBoss(projectId)
                onAuthenticate(userId, projectId) { canExit ->
                    if(result && !canExit) {
                        holder.exitBtn.post {
                            holder.exitBtn.visibility = View.VISIBLE
                        }
                    } else if (!result && !canExit) {
                        holder.exitBtn.post {
                            holder.exitBtn.visibility = View.VISIBLE
                        }
                    } else if (result) {
                        holder.exitBtn.post {
                            holder.exitBtn.visibility = View.VISIBLE
                        }
                    } else {
                        holder.exitBtn.post {
                            holder.exitBtn.visibility = View.GONE
                        }
                    }
                }
            }
        }

        //弹出删除对话框，确认删除就删掉项目
        holder.deleteBtn.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(holder.itemView.context).apply {
                setTitle("删除项目")
                setMessage("确定要删除项目吗?\n此操作一旦进行将无法撤销!")
                setCancelable(false)
                setPositiveButton("确定") { dialog, which ->
                    onDelete(project.uuid)
                }
                setNegativeButton("取消") {dialog, which ->
                }
                show()
            }
        }

        holder.enterBtn.setOnClickListener {
            val dialogView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.dialog_transfer, null)
            val frontendBtn = dialogView.findViewById<Button>(R.id.frontend_button)
            val mobileBtn = dialogView.findViewById<Button>(R.id.mobile_button)
            val backendBtn = dialogView.findViewById<Button>(R.id.backend_button)
            val dialog = MaterialAlertDialogBuilder(holder.itemView.context).setView(dialogView).create()

            val window = dialog.window
            window?.let {
                it.setDimAmount(0.7f)  // 设置背景虚化的透明度，值越小背景越模糊
                it.setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            }

            //进入前端
            frontendBtn.setOnClickListener {
                val uuid = project.uuid
                val userId = GlobalData.userInfo?.id
                val userRole = project.userRole
                val intent = Intent(holder.itemView.context, FrontendDetail::class.java)
                intent.putExtra("extra_uuid",uuid)
                intent.putExtra("extra_userId",userId)
                intent.putExtra("extra_userRole",userRole)
                holder.itemView.context.startActivity(intent)
            }

            //进入移动端
            mobileBtn.setOnClickListener {
                val uuid = project.uuid
                val userId = GlobalData.userInfo?.id
                val userRole = project.userRole
                val intent = Intent(holder.itemView.context, MobileDetail::class.java)
                intent.putExtra("extra_uuid",uuid)
                intent.putExtra("extra_userId",userId)
                intent.putExtra("extra_userRole",userRole)
                holder.itemView.context.startActivity(intent)
            }

            //进入后端
            backendBtn.setOnClickListener {
                val uuid = project.uuid
                val userId = GlobalData.userInfo?.id
                val userRole = project.userRole
                val intent = Intent(holder.itemView.context, BackendDetail::class.java)
                intent.putExtra("extra_uuid",uuid)
                intent.putExtra("extra_userId",userId)
                intent.putExtra("extra_userRole",userRole)
                holder.itemView.context.startActivity(intent)
            }

            adaptUI(dialog)

            dialog.show()
        }

        holder.exitBtn.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(holder.itemView.context).apply {
                setTitle("退出项目")
                setMessage("确定要退出项目吗?\n此操作一旦进行将无法撤销!")
                setCancelable(false)
                setPositiveButton("确定") { dialog, which ->
                    onExit(project.uuid, GlobalData.userInfo?.id!!)
                }
                setNegativeButton("取消") {dialog, which ->
                }
                show()
            }
        }

    }

    override fun getItemCount(): Int = dataList.size

    fun removeProjectByUuid(uuid: String) {
        val index = dataList.indexOfFirst { it.uuid == uuid }
        if (index != -1) {
            dataList.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun adaptUI(dialog : androidx.appcompat.app.AlertDialog){  //Dialog适配器
        val displayMetrics = dialog.context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val window = dialog.window
        window?.setLayout(
            (screenWidth * 0.888888).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    fun convertDateFormat(dateString: String): String {
        return try {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSSSSS][.SSSSS][.SSS]")
            val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val date = LocalDateTime.parse(dateString, inputFormatter)
            date.format(outputFormatter)
        } catch (e: Exception) {
            dateString
        }
    }
}