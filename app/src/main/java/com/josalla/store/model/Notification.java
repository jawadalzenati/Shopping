package com.josalla.store.model;

public class Notification {
    private String notifi_id;
    private String notifi_tag;
    private String notifi_title;
    private String notifi_text ;
    private String notifi_user ;

    public Notification(String notifi_tag, String notifi_title, String notifi_text, String notifi_user) {
        this.notifi_tag = notifi_tag;
        this.notifi_title = notifi_title;
        this.notifi_text = notifi_text;
        this.notifi_user = notifi_user;
    }

    public Notification() {
    }

    public String getNotifi_id() {
        return notifi_id;
    }

    public void setNotifi_id(String notifi_id) {
        this.notifi_id = notifi_id;
    }

    public String getNotifi_tag() {
        return notifi_tag;
    }

    public void setNotifi_tag(String notifi_tag) {
        this.notifi_tag = notifi_tag;
    }

    public String getNotifi_title() {
        return notifi_title;
    }

    public void setNotifi_title(String notifi_title) {
        this.notifi_title = notifi_title;
    }

    public String getNotifi_text() {
        return notifi_text;
    }

    public void setNotifi_text(String notifi_text) {
        this.notifi_text = notifi_text;
    }

    public String getNotifi_user() {
        return notifi_user;
    }

    public void setNotifi_user(String notifi_user) {
        this.notifi_user = notifi_user;
    }
}

