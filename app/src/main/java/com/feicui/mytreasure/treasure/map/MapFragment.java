package com.feicui.mytreasure.treasure.map;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.feicui.mytreasure.R;
import com.feicui.mytreasure.commons.ActivityUtils;
import com.feicui.mytreasure.custom.TreasureView;
import com.feicui.mytreasure.treasure.Area;
import com.feicui.mytreasure.treasure.Treasure;
import com.feicui.mytreasure.treasure.TreasureRepo;
import com.feicui.mytreasure.treasure.detail.TreasureDetailActivity;
import com.feicui.mytreasure.treasure.hide.HideTreasureActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 宝藏页面：地图的展示和宝藏数据的展示
 */

public class MapFragment extends Fragment implements MapMvpView {

    @BindView(R.id.iv_located)//藏在这里的图标
            ImageView ivLocated;
    @BindView(R.id.btn_HideHere)//藏在这里的按钮
            Button btnHideHere;
    @BindView(R.id.centerLayout)//藏宝的控件
            RelativeLayout centerLayout;
    @BindView(R.id.iv_scaleUp)//放大地图
            ImageView ivScaleUp;
    @BindView(R.id.iv_scaleDown)//缩小地图
            ImageView ivScaleDown;
    @BindView(R.id.tv_located)//点位按钮
            TextView tvLocated;
    @BindView(R.id.tv_satellite)//卫星地图
            TextView tvSatellite;
    @BindView(R.id.tv_compass)//指南
            TextView tvCompass;

    @BindView(R.id.tv_currentLocation) //标签中显示的当前位置
            TextView tvCurrentLocation;
    @BindView(R.id.iv_toTreasureInfo)//查看详细信息
            ImageView ivToTreasureInfo;
    @BindView(R.id.et_treasureTitle)//输入宝物信息
            EditText etTreasureTitle;

    @BindView(R.id.layout_bottom)//卡片id
            FrameLayout layoutBottom;
    @BindView(R.id.map_frame)//地图fragment
            FrameLayout mapFrame;

    @BindView(R.id.treasureView) //宝藏信息卡片
            TreasureView treasureView;

    @BindView(R.id.hide_treasure)//埋藏宝藏卡片
            RelativeLayout hideTreasure;

    private ActivityUtils activityUtils;
    private MapPresenter mapPresenter;
    private BaiduMap baiduMap;// 地图的操作类
    private LocationClient locationClient;
    private boolean mIsFirst = true;
    private static LatLng currentLocation; //当前位置经纬度
    private LatLng currentStatus;//当前状态
    private Marker currentMarker;//当前覆盖物

    //定位图标
    private BitmapDescriptor dot = BitmapDescriptorFactory.fromResource(R.mipmap.treasure_dot);
    private BitmapDescriptor expanded = BitmapDescriptorFactory.fromResource(R.mipmap.treasure_expanded);
    private GeoCoder geoCoder;
    private static String currentAddr;//当前位置

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mapPresenter = new MapPresenter(this);

        activityUtils = new ActivityUtils(this);
        // 初始化百度地图
        initMapView();

        // 初始化定位相关
        initLocation();

        // 地理编码的初始化相关
        initGeoCoder();

    }

    /*
    * 地理编码的初始化相关
    * */
    private void initGeoCoder() {

        // 初始化：创建出一个地理编码查询的对象
        geoCoder = GeoCoder.newInstance();
        // 设置查询结果的监听:地理编码的监听
        geoCoder.setOnGetGeoCodeResultListener(geoCoderResultListener);

    }

    /*
    * 地理编码的监听
    * */
    private OnGetGeoCoderResultListener geoCoderResultListener = new OnGetGeoCoderResultListener() {
        // 得到地理编码的结果：地址-->经纬度
        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

        }

        // 得到反向地理编码的结果：经纬度-->地址
        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

            if (reverseGeoCodeResult == null) {
                currentAddr = "未知位置";
                return;
            }

            String address = reverseGeoCodeResult.getAddress();
            tvCurrentLocation.setText(address);

        }
    };


    /*
    * 初始化百度地图
    * */
    private void initMapView() {
        MapStatus status = new MapStatus.Builder()
                .zoom(19)//地图缩放级别（3--21） 默认12
                .overlook(0)//地图俯仰角f
                .rotate(0)//旋转角度 逆时针旋转
                .build();


        BaiduMapOptions mapOptions = new BaiduMapOptions()
                .mapStatus(status)
                .compassEnabled(true)//是否设置指南针
                .zoomControlsEnabled(false)//不显示缩放的控件
                .zoomGesturesEnabled(true)//是否允许缩放手势
                .scaleControlEnabled(true)//是否显示比例尺
                ;

        //创建
        MapView mapView = new MapView(getContext(), mapOptions);

        // 在布局上添加地图控件：0，代表第一位
        mapFrame.addView(mapView, 0);

        // 拿到地图的操作类(控制器：操作地图等都是使用这个)
        baiduMap = mapView.getMap();

        // 设置地图状态的监听
        baiduMap.setOnMapStatusChangeListener(statusChangeListener);

        //设置地图上标注物的点击监听
        baiduMap.setOnMarkerClickListener(markerClickListener);
    }

    /*
    * 初始化定位相关
    * */
    private void initLocation() {

        //激活定位图层（前置）
        baiduMap.setMyLocationEnabled(true);

        //初始化LocationClient类
        locationClient = new LocationClient(getContext().getApplicationContext());

        //配置定位SDK参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//打开GPS
        option.setCoorType("bd09ll");//设置百度坐标类型，默认gcj02，会有偏差，bd09ll百度地图坐标类型，将无偏差的展示到地图上
        option.setIsNeedAddress(true);// 需要地址信息
        locationClient.setLocOption(option);
        locationClient.registerLocationListener(mBDLocationListener);
        locationClient.start();//开始定位
    }

    /*
      * 定位监听
      * */
    private BDLocationListener mBDLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            // 如果没有拿到结果，重新请求
            if (bdLocation == null) {
                locationClient.requestLocation();
                return;
            }

            double latitude = bdLocation.getLatitude();//维度
            double longitude = bdLocation.getLongitude();//经度

            currentLocation = new LatLng(latitude, longitude);
            currentAddr = bdLocation.getAddrStr();

            // 设置定位图层展示的数据
            MyLocationData data = new MyLocationData.Builder()
                    .latitude(latitude)
                    .longitude(longitude)
                    .accuracy(100f)//定位经度 单位：米
                    .build();

            // 定位数据展示到地图上
            baiduMap.setMyLocationData(data);

            // 移动到定位的地方，在地图上展示定位的信息：位置
            // 做一个判断：第一次进入页面自动移动，其他时候点击按钮移动
            if (mIsFirst) {
                moveToLocation();
                mIsFirst = false;
            }
        }
    };


    /*
    * 地图状态改变监听
    * */
    private BaiduMap.OnMapStatusChangeListener statusChangeListener = new BaiduMap.OnMapStatusChangeListener() {
        @Override
        public void onMapStatusChangeStart(MapStatus mapStatus) {


        }

        @Override
        public void onMapStatusChange(MapStatus mapStatus) {

        }

        @Override
        public void onMapStatusChangeFinish(MapStatus mapStatus) {

            LatLng target = mapStatus.target;

            if (target != MapFragment.this.currentStatus) {

                // 地图状态发生变化以后实时获取当前区域内的宝藏
                updateMapArea();

                // 在埋藏宝藏的情况下
                if (mUIMode == UI_MODE_HIDE) {

                    // 设置反地理编码的位置
                    ReverseGeoCodeOption option = new ReverseGeoCodeOption();
                    option.location(target);

                    // 发起反地理编码
                    geoCoder.reverseGeoCode(option);
                }
                MapFragment.this.currentStatus = target;
            }
        }
    };


    /*
    * 地图状态发生变化以后实时获取当前区域内的宝藏
    * */
    private void updateMapArea() {

        //当前地图状态
        MapStatus mapStatus = baiduMap.getMapStatus();

        double latitude = mapStatus.target.latitude;
        double longitude = mapStatus.target.longitude;

        Area area = new Area();
        area.setMaxLat(Math.ceil(latitude));
        area.setMaxLng(Math.ceil(longitude));
        area.setMinLat(Math.floor(latitude));
        area.setMinLng(Math.floor(longitude));

        mapPresenter.getTreasure(area);
    }

    /*
       * 设置地图上标注物的点击监听
       * */
    private BaiduMap.OnMarkerClickListener markerClickListener = new BaiduMap.OnMarkerClickListener() {

        @Override
        public boolean onMarkerClick(Marker marker) {
            if (currentMarker != null) {
                if (currentMarker != marker) {
                    currentMarker.setVisible(true);
                }
                currentMarker.setVisible(true);
            }
            currentMarker = marker;

            currentMarker.setVisible(false);

            InfoWindow infoWindow = new InfoWindow(expanded, marker.getPosition(), 0, new InfoWindow.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick() {
                    // 切换回普通的视图
                    changeUIMode(UI_MODE_NORMAL);
                }
            });

            //地图上显示一个InfoWindow
            baiduMap.showInfoWindow(infoWindow);

            int id = marker.getExtraInfo().getInt("id");
            Treasure treasure = TreasureRepo.getInstance().getTreasure(id);
            treasureView.bindTreasure(treasure);
            // 切换到宝藏选中视图
            changeUIMode(UI_MODE_SECLECT);

            return false;
        }
    };

    /*
    * 点击定位按钮
    * */
    @OnClick(R.id.tv_located)
    public void moveToLocation() {

        // 地图状态的设置：设置到定位的地方
        MapStatus mapStatus = new MapStatus.Builder()
                .target(currentLocation)// 定位的位置
                .rotate(0)
                .overlook(0)
                .build();
        // 更新状态
        MapStatusUpdate update = MapStatusUpdateFactory.newMapStatus(mapStatus);

        //更新展示的地图的状态
        baiduMap.animateMapStatus(update);
    }


    /*
    * 点击缩放按钮
    * */
    @OnClick({R.id.iv_scaleUp, R.id.iv_scaleDown})
    public void scaleMap(View view) {
        switch (view.getId()) {
            case R.id.iv_scaleUp:
                baiduMap.setMapStatus(MapStatusUpdateFactory.zoomIn());
                break;
            case R.id.iv_scaleDown:
                baiduMap.setMapStatus(MapStatusUpdateFactory.zoomOut());
                break;
        }
    }


    /*
    * 点击卫星地图
    * */
    @OnClick(R.id.tv_satellite)
    public void switchMapType() {

        int mapType = baiduMap.getMapType();//获取当前地图类型
        //切换类型
        mapType = (mapType == BaiduMap.MAP_TYPE_NORMAL) ? BaiduMap.MAP_TYPE_SATELLITE : BaiduMap.MAP_TYPE_NORMAL;
        String msg = mapType == BaiduMap.MAP_TYPE_NORMAL ? "卫星" : "普通";

        baiduMap.setMapType(mapType);

        tvSatellite.setText(msg);
    }


    /*
    * 点击指南针
    * */
    @OnClick(R.id.tv_compass)
    public void switchCompass() {
        boolean compassEnabled = baiduMap.getUiSettings().isCompassEnabled();
        baiduMap.getUiSettings().setCompassEnabled(!compassEnabled);

    }


    @OnClick(R.id.treasureView)
    public void clickTreasureView() {

        int id = currentMarker.getExtraInfo().getInt("id");
        Treasure treasure = TreasureRepo.getInstance().getTreasure(id);
        TreasureDetailActivity.open(getContext(), treasure);

    }

    // 点击宝藏标题录入的卡片，跳转埋藏宝藏的详细页面
    @OnClick(R.id.hide_treasure)
    public void hideTreasure() {
        String title = etTreasureTitle.getText().toString();
        if (TextUtils.isEmpty(title)) {
            activityUtils.showToast("请输入宝藏标题");
            return;
        }
        // 跳转到埋藏宝藏的详细页面
        LatLng latLng = baiduMap.getMapStatus().target;
        HideTreasureActivity.open(getContext(), title, currentAddr, latLng, 0);
    }

    /*
  * 添加覆盖物
  * */
    private void addMarker(LatLng latLng, int treasureId) {

        MarkerOptions options = new MarkerOptions();
        options.position(latLng);//覆盖物位置
        options.icon(dot);//覆盖物图标
        options.anchor(0.5f, 0.5f);//锚点位置：居中

        Bundle bundle = new Bundle();
        bundle.putInt("id", treasureId);
        options.extraInfo(bundle);

        // 添加覆盖物
        baiduMap.addOverlay(options);
    }

    private static final int UI_MODE_NORMAL = 0;// 普通的视图
    private static final int UI_MODE_SECLECT = 1;// 宝藏选中的视图
    private static final int UI_MODE_HIDE = 2;// 埋藏宝藏的视图

    private static int mUIMode = UI_MODE_NORMAL;

    // 把所有视图的变化都统一到一个方法里面:视图的切换是根据布局控件或其他(marker、infowindow)显示和隐藏来实现
    public void changeUIMode(int uiMode) {

        if (mUIMode == uiMode) return;
        mUIMode = uiMode;

        switch (uiMode) {
            // 普通的视图
            case UI_MODE_NORMAL:
                if (currentMarker != null) {
                    currentMarker.setVisible(true);
                }
                baiduMap.hideInfoWindow();
                layoutBottom.setVisibility(View.GONE);
                centerLayout.setVisibility(View.GONE);
                break;

            // 宝藏选中(信息卡片展示的视图)
            case UI_MODE_SECLECT:
                layoutBottom.setVisibility(View.VISIBLE);
                treasureView.setVisibility(View.VISIBLE);
                centerLayout.setVisibility(View.GONE);
                hideTreasure.setVisibility(View.GONE);
                break;

            // 宝藏埋藏的视图
            case UI_MODE_HIDE:
                if (currentMarker != null) {
                    currentMarker.setVisible(true);
                }
                baiduMap.hideInfoWindow();
                centerLayout.setVisibility(View.VISIBLE);
                layoutBottom.setVisibility(View.GONE);
                btnHideHere.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        layoutBottom.setVisibility(View.VISIBLE);
                        treasureView.setVisibility(View.GONE);
                        hideTreasure.setVisibility(View.VISIBLE);
                    }
                });
                break;
        }
    }


    @Override
    public void showMessage(String msg) {
        activityUtils.showToast(msg);
    }

    @Override
    public void setData(List<Treasure> list) {

        // 再次网络请求拿到数据添加覆盖物之前，清理之前的覆盖物
        baiduMap.clear();// 清空地图上所有的覆盖物和infoWindow

        for (Treasure treature : list) {
            LatLng latLng = new LatLng(treature.getLatitude(), treature.getLongitude());

            addMarker(latLng, treature.getId());
        }
    }

    // 将定位的位置供其它调用获取
    public static LatLng getMyLocation(){
        return currentLocation;
    }

    // 将定位的地址供其它调用获取
    public static String getLocationAddr(){
        return currentAddr;
    }

    // 对外提供一个方法：什么时候可以退出了
    public boolean clickbackPrssed() {

        // 如果不是普通视图，切换成普通视图
        if (mUIMode != UI_MODE_NORMAL) {
            changeUIMode(UI_MODE_NORMAL);
            return false;
        }
        // 是普通的视图：告诉HomeActivity，可以退出了
        return true;
    }

}
