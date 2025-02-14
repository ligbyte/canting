package com.stkj.common.ui.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.stkj.common.core.ActivityHolderHelper;
import com.stkj.common.core.ActivityMethodProxy;
import com.stkj.common.core.ActivityWeakRefHolder;
import com.stkj.common.core.AppLoadingDialogHelper;
import com.stkj.common.ui.fragment.BaseFragment;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.utils.ActivityUtils;
import com.stkj.common.utils.FragmentUtils;
import com.stkj.common.utils.ScreenUtils;
import com.stkj.common.utils.StatusBarUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * app 基类Activity
 */
public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";
    protected Set<ActivityMethodProxy> methodProxySet = new HashSet<>();
    protected ActivityHolderHelper activityHolderHelper = new ActivityHolderHelper();
    protected AppLoadingDialogHelper loadingDialogHelper = new AppLoadingDialogHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ScreenUtils.setDisplayCutoutMode(this, WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES);
        }
        Log.d(TAG, "BaseActivity 周期 onCreate -> this = " + this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "BaseActivity 周期 onStart -> this = " + this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "BaseActivity 周期 onStop -> this = " + this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "BaseActivity 周期 onResume -> this = " + this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "BaseActivity 周期 onPause -> this = " + this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        methodProxySet.clear();
        activityHolderHelper.clear();
        loadingDialogHelper.clear();
        Log.d(TAG, "BaseActivity 周期 onDestroy -> this = " + this);
    }

    public void showLoadingDialog() {
        runUIThreadWithCheck(new Runnable() {
            @Override
            public void run() {
                loadingDialogHelper.showLoadingDialog(BaseActivity.this, 0, "");
            }
        });
    }

    public void showLoadingDialog(String loadingText) {
        runUIThreadWithCheck(new Runnable() {
            @Override
            public void run() {
                loadingDialogHelper.showLoadingDialog(BaseActivity.this, 0, loadingText);
            }
        });
    }

    public void showLoadingDialog(int tag) {
        runUIThreadWithCheck(new Runnable() {
            @Override
            public void run() {
                loadingDialogHelper.showLoadingDialog(BaseActivity.this, tag, "");
            }
        });
    }

    public void showLoadingDialog(int tag, String loadingText) {
        runUIThreadWithCheck(new Runnable() {
            @Override
            public void run() {
                loadingDialogHelper.showLoadingDialog(BaseActivity.this, tag, loadingText);
            }
        });
    }

    public void hideLoadingDialog() {
        runUIThreadWithCheck(new Runnable() {
            @Override
            public void run() {
                loadingDialogHelper.hideLoadingDialog(0);
            }
        });
    }

    public void hideLoadingDialog(int tag) {
        runUIThreadWithCheck(new Runnable() {
            @Override
            public void run() {
                loadingDialogHelper.hideLoadingDialog(tag);
            }
        });
    }

    public boolean isActivityFinished() {
        return ActivityUtils.isActivityFinished(this);
    }

    public void runUIThreadWithCheck(Runnable task) {
        if (!isActivityFinished()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isActivityFinished()) {
                        task.run();
                    }
                }
            });
        }
    }

    public <T extends ActivityWeakRefHolder> T getWeakRefHolder(@NonNull Class<T> tClass) {
        return activityHolderHelper.get(tClass, this);
    }

    public <T extends ActivityWeakRefHolder> void clearWeakRefHolder(@NonNull Class<T> tClass) {
        activityHolderHelper.clear(tClass);
    }

    public String getActivityClassName() {
        return this.getClass().getCanonicalName();
    }

    public void setSystemBarMode(boolean isLightMode) {
        StatusBarUtils.setSystemBarMode(this, isLightMode);
    }

    public void addActivityMethodCallback(ActivityMethodProxy methodCallback) {
        if (methodCallback != null) {
            methodProxySet.add(methodCallback);
        }
    }

    public void removeActivityMethodCallback(ActivityMethodProxy methodCallback) {
        if (methodCallback != null) {
            methodProxySet.remove(methodCallback);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (ActivityMethodProxy methodCallback : methodProxySet) {
            methodCallback.onActivityResult(requestCode, resultCode, data);
        }
    }

    //设置字体为默认大小，不随系统字体大小改而改变
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale != 1) {
            //非默认值
            getResources();
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res.getConfiguration().fontScale != 1) {
            //非默认值
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }

    /**
     * 获取内容占位布局id（弹窗和loading布局等）
     */
    public int getContentPlaceHolderId() {
        return 0;
    }

    public void addContentPlaceHolderFragment(Fragment fragment) {
        int contentPlaceHolderId = getContentPlaceHolderId();
        if (contentPlaceHolderId != 0) {
            FragmentUtils.safeAddFragment(getSupportFragmentManager(), fragment, contentPlaceHolderId);
        } else {
            AppToast.toastMsg("添加内容失败");
        }
    }

}