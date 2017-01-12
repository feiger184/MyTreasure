package com.feicui.mytreasure.net;

import com.feicui.mytreasure.treasure.Area;
import com.feicui.mytreasure.treasure.Treasure;
import com.feicui.mytreasure.user.User;
import com.feicui.mytreasure.user.login.LoginResult;
import com.feicui.mytreasure.user.register.RegisterResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * 请求构建接口
 */

public interface TreasureApi {

    @POST("/Handler/UserHandler.ashx?action=login")
    Call<LoginResult> login(@Body User user);

    @POST("/Handler/UserHandler.ashx?action=register")
    Call<RegisterResult> register(@Body User user);

    // 获取区域内的宝藏数据请求
    @POST("/Handler/TreasureHandler.ashx?action=show")
    Call<List<Treasure>> getTreasureInArea(@Body Area area);

}

