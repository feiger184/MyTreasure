package com.feicui.mytreasure.treasure.hide;

/**
 * 埋藏宝藏业务接口
 */

public interface HideTreasureView {

    // 宝藏上传中视图的交互

    void showProgress();

    void hideProgress();

    void showMessage(String msg);

    void navigationToHome();

}
