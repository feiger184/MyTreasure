package com.feicui.mytreasure.user.register;

/**
 * 注册业务视图接口
 */

public interface RegisterView {

    void showProgress();

    void hideProgress();

    void showMessage(String msg);

    void navigationToHome();

}

