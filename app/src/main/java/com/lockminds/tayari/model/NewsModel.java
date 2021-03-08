package com.lockminds.tayari.model;

import android.graphics.drawable.Drawable;

public class NewsModel {

    public int image;
    public Drawable imageDrw;
    public String name;
    public String brief;
    public Integer counter = null;
    public String news_key,news_date,news_desc,news_title,news_location,news_category_name,news_image;

    public NewsModel() {
    }

    public NewsModel(String news_key, String news_date, String news_desc, String news_title, String news_location, String news_category_name, String news_image) {
        this.news_key = news_key;
        this.news_date = news_date;
        this.news_desc = news_desc;
        this.news_title = news_title;
        this.news_location = news_location;
        this.news_category_name = news_category_name;
        this.news_image = news_image;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public Drawable getImageDrw() {
        return imageDrw;
    }

    public void setImageDrw(Drawable imageDrw) {
        this.imageDrw = imageDrw;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    public String getNews_key() {
        return news_key;
    }

    public void setNews_key(String news_key) {
        this.news_key = news_key;
    }

    public String getNews_date() {
        return news_date;
    }

    public void setNews_date(String news_date) {
        this.news_date = news_date;
    }

    public String getNews_desc() {
        return news_desc;
    }

    public void setNews_desc(String news_desc) {
        this.news_desc = news_desc;
    }

    public String getNews_title() {
        return news_title;
    }

    public void setNews_title(String news_title) {
        this.news_title = news_title;
    }

    public String getNews_location() {
        return news_location;
    }

    public void setNews_location(String news_location) {
        this.news_location = news_location;
    }

    public String getNews_category_name() {
        return news_category_name;
    }

    public void setNews_category_name(String news_category_name) {
        this.news_category_name = news_category_name;
    }

    public String getNews_image() {
        return news_image;
    }

    public void setNews_image(String news_image) {
        this.news_image = news_image;
    }
}
