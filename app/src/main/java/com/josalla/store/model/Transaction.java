package com.josalla.store.model;

public class Transaction {
    private String t_id;
    private String user_id;
    private String t_text;

    public Transaction(String user_id, String t_text) {
        this.user_id = user_id;
        this.t_text = t_text;
    }

    public Transaction() {
    }

    public String getT_id() {
        return t_id;
    }

    public void setT_id(String t_id) {
        this.t_id = t_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getT_text() {
        return t_text;
    }

    public void setT_text(String t_text) {
        this.t_text = t_text;
    }
}
