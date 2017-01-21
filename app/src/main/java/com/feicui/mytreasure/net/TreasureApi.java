package com.feicui.mytreasure.net;

import com.feicui.mytreasure.treasure.Area;
import com.feicui.mytreasure.treasure.Treasure;
import com.feicui.mytreasure.treasure.detail.TreasureDetail;
import com.feicui.mytreasure.treasure.detail.TreasureDetailResult;
import com.feicui.mytreasure.treasure.hide.HideTreasure;
import com.feicui.mytreasure.treasure.hide.HideTreasureResult;
import com.feicui.mytreasure.user.User;
import com.feicui.mytreasure.user.account.Update;
import com.feicui.mytreasure.user.account.UpdateResult;
import com.feicui.mytreasure.user.account.UploadResult;
import com.feicui.mytreasure.user.login.LoginResult;
import com.feicui.mytreasure.user.register.RegisterResult;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

import static com.baidu.location.b.g.U;
import static com.baidu.location.b.g.p;

/**
 * 请求构建接口
 */

public interface TreasureApi {

    //登录请求
    @POST("/Handler/UserHandler.ashx?action=login")
    Call<LoginResult> login(@Body User user);

    //注册请求
    @POST("/Handler/UserHandler.ashx?action=register")
    Call<RegisterResult> register(@Body User user);

    // 获取区域内的宝藏数据请求
    @POST("/Handler/TreasureHandler.ashx?action=show")
    Call<List<Treasure>> getTreasureInArea(@Body Area area);

    // 埋藏宝藏的请求
    @POST("/Handler/TreasureHandler.ashx?action=hide")
    Call<HideTreasureResult> hideTreasure(@Body HideTreasure hideTreasure);


    // 宝藏详情的请求
    @POST("/Handler/TreasureHandler.ashx?action=tdetails")
    Call<List<TreasureDetailResult>> getTreasureDetail(@Body TreasureDetail treasureDetail);

    // 两种方式
    @Multipart
    @POST("/Handler/UserLoadPicHandler1.ashx")
    Call<UploadResult> upload(@Part("file\";filename=\"image.png\"") RequestBody body);

    @Multipart
    @POST("/Handler/UserLoadPicHandler1.ashx")
    Call<UploadResult> upload(@Part MultipartBody.Part part);

    // 更新头像
    @POST("/Handler/UserHandler.ashx?action=update")
    Call<UpdateResult> update(@Body Update update);

}

