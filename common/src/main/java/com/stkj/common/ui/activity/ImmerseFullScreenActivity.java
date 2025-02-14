package com.stkj.common.ui.activity;

import android.os.Bundle;

import com.stkj.common.utils.StatusBarUtils;

/**
 * 默认沉浸式状态栏(全屏：表示状态栏底层可绘制) Activity
 */
public class ImmerseFullScreenActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.setTransStatusBarFullScreen(getWindow());
    }

}