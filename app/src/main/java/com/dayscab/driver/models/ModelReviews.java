package com.dayscab.driver.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelReviews implements Serializable {

    private ArrayList<Result> result;
    private String message;
    private int status;

    public void setResult(ArrayList<Result> result){
        this.result = result;
    }
    public ArrayList<Result> getResult(){
        return this.result;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
    public void setStatus(int status){
        this.status = status;
    }
    public int getStatus(){
        return this.status;
    }

    public class Result implements Serializable
    {
        private String id;

        private String request_id;

        private String driver_id;

        private String user_id;

        private String rating;

        private String review;

        private String date_time;

        private String user_name;

        private String image;

        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
        }
        public void setRequest_id(String request_id){
            this.request_id = request_id;
        }
        public String getRequest_id(){
            return this.request_id;
        }
        public void setDriver_id(String driver_id){
            this.driver_id = driver_id;
        }
        public String getDriver_id(){
            return this.driver_id;
        }
        public void setUser_id(String user_id){
            this.user_id = user_id;
        }
        public String getUser_id(){
            return this.user_id;
        }
        public void setRating(String rating){
            this.rating = rating;
        }
        public String getRating(){
            return this.rating;
        }
        public void setReview(String review){
            this.review = review;
        }
        public String getReview(){
            return this.review;
        }
        public void setDate_time(String date_time){
            this.date_time = date_time;
        }
        public String getDate_time(){
            return this.date_time;
        }
        public void setUser_name(String user_name){
            this.user_name = user_name;
        }
        public String getUser_name(){
            return this.user_name;
        }
        public void setImage(String image){
            this.image = image;
        }
        public String getImage(){
            return this.image;
        }
    }


}
