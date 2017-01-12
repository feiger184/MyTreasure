package com.feicui.mytreasure.user.login;

/**
 * 登录业务接口
 */

public interface LoginView {

    void showProgress();

    void hideProgress();

    void showMessage(String msg);

    void navigationToHome();
}
