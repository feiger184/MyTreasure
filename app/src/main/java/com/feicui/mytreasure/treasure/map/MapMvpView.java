package com.feicui.mytreasure.treasure.map;

import com.feicui.mytreasure.treasure.Treasure;

import java.util.List;

/**
 * 宝藏接口
 */

public interface MapMvpView {
    void showMessage(String msg);// 弹吐司
    void setData(List<Treasure> list);// 设置数据
}
