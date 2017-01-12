package com.feicui.mytreasure.user.login;

import com.feicui.mytreasure.net.NetClient;
import com.feicui.mytreasure.user.User;
import com.feicui.mytreasure.user.UserPrefs;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 登录业务类
 */

public class LoginPresenter {
    private LoginView loginView;

    public LoginPresenter(LoginView loginView) {
        this.loginView = loginView;
    }

    public void login(User user) {
        loginView.showProgress();
        Call<LoginResult> loginResultCall = NetClient.getInstance().getTreasureApi().login(user);
        loginResultCall.enqueue(callBack);
    }
    private Callback<LoginResult> callBack = new Callback<LoginResult>() {
        @Override
        public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
            loginView.hideProgress();
            if (response.isSuccessful()) {
                LoginResult body = response.body();
                if (body == null) {
                    loginView.showMessage("发生未知错误");
                }
                if (body.getCode() == 1) {

                    // 保存头像和tokenId
                    UserPrefs.getInstance().setPhoto(NetClient.BASE_URL+body.getHeadpic());
                    UserPrefs.getInstance().setTokenid(body.getTokenid());
                    loginView.navigationToHome();
                }

                loginView.showMessage(body.getMsg());
            }
        }

        @Override
        public void onFailure(Call<LoginResult> call, Throwable t) {

            loginView.hideProgress();
            loginView.showMessage("请求失败："+t.getMessage());

        }
    };
}
