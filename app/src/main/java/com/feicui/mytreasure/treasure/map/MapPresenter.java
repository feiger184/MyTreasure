package com.feicui.mytreasure.treasure.map;

import com.feicui.mytreasure.net.NetClient;
import com.feicui.mytreasure.treasure.Area;
import com.feicui.mytreasure.treasure.Treasure;
import com.feicui.mytreasure.treasure.TreasureRepo;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 获取宝藏数据的业务类
 */

public class MapPresenter {
    private MapMvpView mapMvpView;
    private Area mArea;

    public MapPresenter(MapMvpView mapMvpView) {
        this.mapMvpView = mapMvpView;
    }

    public void getTreasure(Area area) {

        if (TreasureRepo.getInstance().isCached(area)) {
            return;
        }
        this.mArea = area;
        Call<List<Treasure>> listCall = NetClient.getInstance().getTreasureApi().getTreasureInArea(area);
        listCall.enqueue(listCallBack);
    }

    private Callback<List<Treasure>> listCallBack =new Callback<List<Treasure>>() {
        @Override
        public void onResponse(Call<List<Treasure>> call, Response<List<Treasure>> response) {
            if (response.isSuccessful()) {
                List<Treasure> treasureList = response.body();
                if (treasureList == null) {
                    mapMvpView.showMessage("发生未知错误");
                    return;
                }
                TreasureRepo.getInstance().addTreasure(treasureList);
                TreasureRepo.getInstance().cache(mArea);
                mapMvpView.setData(treasureList);

            }

        }

        @Override
        public void onFailure(Call<List<Treasure>> call, Throwable t) {
            mapMvpView.showMessage("请求失败"+t.getMessage());

        }
    };


}
