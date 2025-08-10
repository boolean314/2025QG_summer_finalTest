package com.example.pmp.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pmp.R

class CreateProject : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_project)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val items1 = arrayOf("前端", "后台", "移动")
        val items2=arrayOf("公开","私有")
        val adapter1 = ArrayAdapter(
            this,
            R.layout.create_project_dropdown_item,
            items1
        )
        val adapter2= ArrayAdapter(
            this,
            R.layout.create_project_dropdown_item,
            items2
        )

        val dropdown1 = findViewById<AutoCompleteTextView>(R.id.create_project_type)
        val dropdown2 = findViewById<AutoCompleteTextView>(R.id.create_project_permission)
        dropdown1.setAdapter(adapter1)
        dropdown2.setAdapter(adapter2)
    }
}