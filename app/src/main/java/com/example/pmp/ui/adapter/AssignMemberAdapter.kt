package com.example.pmp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pmp.R
import com.example.pmp.data.model.MemberListData
import de.hdodenhof.circleimageview.CircleImageView
import com.bumptech.glide.Glide

class AssignMemberAdapter(
    private var memberList: List<MemberListData>,
    private val onMemberClickListener: (MemberListData) -> Unit = {}
) : RecyclerView.Adapter<AssignMemberAdapter.MemberViewHolder>() {

    fun updateMemberList(newList: List<MemberListData>) {
        // 只显示 userRole 为 2 的成员
        memberList = newList.filter { it.userRole == 2 }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dialog_member, parent, false)
        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val member = memberList[position]
        holder.bind(member)
    }

    override fun getItemCount(): Int = memberList.size

    inner class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val memberNameTextView: TextView = itemView.findViewById(R.id.member_dialog_name)
        private val memberAvatarImageView: CircleImageView = itemView.findViewById(R.id.member_dialog_image)

        fun bind(member: MemberListData) {
            memberNameTextView.text = member.username

            // 加载头像（如果有的话，这里假设avatar是图片URL）
            if (member.avatar.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(member.avatar)
                    .into(memberAvatarImageView)
            }

            itemView.setOnClickListener {
                onMemberClickListener(member)
            }
        }
    }
}
