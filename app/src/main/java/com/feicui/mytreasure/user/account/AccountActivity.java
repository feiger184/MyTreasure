package com.feicui.mytreasure.user.account;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.feicui.mytreasure.R;
import com.feicui.mytreasure.commons.ActivityUtils;
import com.feicui.mytreasure.custom.IconSelectWindow;
import com.feicui.mytreasure.treasure.TreasureRepo;
import com.feicui.mytreasure.user.UserPrefs;
import com.pkmmte.view.CircularImageView;

import org.hybridsquad.android.library.CropHandler;
import org.hybridsquad.android.library.CropHelper;
import org.hybridsquad.android.library.CropParams;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.R.attr.id;
import static com.baidu.location.b.g.f;
import static com.baidu.location.b.g.p;
import static com.baidu.location.b.g.s;
import static com.baidu.location.b.k.co;

/**
 * 个人信息界面
 */

public class AccountActivity extends AppCompatActivity implements AccountView{

    @BindView(R.id.account_toolbar)
    Toolbar toolbar;
    @BindView(R.id.iv_userIcon)
    ImageView ivUserIcon;


    private ActivityUtils activityUtils;
    private IconSelectWindow iconSelectWindow;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        ButterKnife.bind(this);
        activityUtils = new ActivityUtils(this);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() == null) {
            getSupportActionBar().setTitle("个人信息");
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        }

        String photo = UserPrefs.getInstance().getPhoto();
        if (photo != null) {
            Glide.with(this)
                    .load(photo)
                    .error(R.mipmap.user_icon)
                    .placeholder(R.mipmap.user_icon)
                    .dontAnimate()
                    .into(ivUserIcon);

        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.iv_userIcon)
    public void onClick() {
        if (iconSelectWindow == null) {
            iconSelectWindow = new IconSelectWindow(this, listener);
        }

        if (iconSelectWindow.isShowing()) {
            iconSelectWindow.dismiss();
            return;
        }
        iconSelectWindow.show();
    }

    //用一个第三方的库：到相册、到相机、剪切的功能：photoCropper
    /**
     * 1. 依赖：compile 'org.hybridsquad.android.photocropper:library:2.1.0'
     * 清单合并的问题：aar
     * 2. 使用：
     * 1. 拿到结果处理
     * 2. 处理的回调
     * 3. 分别调用到相册到相机：之前一定要清理上次剪切的图片的缓存
     */
    private IconSelectWindow.Listener listener = new IconSelectWindow.Listener() {
        @Override
        public void toGallery() {

            //清除上一次剪切的图片的缓存
            CropHelper.clearCachedCropFile(cropHandler.getCropParams().uri);

            Intent intent = CropHelper.buildCropFromGalleryIntent(cropHandler.getCropParams());
            startActivityForResult(intent, CropHelper.REQUEST_CROP);
        }

        @Override
        public void toCamera() {
            //清除上一次剪切的图片的缓存
            CropHelper.clearCachedCropFile(cropHandler.getCropParams().uri);

            Intent intent = CropHelper.buildCaptureIntent(cropHandler.getCropParams().uri);
            startActivityForResult(intent, CropHelper.REQUEST_CAMERA);
        }
    };


    private CropHandler cropHandler = new CropHandler() {

        @Override
        public void onPhotoCropped(Uri uri) {
            File file = new File(uri.getPath());
            new AccountPresenter(AccountActivity.this).uploadPhoto(file);
        }

        @Override
        public void onCropCancel() {
            Toast.makeText(AccountActivity.this, "取消", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCropFailed(String message) {
            Toast.makeText(AccountActivity.this, "失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public CropParams getCropParams() {
            CropParams cropParams = new CropParams();
            return cropParams;
        }

        @Override
        public Activity getContext() {
            return AccountActivity.this;
        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CropHelper.handleResult(cropHandler, requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        if (cropHandler.getCropParams() != null)
            CropHelper.clearCachedCropFile(cropHandler.getCropParams().uri);
        super.onDestroy();
    }

    //视图操作

    @Override
    public void showProgress() {
        progressDialog = ProgressDialog.show(this, "头像上传", "正在上传中~");
    }

    @Override
    public void hideProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void showMessage(String msg) {
        activityUtils.showToast(msg);
    }

    @Override
    public void updatePhoto(String photoUrl) {
        if (photoUrl != null) {
            Glide.with(this)
                    .load(photoUrl)
                    .error(R.mipmap.user_icon)
                    .placeholder(R.mipmap.user_icon)// 占位图
                    .dontAnimate()
                    .into(ivUserIcon);
        }
    }
}
