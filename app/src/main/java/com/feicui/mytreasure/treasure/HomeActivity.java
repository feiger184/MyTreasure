package com.feicui.mytreasure.treasure;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.feicui.mytreasure.MainActivity;
import com.feicui.mytreasure.R;
import com.feicui.mytreasure.commons.ActivityUtils;
import com.feicui.mytreasure.treasure.map.MapFragment;
import com.feicui.mytreasure.user.UserPrefs;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 主界面
 */

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.navigation)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    private ActivityUtils activityUtils;
    private ImageView ivIcon;
    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        // 通过id找到MapFragment
        mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        // 进入页面，将宝藏数据的缓存清空
        TreasureRepo.getInstance().clear();

        activityUtils = new ActivityUtils(this);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        toggle.syncState();
        drawerLayout.addDrawerListener(toggle);
        navigationView.setNavigationItemSelectedListener(this);

        ivIcon = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.iv_usericon);
        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2017/1/5 0005   更换头像
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        // 更新侧滑上面的头像信息
        String photo = UserPrefs.getInstance().getPhoto();
        if (photo != null) {
            // 加载头像
            Glide.with(this)
                    .load(photo)
                    .error(R.mipmap.user_icon)
                    .placeholder(R.mipmap.user_icon)// 占位图
                    .into(ivIcon);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_hide:
                mapFragment.changeUIMode(2);
                break;
            case R.id.menu_logout:
                // 清空登录用户数据
                UserPrefs.getInstance().clearUser();
                activityUtils.startActivity(MainActivity.class);
                finish();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // 处理back返回键
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
