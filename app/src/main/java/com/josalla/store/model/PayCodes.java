package com.josalla.store.model;

public class PayCodes {
    private String code_id;
    private String user_id;
    private String code_text;
    private String code_share;
    private String code_discounts;

    public PayCodes(String user_id, String code_text, String code_share, String code_discounts) {
        this.user_id = user_id;
        this.code_text = code_text;
        this.code_share = code_share;
        this.code_discounts = code_discounts;
    }

    public PayCodes() {
    }

    public String getCode_id() {
        return code_id;
    }

    public void setCode_id(String code_id) {
        this.code_id = code_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCode_text() {
        return code_text;
    }

    public void setCode_text(String code_text) {
        this.code_text = code_text;
    }

    public String getCode_share() {
        return code_share;
    }

    public void setCode_share(String code_share) {
        this.code_share = code_share;
    }

    public String getCode_discounts() {
        return code_discounts;
    }

    public void setCode_discounts(String code_discounts) {
        this.code_discounts = code_discounts;
    }
}
