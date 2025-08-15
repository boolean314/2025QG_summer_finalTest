package com.example.pmp.ui.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pmp.R
import com.example.pmp.data.model.GlobalData
import com.example.pmp.data.model.PersonalProject
import com.example.pmp.data.model.PublicProject
import com.example.pmp.ui.detail.BackendDetail
import com.example.pmp.ui.detail.FrontendDetail
import com.example.pmp.ui.detail.MobileDetail
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.text.format

class PublicProjectAdapter(private val dataList: List<PublicProject>) :
    RecyclerView.Adapter<PublicProjectAdapter.ProjectViewHolder>() {

    private val expandedIndexes = HashSet<Int>()

    class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val titleLayout: View = itemView.findViewById(R.id.titleViewCardPublic)
        val detailLayout: View = itemView.findViewById(R.id.contentViewCardPublic)
        val projectName: TextView = itemView.findViewById(R.id.projectNamePublic)
        val statusText: TextView = itemView.findViewById(R.id.statusTextPublic)
        val projectDescription: TextView = itemView.findViewById(R.id.projectDescriptionPublic)
        val projectCreatedTime: TextView = itemView.findViewById(R.id.projectCreatedDatePublic)
        val statusCard: MaterialCardView = itemView.findViewById(R.id.statusCardPublic)
        val enterBtn: Button = itemView.findViewById(R.id.enterBtnPublic)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.public_project_item, parent, false)
        return ProjectViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = dataList[position]
        holder.projectName.text = project.name
        holder.statusText.text = "公开"
        val color =  R.color.stroke_green
        holder.statusText.setTextColor(ContextCompat.getColor(holder.itemView.context, color))
        holder.statusCard.strokeColor = ContextCompat.getColor(holder.itemView.context, color)
        holder.projectDescription.text = project.description

        val formattedDate = convertDateFormat(project.createdTime)
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
                val userRole = 2
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
                val userRole = 2
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
                val userRole = 2
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

    fun convertDateFormat(dateString: String): String {
        return try {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSSSSS][.SSSSS][.SSS]")
            val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val date = LocalDateTime.parse(dateString, inputFormatter)
            date.format(outputFormatter)
        } catch (e: Exception) {
            dateString //解析失败时直接返回原字符串，防止闪退
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