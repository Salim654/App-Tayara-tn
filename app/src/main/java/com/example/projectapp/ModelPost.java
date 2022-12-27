package com.example.projectapp;

public class ModelPost {
    String postid,title,description,imglink,price,phone,userid;

    public ModelPost() {
    }

    public ModelPost(String title, String description,String postid, String imglink, String price, String phone, String userid) {

        this.title = title;
        this.description = description;
        this.imglink = imglink;
        this.price = price;
        this.phone = phone;
        this.userid = userid;
        this.postid=postid;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImglink() {
        return imglink;
    }

    public void setImglink(String imglink) {
        this.imglink = imglink;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
