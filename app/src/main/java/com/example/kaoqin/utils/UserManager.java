package com.example.kaoqin.utils;

import com.example.kaoqin.Data.User;

public class UserManager {
    private static User mUser;

    public static User getCurrentUser(){
        if (mUser == null){
            mUser = new User();
        }
        return mUser;
    }

    public static void setCurrentUser(User user){
        mUser = user;
    }

    public static void clear(){
        mUser = null;
    }
}
