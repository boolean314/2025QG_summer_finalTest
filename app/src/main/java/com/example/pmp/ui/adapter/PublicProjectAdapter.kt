package com.example.pmp.ui.adapter

import android.annotation.SuppressLint
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
import com.example.pmp.data.model.PublicProject
import com.google.android.material.card.MaterialCardView
import java.time.format.DateTimeFormatter

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
    }

    override fun getItemCount(): Int = dataList.size
}