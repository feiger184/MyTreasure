package com.feicui.mytreasure.user.register;

import com.google.gson.annotations.SerializedName;

/**
 * 注册结果实体类
 */

public class RegisterResult {

    /**
     * errcode : 1
     * errmsg : 登录成功！
     * tokenid : 171
     */

    @SerializedName("errcode")
    private int code;
    @SerializedName("errmsg")
    private String msg;
    @SerializedName("tokenid")
    private int tokenId;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getTokenId() {
        return tokenId;
    }

    public void setTokenId(int tokenId) {
        this.tokenId = tokenId;
    }
}
