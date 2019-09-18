package com.example.instagram;

import java.util.ArrayList;

public class Upload {
    private String mName;
    private String mImageUrl;
    private User mUser;
    private ArrayList<String> mHashtag;

    public Upload() {
        //empty constructor needed
    }

    public Upload(String name, String imageUrl) {
        if (name.trim().equals("")) {
            name = "No Name";
        }

        mName = name;
        mImageUrl = imageUrl;
    }
    public Upload(String name, String imageUrl,User user) {
        if (name.trim().equals("")) {
            name = "No Name";
        }

        mName = name;
        mImageUrl = imageUrl;
        mUser=user;
    }

    public Upload(String name, String imageUrl,User user,ArrayList<String> hashtag) {
        if (name.trim().equals("")) {
            name = "No Name";
        }

        mName = name;
        mImageUrl = imageUrl;
        mUser=user;
        mHashtag=hashtag;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }
    public User getmUser() {
        return mUser;
    }

    public void setmUser(User mUser) {
        this.mUser = mUser;
    }
    public ArrayList<String> getmHashtag() {
        return mHashtag;
    }

    public void setmHashtag(ArrayList<String> mHashtag) {
        this.mHashtag = mHashtag;
    }
}