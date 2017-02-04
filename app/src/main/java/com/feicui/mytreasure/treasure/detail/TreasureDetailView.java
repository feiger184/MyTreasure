package com.feicui.mytreasure.treasure.detail;

import java.util.List;

/**
 * 宝藏详情获取的视图接口
 */

public interface TreasureDetailView {

    // 显示信息
    void showMessage(String msg);

    // 设置数据
    void setData(List<TreasureDetailResult> resultList);

}
