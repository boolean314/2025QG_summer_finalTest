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
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pmp.R
import com.example.pmp.data.model.GlobalData
import com.example.pmp.data.model.PersonalProject
import com.example.pmp.ui.detail.BackendDetail
import com.example.pmp.ui.detail.FrontendDetail
import com.example.pmp.ui.detail.MobileDetail
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.format.DateTimeFormatter

class PersonalProjectAdapter(private val dataList: MutableList<PersonalProject>, private val onDelete: (String) -> Unit) :
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.personal_project_item, parent, false)
        return ProjectViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = dataList[position]
        holder.projectName.text = project.name
        holder.statusText.text = if (project.isPublic) "公开" else "私有"
        val color = if (project.isPublic) R.color.stroke_green else R.color.red_deeper
        holder.statusText.setTextColor(ContextCompat.getColor(holder.itemView.context, color))
        holder.statusCard.strokeColor = ContextCompat.getColor(holder.itemView.context, color)
        holder.projectDescription.text = project.description

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formattedDate = project.createTime.format(formatter)
        holder.projectCreatedTime.text = formattedDate


        holder.detailLayout.visibility = if (expandedIndexes.contains(position)) View.VISIBLE else View.GONE

        holder.titleLayout.setOnClickListener {
            if (expandedIndexes.contains(position)) {
                expandedIndexes.remove(position)
            } else {
                expandedIndexes.add(position)
            }
            notifyItemChanged(position)
        }

        holder.enterBtn.setOnClickListener {
            Toast.makeText(holder.itemView.context, "进入 ${project.name} 项目", Toast.LENGTH_SHORT).show()
        }

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
}