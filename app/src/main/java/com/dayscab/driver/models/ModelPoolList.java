package com.dayscab.driver.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelPoolList implements Serializable {

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

        private String driver_id;

        private String start_location;

        private String end_location;

        private String start_lat;

        private String start_lon;

        private String end_lat;

        private String end_lon;

        private String stop_1;

        private String lat1;

        private String lon1;

        private String stop_2;

        private String lat2;

        private String lon2;

        private String stop_3;

        private String lat3;

        private String lon3;

        private String date;

        private String time;

        private String seats_offer;

        private String date_time;

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return this.id;
        }

        public void setDriver_id(String driver_id) {
            this.driver_id = driver_id;
        }

        public String getDriver_id() {
            return this.driver_id;
        }

        public void setStart_location(String start_location) {
            this.start_location = start_location;
        }

        public String getStart_location() {
            return this.start_location;
        }

        public void setEnd_location(String end_location) {
            this.end_location = end_location;
        }

        public String getEnd_location() {
            return this.end_location;
        }

        public void setStart_lat(String start_lat) {
            this.start_lat = start_lat;
        }

        public String getStart_lat() {
            return this.start_lat;
        }

        public void setStart_lon(String start_lon) {
            this.start_lon = start_lon;
        }

        public String getStart_lon() {
            return this.start_lon;
        }

        public void setEnd_lat(String end_lat) {
            this.end_lat = end_lat;
        }

        public String getEnd_lat() {
            return this.end_lat;
        }

        public void setEnd_lon(String end_lon) {
            this.end_lon = end_lon;
        }

        public String getEnd_lon() {
            return this.end_lon;
        }

        public void setStop_1(String stop_1) {
            this.stop_1 = stop_1;
        }

        public String getStop_1() {
            return this.stop_1;
        }

        public void setLat1(String lat1) {
            this.lat1 = lat1;
        }

        public String getLat1() {
            return this.lat1;
        }

        public void setLon1(String lon1) {
            this.lon1 = lon1;
        }

        public String getLon1() {
            return this.lon1;
        }

        public void setStop_2(String stop_2) {
            this.stop_2 = stop_2;
        }

        public String getStop_2() {
            return this.stop_2;
        }

        public void setLat2(String lat2) {
            this.lat2 = lat2;
        }

        public String getLat2() {
            return this.lat2;
        }

        public void setLon2(String lon2) {
            this.lon2 = lon2;
        }

        public String getLon2() {
            return this.lon2;
        }

        public void setStop_3(String stop_3) {
            this.stop_3 = stop_3;
        }

        public String getStop_3() {
            return this.stop_3;
        }

        public void setLat3(String lat3) {
            this.lat3 = lat3;
        }

        public String getLat3() {
            return this.lat3;
        }

        public void setLon3(String lon3) {
            this.lon3 = lon3;
        }

        public String getLon3() {
            return this.lon3;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getDate() {
            return this.date;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getTime() {
            return this.time;
        }

        public void setSeats_offer(String seats_offer) {
            this.seats_offer = seats_offer;
        }

        public String getSeats_offer() {
            return this.seats_offer;
        }

        public void setDate_time(String date_time) {
            this.date_time = date_time;
        }

        public String getDate_time() {
            return this.date_time;
        }
    }


}
