package com.lockminds.tayari.data


data class News(
         val news_desc: String = "",
         val news_title: String = "",
         val news_category_name: String = "",
         val news_date: String = "",
         val news_image: String = "",
         val news_category: String = "",
         val news_key: String = "",
         val news_category_key: String = "",
         val news_id: String = ""
 ){
    constructor(): this(
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
    ""
    )
}
