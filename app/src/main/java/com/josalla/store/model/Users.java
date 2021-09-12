package com.josalla.store.model;

public class Users {
    private String user_id;
    private String user_accessToken;
    private String user_name;
    private String user_location;
    private String user_address;
    private String user_phone;
    private boolean has_address ;
    private String user_type;
    private String allowToOrder;
    private Integer userPoints;

    public Users(String user_id, String user_accessToken, String user_name, String user_location, String user_address, String user_phone, boolean has_address, String user_type , String allowToOrder,Integer userPoints) {
        this.user_id = user_id;
        this.user_accessToken = user_accessToken;
        this.user_name = user_name;
        this.user_location = user_location;
        this.user_address = user_address;
        this.user_phone = user_phone;
        this.has_address = has_address;
        this.user_type = user_type;
        this.allowToOrder = allowToOrder;
    }

    public Users() {
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_accessToken() {
        return user_accessToken;
    }

    public void setUser_accessToken(String user_accessToken) {
        this.user_accessToken = user_accessToken;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_location() {
        return user_location;
    }

    public void setUser_location(String user_location) {
        this.user_location = user_location;
    }

    public String getUser_address() {
        return user_address;
    }

    public void setUser_address(String user_address) {
        this.user_address = user_address;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public boolean isHas_address() {
        return has_address;
    }

    public void setHas_address(boolean has_address) {
        this.has_address = has_address;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getAllowToOrder() {
        return allowToOrder;
    }

    public Integer userPoints(){return userPoints;}
    public void setAllowToOrder(String allowToOrder) {
        this.allowToOrder = allowToOrder;
    }
}
