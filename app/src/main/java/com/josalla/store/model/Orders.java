package com.josalla.store.model;

public class Orders {
    private String order_id;
    private String product_id;
    private String order_account;
    private String order_price;
    private String order_ammount;
    private String order_discrp;
    private String order_payment;
    private String order_state;
    private String order_number;
    private String order_code;
    private String order_date;

    public Orders(String product_id, String order_account, String order_price, String order_ammount, String order_discrp, String order_payment, String order_state, String order_number, String order_code, String order_date) {
        this.product_id = product_id;
        this.order_account = order_account;
        this.order_price = order_price;
        this.order_ammount = order_ammount;
        this.order_discrp = order_discrp;
        this.order_payment = order_payment;
        this.order_state = order_state;
        this.order_number = order_number;
        this.order_code = order_code;
        this.order_date = order_date;
    }


    public Orders(String product_id, String order_account, String order_price, String order_discrp, String order_payment, String order_state, String order_number, String order_code, String order_date) {
        this.product_id = product_id;
        this.order_account = order_account;
        this.order_price = order_price;
        this.order_discrp = order_discrp;
        this.order_payment = order_payment;
        this.order_state = order_state;
        this.order_number = order_number;
        this.order_code = order_code;
        this.order_date = order_date;
    }


    public Orders() {
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getOrder_code() {
        return order_code;
    }

    public void setOrder_code(String order_code) {
        this.order_code = order_code;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getOrder_account() {
        return order_account;
    }

    public void setOrder_account(String order_account) {
        this.order_account = order_account;
    }

    public String getOrder_price() {
        return order_price;
    }

    public void setOrder_price(String order_price) {
        this.order_price = order_price;
    }

    public String getOrder_ammount() {
        return order_ammount;
    }

    public void setOrder_ammount(String order_ammount) {
        this.order_ammount = order_ammount;
    }

    public String getOrder_discrp() {
        return order_discrp;
    }

    public void setOrder_discrp(String order_discrp) {
        this.order_discrp = order_discrp;
    }

    public String getOrder_payment() {
        return order_payment;
    }

    public void setOrder_payment(String order_payment) {
        this.order_payment = order_payment;
    }

    public String getOrder_state() {
        return order_state;
    }

    public void setOrder_state(String order_state) {
        this.order_state = order_state;
    }

    public String getOrder_number() {
        return order_number;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }
}
