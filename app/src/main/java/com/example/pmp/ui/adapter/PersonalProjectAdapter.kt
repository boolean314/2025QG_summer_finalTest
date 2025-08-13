package com.example.pmp.ui.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pmp.R
import com.example.pmp.data.model.PersonalProject
import com.google.android.material.card.MaterialCardView
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

    }

    override fun getItemCount(): Int = dataList.size

    fun removeProjectByUuid(uuid: String) {
        val index = dataList.indexOfFirst { it.uuid == uuid }
        if (index != -1) {
            dataList.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}