package com.dayscab.common.models;

import java.io.Serializable;
import java.util.ArrayList;

public class AccountModel implements Serializable {

    private ArrayList<Result> result;
    private String status;
    private String message;

    public void setResult(ArrayList<Result> result){
        this.result = result;
    }
    public ArrayList<Result> getResult(){
        return this.result;
    }
    public void setStatus(String status){
        this.status = status;
    }
    public String getStatus(){
        return this.status;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }

    public class Result implements Serializable
    {
        private String id;

        private String question;

        private String answer;

        private String date_time;

        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
        }
        public void setQuestion(String question){
            this.question = question;
        }
        public String getQuestion(){
            return this.question;
        }
        public void setAnswer(String answer){
            this.answer = answer;
        }
        public String getAnswer(){
            return this.answer;
        }
        public void setDate_time(String date_time){
            this.date_time = date_time;
        }
        public String getDate_time(){
            return this.date_time;
        }
    }


}
