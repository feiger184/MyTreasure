package com.feicui.mytreasure.user;

import com.google.gson.annotations.SerializedName;

/**
 * 用户请求体
 */

public class User {


    /**
     * UserName : qjd
     * Password : 654321
     */

    @SerializedName("UserName")
    private String userName;
    @SerializedName("Password")
    private String passWord;

    public User(String userName, String passWord) {
        this.userName = userName;
        this.passWord = passWord;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
}
