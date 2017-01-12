package com.feicui.mytreasure.net;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络请求类
 */

public class NetClient {

    private static NetClient netClient;
    public static final String BASE_URL = "http://admin.syfeicuiedu.com";
    private final Retrofit retrofit;
    private TreasureApi treasureApi;

    public static synchronized NetClient getInstance() {

        if (netClient == null) {
            netClient = new NetClient();
        }

        return netClient;
    }

    private NetClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        //初始化retrofit
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public TreasureApi getTreasureApi() {
        if (treasureApi == null) {
            treasureApi = retrofit.create(TreasureApi.class);
        }
        return treasureApi;
    }
}
