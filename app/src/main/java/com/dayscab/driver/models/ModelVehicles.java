package com.dayscab.driver.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelVehicles implements Serializable {

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

        private String driver_id;

        private String car_model;

        private String brand;

        private String car_type_id;

        private String car_number;

        private String year_of_manufacture;

        private String car_color;

        private String car_image;

        private String status;

        private String model_name;

        private String brand_name;

        private String car_name;

        private String car_regist_img;

        private String vehicle_regist_img;

        private String car_regist_date;

        private String vehicle_inspection_img;

        private String vehicle_regist_date;

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

        public String getCar_regist_date() {
            return car_regist_date;
        }

        public void setCar_regist_date(String car_regist_date) {
            this.car_regist_date = car_regist_date;
        }

        public String getVehicle_inspection_img() {
            return vehicle_inspection_img;
        }

        public void setVehicle_inspection_img(String vehicle_inspection_img) {
            this.vehicle_inspection_img = vehicle_inspection_img;
        }

        public String getVehicle_regist_date() {
            return vehicle_regist_date;
        }

        public void setVehicle_regist_date(String vehicle_regist_date) {
            this.vehicle_regist_date = vehicle_regist_date;
        }

        public String getModel_name() {
            return model_name;
        }

        public void setModel_name(String model_name) {
            this.model_name = model_name;
        }

        public String getBrand_name() {
            return brand_name;
        }

        public void setBrand_name(String brand_name) {
            this.brand_name = brand_name;
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

        public void setDriver_id(String driver_id) {
            this.driver_id = driver_id;
        }

        public String getDriver_id() {
            return this.driver_id;
        }

        public void setCar_model(String car_model) {
            this.car_model = car_model;
        }

        public String getCar_model() {
            return this.car_model;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getBrand() {
            return this.brand;
        }

        public void setCar_type_id(String car_type_id) {
            this.car_type_id = car_type_id;
        }

        public String getCar_type_id() {
            return this.car_type_id;
        }

        public void setCar_number(String car_number) {
            this.car_number = car_number;
        }

        public String getCar_number() {
            return this.car_number;
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

        public void setCar_image(String car_image) {
            this.car_image = car_image;
        }

        public String getCar_image() {
            return this.car_image;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStatus() {
            return this.status;
        }
    }

}
