package com.example.pmp.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.pmp.R
import com.example.pmp.databinding.FragmentHomepageBinding
import com.example.pmp.ui.LR.LoginUI
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HomepageFragment : Fragment() {

    private lateinit var binding: FragmentHomepageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomepageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val accountEditBtn: ImageButton = view.findViewById(R.id.account_edit)
        val passwordEditBtn: ImageButton = view.findViewById(R.id.password_edit)

        binding.logoutButton.setOnClickListener {
            val intent = Intent(requireContext(), LoginUI::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }

        fun showEditDialog() {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_ap, null)
            val dialog = MaterialAlertDialogBuilder(requireContext()).setView(dialogView).create()
            dialogView.findViewById<View>(R.id.ensure_button).setOnClickListener {
                //处理输入内容
                dialog.dismiss()
            }
            dialog.show()
        }

        accountEditBtn.setOnClickListener { showEditDialog() }
        passwordEditBtn.setOnClickListener { showEditDialog() }
    }
}