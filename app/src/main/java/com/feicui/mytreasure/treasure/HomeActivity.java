package com.feicui.mytreasure.treasure;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.feicui.mytreasure.MainActivity;
import com.feicui.mytreasure.R;
import com.feicui.mytreasure.commons.ActivityUtils;
import com.feicui.mytreasure.treasure.list.TreasureListFragment;
import com.feicui.mytreasure.treasure.map.MapFragment;
import com.feicui.mytreasure.user.UserPrefs;
import com.feicui.mytreasure.user.account.AccountActivity;

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
    private FragmentManager supportFragmentManager;

    private TreasureListFragment treasureListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        // 通过id找到MapFragment
        supportFragmentManager = getSupportFragmentManager();
        mapFragment = (MapFragment) supportFragmentManager.findFragmentById(R.id.mapFragment);

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
                //跳到个人信息界面
                activityUtils.startActivity(AccountActivity.class);
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
                    .dontAnimate()
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

    //准备
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_toggle);
        // 根据显示的视图不一样，设置不一样的图标
        if (treasureListFragment != null && treasureListFragment.isAdded()) {
            item.setIcon(R.drawable.ic_map);
        } else {
            item.setIcon(R.drawable.ic_view_list);
        }
        return true;
    }

    //创建OptionMenu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_toggle:
                showListFragment();
                invalidateOptionsMenu();//更新选项菜单视图
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    * 显示或隐藏列表的视图
    * */
    private void showListFragment() {

        // 如果列表正在展示
        if (treasureListFragment != null && treasureListFragment.isAdded()) {
            // 将Fragment弹出回退栈
            supportFragmentManager.popBackStack();
            // 移除Fragment
            supportFragmentManager.beginTransaction().remove(treasureListFragment).commit();
            return;
        }
        treasureListFragment = new TreasureListFragment();

        // 在布局的fragment_container（Framelayout上展示Fragment）
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, treasureListFragment)
                // 添加到回退栈
                .addToBackStack(null)
                .commit();
    }

    // 处理back返回键
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            // MapFragment里面视图的普通的视图，可以退出
            if (mapFragment.clickbackPrssed()) {
                super.onBackPressed();
            }
        }
    }

}
