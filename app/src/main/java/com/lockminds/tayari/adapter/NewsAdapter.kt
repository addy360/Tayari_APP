package com.lockminds.tayari.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lockminds.tayari.R
import com.lockminds.tayari.Tools
import com.lockminds.tayari.data.News

class NewsAdapter(context: Context,private val onClick: (News) -> Unit) :
        ListAdapter<News, NewsAdapter.NewsViewHolder>(NewsDiffCallback) {
    private val context: Context? = context
    /* ViewHolder for News, takes in the inflated view and the onClick behavior. */
    class NewsViewHolder(itemView: View, val onClick: (News) -> Unit) :
            RecyclerView.ViewHolder(itemView) {
        private val newsTitle: TextView = itemView.findViewById(R.id.news_title)
        private val newsCategory: TextView = itemView.findViewById(R.id.news_category)
        private val newsDate: TextView = itemView.findViewById(R.id.news_date)
        private val newsImage: ImageView = itemView.findViewById(R.id.news_image)
        private var currentNews: News? = null

        init {
            itemView.setOnClickListener {
                currentNews?.let {
                    onClick(it)
                }
            }
        }

        /* Bind news name and image. */
        fun bind(news: News, context: Context?) {
            currentNews = news
            newsTitle.text = news.news_title
            newsCategory.text = news.news_category_name
            newsDate.text = news.news_date
            Tools.displayImageNews(context, newsImage, news.news_image)
        }


    }

    /* Creates and inflates view and return FlowerViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view, onClick)
    }


    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = getItem(position)
        holder.bind(news,context)
    }
}

object NewsDiffCallback : DiffUtil.ItemCallback<News>() {
    override fun areItemsTheSame(oldItem: News, newItem: News): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: News, newItem: News): Boolean {
        return oldItem.news_key == newItem.news_key
    }
}