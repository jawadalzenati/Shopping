package com.josalla.store.model;

public class PaymentRequests {
    private String request_id;
    private String request_user_id;
    private String requset_user_email;
    private String request_date;
    private String requset_amount;
    private String requset_State;

    public PaymentRequests() {
    }

    public PaymentRequests(String request_user_id, String requset_user_email, String request_date, String requset_amount , String requset_State) {
        this.request_user_id = request_user_id;
        this.requset_user_email = requset_user_email;
        this.request_date = request_date;
        this.requset_amount = requset_amount;
        this.requset_State = requset_State;
    }

    public String getRequest_id() {
        return request_id;
    }

    public String getRequset_State() {
        return requset_State;
    }

    public void setRequset_State(String requset_State) {
        this.requset_State = requset_State;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public String getRequest_user_id() {
        return request_user_id;
    }

    public void setRequest_user_id(String request_user_id) {
        this.request_user_id = request_user_id;
    }

    public String getRequset_user_email() {
        return requset_user_email;
    }

    public void setRequset_user_email(String requset_user_email) {
        this.requset_user_email = requset_user_email;
    }

    public String getRequest_date() {
        return request_date;
    }

    public void setRequest_date(String request_date) {
        this.request_date = request_date;
    }

    public String getRequset_amount() {
        return requset_amount;
    }

    public void setRequset_amount(String requset_amount) {
        this.requset_amount = requset_amount;
    }
}
