package com.feicui.mytreasure.treasure.detail;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.utils.OpenClientUtil;
import com.feicui.mytreasure.R;
import com.feicui.mytreasure.commons.ActivityUtils;
import com.feicui.mytreasure.custom.TreasureView;
import com.feicui.mytreasure.treasure.Treasure;
import com.feicui.mytreasure.treasure.map.MapFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 宝藏详情界面
 */

public class TreasureDetailActivity extends AppCompatActivity implements TreasureDetailView{


    @BindView(R.id.iv_navigation)//右上角提交按钮
            ImageView ivNavigation;
    @BindView(R.id.toolbar)
            Toolbar toolbar;
    @BindView(R.id.frameLayout)//上方头像
            FrameLayout frameLayout;
    @BindView(R.id.detail_treasure)//卡片信息
            TreasureView detailTreasure;
    @BindView(R.id.tv_detail_description)//宝藏详情
            TextView tvDetailDescription;

    private static final String KEY_TREASURE = "key_treasure";
    private ActivityUtils activityUtils;
    private Treasure treature;
    private TreasureDetailPresenter treasureDetailPresenter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure_detail);
    }

    /*
    * 对外提供一个跳转到本页面的方法
    * */
    public static void open(Context context, Treasure treasure) {
        Intent intent = new Intent(context, TreasureDetailActivity.class);
        intent.putExtra(KEY_TREASURE, treasure);
        context.startActivity(intent);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        ButterKnife.bind(this);

        activityUtils = new ActivityUtils(this);
        treasureDetailPresenter = new TreasureDetailPresenter(this);
        //拿到传递过来的数据
        treature = (Treasure) getIntent().getSerializableExtra(KEY_TREASURE);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {

            getSupportActionBar().setTitle(treature.getTitle());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 地图和宝藏的展示
        initMapView();

        //宝藏卡片的视图展示
        detailTreasure.bindTreasure(treature);

        //去进行网络获取得到宝藏的详情
        TreasureDetail treasureDetail = new TreasureDetail(treature.getId());
        treasureDetailPresenter.getTreasureDetail(treasureDetail);


    }


    /*
    * 地图和宝藏的展示
    * */
    private void initMapView() {
        LatLng latLng = new LatLng(treature.getLatitude(), treature.getLongitude());

        MapStatus mapStatus = new MapStatus.Builder()
                .rotate(0)
                .overlook(0)
                .target(latLng)
                .zoom(18)
                .build();

        BaiduMapOptions options = new BaiduMapOptions()
                .mapStatus(mapStatus)
                .compassEnabled(false)
                .scaleControlEnabled(false)
                .scrollGesturesEnabled(false)
                .zoomControlsEnabled(false)
                .zoomGesturesEnabled(false)
                .rotateGesturesEnabled(false);

        MapView mapView = new MapView(this, options);

        frameLayout.addView(mapView);

        BaiduMap map = mapView.getMap();

        BitmapDescriptor dot_expand = BitmapDescriptorFactory.fromResource(R.mipmap.treasure_expanded);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(dot_expand)
                .anchor(0.5f, 0.5f);
        map.addOverlay(markerOptions);

    }


    //处理toolbar上面的返回箭头
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    //导航的图标的点击事件
    @OnClick(R.id.iv_navigation)
    public void showPopupMenu(View view) {

        PopupMenu popupMenu = new PopupMenu(this, view);

        popupMenu.inflate(R.menu.menu_navigation);

        popupMenu.setOnMenuItemClickListener(menuItemListener);

        popupMenu.show();
    }

    private PopupMenu.OnMenuItemClickListener menuItemListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {


            LatLng start = MapFragment.getMyLocation();
            String startAddr = MapFragment.getLocationAddr();

            LatLng end = new LatLng(treature.getLatitude(), treature.getLongitude());
            String endAddr = treature.getLocation();

            switch (item.getItemId()) {

                case R.id.walking_navi:
                    // 开始步行导航
                    startWalkingNavi(start,startAddr,end,endAddr);
                    break;

                case R.id.biking_navi:
                    // 开始骑行导航
                    startBikingNavi(start,startAddr,end,endAddr);
                    break;
            }
            return false;
        }

    };

    /*
    * 开始步行导航
    * */
    private void startWalkingNavi(LatLng start, String startAddr, LatLng end, String endAddr) {

        NaviParaOption naviParaOption = new NaviParaOption()
                .startPoint(start)
                .startName(startAddr)
                .endPoint(end)
                .endName(endAddr);

        boolean walkNavi = BaiduMapNavigation.openBaiduMapWalkNavi(naviParaOption, this);

        // 未开启成功
        if (!walkNavi){
            startWebNavi(start, startAddr, end, endAddr);
        }

    }


    /*
    * 开启网页导航
    * */
    private void startWebNavi(LatLng startPoint,String startAddr,LatLng endPoint,String endAddr) {
        // 导航的起点和终点的设置
        NaviParaOption option = new NaviParaOption()
                .startName(startAddr)
                .startPoint(startPoint)
                .endName(endAddr)
                .endPoint(endPoint);

        // 开启导航
        BaiduMapNavigation.openWebBaiduMapNavi(option, this);
    }


    /*
    * 开始骑行导航
    * */
    public void startBikingNavi(LatLng startPoint,String startAddr,LatLng endPoint,String endAddr) {
        // 导航的起点和终点的设置
        NaviParaOption option = new NaviParaOption()
                .startName(startAddr)
                .startPoint(startPoint)
                .endName(endAddr)
                .endPoint(endPoint);

        // 开启导航
        boolean walkNavi = BaiduMapNavigation.openBaiduMapBikeNavi(option, this);

        // 未开启成功
        if (!walkNavi){
            showDialog();
        }
    }


    /*
    * 显示一个对话框提示没有安装百度地图
    * */
    public void showDialog(){
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("您未安装百度地图的APP或版本过低，要不要安装呢？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OpenClientUtil.getLatestBaiduMapApp(TreasureDetailActivity.this);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    @Override
    public void showMessage(String msg) {
        activityUtils.showToast(msg);
    }

    @Override
    public void setData(List<TreasureDetailResult> resultList) {

        // 请求的数据有内容
        if (resultList.size() >= 1) {
            TreasureDetailResult result = resultList.get(0);
            tvDetailDescription.setText(result.description);
            return;
        }
        tvDetailDescription.setText("当前的宝藏没有详情信息");
    }
}
