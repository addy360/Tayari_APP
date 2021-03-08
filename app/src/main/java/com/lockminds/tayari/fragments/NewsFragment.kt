package com.lockminds.tayari.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.lockminds.tayari.adapter.NewsAdapter
import com.lockminds.tayari.data.News
import com.lockminds.tayari.databinding.FragmentNewsBinding
import com.lockminds.tayari.viewModels.NewsListViewModel
import com.lockminds.tayari.viewModels.NewsListViewModelFactory

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class NewsFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!
    private lateinit var linearLayoutManager: LinearLayoutManager

    val newsListViewModel by viewModels<NewsListViewModel>{
        NewsListViewModelFactory(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val newsAdapter = NewsAdapter(requireContext()) { news -> adapterOnClick(news) }

        linearLayoutManager = LinearLayoutManager(activity)
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = newsAdapter

        newsListViewModel.newsLiveData.observe(requireActivity(), {
            it?.let {
                newsAdapter.submitList(it as MutableList<News>)
            }
        })

        fetchNews()
    }

    private fun fetchNews() {
        AndroidNetworking.get("https://api.tayari.co.tz/news/get_news")
            .setTag(this)
            .setPriority(Priority.LOW)
            .build()
            .getAsObjectList(News::class.java, object : ParsedRequestListener<List<News>> {
                override fun onResponse(news: List<News>) {
                    val items: List<News> = news
                    if (items.size > 0) {
                        newsListViewModel.insertNewsList(items)
                        binding.recyclerView.bringToFront()
                    }
                }
                override fun onError(anError: ANError) {
                }
            })
    }


    private fun adapterOnClick(news: News) {
//        val intent = Intent(requireContext(), NewsDetailsActivity()::class.java)
//        intent.putExtra(General.NEWS_KEY, news.news_key)
//        startActivity(intent)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NewsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}