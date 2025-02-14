package com.stkj.common.core;

import android.util.SparseArray;

import androidx.fragment.app.FragmentActivity;

import com.stkj.common.log.LogHelper;
import com.stkj.common.ui.fragment.AppLoadingDialogFragment;
import com.stkj.common.ui.fragment.BaseDialogFragment;

/**
 * 应用通用loading dialog处理
 * 与BaseActivity配合使用
 */
public class AppLoadingDialogHelper {

    private final SparseArray<AppLoadingDialogFragment> loadingDialogSA = new SparseArray<>();

    public void showLoadingDialog(FragmentActivity activity, int tag) {
        showLoadingDialog(activity, tag, "");
    }

    public void showLoadingDialog(FragmentActivity activity, int tag, String loadingText) {
        AppLoadingDialogFragment dialogFragment = loadingDialogSA.get(tag);
        if (dialogFragment != null) {
            LogHelper.print("newLoadingDialog--not null tag: " + tag);
            dialogFragment.setLoadingText(loadingText);
            return;
        }
        AppLoadingDialogFragment newLoadingDialog = AppLoadingDialogFragment.build(styleId).setLoadingText(loadingText);
        newLoadingDialog.setOnDismissListener(new BaseDialogFragment.OnDismissListener() {
            @Override
            public void onDismiss() {
                LogHelper.print("newLoadingDialog-onDismiss-removed tag: " + tag);
                loadingDialogSA.remove(tag);
            }
        });
//        newLoadingDialog.getLifecycle().addObserver(new LifecycleEventObserver() {
//            @Override
//            public void onStateChanged(@NonNull LifecycleOwner lifecycleOwner, @NonNull Lifecycle.Event event) {
//                LogHelper.print("newLoadingDialog--onStateChanged: " + event + " tag: " + tag);
//                if (event == Lifecycle.Event.ON_DESTROY) {
//                    loadingDialogSA.remove(tag);
//                    LogHelper.print("newLoadingDialog-onDestroy-removed tag: " + tag);
//                }
//            }
//        });
        newLoadingDialog.show(activity);
        loadingDialogSA.put(tag, newLoadingDialog);
    }

    public void hideLoadingDialog(int tag) {
        AppLoadingDialogFragment loadingDialog = loadingDialogSA.get(tag);
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    public void clear() {
        loadingDialogSA.clear();
    }

    private int styleId;

    public void setStyleId(int styleId) {
        this.styleId = styleId;
    }
}
