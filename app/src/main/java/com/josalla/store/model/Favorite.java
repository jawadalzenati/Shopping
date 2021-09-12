package com.josalla.store.model;

public class Favorite {
    private String favorite_id;
    private String favorite_product_id;
    private String favorite_user_id;

    public Favorite(String favorite_product_id, String favorite_user_id) {
        this.favorite_product_id = favorite_product_id;
        this.favorite_user_id = favorite_user_id;
    }

    public Favorite() {
    }

    public String getFavorite_id() {
        return favorite_id;
    }

    public void setFavorite_id(String favorite_id) {
        this.favorite_id = favorite_id;
    }

    public String getFavorite_product_id() {
        return favorite_product_id;
    }

    public void setFavorite_product_id(String favorite_product_id) {
        this.favorite_product_id = favorite_product_id;
    }

    public String getFavorite_user_id() {
        return favorite_user_id;
    }

    public void setFavorite_user_id(String favorite_user_id) {
        this.favorite_user_id = favorite_user_id;
    }
}
