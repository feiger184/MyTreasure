package com.feicui.mytreasure.treasure.detail;

import com.feicui.mytreasure.net.NetClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 宝藏详情的业务类
 */

public class TreasureDetailPresenter {

    private TreasureDetailView treasureDetailView;
    public TreasureDetailPresenter(TreasureDetailView detailView) {
        treasureDetailView = detailView;
    }

    public void getTreasureDetail(TreasureDetail treasureDetail) {
        Call<List<TreasureDetailResult>> treasureDetail1Call = NetClient.getInstance().getTreasureApi().getTreasureDetail(treasureDetail);
        treasureDetail1Call.enqueue(treasureDetailCallBack);
    }

    private Callback<List<TreasureDetailResult>> treasureDetailCallBack = new Callback<List<TreasureDetailResult>>() {
        @Override
        public void onResponse(Call<List<TreasureDetailResult>> call, Response<List<TreasureDetailResult>> response) {
            if (response.isSuccessful()){
                List<TreasureDetailResult> resultList = response.body();
                if (resultList==null){
                    // 弹个吐司说明一下
                    treasureDetailView.showMessage("未知的错误");
                    return;
                }
                // 数据获取到了，要给视图设置上(TextView上展示)
                treasureDetailView.setData(resultList);
            }

        }

        @Override
        public void onFailure(Call<List<TreasureDetailResult>> call, Throwable t) {
            // 提示信息：请求失败
            treasureDetailView.showMessage("请求失败了"+t.getMessage());
        }
    };
}
