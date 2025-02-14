package com.stkj.common.ui.fragment;

import android.content.Context;

import androidx.fragment.app.FragmentManager;

import com.stkj.common.log.LogHelper;
import com.stkj.common.ui.activity.BaseActivity;
import com.stkj.common.utils.FragmentUtils;

public abstract class BaseDialogFragment extends BaseRecyclerFragment {

    private OnDismissListener onDismissListener;

    public void dismiss() {
        if (onDismissListener != null) {
            onDismissListener.onDismiss();
        }
        FragmentManager parentFragmentManager = null;
        try {
            parentFragmentManager = getParentFragmentManager();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (parentFragmentManager == null) {
            LogHelper.print("BaseDialogFragment--dismiss--getParentFragmentManager()" + this.getClass().getName());
            return;
        }
        LogHelper.print("BaseDialogFragment--dismiss--" + this.getClass().getName());
        FragmentUtils.safeRemoveFragment(getParentFragmentManager(), this);
    }

    public void show(Context context) {
        if (context instanceof BaseActivity) {
            BaseActivity commonActivity = (BaseActivity) context;
            commonActivity.addContentPlaceHolderFragment(this);
            LogHelper.print("BaseDialogFragment--show--addContentPlaceHolderFragment()" + this.getClass().getName());
        }
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public interface OnDismissListener {
        void onDismiss();
    }

}
