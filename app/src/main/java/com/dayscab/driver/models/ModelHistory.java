package com.dayscab.driver.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelHistory implements Serializable {

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

        private String driver_id;

        private String driver_ids;

        private String picuplocation;

        private String dropofflocation;

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

        private ArrayList<Driver_details> driver_details;

        private ArrayList<User_details> user_details;

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

        public void setDriver_details(ArrayList<Driver_details> driver_details) {
            this.driver_details = driver_details;
        }

        public ArrayList<Driver_details> getDriver_details() {
            return this.driver_details;
        }

        public void setUser_details(ArrayList<User_details> user_details) {
            this.user_details = user_details;
        }

        public ArrayList<User_details> getUser_details() {
            return this.user_details;
        }

        public class Driver_details implements Serializable{
            private String id;

            private String first_name;

            private String last_name;

            private String user_name;

            private String car_type_id;

            private String email;

            private String mobile;

            private String password;

            private String city;

            private String image;

            private String brand;

            private String car_number;

            private String car_model;

            private String step;

            private String car_image;

            private String register_id;

            private String date_time;

            private String social_id;

            private String lat;

            private String lon;

            private String address;

            private String zipcode;

            private String country;

            private String state;

            private String car_id;

            private String ios_register_id;

            private String stripe_account_login_link;

            private String wallet;

            private String verify_code;

            private String phone_code;

            private String cust_id;

            private String lang;

            private String stripe_account_id;

            private String promo_code;

            private String amount;

            private String license;

            private String year_of_manufacture;

            private String car_color;

            private String car_document;

            private String insurance;

            private String document;

            private String distance;

            private String status;

            private String type;

            private String online_status;

            private String driver_lisence_img;

            private String car_regist_img;

            private String vehicle_regist_img;

            private String rating;

            private String profile_image;

            private String result;

            private ArrayList<String> car_details;

            public void setId(String id) {
                this.id = id;
            }

            public String getId() {
                return this.id;
            }

            public void setFirst_name(String first_name) {
                this.first_name = first_name;
            }

            public String getFirst_name() {
                return this.first_name;
            }

            public void setLast_name(String last_name) {
                this.last_name = last_name;
            }

            public String getLast_name() {
                return this.last_name;
            }

            public void setUser_name(String user_name) {
                this.user_name = user_name;
            }

            public String getUser_name() {
                return this.user_name;
            }

            public void setCar_type_id(String car_type_id) {
                this.car_type_id = car_type_id;
            }

            public String getCar_type_id() {
                return this.car_type_id;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getEmail() {
                return this.email;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public String getMobile() {
                return this.mobile;
            }

            public void setPassword(String password) {
                this.password = password;
            }

            public String getPassword() {
                return this.password;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getCity() {
                return this.city;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public String getImage() {
                return this.image;
            }

            public void setBrand(String brand) {
                this.brand = brand;
            }

            public String getBrand() {
                return this.brand;
            }

            public void setCar_number(String car_number) {
                this.car_number = car_number;
            }

            public String getCar_number() {
                return this.car_number;
            }

            public void setCar_model(String car_model) {
                this.car_model = car_model;
            }

            public String getCar_model() {
                return this.car_model;
            }

            public void setStep(String step) {
                this.step = step;
            }

            public String getStep() {
                return this.step;
            }

            public void setCar_image(String car_image) {
                this.car_image = car_image;
            }

            public String getCar_image() {
                return this.car_image;
            }

            public void setRegister_id(String register_id) {
                this.register_id = register_id;
            }

            public String getRegister_id() {
                return this.register_id;
            }

            public void setDate_time(String date_time) {
                this.date_time = date_time;
            }

            public String getDate_time() {
                return this.date_time;
            }

            public void setSocial_id(String social_id) {
                this.social_id = social_id;
            }

            public String getSocial_id() {
                return this.social_id;
            }

            public void setLat(String lat) {
                this.lat = lat;
            }

            public String getLat() {
                return this.lat;
            }

            public void setLon(String lon) {
                this.lon = lon;
            }

            public String getLon() {
                return this.lon;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getAddress() {
                return this.address;
            }

            public void setZipcode(String zipcode) {
                this.zipcode = zipcode;
            }

            public String getZipcode() {
                return this.zipcode;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public String getCountry() {
                return this.country;
            }

            public void setState(String state) {
                this.state = state;
            }

            public String getState() {
                return this.state;
            }

            public void setCar_id(String car_id) {
                this.car_id = car_id;
            }

            public String getCar_id() {
                return this.car_id;
            }

            public void setIos_register_id(String ios_register_id) {
                this.ios_register_id = ios_register_id;
            }

            public String getIos_register_id() {
                return this.ios_register_id;
            }

            public void setStripe_account_login_link(String stripe_account_login_link) {
                this.stripe_account_login_link = stripe_account_login_link;
            }

            public String getStripe_account_login_link() {
                return this.stripe_account_login_link;
            }

            public void setWallet(String wallet) {
                this.wallet = wallet;
            }

            public String getWallet() {
                return this.wallet;
            }

            public void setVerify_code(String verify_code) {
                this.verify_code = verify_code;
            }

            public String getVerify_code() {
                return this.verify_code;
            }

            public void setPhone_code(String phone_code) {
                this.phone_code = phone_code;
            }

            public String getPhone_code() {
                return this.phone_code;
            }

            public void setCust_id(String cust_id) {
                this.cust_id = cust_id;
            }

            public String getCust_id() {
                return this.cust_id;
            }

            public void setLang(String lang) {
                this.lang = lang;
            }

            public String getLang() {
                return this.lang;
            }

            public void setStripe_account_id(String stripe_account_id) {
                this.stripe_account_id = stripe_account_id;
            }

            public String getStripe_account_id() {
                return this.stripe_account_id;
            }

            public void setPromo_code(String promo_code) {
                this.promo_code = promo_code;
            }

            public String getPromo_code() {
                return this.promo_code;
            }

            public void setAmount(String amount) {
                this.amount = amount;
            }

            public String getAmount() {
                return this.amount;
            }

            public void setLicense(String license) {
                this.license = license;
            }

            public String getLicense() {
                return this.license;
            }

            public void setYear_of_manufacture(String year_of_manufacture) {
                this.year_of_manufacture = year_of_manufacture;
            }

            public String getYear_of_manufacture() {
                return this.year_of_manufacture;
            }

            public void setCar_color(String car_color) {
                this.car_color = car_color;
            }

            public String getCar_color() {
                return this.car_color;
            }

            public void setCar_document(String car_document) {
                this.car_document = car_document;
            }

            public String getCar_document() {
                return this.car_document;
            }

            public void setInsurance(String insurance) {
                this.insurance = insurance;
            }

            public String getInsurance() {
                return this.insurance;
            }

            public void setDocument(String document) {
                this.document = document;
            }

            public String getDocument() {
                return this.document;
            }

            public void setDistance(String distance) {
                this.distance = distance;
            }

            public String getDistance() {
                return this.distance;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getStatus() {
                return this.status;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getType() {
                return this.type;
            }

            public void setOnline_status(String online_status) {
                this.online_status = online_status;
            }

            public String getOnline_status() {
                return this.online_status;
            }

            public void setDriver_lisence_img(String driver_lisence_img) {
                this.driver_lisence_img = driver_lisence_img;
            }

            public String getDriver_lisence_img() {
                return this.driver_lisence_img;
            }

            public void setCar_regist_img(String car_regist_img) {
                this.car_regist_img = car_regist_img;
            }

            public String getCar_regist_img() {
                return this.car_regist_img;
            }

            public void setVehicle_regist_img(String vehicle_regist_img) {
                this.vehicle_regist_img = vehicle_regist_img;
            }

            public String getVehicle_regist_img() {
                return this.vehicle_regist_img;
            }

            public void setRating(String rating) {
                this.rating = rating;
            }

            public String getRating() {
                return this.rating;
            }

            public void setProfile_image(String profile_image) {
                this.profile_image = profile_image;
            }

            public String getProfile_image() {
                return this.profile_image;
            }

            public void setResult(String result) {
                this.result = result;
            }

            public String getResult() {
                return this.result;
            }

            public void setCar_details(ArrayList<String> car_details) {
                this.car_details = car_details;
            }

            public ArrayList<String> getCar_details() {
                return this.car_details;
            }
        }

        public class User_details implements Serializable{
            private String id;

            private String first_name;

            private String last_name;

            private String user_name;

            private String car_type_id;

            private String email;

            private String mobile;

            private String password;

            private String city;

            private String image;

            private String brand;

            private String car_number;

            private String car_model;

            private String step;

            private String car_image;

            private String register_id;

            private String date_time;

            private String social_id;

            private String lat;

            private String lon;

            private String address;

            private String zipcode;

            private String country;

            private String state;

            private String car_id;

            private String ios_register_id;

            private String stripe_account_login_link;

            private String wallet;

            private String verify_code;

            private String phone_code;

            private String cust_id;

            private String lang;

            private String stripe_account_id;

            private String promo_code;

            private String amount;

            private String license;

            private String year_of_manufacture;

            private String car_color;

            private String car_document;

            private String insurance;

            private String document;

            private String distance;

            private String status;

            private String type;

            private String online_status;

            private String driver_lisence_img;

            private String car_regist_img;

            private String vehicle_regist_img;

            private String rating;

            private String profile_image;

            private String result;

            private ArrayList<String> car_details;

            public void setId(String id) {
                this.id = id;
            }

            public String getId() {
                return this.id;
            }

            public void setFirst_name(String first_name) {
                this.first_name = first_name;
            }

            public String getFirst_name() {
                return this.first_name;
            }

            public void setLast_name(String last_name) {
                this.last_name = last_name;
            }

            public String getLast_name() {
                return this.last_name;
            }

            public void setUser_name(String user_name) {
                this.user_name = user_name;
            }

            public String getUser_name() {
                return this.user_name;
            }

            public void setCar_type_id(String car_type_id) {
                this.car_type_id = car_type_id;
            }

            public String getCar_type_id() {
                return this.car_type_id;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getEmail() {
                return this.email;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public String getMobile() {
                return this.mobile;
            }

            public void setPassword(String password) {
                this.password = password;
            }

            public String getPassword() {
                return this.password;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getCity() {
                return this.city;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public String getImage() {
                return this.image;
            }

            public void setBrand(String brand) {
                this.brand = brand;
            }

            public String getBrand() {
                return this.brand;
            }

            public void setCar_number(String car_number) {
                this.car_number = car_number;
            }

            public String getCar_number() {
                return this.car_number;
            }

            public void setCar_model(String car_model) {
                this.car_model = car_model;
            }

            public String getCar_model() {
                return this.car_model;
            }

            public void setStep(String step) {
                this.step = step;
            }

            public String getStep() {
                return this.step;
            }

            public void setCar_image(String car_image) {
                this.car_image = car_image;
            }

            public String getCar_image() {
                return this.car_image;
            }

            public void setRegister_id(String register_id) {
                this.register_id = register_id;
            }

            public String getRegister_id() {
                return this.register_id;
            }

            public void setDate_time(String date_time) {
                this.date_time = date_time;
            }

            public String getDate_time() {
                return this.date_time;
            }

            public void setSocial_id(String social_id) {
                this.social_id = social_id;
            }

            public String getSocial_id() {
                return this.social_id;
            }

            public void setLat(String lat) {
                this.lat = lat;
            }

            public String getLat() {
                return this.lat;
            }

            public void setLon(String lon) {
                this.lon = lon;
            }

            public String getLon() {
                return this.lon;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getAddress() {
                return this.address;
            }

            public void setZipcode(String zipcode) {
                this.zipcode = zipcode;
            }

            public String getZipcode() {
                return this.zipcode;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public String getCountry() {
                return this.country;
            }

            public void setState(String state) {
                this.state = state;
            }

            public String getState() {
                return this.state;
            }

            public void setCar_id(String car_id) {
                this.car_id = car_id;
            }

            public String getCar_id() {
                return this.car_id;
            }

            public void setIos_register_id(String ios_register_id) {
                this.ios_register_id = ios_register_id;
            }

            public String getIos_register_id() {
                return this.ios_register_id;
            }

            public void setStripe_account_login_link(String stripe_account_login_link) {
                this.stripe_account_login_link = stripe_account_login_link;
            }

            public String getStripe_account_login_link() {
                return this.stripe_account_login_link;
            }

            public void setWallet(String wallet) {
                this.wallet = wallet;
            }

            public String getWallet() {
                return this.wallet;
            }

            public void setVerify_code(String verify_code) {
                this.verify_code = verify_code;
            }

            public String getVerify_code() {
                return this.verify_code;
            }

            public void setPhone_code(String phone_code) {
                this.phone_code = phone_code;
            }

            public String getPhone_code() {
                return this.phone_code;
            }

            public void setCust_id(String cust_id) {
                this.cust_id = cust_id;
            }

            public String getCust_id() {
                return this.cust_id;
            }

            public void setLang(String lang) {
                this.lang = lang;
            }

            public String getLang() {
                return this.lang;
            }

            public void setStripe_account_id(String stripe_account_id) {
                this.stripe_account_id = stripe_account_id;
            }

            public String getStripe_account_id() {
                return this.stripe_account_id;
            }

            public void setPromo_code(String promo_code) {
                this.promo_code = promo_code;
            }

            public String getPromo_code() {
                return this.promo_code;
            }

            public void setAmount(String amount) {
                this.amount = amount;
            }

            public String getAmount() {
                return this.amount;
            }

            public void setLicense(String license) {
                this.license = license;
            }

            public String getLicense() {
                return this.license;
            }

            public void setYear_of_manufacture(String year_of_manufacture) {
                this.year_of_manufacture = year_of_manufacture;
            }

            public String getYear_of_manufacture() {
                return this.year_of_manufacture;
            }

            public void setCar_color(String car_color) {
                this.car_color = car_color;
            }

            public String getCar_color() {
                return this.car_color;
            }

            public void setCar_document(String car_document) {
                this.car_document = car_document;
            }

            public String getCar_document() {
                return this.car_document;
            }

            public void setInsurance(String insurance) {
                this.insurance = insurance;
            }

            public String getInsurance() {
                return this.insurance;
            }

            public void setDocument(String document) {
                this.document = document;
            }

            public String getDocument() {
                return this.document;
            }

            public void setDistance(String distance) {
                this.distance = distance;
            }

            public String getDistance() {
                return this.distance;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getStatus() {
                return this.status;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getType() {
                return this.type;
            }

            public void setOnline_status(String online_status) {
                this.online_status = online_status;
            }

            public String getOnline_status() {
                return this.online_status;
            }

            public void setDriver_lisence_img(String driver_lisence_img) {
                this.driver_lisence_img = driver_lisence_img;
            }

            public String getDriver_lisence_img() {
                return this.driver_lisence_img;
            }

            public void setCar_regist_img(String car_regist_img) {
                this.car_regist_img = car_regist_img;
            }

            public String getCar_regist_img() {
                return this.car_regist_img;
            }

            public void setVehicle_regist_img(String vehicle_regist_img) {
                this.vehicle_regist_img = vehicle_regist_img;
            }

            public String getVehicle_regist_img() {
                return this.vehicle_regist_img;
            }

            public void setRating(String rating) {
                this.rating = rating;
            }

            public String getRating() {
                return this.rating;
            }

            public void setProfile_image(String profile_image) {
                this.profile_image = profile_image;
            }

            public String getProfile_image() {
                return this.profile_image;
            }

            public void setResult(String result) {
                this.result = result;
            }

            public String getResult() {
                return this.result;
            }

            public void setCar_details(ArrayList<String> car_details) {
                this.car_details = car_details;
            }

            public ArrayList<String> getCar_details() {
                return this.car_details;
            }

        }

    }

}
