package com.feicui.mytreasure.user.register;

import com.feicui.mytreasure.net.NetClient;
import com.feicui.mytreasure.user.User;
import com.feicui.mytreasure.user.UserPrefs;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *  注册业务类
 */

public class RegisterPresenter {

    private RegisterView registerView;

    public RegisterPresenter(RegisterView registerView) {
        this.registerView = registerView;
    }

    public void register(User user){
        Call<RegisterResult> registerCall = NetClient.getInstance().getTreasureApi().register(user);
        registerCall.enqueue(callBack);
    }

    private Callback<RegisterResult> callBack = new Callback<RegisterResult>() {
        @Override
        public void onResponse(Call<RegisterResult> call, Response<RegisterResult> response) {
            registerView.hideProgress();
            RegisterResult body = response.body();
            if (body == null) {
                registerView.showMessage("发生未知错误");
            }
            if (body.getCode() == 1) {
                // 保存用户token
                UserPrefs.getInstance().setTokenid(body.getTokenId());
                registerView.navigationToHome();
            }
            registerView.showMessage(body.getMsg());
        }

        @Override
        public void onFailure(Call<RegisterResult> call, Throwable t) {
            registerView.hideProgress();
            registerView.showMessage("注册失败：" + t.getMessage());
        }
    };

}
