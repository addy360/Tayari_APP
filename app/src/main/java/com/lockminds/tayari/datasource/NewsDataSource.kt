package com.lockminds.tayari.datasource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lockminds.tayari.data.News
import com.lockminds.tayari.data.newsList


/* Handles operations on flowersLiveData and holds details about it. */
class NewsDataSource() {
    private val initialNewsList = newsList()
    private val newsLiveData = MutableLiveData(initialNewsList)

    /* Adds news to liveData and posts value. */
    fun addNews(news: News) {
        val currentList = newsLiveData.value
        if (currentList == null) {
            newsLiveData.postValue(listOf(news))
        } else {
            val updatedList = currentList.toMutableList()
            updatedList.add(0, news)
            newsLiveData.postValue(updatedList)
        }
    }

    /* Adds news to liveData and posts value. */
    fun addNewsList(news: List<News>) {
        newsLiveData.postValue(news)
    }

    /* Removes news from liveData and posts value. */
    fun removeNews(news: News) {
        val currentList = newsLiveData.value
        if (currentList != null) {
            val updatedList = currentList.toMutableList()
            updatedList.remove(news)
            newsLiveData.postValue(updatedList)
        }
    }

    /* Returns flower given an ID. */
    fun getNewsForKey(key: String): News? {
        newsLiveData.value?.let { flowers ->
            return flowers.firstOrNull{ it.news_key == key}
        }
        return null
    }


    fun getNewsList(): LiveData<List<News>> {
        return newsLiveData
    }


    companion object {
        private var INSTANCE: NewsDataSource? = null

        fun getDataSource(): NewsDataSource {
            return synchronized(NewsDataSource::class) {
                val newInstance = INSTANCE ?: NewsDataSource()
                INSTANCE = newInstance
                newInstance
            }
        }
    }
}