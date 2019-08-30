package com.youngwu.ffmpegplay;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.youngwu.ffmpeg.FFmpegUtil;
import com.youngwu.ffmpeg.FFmpegVideoView;

import java.io.File;
import java.util.List;

/**
 * Desc:主页面
 * <p>
 * Created by YoungWu on 2019-08-29.
 */
public class MainActivity extends Activity implements View.OnClickListener {
    /**
     * 动态申请的权限
     */
    private static final String[] PERMISSIONS = {Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE,
            Permission.RECORD_AUDIO};
    private FFmpegVideoView videoView;
    private TextView tv_info;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main);
        videoView = findViewById(R.id.videoView);
        tv_info = findViewById(R.id.tv_info);

        findViewById(R.id.btn_protocol).setOnClickListener(this);
        findViewById(R.id.btn_codec).setOnClickListener(this);
        findViewById(R.id.btn_filter).setOnClickListener(this);
        findViewById(R.id.btn_format).setOnClickListener(this);
        findViewById(R.id.btn_play).setOnClickListener(this);

        requestPermission();
    }

    /**
     * 请求权限
     */
    private void requestPermission() {
        if (!XXPermissions.isHasPermission(MainActivity.this, PERMISSIONS)) {
            XXPermissions.with(this).constantRequest().permission(PERMISSIONS)
                    .request(new OnPermission() {
                        @Override
                        public void hasPermission(List<String> granted, boolean isAll) {
                            if (isAll) {
                                ToastUtils.toast(MainActivity.this, "获取权限成功");
                            } else {
                                ToastUtils.toast(MainActivity.this, "获取权限成功，部分权限未正常授予");
                            }
                        }

                        @Override
                        public void noPermission(List<String> denied, boolean quick) {
                            if (quick) {
                                ToastUtils.toast(MainActivity.this, "被永久拒绝授权，请手动授予权限");
                                //如果是被永久拒绝就跳转到应用权限系统设置页面
                                XXPermissions.gotoPermissionSettings(MainActivity.this);
                            } else {
                                ToastUtils.toast(MainActivity.this, "获取权限失败");
                            }
                        }
                    });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_protocol:
                tv_info.setText(FFmpegUtil.urlProtocolInfo());
                break;
            case R.id.btn_codec:
                tv_info.setText(FFmpegUtil.avCodecInfo());
                break;
            case R.id.btn_filter:
                tv_info.setText(FFmpegUtil.avFilterInfo());
                break;
            case R.id.btn_format:
                tv_info.setText(FFmpegUtil.avFormatInfo());
                break;
            case R.id.btn_play:
                String filePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "test.mp4";
                File file = new File(filePath);
                if (file.exists()) {
                    videoView.playVideo(filePath);
                }
                break;
        }
    }
}
