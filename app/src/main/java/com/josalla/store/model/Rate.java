package com.josalla.store.model;

public class Rate {
    private  String rate_id;
    private  String rate_product_id;
    private  String rate_stars;
    private  String rate_text;
    private  String rate_time ;

    public Rate(String rate_product_id, String rate_stars, String rate_text, String rate_time) {
        this.rate_product_id = rate_product_id;
        this.rate_stars = rate_stars;
        this.rate_text = rate_text;
        this.rate_time = rate_time;
    }

    public Rate() {
    }

    public String getRate_id() {
        return rate_id;
    }

    public void setRate_id(String rate_id) {
        this.rate_id = rate_id;
    }

    public String getRate_product_id() {
        return rate_product_id;
    }

    public void setRate_product_id(String rate_product_id) {
        this.rate_product_id = rate_product_id;
    }

    public String getRate_stars() {
        return rate_stars;
    }

    public void setRate_stars(String rate_stars) {
        this.rate_stars = rate_stars;
    }

    public String getRate_text() {
        return rate_text;
    }

    public void setRate_text(String rate_text) {
        this.rate_text = rate_text;
    }

    public String getRate_time() {
        return rate_time;
    }

    public void setRate_time(String rate_time) {
        this.rate_time = rate_time;
    }
}
