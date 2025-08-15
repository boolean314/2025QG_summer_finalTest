package com.example.pmp.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.pmp.R
import com.example.pmp.data.model.GlobalData
import com.example.pmp.databinding.FragmentHomepageBinding
import com.example.pmp.ui.LR.Login
import com.example.pmp.viewModel.HomepageVM
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LifecycleOwner
import com.example.pmp.viewModel.DialogPasswordVM
import com.example.pmp.viewModel.DialogUsernameVM
import java.io.File
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide

class HomepageFragment : Fragment() {

    private lateinit var binding: FragmentHomepageBinding
    private val viewModel: HomepageVM by viewModels()
    private val viewModelUsername : DialogUsernameVM by viewModels()
    private val viewModelPassword : DialogPasswordVM by viewModels()
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val file = uriToFile(it)
            viewModel.uploadAvatar(file)
        }
    }

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
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.userAvatar.value = GlobalData.userInfo?.avatar
        viewModel.userAvatar.value = GlobalData.userInfo?.avatar
        Glide.with(this).load(GlobalData.userInfo?.avatar).placeholder(R.drawable.coach).error(R.drawable.coach).into(binding.iconImage)
        val rawUsername = GlobalData.userInfo?.username?: "未知用户"
        val displayUsername = rawUsername.removePrefix("用户：")
        viewModel.username.value = displayUsername
        binding.logoutButton.setOnClickListener {
            val intent = Intent(requireContext(), Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }

        fun showEditUsernameDialog() {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_username, null)
            val usernameEdit = dialogView.findViewById<EditText>(R.id.edit_account)
            val ensureButton = dialogView.findViewById<Button>(R.id.ensure_button)

            val rawUsername = GlobalData.userInfo?.username?: "未知用户"
            val displayUsername = rawUsername.removePrefix("用户：")
            usernameEdit.setText(displayUsername)
            ensureButton.isEnabled = false

            val watcher = {
                val username = usernameEdit.text.toString()
                ensureButton.isEnabled = username.isNotBlank()
            }
            val textWatcher = object : TextWatcher {
                override fun afterTextChanged(s: Editable?) { watcher() }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            }
            usernameEdit.addTextChangedListener(textWatcher)

            val dialog = MaterialAlertDialogBuilder(requireContext()).setView(dialogView).create()

            ensureButton.setOnClickListener {
                viewModelUsername.newUsername.value = usernameEdit.text.toString()
                viewModelUsername.modify(
                    requireContext(),
                    onSuccess = {
                        viewModel.username.value = viewModelUsername.newUsername.value ?: ""
                        dialog.dismiss()
                    },
                    onError = { msg -> Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show() }
                )
            }

            adaptUI(dialog)

            dialog.show()
        }

        fun showEditPasswordDialog() {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_password, null)
            val email = dialogView.findViewById<EditText>(R.id.edit_email)
            val verifyCode = dialogView.findViewById<EditText>(R.id.edit_verify_code)
            val getVerifyCodeButton = dialogView.findViewById<Button>(R.id.GetVerifyCodeButton)
            val passwordEdit = dialogView.findViewById<EditText>(R.id.edit_password)
            val passwordAgainEdit = dialogView.findViewById<EditText>(R.id.edit_password_again)
            val ensureButton = dialogView.findViewById<Button>(R.id.ensure_button)

            viewModelPassword.email.observe(requireContext() as LifecycleOwner) { email ->
                getVerifyCodeButton.isEnabled = isEmailValid(email ?: "")
            }
            ensureButton.isEnabled = false

            val watcher = {
                val email = email.text.toString()
                val verifyCodeText = verifyCode.text.toString()
                val password = passwordEdit.text.toString()
                val passwordAgain = passwordAgainEdit.text.toString()
                ensureButton.isEnabled = email.isNotBlank() && verifyCodeText.isNotBlank() && password.isNotBlank() && passwordAgain.isNotBlank() && password == passwordAgain
            }
            val textWatcher = object : TextWatcher {
                override fun afterTextChanged(s: Editable?) { watcher() }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            }
            email.addTextChangedListener(textWatcher)
            verifyCode.addTextChangedListener(textWatcher)
            passwordEdit.addTextChangedListener(textWatcher)
            passwordAgainEdit.addTextChangedListener(textWatcher)

            val dialog = MaterialAlertDialogBuilder(requireContext()).setView(dialogView).create()

            getVerifyCodeButton.setOnClickListener {
                viewModelPassword.email.value = email.text.toString()
                viewModelPassword.verifyCode(requireContext(), getVerifyCodeButton)
            }

            ensureButton.setOnClickListener {
                viewModelPassword.email.value = email.text.toString()
                viewModelPassword.verifyCode.value = verifyCode.text.toString()
                viewModelPassword.newPassword.value = passwordEdit.text.toString()
                viewModelPassword.newPasswordAgain.value = passwordAgainEdit.text.toString()
                viewModelPassword.modify(
                    requireContext(),
                    onSuccess = { dialog.dismiss() },
                    onError = { msg -> Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show() }
                )
            }

            adaptUI(dialog)

            dialog.show()
        }

        binding.avatarEdit.setOnClickListener {
            AlertDialog.Builder(requireContext()).apply {
                setTitle("权限申请")
                setMessage("允许该应用访问本地相册吗?")
                setCancelable(false)
                setPositiveButton("允许") { dialog, which ->
                    pickAvatar()
                }
                setNegativeButton("拒绝") {dialog, which ->
                }
                show()
            }
        }

        binding.accountEdit.setOnClickListener { showEditUsernameDialog() }
        binding.passwordEdit.setOnClickListener { showEditPasswordDialog() }
    }

    fun adaptUI(dialog : AlertDialog){  //Dialog适配器
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val window = dialog.window
        window?.setLayout(
            (screenWidth * 0.888888).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun uriToFile(uri: Uri): File {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("avatar", ".jpg", requireContext().cacheDir)
        inputStream?.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }

    fun pickAvatar() {
        pickImageLauncher.launch("image/*")
    }
}