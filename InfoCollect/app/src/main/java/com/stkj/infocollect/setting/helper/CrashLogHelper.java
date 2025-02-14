package com.stkj.infocollect.setting.helper;

import com.stkj.infocollect.base.utils.CommonDialogUtils;
import com.stkj.common.crash.XCrashHelper;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.ui.activity.BaseActivity;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.utils.FileUtils;

import java.io.File;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import xcrash.TombstoneManager;

/**
 * 崩溃日志帮助类
 */
public class CrashLogHelper {

    /**
     * 导出崩溃日志
     */
    public static void saveCrashLogToSDCard(BaseActivity mActivity) {
        File[] allTombstones = TombstoneManager.getAllTombstones();
        if (allTombstones != null && allTombstones.length > 0) {
            mActivity.showLoadingDialog();
            Observable.just(allTombstones)
                    .map(new Function<File[], Boolean>() {
                        @Override
                        public Boolean apply(File[] files) throws Throwable {
                            for (File file : files) {
                                FileUtils.copyFileToOtherFile(file, new File(XCrashHelper.getLogDir(), file.getName()));
                            }
                            return true;
                        }
                    }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .to(AutoDisposeUtils.onDestroyDispose(mActivity))
                    .subscribe(new DefaultObserver<Boolean>() {
                        @Override
                        protected void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Boolean aBoolean) {
                            mActivity.hideLoadingDialog();
                            CommonDialogUtils.showTipsDialog(mActivity, "日志已导出到sd卡" + XCrashHelper.SD_LOG_PATH + "目录下");
                        }

                        @Override
                        public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                            mActivity.hideLoadingDialog();
                            AppToast.toastMsg("日志导出失败");
                        }
                    });
        } else {
            CommonDialogUtils.showTipsDialog(mActivity, "本地无崩溃日志");
        }
    }


    public static void clearCrashLog(BaseActivity mActivity) {
        mActivity.showLoadingDialog();
        Observable.create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<Boolean> emitter) throws Throwable {
                        TombstoneManager.clearAllTombstones();
                        FileUtils.deleteDirectory(XCrashHelper.getLogDir());
                        emitter.onNext(true);
                        emitter.onComplete();
                    }
                }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .to(AutoDisposeUtils.onDestroyDispose(mActivity))
                .subscribe(new DefaultObserver<Boolean>() {
                    @Override
                    protected void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Boolean aBoolean) {
                        mActivity.hideLoadingDialog();
                        AppToast.toastMsg("清理完成");
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        mActivity.hideLoadingDialog();
                        AppToast.toastMsg("清理失败");
                    }
                });
    }

}
