package com.lightone.lighthouse.kotlin.src.search

import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.lightone.lighthouse.kotlin.R
import com.lightone.lighthouse.kotlin.config.BaseFragment
import com.lightone.lighthouse.kotlin.databinding.FragmentRecentSearchBinding
import com.lightone.lighthouse.kotlin.src.search.adapter.RecentsAdapter
import com.lightone.lighthouse.kotlin.src.search.model.Recents
import com.lightone.lighthouse.kotlin.viewmodel.RecentSearchViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class RecentSearchFragment : BaseFragment<FragmentRecentSearchBinding, RecentSearchViewModel>(R.layout.fragment_recent_search) {

    override val layoutResourceId: Int
        get() = R.layout.fragment_recent_search // get() : 커스텀 접근자, 코틀린 문법

    override val viewModel: RecentSearchViewModel by viewModel()
    private val recentAdapter : RecentsAdapter by inject()

    override fun initStartView() {
        binding.recentRecycler.run {
            adapter = recentAdapter
            layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
            setHasFixedSize(true)
        }
    }

    override fun initDataBinding() {
        viewModel.getSearchList().observe(this, Observer {
            recentAdapter.clear()
            it.forEach { item ->
                recentAdapter.addItem(item)
            }
            recentAdapter.notifyDataSetChanged()
        })

    }

    override fun initAfterBinding() {
        // Recycler view item click event 처리
        recentAdapter.delteItemClickListener(object : RecentsAdapter.OnItemClickEventListener {
            override fun onItemClick(a_view: View?, a_position: Int) {
                recentAdapter.deleteItem(a_position)
            }
        })
    }

}