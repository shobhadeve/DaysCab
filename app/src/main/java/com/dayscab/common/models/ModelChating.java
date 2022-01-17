package com.dayscab.common.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelChating implements Serializable {

    private ArrayList<Result> result;
    private String message;
    private String status;

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
    public void setStatus(String status){
        this.status = status;
    }
    public String getStatus(){
        return this.status;
    }

    public class Result implements Serializable{
        private String sender_id;
        private String receiver_id;
        private String chat_message;
        private String date_time;
        private String chat_image;
        private String chat_audio;
        private String chat_video;
        private String chat_document;
        private String lat;
        private String lon;
        private String name;
        private String contact;
        private String clear_chat;
        private String status;
        private String date;
        private String id;
        private String result;
        private Sender_detail sender_detail;
        private Receiver_detail receiver_detail;


        public String getDate_time() {
            return date_time;
        }

        public void setDate_time(String date_time) {
            this.date_time = date_time;
        }

        public void setSender_id(String sender_id) {
            this.sender_id = sender_id;
        }

        public String getSender_id() {
            return this.sender_id;
        }

        public void setReceiver_id(String receiver_id) {
            this.receiver_id = receiver_id;
        }

        public String getReceiver_id() {
            return this.receiver_id;
        }

        public void setChat_message(String chat_message) {
            this.chat_message = chat_message;
        }

        public String getChat_message() {
            return this.chat_message;
        }

        public void setChat_image(String chat_image) {
            this.chat_image = chat_image;
        }

        public String getChat_image() {
            return this.chat_image;
        }

        public void setChat_audio(String chat_audio) {
            this.chat_audio = chat_audio;
        }

        public String getChat_audio() {
            return this.chat_audio;
        }

        public void setChat_video(String chat_video) {
            this.chat_video = chat_video;
        }

        public String getChat_video() {
            return this.chat_video;
        }

        public void setChat_document(String chat_document) {
            this.chat_document = chat_document;
        }

        public String getChat_document() {
            return this.chat_document;
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

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

        public String getContact() {
            return this.contact;
        }

        public void setClear_chat(String clear_chat) {
            this.clear_chat = clear_chat;
        }

        public String getClear_chat() {
            return this.clear_chat;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStatus() {
            return this.status;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getDate() {
            return this.date;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return this.id;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getResult() {
            return this.result;
        }

        public void setSender_detail(Sender_detail sender_detail) {
            this.sender_detail = sender_detail;
        }

        public Sender_detail getSender_detail() {
            return this.sender_detail;
        }

        public void setReceiver_detail(Receiver_detail receiver_detail) {
            this.receiver_detail = receiver_detail;
        }

        public Receiver_detail getReceiver_detail() {
            return this.receiver_detail;
        }

        public class Sender_detail {
            private String id;

            private String username;

            private String mobile;

            private String email;

            private String password;

            private String address;

            private String image;

            private String lat;

            private String lon;

            private String register_id;

            private String date_time;

            private String sender_image;

            public void setId(String id) {
                this.id = id;
            }

            public String getId() {
                return this.id;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getUsername() {
                return this.username;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public String getMobile() {
                return this.mobile;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getEmail() {
                return this.email;
            }

            public void setPassword(String password) {
                this.password = password;
            }

            public String getPassword() {
                return this.password;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getAddress() {
                return this.address;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public String getImage() {
                return this.image;
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

            public void setSender_image(String sender_image) {
                this.sender_image = sender_image;
            }

            public String getSender_image() {
                return this.sender_image;
            }
        }

        public class Receiver_detail {
            private String id;

            private String username;

            private String mobile;

            private String email;

            private String password;

            private String address;

            private String image;

            private String lat;

            private String lon;

            private String register_id;

            private String date_time;

            private String receiver_image;

            public void setId(String id) {
                this.id = id;
            }

            public String getId() {
                return this.id;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getUsername() {
                return this.username;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public String getMobile() {
                return this.mobile;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getEmail() {
                return this.email;
            }

            public void setPassword(String password) {
                this.password = password;
            }

            public String getPassword() {
                return this.password;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getAddress() {
                return this.address;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public String getImage() {
                return this.image;
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

            public void setReceiver_image(String receiver_image) {
                this.receiver_image = receiver_image;
            }

            public String getReceiver_image() {
                return this.receiver_image;
            }
        }

    }


}
