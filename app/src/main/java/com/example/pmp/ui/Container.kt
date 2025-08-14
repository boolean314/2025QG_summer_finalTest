package com.example.pmp.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.dxtt.coolmenu.CoolMenuFrameLayout
import com.example.pmp.R

class Container : AppCompatActivity(){

    private lateinit var coolMenuFrameLayout: CoolMenuFrameLayout
    private val fragments = listOf(
        PublicEventUI(),
        HomepageFragment(),
        PersonalProjectUI(),


    )
    private val titles = listOf("公开项目", "个人主页", "所有项目")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.container)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
        insets
    }


        coolMenuFrameLayout = findViewById(R.id.rl_container)
        coolMenuFrameLayout.setTitles(titles) // 设置标题
        // 可选：设置菜单图标
        // coolMenuFrameLayout.setMenuIcon(R.drawable.menu)
        val adapter = object : FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            override fun getCount(): Int = fragments.size
            override fun getItem(position: Int): Fragment = fragments[position]
        }
        coolMenuFrameLayout.setAdapter(adapter)


    }

}