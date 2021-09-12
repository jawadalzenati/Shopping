package com.josalla.store.model;

public class SubCategory {
    private String category_id;
    private String category_name;
    private String category_image;
    private String category_main;

    public SubCategory(String category_name, String category_image, String category_main) {
        this.category_name = category_name;
        this.category_image = category_image;
        this.category_main = category_main;
    }

    public SubCategory() {
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCategory_image() {
        return category_image;
    }

    public void setCategory_image(String category_image) {
        this.category_image = category_image;
    }

    public String getCategory_main() {
        return category_main;
    }

    public void setCategory_main(String category_main) {
        this.category_main = category_main;
    }
}
