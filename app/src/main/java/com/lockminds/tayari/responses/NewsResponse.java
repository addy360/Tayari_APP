package com.lockminds.tayari.responses;

import com.lockminds.tayari.data.News;

import java.util.List;

public class NewsResponse {
    Boolean status;
    String message;
    List<News> news;

    public NewsResponse() {
    }

    public NewsResponse(Boolean status, String message, List<News> news) {
        this.status = status;
        this.message = message;
        this.news = news;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<News> getNews() {
        return news;
    }

    public void setNews(List<News> news) {
        this.news = news;
    }
}
