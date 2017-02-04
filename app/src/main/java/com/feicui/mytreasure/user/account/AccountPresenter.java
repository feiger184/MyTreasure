package com.feicui.mytreasure.user.account; 
import com.feicui.mytreasure.net.NetClient;
import com.feicui.mytreasure.user.UserPrefs;

import java.io.File;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 头像处理的业务类
 */
public class AccountPresenter {
    private AccountView accountView;

    public AccountPresenter(AccountView accountView) {
        this.accountView = accountView;
    }

    public void uploadPhoto(File file) {
        //进度显示
        accountView.showProgress();
        //构建上传的图片文件的“部分”
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", "photo.png", RequestBody.create(null, file));

        Call<UploadResult> uploadResultCall = NetClient.getInstance().getTreasureApi().upload(part);
        uploadResultCall.enqueue(upLoadCallBack);
    }
    private Callback<UploadResult> upLoadCallBack = new Callback<UploadResult>() {
        @Override
        public void onResponse(Call<UploadResult> call, Response<UploadResult> response) {

            if (response.isSuccessful()) {
                UploadResult body = response.body();
                if (body == null) {
                    accountView.showMessage("发生未知错误");
                    return;
                }
                // 提示
                accountView.showMessage(body.getMsg());
                if (body.getCount()!=1){
                    return;
                }
                String photoUrl = body.getUrl();
                UserPrefs.getInstance().setPhoto(NetClient.BASE_URL + photoUrl);

                accountView.updatePhoto(NetClient.BASE_URL + photoUrl);

                // 更新信息！！重新在个人信息上加载、保存到用户信息里面等
                String substring = photoUrl.substring(photoUrl.lastIndexOf("/") + 1, photoUrl.length());
                Update update = new Update(UserPrefs.getInstance().getTokenid(), substring);

                Call<UpdateResult> updateResultCall = NetClient.getInstance().getTreasureApi().update(update);
                updateResultCall.enqueue(upDateCallBack);
            }
        }

        @Override
        public void onFailure(Call<UploadResult> call, Throwable t) {
            // 提示
            accountView.hideProgress();
            accountView.showMessage("请求失败"+t.getMessage());
        }
    };


    private Callback<UpdateResult> upDateCallBack = new Callback<UpdateResult>() {
        @Override
        public void onResponse(Call<UpdateResult> call, Response<UpdateResult> response) {
            accountView.hideProgress();
            if (response.isSuccessful()){
                UpdateResult result = response.body();
                if (result==null){
                    accountView.showMessage("未知的错误");
                    return;
                }
                accountView.showMessage(result.getMsg());
                if (result.getCode()!=1){
                    return;
                }
            }

        }

        @Override
        public void onFailure(Call<UpdateResult> call, Throwable t) {
            accountView.hideProgress();

            accountView.showMessage("更新失败"+t.getMessage());
        }
    };

}
