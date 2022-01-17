package com.dayscab.driver.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelPlans implements Serializable {

    private ArrayList<Result> result;
    private String status;
    private String message;

    public void setResult(ArrayList<Result> result) {
        this.result = result;
    }

    public ArrayList<Result> getResult() {
        return this.result;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public class Result implements Serializable {

        private String id;

        private String plan_name;

        private String amount;

        private String total_ride;

        private String plan_exp_date;

        private String ride_fees;

        private String plan_status;

        private String bonus_trip;

        private String date_time;

        public String getPlan_exp_date() {
            return plan_exp_date;
        }

        public void setPlan_exp_date(String plan_exp_date) {
            this.plan_exp_date = plan_exp_date;
        }

        public String getPlan_status() {
            return plan_status;
        }

        public void setPlan_status(String plan_status) {
            this.plan_status = plan_status;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return this.id;
        }

        public void setPlan_name(String plan_name) {
            this.plan_name = plan_name;
        }

        public String getPlan_name() {
            return this.plan_name;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getAmount() {
            return this.amount;
        }

        public void setTotal_ride(String total_ride) {
            this.total_ride = total_ride;
        }

        public String getTotal_ride() {
            return this.total_ride;
        }

        public void setRide_fees(String ride_fees) {
            this.ride_fees = ride_fees;
        }

        public String getRide_fees() {
            return this.ride_fees;
        }

        public void setBonus_trip(String bonus_trip) {
            this.bonus_trip = bonus_trip;
        }

        public String getBonus_trip() {
            return this.bonus_trip;
        }

        public void setDate_time(String date_time) {
            this.date_time = date_time;
        }

        public String getDate_time() {
            return this.date_time;
        }
    }

}
