package com.example.pmp.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pmp.R
import com.example.pmp.data.model.Project
import com.ramotion.foldingcell.FoldingCell

class ProjectItemAdapter(private val dataList: List<Project>) :
    RecyclerView.Adapter<ProjectItemAdapter.ProjectViewHolder>() {

    private val expandedIndexes = HashSet<Int>()

    class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val titleLayout: View = itemView.findViewById(R.id.titleViewCard)
        val detailLayout: View = itemView.findViewById(R.id.contentViewCard)
        val projectName: TextView = itemView.findViewById(R.id.projectName)
        val platformText: TextView = itemView.findViewById(R.id.platformText)
        val statusText: TextView = itemView.findViewById(R.id.statusText)
        val projectDescription: TextView = itemView.findViewById(R.id.projectDescription)
        val projectCreatedDate: TextView = itemView.findViewById(R.id.projectCreatedDate)
        val shareCode: TextView = itemView.findViewById(R.id.shareCodeText)
        val botUrl: TextView = itemView.findViewById(R.id.botUrlText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.project_item, parent, false)
        return ProjectViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = dataList[position]
        holder.projectName.text = project.name
        holder.platformText.text = project.platform
        holder.statusText.text = project.status
        holder.projectDescription.text = project.description
        holder.projectCreatedDate.text = project.createdDate
        holder.shareCode.text = project.shareCode
        holder.botUrl.text = project.botUrl
        holder.detailLayout.visibility = if (expandedIndexes.contains(position)) View.VISIBLE else View.GONE

        holder.titleLayout.setOnClickListener {
            if (expandedIndexes.contains(position)) {
                expandedIndexes.remove(position)
            } else {
                expandedIndexes.add(position)
            }
            notifyItemChanged(position)
        }
        holder.botUrl.movementMethod = android.text.method.ScrollingMovementMethod.getInstance()
        holder.botUrl.setOnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        }
    }

    override fun getItemCount(): Int = dataList.size
}