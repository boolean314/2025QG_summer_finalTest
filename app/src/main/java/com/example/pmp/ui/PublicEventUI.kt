package com.example.pmp.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pmp.R
import com.example.pmp.data.model.GlobalData
import com.example.pmp.ui.adapter.PersonalProjectAdapter
import com.example.pmp.ui.adapter.PublicProjectAdapter
import com.example.pmp.viewModel.PublicProjectVM
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import kotlin.concurrent.thread

class PublicEventUI : Fragment() {

    private val viewModel : PublicProjectVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_public_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.public_recycler_view)
        val swipeRefresh = view.findViewById<androidx.swiperefreshlayout.widget.SwipeRefreshLayout>(R.id.swipeRefresh)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val userId = GlobalData.userInfo?.id?.toLong() ?: return
        viewModel.loadProjects(userId)

        swipeRefresh.setColorSchemeResources(R.color.qq_blue)
        swipeRefresh.setOnRefreshListener {
            refreshProject(recyclerView.adapter as PublicProjectAdapter, swipeRefresh)
        }

        viewModel.projects.observe(viewLifecycleOwner) { projects ->
            recyclerView.adapter = PublicProjectAdapter(projects)
        }

        val searchEditText = view.findViewById<TextInputLayout>(R.id.search_bar_public).editText
        val searchButton = view.findViewById<android.widget.ImageButton>(R.id.search_button_public)

        searchEditText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    viewModel.filterProjectsByName("")
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        searchButton.setOnClickListener {
            val query = searchEditText?.text?.toString() ?: ""
            viewModel.filterProjectsByName(query)
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshProject(adapter: PublicProjectAdapter, swipeRefresh: androidx.swiperefreshlayout.widget.SwipeRefreshLayout) {
        CoroutineScope(Dispatchers.IO).launch {
            delay(2000)
            val userId = GlobalData.userInfo?.id ?: return@launch
            viewModel.loadProjects(userId)
            requireActivity().runOnUiThread{
                adapter.notifyDataSetChanged()
                swipeRefresh.isRefreshing = false
            }
        }
    }
}