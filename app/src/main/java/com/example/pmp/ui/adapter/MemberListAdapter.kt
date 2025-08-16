package com.example.pmp.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pmp.R
import com.example.pmp.data.apiService.MyApiService
import com.example.pmp.data.apiService.ServiceCreator
import com.example.pmp.data.model.ApiResponse
import com.example.pmp.data.model.GlobalData
import com.example.pmp.data.model.MemberListData
import com.example.pmp.data.model.updateRoles
import com.example.pmp.databinding.ItemMemberListBinding
import retrofit2.Call
import retrofit2.Response

class MemberListAdapter(private val memberList: List<MemberListData>) : RecyclerView.Adapter<MemberListAdapter.MemberListViewHolder>() {

    // 用于接收项目ID
    private var projectId: String = ""

    // 设置项目ID的方法
    fun setProjectId(projectId: String) {
        this.projectId = projectId
    }

    class MemberListViewHolder(
        private val binding: ItemMemberListBinding,
        private val projectId: String,
        private val allMembers: List<MemberListData>
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(member: MemberListData) {
            binding.memberListName.text = member.username
            binding.memberRole.text = when (member.userRole) {
                0 -> "老板"
                1 -> "管理员"
                else -> "成员"
            }
            binding.memberRole.setTextColor(when(member.userRole){
                0 -> ContextCompat.getColor(binding.root.context, R.color.purple)
                1 -> ContextCompat.getColor(binding.root.context, R.color.wx_green)
                else -> ContextCompat.getColor(binding.root.context, R.color.black)
            })
            binding.memberRoleLayout.strokeColor = when(member.userRole){
                0 -> ContextCompat.getColor(binding.root.context, R.color.purple)
                1 -> ContextCompat.getColor(binding.root.context, R.color.wx_green)
                else -> ContextCompat.getColor(binding.root.context, R.color.black)
            }

            Glide.with(binding.root.context)
                .load(member.avatar)
                .into(binding.memberListImage)

            binding.memberListMore.setOnClickListener {
                showPopupMenu(it, member)
            }
        }

        private fun showPopupMenu(view: View, member: MemberListData) {
            val currentUser = GlobalData.userInfo
            if (currentUser == null) {
                Toast.makeText(view.context, "无法获取当前用户信息", Toast.LENGTH_SHORT).show()
                return
            }

            val popupMenu = PopupMenu(view.context, view)
            val menu = popupMenu.menu

            // 获取当前用户的角色
            val currentUserId = currentUser.id
            val currentUserRole = getCurrentUserRole(currentUserId)

            // 根据不同用户身份显示不同菜单项
            when {
                // 如果是老板 (userRole = 0) 或 管理员 (userRole = 1)
                currentUserRole == 0 || currentUserRole == 1 -> {
                    // 检查是否是对自己操作
                    if (member.userId == currentUserId) {
                        // 对自己操作 - 不能移除自己
                        when (currentUserRole) {
                            0 -> {
                                // 老板可以降级为管理员
                                menu.add(0, 1, 0, "降为管理员")
                                // 老板也可以降级为成员（除非是最后一个管理员角色）
                                if (getAdminCount() > 1) {
                                    menu.add(0, 2, 0, "降为成员")
                                }
                            }
                            1 -> {
                                // 管理员可以降级为成员或升级为老板
                                // 检查降级为成员后是否还至少有一个管理员
                                if (getAdminCount() > 1) {
                                    menu.add(0, 2, 0, "降为成员")
                                }
                                menu.add(0, 6, 0, "提升为老板")
                            }
                        }
                    } else {
                        // 对其他用户操作
                        when (member.userRole) {
                            0 -> {
                                // 对老板操作
                                if (currentUserRole == 0) {
                                    // 只有老板可以操作其他老板
                                    menu.add(0, 1, 0, "降为管理员")
                                    // 检查降级为成员后是否还至少有一个管理员
                                    if (getAdminCount() > 1) {
                                        menu.add(0, 2, 0, "降为成员")
                                    }
                                    // 检查移出后是否还至少有一个管理员
                                    if (getAdminCount() > 1) {
                                        menu.add(0, 5, 0, "移出项目")
                                    }
                                }
                            }
                            1 -> {
                                // 对管理员操作
                                // 检查降级为成员后是否还至少有一个管理员
                                if (getAdminCount() > 1) {
                                    menu.add(0, 2, 0, "降为成员")
                                }
                                menu.add(0, 6, 0, "提升为老板")
                                // 检查移出后是否还至少有一个管理员
                                if (getAdminCount() > 1) {
                                    menu.add(0, 5, 0, "移出项目")
                                }
                            }
                            else -> {
                                // 对成员操作
                                menu.add(0, 7, 0, "提升为管理员")
                                menu.add(0, 6, 0, "提升为老板")
                                menu.add(0, 5, 0, "移出项目")
                            }
                        }
                    }
                }
            }

            // 如果菜单项为空，不显示菜单
            if (menu.size() == 0) {
                Toast.makeText(view.context, "权限不足", Toast.LENGTH_SHORT).show()
                return
            }

            popupMenu.setOnMenuItemClickListener { menuItem ->
                handleMenuClick(menuItem.itemId, member)
                true
            }

            popupMenu.show()
        }

        private fun getCurrentUserRole(currentUserId: Long): Int {
            // 在成员列表中查找当前用户的角色
            for (member in allMembers) {
                if (member.userId == currentUserId) {
                    return member.userRole
                }
            }
            return 2 // 默认为成员
        }

        // 获取管理员数量（老板+管理员）
        private fun getAdminCount(): Int {
            var adminCount = 0
            for (member in allMembers) {
                if (member.userRole == 0 || member.userRole == 1) {
                    adminCount++
                }
            }
            return adminCount
        }

        private fun handleMenuClick(menuId: Int, member: MemberListData) {
            val context = binding.root.context

            when (menuId) {
                1 -> {
                    // 降为管理员
                    Toast.makeText(context, "降为管理员: ${member.username}", Toast.LENGTH_SHORT).show()
                    updateMemberRole(projectId, member.userId, 1)
                }
                2 -> {
                    // 降为成员
                    // 检查操作后是否还至少有一个管理员
                    var adminCount = getAdminCount()
                    if (member.userRole == 0 || member.userRole == 1) { // 如果原来是管理员角色
                        adminCount-- // 降级后管理员数量减少
                    }
                    if (adminCount < 1) {
                        Toast.makeText(context, "操作后必须至少保留一个管理员", Toast.LENGTH_SHORT).show()
                        return
                    }
                    Toast.makeText(context, "降为成员: ${member.username}", Toast.LENGTH_SHORT).show()
                    updateMemberRole(projectId, member.userId, 2)
                }
                5 -> {
                    // 移出项目
                    // 检查操作后是否还至少有一个管理员
                    var adminCount = getAdminCount()
                    if (member.userRole == 0 || member.userRole == 1) { // 如果原来是管理员角色
                        adminCount-- // 移出后管理员数量减少
                    }
                    if (adminCount < 1) {
                        Toast.makeText(context, "操作后必须至少保留一个管理员", Toast.LENGTH_SHORT).show()
                        return
                    }
                    Toast.makeText(context, "移出项目: ${member.username}", Toast.LENGTH_SHORT).show()
                    removeMemberFromProject(projectId, member.userId)
                }
                6 -> {
                    // 提升为老板
                    Toast.makeText(context, "提升为老板: ${member.username}", Toast.LENGTH_SHORT).show()
                    updateMemberRole(projectId, member.userId, 0)
                }
                7 -> {
                    // 提升为管理员
                    Toast.makeText(context, "提升为管理员: ${member.username}", Toast.LENGTH_SHORT).show()
                    updateMemberRole(projectId, member.userId, 1)
                }
            }
        }

        /**
         * 更新成员角色
         * @param projectId 项目UUID
         * @param userId 被操作用户的ID
         * @param newRole 新的角色 (0:老板, 1:管理员, 2:成员)
         */
        private fun updateMemberRole(projectId: String, userId: Long, newRole: Int) {
            // 检查参数有效性
            if (projectId.isEmpty() || userId <= 0) {
                Log.e("MemberListAdapter", "参数无效: projectId=$projectId, userId=$userId")
                Toast.makeText(binding.root.context, "参数错误", Toast.LENGTH_SHORT).show()
                return
            }

            val apiService = ServiceCreator.create(MyApiService::class.java)
            apiService.updateRoles("Bearer ${GlobalData.token}", GlobalData.Rsakey, updateRoles(userId=userId, projectId=projectId, userRole=newRole)).enqueue(object: retrofit2.Callback<ApiResponse<Any>>{
                override fun onResponse(
                    call: Call<ApiResponse<Any>>,
                    response: Response<ApiResponse<Any>>
                ) {
                    Log.d("MemberListAdapter", "更新成员角色 - 响应: $response")
                    if(response.isSuccessful){
                        Toast.makeText(binding.root.context, "更新成功", Toast.LENGTH_SHORT).show()
                        // 刷新成员列表
                        refreshMemberList()
                    } else {
                        Log.e("MemberListAdapter", "更新成员角色失败 - 错误码: ${response.code()}, 信息: ${response.message()}")
                        Toast.makeText(binding.root.context, "更新失败: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
                    Log.e("MemberListAdapter", "更新成员角色网络错误: ${t.message}", t)
                    Toast.makeText(binding.root.context, "网络错误: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })

            Log.d("MemberListAdapter", "更新成员角色 - 项目ID: $projectId, 用户ID: $userId, 新角色: $newRole")
        }

        /**
         * 将成员移出项目
         * @param projectId 项目UUID
         * @param userId 被移出项目的用户ID
         */
        private fun removeMemberFromProject(projectId: String, userId: Long) {
            // 检查参数有效性
            if (projectId.isEmpty() || userId <= 0) {
                Log.e("MemberListAdapter", "参数无效: projectId=$projectId, userId=$userId")
                Toast.makeText(binding.root.context, "参数错误", Toast.LENGTH_SHORT).show()
                return
            }

            val apiService = ServiceCreator.create(MyApiService::class.java)
            apiService.deleteMember("Bearer ${GlobalData.token}",
                GlobalData.Rsakey, projectId, userId).enqueue(object : retrofit2.Callback<ApiResponse<Any>> {
                override fun onResponse(
                    call: Call<ApiResponse<Any>>,
                    response: Response<ApiResponse<Any>>
                ) {
                    Log.d("MemberListAdapter", "移出项目成员 - 响应: $response")
                    if (response.isSuccessful) {
                        Log.d("MemberListAdapter", "移出项目成员成功 - 响应: ${response.body()}")
                        Toast.makeText(binding.root.context, "移出成功", Toast.LENGTH_SHORT).show()
                        // 刷新成员列表
                        refreshMemberList()
                    } else {
                        Log.e("MemberListAdapter", "移出项目成员失败 - 错误码: ${response.code()}, 信息: ${response.message()}")
                        Toast.makeText(binding.root.context, "移出失败: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
                    Log.e("MemberListAdapter", "移出项目成员网络错误: ${t.message}", t)
                    Toast.makeText(binding.root.context, "网络错误: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })

            Log.d("MemberListAdapter", "移出项目成员 - 项目ID: $projectId, 用户ID: $userId")
        }

        /**
         * 通知Activity刷新成员列表
         */
        private fun refreshMemberList() {
            try {
                // 发送广播通知Activity刷新数据
                val intent = android.content.Intent("REFRESH_MEMBER_LIST")
                androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(binding.root.context).sendBroadcast(intent)
            } catch (e: Exception) {
                Log.e("MemberListAdapter", "发送刷新广播失败", e)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberListViewHolder {
        val binding = ItemMemberListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MemberListViewHolder(binding, projectId, memberList)
    }

    override fun onBindViewHolder(holder: MemberListViewHolder, position: Int) {
        holder.bind(memberList[position])
    }

    override fun getItemCount(): Int = memberList.size
}

