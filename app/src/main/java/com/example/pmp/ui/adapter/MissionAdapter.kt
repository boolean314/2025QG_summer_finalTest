package com.example.pmp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pmp.R
import com.example.pmp.data.model.ListMissions
import com.example.pmp.ui.adapter.PublicProjectAdapter.ProjectViewHolder
import com.example.pmp.viewModel.MissionsVM
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.w3c.dom.Text

class MissionAdapter(
    private val dataList: MutableList<ListMissions>,
    private val viewModel: MissionsVM
): RecyclerView.Adapter<MissionAdapter.MissionViewHolder>(){

    class MissionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar: de.hdodenhof.circleimageview.CircleImageView = itemView.findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.mission_avatar_image)
        val missionName: TextView = itemView.findViewById<TextView>(R.id.mission_name_text)
        val errorText: TextView = itemView.findViewById<TextView>(R.id.mission_error_text)
        val projectText: TextView = itemView.findViewById<TextView>(R.id.mission_project_text)
        val missionStatus: TextView = itemView.findViewById<TextView>(R.id.mission_status)
        val finishBtn: ImageButton = itemView.findViewById<ImageButton>(R.id.mission_finish_Btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MissionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.designate_item, parent, false)
        return MissionViewHolder(view)
    }

    override fun onBindViewHolder(holder: MissionViewHolder, position: Int) {
        val mission = dataList[position]
        Glide.with(holder.itemView.context)
            .load(mission.senderAvatar)
            .placeholder(R.drawable.coach)
            .error(R.drawable.coach)
            .into(holder.avatar)
        holder.missionName.text = mission.senderName
        holder.errorText.text = mission.errorType
        holder.projectText.text = mission.projectName
        holder.missionStatus.text = if (mission.isHandled) "已处理" else "未处理"
        val color = if (mission.isHandled) R.color.stroke_green else R.color.red_deeper
        holder.missionStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, color))
        holder.finishBtn.visibility = if(!mission.isHandled) View.VISIBLE else View.GONE

        holder.finishBtn.setOnClickListener {
            viewModel.updateHandleStatus(
                holder.itemView.context,
                mission.projectId,
                mission.platform,
                mission.errorType
            ) { success ->
                if (success) {
                    mission.isHandled = true
                    notifyItemChanged(position)
                }
            }
        }

        holder.itemView.setOnClickListener {
            val dialogView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.dialog_error_detail, null)
            val errorType: TextView = dialogView.findViewById(R.id.error_type)
            val errorMessage: TextView = dialogView.findViewById(R.id.error_message)
            val dialog = MaterialAlertDialogBuilder(holder.itemView.context).setView(dialogView).create()
            errorType.text = mission.errorType
            errorMessage.text = mission.errorMessage
            errorMessage.movementMethod = android.text.method.ScrollingMovementMethod()
            adaptUI(dialog)
            dialog.show()
        }
    }

    override fun getItemCount(): Int = dataList.size

    fun updateData(newData: List<ListMissions>) {
        dataList.clear()
        dataList.addAll(newData)
        notifyDataSetChanged()
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