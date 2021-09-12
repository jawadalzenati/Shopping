package com.josalla.store.model;

public class Cart {
    private String card_id;
    private String product_id;
    private String user_id;
    private String selected_color;
    private String selected_size;
    private String product_count;
    private String product_image;
    private String total_price;
    private String product_name;
    private boolean set_selected;
    private String shear_code;
    private String order_id;


    public Cart(String product_id, String user_id, String selected_color, String selected_size, String product_count, String product_image ,String total_price ,String product_name , boolean set_selected , String shear_code ,String order_id) {
        this.product_id = product_id;
        this.user_id = user_id;
        this.selected_color = selected_color;
        this.selected_size = selected_size;
        this.product_count = product_count;
        this.product_image = product_image;
        this.total_price = total_price;
        this.product_name = product_name;
        this.set_selected = set_selected;
        this.shear_code = shear_code;
        this.order_id = order_id;
    }

    public Cart() {
    }

    public boolean isSet_selected() {
        return set_selected;
    }

    public void setSet_selected(boolean set_selected) {
        this.set_selected = set_selected;
    }

    public String getShear_code() {
        return shear_code;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public void setShear_code(String shear_code) {
        this.shear_code = shear_code;
    }

    public String getTotal_price() {
        return total_price;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public String getCard_id() {
        return card_id;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getSelected_color() {
        return selected_color;
    }

    public void setSelected_color(String selected_color) {
        this.selected_color = selected_color;
    }

    public String getSelected_size() {
        return selected_size;
    }

    public void setSelected_size(String selected_size) {
        this.selected_size = selected_size;
    }

    public String getProduct_count() {
        return product_count;
    }

    public void setProduct_count(String product_count) {
        this.product_count = product_count;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }
}
