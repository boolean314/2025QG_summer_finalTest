package com.example.pmp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.dxtt.coolmenu.CoolMenuFrameLayout
import com.example.pmp.R

class Container : AppCompatActivity(){

    private lateinit var coolMenuFrameLayout: CoolMenuFrameLayout
    private val fragments = listOf(
        PublicEventFragment(),
        HomepageFragment(),
        AllEventFragment(),
    )
    private val titles = listOf("公开项目", "个人主页", "所有项目")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.container)
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