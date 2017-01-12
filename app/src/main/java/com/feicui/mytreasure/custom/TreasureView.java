package com.feicui.mytreasure.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.feicui.mytreasure.R;
import com.feicui.mytreasure.treasure.Treasure;
import com.feicui.mytreasure.treasure.map.MapFragment;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 自定义卡片视图
 */

public class TreasureView extends RelativeLayout {

    @BindView(R.id.tv_treasureTitle)
    TextView mTvTreasureTitle;
    @BindView(R.id.tv_distance)
    TextView mTvDistance;
    @BindView(R.id.tv_treasureLocation)
    TextView mTvTreasureLocation;

    public TreasureView(Context context) {
        super(context);
        init();
    }

    public TreasureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TreasureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_treasure, this, true);
        ButterKnife.bind(this);
    }

    public void bindTreasure(@NonNull Treasure treasure) {
        mTvTreasureTitle.setText(treasure.getTitle());
        mTvTreasureLocation.setText(treasure.getLocation());

        double distance = 0.00d;// 距离
        LatLng latLng = new LatLng(treasure.getLatitude(), treasure.getLongitude());

        LatLng myLocation = MapFragment.getMyLocation();

        if (myLocation == null) {
            distance = 0.00d;
        }


        distance = DistanceUtil.getDistance(latLng, myLocation);
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        String text = decimalFormat.format(distance / 1000) + "km";
        mTvDistance.setText(text);

    }
}
