package com.example.pmp.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.pmp.R
import com.example.pmp.databinding.ActivityCreateProjectBinding
import com.example.pmp.viewModel.CreateProjectVM

class CreateProject : AppCompatActivity() {
    private lateinit var binding: ActivityCreateProjectBinding
    private lateinit var createProjectVM: CreateProjectVM
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
       binding=DataBindingUtil.setContentView(this,R.layout.activity_create_project)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        createProjectVM=ViewModelProvider(this).get(CreateProjectVM::class.java)
        binding.createProjectVM=createProjectVM
        binding.lifecycleOwner=this
        val items1 = arrayOf("frontend", "backend", "mobile")
        val items2=arrayOf("公开","私有")

        val adapter2= ArrayAdapter(
            this,
            R.layout.create_project_dropdown_item,
            items2
        )

        val dropdown2 = findViewById<AutoCompleteTextView>(R.id.create_project_permission)
        dropdown2.setAdapter(adapter2)
    }
}