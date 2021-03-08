package com.lockminds.tayari.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lockminds.tayari.data.News
import com.lockminds.tayari.datasource.NewsDataSource


class NewsListViewModel(val newsDataSource: NewsDataSource) : ViewModel() {

    val newsLiveData = newsDataSource.getNewsList()

    /* If the name and description are present, create new Service and add it to the datasource */
    fun insertNews(news: News) {
         newsDataSource.addNews(news)
    }

    fun insertNewsList(news: List<News>) {
        newsDataSource.addNewsList(news)
    }

}

class NewsListViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewsListViewModel(
                    newsDataSource = NewsDataSource.getDataSource()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}