package com.josalla.store.model;

public class UserBalance {
    private String b_id;
    private String user_id;
    private String user_balance;

    public UserBalance( String user_balance) {
        this.user_balance = user_balance;
    }

    public UserBalance() {
    }

    public String getB_id() {
        return b_id;
    }

    public void setB_id(String b_id) {
        this.b_id = b_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_balance() {
        return user_balance;
    }

    public void setUser_balance(String user_balance) {
        this.user_balance = user_balance;
    }
}
