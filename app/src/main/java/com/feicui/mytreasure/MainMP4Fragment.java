package com.feicui.mytreasure;

import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.feicui.mytreasure.commons.ActivityUtils;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Created by Administrator on 2016/12/29 0029.
 */

public class MainMP4Fragment extends Fragment implements TextureView.SurfaceTextureListener {


    private TextureView textureView;
    private MediaPlayer mediaPlayer;
    private ActivityUtils activityUtils;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        activityUtils = new ActivityUtils(this);
        textureView = new TextureView(getContext());
        return textureView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textureView.setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(final SurfaceTexture surface, int width, int height) {

        try {
            AssetFileDescriptor assetFileDescriptor = getContext().getAssets().openFd("welcome.mp4");
            FileDescriptor fileDescriptor = assetFileDescriptor.getFileDescriptor();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(fileDescriptor, assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    Surface mySurface = new Surface(surface);
                    mediaPlayer.setSurface(mySurface);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.seekTo(0);
                    mediaPlayer.start();
                }
            });

        } catch (IOException e) {
            activityUtils.showToast("媒体文件播放失败了");
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
