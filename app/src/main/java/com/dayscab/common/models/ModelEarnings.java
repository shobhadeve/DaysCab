package com.dayscab.common.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelEarnings implements Serializable {

    private ArrayList<Result> result;
    private String year;
    private String trip;
    private String status;
    private String message;

    public void setResult(ArrayList<Result> result) {
        this.result = result;
    }

    public ArrayList<Result> getResult() {
        return this.result;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getYear() {
        return this.year;
    }

    public void setTrip(String trip) {
        this.trip = trip;
    }

    public String getTrip() {
        return this.trip;
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

        private String key;

        public void setKey(String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }

    }

}
