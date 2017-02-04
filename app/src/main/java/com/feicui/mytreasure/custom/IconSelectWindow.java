package com.feicui.mytreasure.custom;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.feicui.mytreasure.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 自定义PopupWindow用户头像点击弹出的视图(视图窗口：从相册、从相机、取消)
 */

public class IconSelectWindow extends PopupWindow {

    private Activity activity;
    private Listener listener;

    public interface Listener {
        //到相册
        void toGallery();

        //到相机
        void toCamera();
    }

    public IconSelectWindow(@NonNull Activity activity, Listener listener) {
        super(activity.getLayoutInflater().inflate(R.layout.window_select_icon, null),
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ButterKnife.bind(this,getContentView());
        this.activity = activity;
        this.listener = listener;

        setFocusable(true);

        setBackgroundDrawable(new BitmapDrawable());
    }

    //对外提供的一个展示的方法
    public void show() {
        showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }


    @OnClick({R.id.btn_gallery, R.id.btn_camera, R.id.btn_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_gallery:
                listener.toGallery();
                break;
            case R.id.btn_camera:
                listener.toCamera();
                break;
            case R.id.btn_cancel:
                break;
        }
        dismiss();
    }

}
