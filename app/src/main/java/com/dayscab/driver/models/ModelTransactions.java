package com.dayscab.driver.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelTransactions implements Serializable {

    private ArrayList<Result> result;

    private String message;

    private int status;

    public void setResult(ArrayList<Result> result) {
        this.result = result;
    }

    public ArrayList<Result> getResult() {
        return this.result;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public class Result implements Serializable {

        private String id;

        private String user_id;

        private String type;

        private String type_id;

        private String amount;

        private String transaction_type;

        private String description;

        private String time_zone;

        private String date_time;

        private String status;

        private String user_name;

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return this.id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getUser_id() {
            return this.user_id;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getType() {
            return this.type;
        }

        public void setType_id(String type_id) {
            this.type_id = type_id;
        }

        public String getType_id() {
            return this.type_id;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getAmount() {
            return this.amount;
        }

        public void setTransaction_type(String transaction_type) {
            this.transaction_type = transaction_type;
        }

        public String getTransaction_type() {
            return this.transaction_type;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDescription() {
            return this.description;
        }

        public void setTime_zone(String time_zone) {
            this.time_zone = time_zone;
        }

        public String getTime_zone() {
            return this.time_zone;
        }

        public void setDate_time(String date_time) {
            this.date_time = date_time;
        }

        public String getDate_time() {
            return this.date_time;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStatus() {
            return this.status;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getUser_name() {
            return this.user_name;
        }
    }

}
