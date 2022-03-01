package com.dayscab.common.models;

import java.io.Serializable;

public class ModelLogin implements Serializable {

    private String message;
    private Result result;
    private String status;

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Result getResult() {
        return this.result;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public class Result implements Serializable {

        private String id;

        private String user_name;

        private String profile_image;

        private String mute_music;

        private String driver_complete_ride;

        private String driver_rating;

        private String mute_request_sound;

        private String emergency_name;

        private String token;

        private String emergency_mobile;

        private String emergency_email;

        private String on_way_ride;

        private String complete_rides;

        private String email;

        private String mobile;

        private String password;

        private String plan_name;

        private String plan_amount;

        private String plan_total_ride;

        private String plan_ride_fees;

        private String plan_bonus_trip;

        private String ride_count;

        private String register_id;

        private String driver_lisence_img;

        private String car_regist_img;

        private String vehicle_regist_img;

        private String date_time;

        private String cust_id;

        private String stripe_account_id;

        private String ios_register_id;

        private String stripe_account_login_link;

        private String first_name;

        private String last_name;

        private String phone_code;

        private String image;

        private String social_id;

        private String lat;

        private String lon;

        private String status;

        private String address;

        private String zipcode;

        private String country;

        private String state;

        private String city;

        private String type;

        private String car_id;

        private String car_detail_id;

        private String car_type_id;

        private String online_status;

        private String wallet;

        private String verify_code;

        private String lang;

        private String promo_code;

        private String amount;

        private String license;

        private String car_model;

        private String year_of_manufacture;

        private String car_color;

        private String car_number;

        private String car_image;

        private String car_document;

        private String insurance;

        private String document;

        private String distance;

        private String step;

        private String brand;

        private String work_address;
            
        private String work_lat;
        
        private String work_lon;

        public String getDriver_complete_ride() {
            return driver_complete_ride;
        }

        public void setDriver_complete_ride(String driver_complete_ride) {
            this.driver_complete_ride = driver_complete_ride;
        }

        public String getDriver_rating() {
            return driver_rating;
        }

        public void setDriver_rating(String driver_rating) {
            this.driver_rating = driver_rating;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getWork_address() {
            return work_address;
        }

        public void setWork_address(String work_address) {
            this.work_address = work_address;
        }

        public String getWork_lat() {
            return work_lat;
        }

        public void setWork_lat(String work_lat) {
            this.work_lat = work_lat;
        }

        public String getWork_lon() {
            return work_lon;
        }

        public void setWork_lon(String work_lon) {
            this.work_lon = work_lon;
        }

        public String getEmergency_name() {
            return emergency_name;
        }

        public void setEmergency_name(String emergency_name) {
            this.emergency_name = emergency_name;
        }

        public String getEmergency_mobile() {
            return emergency_mobile;
        }

        public void setEmergency_mobile(String emergency_mobile) {
            this.emergency_mobile = emergency_mobile;
        }

        public String getEmergency_email() {
            return emergency_email;
        }

        public void setEmergency_email(String emergency_email) {
            this.emergency_email = emergency_email;
        }

        public String getMute_music() {
            return mute_music;
        }

        public void setMute_music(String mute_music) {
            this.mute_music = mute_music;
        }

        public String getMute_request_sound() {
            return mute_request_sound;
        }

        public void setMute_request_sound(String mute_request_sound) {
            this.mute_request_sound = mute_request_sound;
        }

        public String getCar_detail_id() {
            return car_detail_id;
        }

        public void setCar_detail_id(String car_detail_id) {
            this.car_detail_id = car_detail_id;
        }

        public String getOn_way_ride() {
            return on_way_ride;
        }

        public void setOn_way_ride(String on_way_ride) {
            this.on_way_ride = on_way_ride;
        }

        public String getComplete_rides() {
            return complete_rides;
        }

        public void setComplete_rides(String complete_rides) {
            this.complete_rides = complete_rides;
        }

        public String getPlan_name() {
            return plan_name;
        }

        public void setPlan_name(String plan_name) {
            this.plan_name = plan_name;
        }

        public String getPlan_amount() {
            return plan_amount;
        }

        public void setPlan_amount(String plan_amount) {
            this.plan_amount = plan_amount;
        }

        public String getPlan_total_ride() {
            return plan_total_ride;
        }

        public void setPlan_total_ride(String plan_total_ride) {
            this.plan_total_ride = plan_total_ride;
        }

        public String getPlan_ride_fees() {
            return plan_ride_fees;
        }

        public void setPlan_ride_fees(String plan_ride_fees) {
            this.plan_ride_fees = plan_ride_fees;
        }

        public String getPlan_bonus_trip() {
            return plan_bonus_trip;
        }

        public void setPlan_bonus_trip(String plan_bonus_trip) {
            this.plan_bonus_trip = plan_bonus_trip;
        }

        public String getRide_count() {
            return ride_count;
        }

        public void setRide_count(String ride_count) {
            this.ride_count = ride_count;
        }

        public String getProfile_image() {
            return profile_image;
        }

        public void setProfile_image(String profile_image) {
            this.profile_image = profile_image;
        }

        public String getDriver_lisence_img() {
            return driver_lisence_img;
        }

        public void setDriver_lisence_img(String driver_lisence_img) {
            this.driver_lisence_img = driver_lisence_img;
        }

        public String getCar_regist_img() {
            return car_regist_img;
        }

        public void setCar_regist_img(String car_regist_img) {
            this.car_regist_img = car_regist_img;
        }

        public String getVehicle_regist_img() {
            return vehicle_regist_img;
        }

        public void setVehicle_regist_img(String vehicle_regist_img) {
            this.vehicle_regist_img = vehicle_regist_img;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return this.id;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getUser_name() {
            return this.user_name;
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

        public void setCust_id(String cust_id) {
            this.cust_id = cust_id;
        }

        public String getCust_id() {
            return this.cust_id;
        }

        public void setStripe_account_id(String stripe_account_id) {
            this.stripe_account_id = stripe_account_id;
        }

        public String getStripe_account_id() {
            return this.stripe_account_id;
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

        public void setPhone_code(String phone_code) {
            this.phone_code = phone_code;
        }

        public String getPhone_code() {
            return this.phone_code;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getImage() {
            return this.image;
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

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStatus() {
            return this.status;
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

        public void setCity(String city) {
            this.city = city;
        }

        public String getCity() {
            return this.city;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getType() {
            return this.type;
        }

        public void setCar_id(String car_id) {
            this.car_id = car_id;
        }

        public String getCar_id() {
            return this.car_id;
        }

        public void setCar_type_id(String car_type_id) {
            this.car_type_id = car_type_id;
        }

        public String getCar_type_id() {
            return this.car_type_id;
        }

        public void setOnline_status(String online_status) {
            this.online_status = online_status;
        }

        public String getOnline_status() {
            return this.online_status;
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

        public void setLang(String lang) {
            this.lang = lang;
        }

        public String getLang() {
            return this.lang;
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

        public void setCar_model(String car_model) {
            this.car_model = car_model;
        }

        public String getCar_model() {
            return this.car_model;
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

        public void setCar_number(String car_number) {
            this.car_number = car_number;
        }

        public String getCar_number() {
            return this.car_number;
        }

        public void setCar_image(String car_image) {
            this.car_image = car_image;
        }

        public String getCar_image() {
            return this.car_image;
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

        public void setStep(String step) {
            this.step = step;
        }

        public String getStep() {
            return this.step;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getBrand() {
            return this.brand;
        }
    }


}