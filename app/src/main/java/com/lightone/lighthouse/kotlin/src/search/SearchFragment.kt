package com.lightone.lighthouse.kotlin.src.search

import android.app.Application
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.lightone.lighthouse.kotlin.R
import com.lightone.lighthouse.kotlin.config.BaseFragment
import com.lightone.lighthouse.kotlin.databinding.FragmentSearchBinding
import com.lightone.lighthouse.kotlin.src.search.adapter.SearchViewpagerFragmentAdapter
import com.lightone.lighthouse.kotlin.viewmodel.SearchViewModel
import android.text.Editable

import android.text.TextWatcher
import android.util.Log

import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.lightone.lighthouse.kotlin.Database.model.UserSearch
import com.lightone.lighthouse.kotlin.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class SearchFragment : BaseFragment<FragmentSearchBinding, SearchViewModel>(R.layout.fragment_search) {

    override val layoutResourceId: Int
        get() = R.layout.fragment_search // get() : 커스텀 접근자, 코틀린 문법

    override val viewModel: SearchViewModel by sharedViewModel()

    var recent = ""

    lateinit var viewpagerFragmentAdapter: SearchViewpagerFragmentAdapter
    lateinit var navController: NavController

    override fun initStartView() {
        navController = Navigation.findNavController(requireView())

        val viewPager: ViewPager2 = binding.viewPager
        val tabLayout: TabLayout = binding.tabLayout

        viewpagerFragmentAdapter = SearchViewpagerFragmentAdapter(this)
        viewPager.adapter = viewpagerFragmentAdapter

        val tabTitles = listOf("최근 검색", "태그 검색")
        // 2. TabLayout과 ViewPager2를 연결하고, TabItem의 메뉴명을 설정한다.
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        viewModel._search.postValue("")
    }

    override fun initDataBinding() {
        viewModel.insertsuccessResponse.observe(viewLifecycleOwner, Observer {
            if(it){
                viewModel.search(recent)
//                viewModel.search(recent)
            }
        })
    }

    override fun initAfterBinding() {
        binding.searchTxt.addTextChangedListener(object : TextWatcher {
            private var searchFor = ""
            var job: Job? = null

            override fun afterTextChanged(s: Editable?) = Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val searchText = s.toString().trim()
                if(s.isNotEmpty()){
                    binding.searchCloseBtn.visibility = View.VISIBLE
                }
                else{
                    binding.searchCloseBtn.visibility = View.GONE
                    viewModel._search.postValue("")
                }
                if (searchText == searchFor)
                    return

                job?.cancel()
                searchFor = searchText
                job = lifecycleScope.launch {
                    delay(500)  //debounce timeOut
                    if (searchText != searchFor)
                        return@launch

                    viewModel.search(searchFor)
                }
            }
        })

        binding.searchTxt.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                var search = binding.searchTxt.text.toString()
                if(search != null){
                    recent = search
                    val request = UserSearch(search)
                    viewModel.insertSearch(request)
                    return@OnEditorActionListener true
                }
            }
            false
        })

        binding.searchCloseBtn.setOnClickListener {
            binding.searchTxt.text = null
        }
    }

}