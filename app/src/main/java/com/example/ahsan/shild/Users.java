package com.example.ahsan.shild;

/**
 * Created by ahsan on 1/19/18.
 */

public class Users {

    public String name,image,bio;

    public Users() {

    }

    public Users(String name, String image, String status) {
        this.name = name;
        this.image = image;
        this.bio = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return bio;
    }

    public void setStatus(String status) {
        this.bio = status;
    }

}
