package com.feicui.mytreasure.user.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.feicui.mytreasure.MainActivity;
import com.feicui.mytreasure.R;
import com.feicui.mytreasure.commons.ActivityUtils;
import com.feicui.mytreasure.commons.RegexUtils;
import com.feicui.mytreasure.custom.AlertDialogFragment;
import com.feicui.mytreasure.treasure.HomeActivity;
import com.feicui.mytreasure.user.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 登录界面
 */

public class LoginActivity extends AppCompatActivity implements LoginView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_Username)
    EditText etUsername;
    @BindView(R.id.et_Password)
    EditText etPassword;
    @BindView(R.id.btn_Login)
    Button btnLogin;

    private Unbinder unbinder;
    private ActivityUtils activityUtils;
    private ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        unbinder = ButterKnife.bind(this);
        activityUtils = new ActivityUtils(this);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.login);
        }

        etUsername.addTextChangedListener(textWatcher);
        etPassword.addTextChangedListener(textWatcher);

    }

    private String userName;
    private String passWord;

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            userName = etUsername.getText().toString();
            passWord = etPassword.getText().toString();

            boolean canlogin = !(TextUtils.isEmpty(userName) || TextUtils.isEmpty(passWord));
            btnLogin.setEnabled(canlogin);
        }
    };


    @OnClick(R.id.btn_Login)
    public void onClick() {

        //用户名输入错误
        if (RegexUtils.verifyUsername(userName) != RegexUtils.VERIFY_SUCCESS) {

            AlertDialogFragment.getInstances(
                    getString(R.string.username_error),
                    getString(R.string.username_rules))
                    .show(getSupportFragmentManager(), "usernameError");
            return;
        }
        //密码输入错误
        if (RegexUtils.verifyUsername(passWord) != RegexUtils.VERIFY_SUCCESS) {

            AlertDialogFragment.getInstances(
                    getString(R.string.password_error),
                    getString(R.string.password_rules))
                    .show(getSupportFragmentManager(), "passwordError");
            return;
        }

        new LoginPresenter(this).login(new User(userName, passWord));
    }

    @Override
    public void showProgress() {
        dialog = ProgressDialog.show(this, "登录", "亲，正在登录中，请稍后~");

    }

    @Override
    public void hideProgress() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void showMessage(String msg) {
        activityUtils.showToast(msg);
    }

    @Override
    public void navigationToHome() {
        activityUtils.startActivity(HomeActivity.class);
        finish();
        // 发广播，关闭Main页面
        Intent intent = new Intent(MainActivity.MAIN_ACTION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
