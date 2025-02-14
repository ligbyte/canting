package com.stkj.cashiermini.base.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

/**
 * app统一下下拉刷新
 */
public class AppSmartRefreshLayout extends SmartRefreshLayout {

    public AppSmartRefreshLayout(Context context) {
        super(context);
        init(context);
    }

    public AppSmartRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        setEnableOverScrollDrag(true);
        ClassicsHeader refreshHeader = new ClassicsHeader(context);
        setRefreshHeader(refreshHeader);
        ClassicsFooter classicsFooter = new ClassicsFooter(context);
        setRefreshFooter(classicsFooter);
    }
}
