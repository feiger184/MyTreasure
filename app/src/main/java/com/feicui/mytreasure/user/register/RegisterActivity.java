package com.feicui.mytreasure.user.register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
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

/**
 * 注册界面
 */

public class RegisterActivity extends AppCompatActivity implements RegisterView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_Username)
    EditText etUsername;
    @BindView(R.id.et_Password)
    EditText etPassword;
    @BindView(R.id.et_Confirm)
    EditText etConfirm;
    @BindView(R.id.btn_Register)
    Button btnRegister;
    private ActivityUtils activityUtils;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 会触发onContentChanged方法
        setContentView(R.layout.activity_register);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        ButterKnife.bind(this);
        activityUtils = new ActivityUtils(this);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.register);
        }
        // EditText 的输入监听，监听文本的变化
        etUsername.addTextChangedListener(textWatcher);
        etPassword.addTextChangedListener(textWatcher);
        etConfirm.addTextChangedListener(textWatcher);

    }

    private String userName;
    private String password;
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {


        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            // 处理文本输入之后的按钮事件
            userName = etUsername.getText().toString();
            password = etPassword.getText().toString();
            String confirm = etConfirm.getText().toString();
            boolean canregister = !(TextUtils.isEmpty(userName) ||
                    TextUtils.isEmpty(password) ||
                    TextUtils.isEmpty(confirm))
                    && password.equals(confirm);
            btnRegister.setEnabled(canregister);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_Register)
    public void onClick() {

        // 注册的视图和业务处理
        if (RegexUtils.verifyUsername(userName) != RegexUtils.VERIFY_SUCCESS) {

            // 显示一个错误的对话框：自定义一个
            AlertDialogFragment.getInstances(
                    getString(R.string.username_error),
                    getString(R.string.username_rules))
                    .show(getSupportFragmentManager(), "usernameError");
            return;
        }

        if (RegexUtils.verifyPassword(password) != RegexUtils.VERIFY_SUCCESS) {
            // 显示一个错误的对话框
            AlertDialogFragment.getInstances(
                    getString(R.string.password_error),
                    getString(R.string.password_rules))
                    .show(getSupportFragmentManager(), "passwordError");

            return;
        }

        new RegisterPresenter(this).register(new User(userName, password));
    }

    @Override
    public void showProgress() {
        dialog = ProgressDialog.show(this, "注册", "亲，正在注册中，请稍后~");

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

        // 发送本地广播去关闭页面
        Intent intent = new Intent(MainActivity.MAIN_ACTION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
