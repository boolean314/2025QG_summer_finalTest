package com.example.pmp.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pmp.R
import com.example.pmp.data.model.ListMails
import com.example.pmp.viewModel.MailsVM
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MailAdapter (
    private val dataList: MutableList<ListMails>,
    private val viewModel: MailsVM
): RecyclerView.Adapter<MailAdapter.MailViewHolder>(){
    
    class MailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val projectText: TextView = itemView.findViewById<TextView>(R.id.mail_project_text)
        val errorText: TextView = itemView.findViewById<TextView>(R.id.mail_error_text)
        val deleteBtn: ImageButton = itemView.findViewById<ImageButton>(R.id.mail_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.mail_item, parent, false)
        return MailViewHolder(view)
    }

    override fun onBindViewHolder(holder: MailViewHolder, position: Int) {
        val mail = dataList[position]
        Log.d("MailAdapter", "Binding item at position: $position")
        holder.errorText.text = mail.errorType
        holder.projectText.text = mail.projectName

        holder.deleteBtn.setOnClickListener {
            viewModel.deleteSingleMail(mail.id)
        }

        holder.itemView.setOnClickListener {
            val dialogView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.dialog_error_detail, null)
            val errorType: TextView = dialogView.findViewById(R.id.error_type)
            val errorMessage: TextView = dialogView.findViewById(R.id.error_message)
            val dialog = MaterialAlertDialogBuilder(holder.itemView.context).setView(dialogView).create()
            errorType.text = mail.errorType
            errorMessage.text = mail.errorMessage
            errorMessage.movementMethod = android.text.method.ScrollingMovementMethod()
            adaptUI(dialog)
            dialog.show()
        }
    }

    override fun getItemCount(): Int = dataList.size

    fun updateData(newData: List<ListMails>) {
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