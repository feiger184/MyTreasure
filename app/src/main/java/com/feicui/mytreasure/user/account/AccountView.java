package com.feicui.mytreasure.user.account;

/**
 * 个人信息业务视图接口
 */

public interface AccountView {

    void showProgress();

    void hideProgress();

    void showMessage(String msg);

    void updatePhoto(String photoUrl);

}
