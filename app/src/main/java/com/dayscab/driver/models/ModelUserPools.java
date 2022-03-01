package com.dayscab.driver.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelUserPools implements Serializable {

    private ArrayList<ModelUserPools.Result> result;
    private String message;
    private int status;
    private String register_id;

    public String getRegister_id() {
        return register_id;
    }

    public void setRegister_id(String register_id) {
        this.register_id = register_id;
    }

    public void setResult(ArrayList<ModelUserPools.Result> result) {
        this.result = result;
    }

    public ArrayList<ModelUserPools.Result> getResult() {
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

        private String waitng_time;

        private String car_number;

        private String per_minute;

        private String per_minute_charge;

        private String cancel_driver_id;

        private String total_trip_cost;

        private String driver_id;

        private String car_name;

        private String driver_ids;

        private String picuplocation;

        private String dropofflocation;

        private String no_of_passenger;

        private String picuplat;

        private String pickuplon;

        private String droplat;

        private String droplon;

        private String shareride_type;

        private String booktype;

        private String car_type_id;

        private String car_seats;

        private String booked_seats;

        private String req_datetime;

        private String timezone;

        private String picklatertime;

        private String picklaterdate;

        private String route_img;

        private String start_time;

        private String end_time;

        private String waiting_status;

        private String accept_time;

        private String waiting_cnt;

        private String apply_code;

        private String payment_type;

        private String card_id;

        private String status;

        private String payment_status;

        private String cancel_reaison;

        private String amount;

        private String otp;

        private String my_booking;

        private int sch_diff;

        private String sch_status;

        private String distance;

        public String getCancel_driver_id() {
            return cancel_driver_id;
        }

        public void setCancel_driver_id(String cancel_driver_id) {
            this.cancel_driver_id = cancel_driver_id;
        }

        public String getNo_of_passenger() {
            return no_of_passenger;
        }

        public void setNo_of_passenger(String no_of_passenger) {
            this.no_of_passenger = no_of_passenger;
        }

        public String getCar_number() {
            return car_number;
        }

        public void setCar_number(String car_number) {
            this.car_number = car_number;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public String getWaitng_time() {
            return waitng_time;
        }

        public void setWaitng_time(String waitng_time) {
            this.waitng_time = waitng_time;
        }

        public String getPer_minute() {
            return per_minute;
        }

        public void setPer_minute(String per_minute) {
            this.per_minute = per_minute;
        }

        public String getPer_minute_charge() {
            return per_minute_charge;
        }

        public void setPer_minute_charge(String per_minute_charge) {
            this.per_minute_charge = per_minute_charge;
        }

        public String getTotal_trip_cost() {
            return total_trip_cost;
        }

        public void setTotal_trip_cost(String total_trip_cost) {
            this.total_trip_cost = total_trip_cost;
        }

        public String getCar_name() {
            return car_name;
        }

        public void setCar_name(String car_name) {
            this.car_name = car_name;
        }

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

        public void setDriver_id(String driver_id) {
            this.driver_id = driver_id;
        }

        public String getDriver_id() {
            return this.driver_id;
        }

        public void setDriver_ids(String driver_ids) {
            this.driver_ids = driver_ids;
        }

        public String getDriver_ids() {
            return this.driver_ids;
        }

        public void setPicuplocation(String picuplocation) {
            this.picuplocation = picuplocation;
        }

        public String getPicuplocation() {
            return this.picuplocation;
        }

        public void setDropofflocation(String dropofflocation) {
            this.dropofflocation = dropofflocation;
        }

        public String getDropofflocation() {
            return this.dropofflocation;
        }

        public void setPicuplat(String picuplat) {
            this.picuplat = picuplat;
        }

        public String getPicuplat() {
            return this.picuplat;
        }

        public void setPickuplon(String pickuplon) {
            this.pickuplon = pickuplon;
        }

        public String getPickuplon() {
            return this.pickuplon;
        }

        public void setDroplat(String droplat) {
            this.droplat = droplat;
        }

        public String getDroplat() {
            return this.droplat;
        }

        public void setDroplon(String droplon) {
            this.droplon = droplon;
        }

        public String getDroplon() {
            return this.droplon;
        }

        public void setShareride_type(String shareride_type) {
            this.shareride_type = shareride_type;
        }

        public String getShareride_type() {
            return this.shareride_type;
        }

        public void setBooktype(String booktype) {
            this.booktype = booktype;
        }

        public String getBooktype() {
            return this.booktype;
        }

        public void setCar_type_id(String car_type_id) {
            this.car_type_id = car_type_id;
        }

        public String getCar_type_id() {
            return this.car_type_id;
        }

        public void setCar_seats(String car_seats) {
            this.car_seats = car_seats;
        }

        public String getCar_seats() {
            return this.car_seats;
        }

        public void setBooked_seats(String booked_seats) {
            this.booked_seats = booked_seats;
        }

        public String getBooked_seats() {
            return this.booked_seats;
        }

        public void setReq_datetime(String req_datetime) {
            this.req_datetime = req_datetime;
        }

        public String getReq_datetime() {
            return this.req_datetime;
        }

        public void setTimezone(String timezone) {
            this.timezone = timezone;
        }

        public String getTimezone() {
            return this.timezone;
        }

        public void setPicklatertime(String picklatertime) {
            this.picklatertime = picklatertime;
        }

        public String getPicklatertime() {
            return this.picklatertime;
        }

        public void setPicklaterdate(String picklaterdate) {
            this.picklaterdate = picklaterdate;
        }

        public String getPicklaterdate() {
            return this.picklaterdate;
        }

        public void setRoute_img(String route_img) {
            this.route_img = route_img;
        }

        public String getRoute_img() {
            return this.route_img;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getStart_time() {
            return this.start_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public String getEnd_time() {
            return this.end_time;
        }

        public void setWaiting_status(String waiting_status) {
            this.waiting_status = waiting_status;
        }

        public String getWaiting_status() {
            return this.waiting_status;
        }

        public void setAccept_time(String accept_time) {
            this.accept_time = accept_time;
        }

        public String getAccept_time() {
            return this.accept_time;
        }

        public void setWaiting_cnt(String waiting_cnt) {
            this.waiting_cnt = waiting_cnt;
        }

        public String getWaiting_cnt() {
            return this.waiting_cnt;
        }

        public void setApply_code(String apply_code) {
            this.apply_code = apply_code;
        }

        public String getApply_code() {
            return this.apply_code;
        }

        public void setPayment_type(String payment_type) {
            this.payment_type = payment_type;
        }

        public String getPayment_type() {
            return this.payment_type;
        }

        public void setCard_id(String card_id) {
            this.card_id = card_id;
        }

        public String getCard_id() {
            return this.card_id;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStatus() {
            return this.status;
        }

        public void setPayment_status(String payment_status) {
            this.payment_status = payment_status;
        }

        public String getPayment_status() {
            return this.payment_status;
        }

        public void setCancel_reaison(String cancel_reaison) {
            this.cancel_reaison = cancel_reaison;
        }

        public String getCancel_reaison() {
            return this.cancel_reaison;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getAmount() {
            return this.amount;
        }

        public void setOtp(String otp) {
            this.otp = otp;
        }

        public String getOtp() {
            return this.otp;
        }

        public void setMy_booking(String my_booking) {
            this.my_booking = my_booking;
        }

        public String getMy_booking() {
            return this.my_booking;
        }

        public void setSch_diff(int sch_diff) {
            this.sch_diff = sch_diff;
        }

        public int getSch_diff() {
            return this.sch_diff;
        }

        public void setSch_status(String sch_status) {
            this.sch_status = sch_status;
        }

        public String getSch_status() {
            return this.sch_status;
        }

    }


}
