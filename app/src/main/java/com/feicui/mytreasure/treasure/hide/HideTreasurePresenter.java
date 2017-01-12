package com.feicui.mytreasure.treasure.hide;

import com.feicui.mytreasure.net.NetClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 埋藏宝藏的业务类
 */

public class HideTreasurePresenter {

    // 过程中与视图的交互？？
    private HideTreasureView mHideView;

    public HideTreasurePresenter(HideTreasureView hideView) {
        mHideView = hideView;
    }

    public void hideTreasure(HideTreasure hideTreasure) {

        // 显示进度
        mHideView.showProgress();

        Call<HideTreasureResult> resultCall = NetClient.getInstance().getTreasureApi().hideTreasure(hideTreasure);
        resultCall.enqueue(mResultCallback);
    }

    private Callback<HideTreasureResult> mResultCallback = new Callback<HideTreasureResult>() {
        @Override
        public void onResponse(Call<HideTreasureResult> call, Response<HideTreasureResult> response) {
            //隐藏进度
            mHideView.hideProgress();

            if (response.isSuccessful()) {
                HideTreasureResult body = response.body();
                if (body == null) {

                    mHideView.showMessage("未知错误");
                }

                if (body.getCode() == 1) {
                    mHideView.navigationToHome();
                }

                mHideView.showMessage(body.getMsg());
            }
        }

        @Override
        public void onFailure(Call<HideTreasureResult> call, Throwable t) {
            // 隐藏进度
            mHideView.hideProgress();
            // 提示：
            mHideView.showMessage("请求失败" + t.getMessage());
        }
    };
}


