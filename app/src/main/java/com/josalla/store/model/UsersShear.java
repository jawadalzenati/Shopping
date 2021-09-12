package com.josalla.store.model;

public class UsersShear {
    private String shear_id;
    private String user_id;
    private String user_shear;
    private String shear_state;
    private String order_id;

    public UsersShear(String user_id, String user_shear, String shear_state , String order_id) {
        this.user_id = user_id;
        this.user_shear = user_shear;
        this.shear_state = shear_state;
        this.order_id = order_id;
    }

    public UsersShear() {
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String product_id) {
        this.order_id = product_id;
    }

    public String getShear_id() {
        return shear_id;
    }

    public void setShear_id(String shear_id) {
        this.shear_id = shear_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_shear() {
        return user_shear;
    }

    public void setUser_shear(String user_shear) {
        this.user_shear = user_shear;
    }

    public String getShear_state() {
        return shear_state;
    }

    public void setShear_state(String shear_state) {
        this.shear_state = shear_state;
    }
}
