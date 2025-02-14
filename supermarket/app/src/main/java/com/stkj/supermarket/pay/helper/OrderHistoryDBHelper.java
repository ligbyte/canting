package com.stkj.supermarket.pay.helper;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.stkj.common.core.ActivityWeakRefHolder;
import com.stkj.common.core.AppManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.supermarket.base.greendao.AppGreenDaoOpenHelper;
import com.stkj.supermarket.base.greendao.GreenDBConstants;
import com.stkj.supermarket.base.greendao.generate.DaoMaster;
import com.stkj.supermarket.base.greendao.generate.DaoSession;
import com.stkj.supermarket.base.greendao.generate.PayHistoryOrderInfoDao;
import com.stkj.supermarket.base.greendao.generate.WaitHistoryOrderInfoDao;
import com.stkj.supermarket.pay.model.PayHistoryOrderInfo;
import com.stkj.supermarket.pay.model.WaitHistoryOrderInfo;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;

public class OrderHistoryDBHelper extends ActivityWeakRefHolder {

    private static final int PAGE_SIZE = 15;
    private DaoSession daoSession;
    private Database database;
    private OnOrderHistoryListener onOrderHistoryListener;

    public OrderHistoryDBHelper(@NonNull Activity activity) {
        super(activity);
        AppGreenDaoOpenHelper daoOpenHelper = new AppGreenDaoOpenHelper(AppManager.INSTANCE.getApplication(), GreenDBConstants.ORDER_DB_NAME, null);
        database = daoOpenHelper.getWritableDb();
        DaoMaster daoMaster = new DaoMaster(database);
        daoSession = daoMaster.newSession();
    }

    public void setOnOrderHistoryListener(OnOrderHistoryListener onOrderHistoryListener) {
        this.onOrderHistoryListener = onOrderHistoryListener;
    }

    /**
     * 添加挂单到数据库
     */
    public void addWaitOrderHistory(WaitHistoryOrderInfo historyOrderInfo) {
        Activity activityWithCheck = getHolderActivityWithCheck();
        if (activityWithCheck == null) {
            return;
        }
        if (historyOrderInfo != null) {
            Observable.create(new ObservableOnSubscribe<Integer>() {
                        @Override
                        public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<Integer> emitter) throws Throwable {
                            try {
                                daoSession.getWaitHistoryOrderInfoDao().insert(historyOrderInfo);
                                emitter.onNext(200);
                                emitter.onComplete();
                            } catch (Throwable e) {
                                e.printStackTrace();
                                emitter.onError(e);
                            }
                        }
                    }).compose(RxTransformerUtils.mainSchedulers())
                    .to(AutoDisposeUtils.onDestroyDispose((LifecycleOwner) activityWithCheck))
                    .subscribe(new DefaultObserver<Integer>() {
                        @Override
                        protected void onSuccess(Integer integer) {
                            if (integer == 200) {
                                onOrderHistoryListener.onAddWaitOrderHistory(historyOrderInfo);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (onOrderHistoryListener != null) {
                                onOrderHistoryListener.onWaitOrderHistoryError("添加数据失败");
                            }
                        }
                    });
        }
    }

    public void deleteWaitOrderHistory(WaitHistoryOrderInfo historyOrderInfo) {
        Activity activityWithCheck = getHolderActivityWithCheck();
        if (activityWithCheck == null) {
            return;
        }
        if (historyOrderInfo != null) {
            Observable.create(new ObservableOnSubscribe<Integer>() {
                        @Override
                        public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<Integer> emitter) throws Throwable {
                            try {
                                daoSession.getWaitHistoryOrderInfoDao().delete(historyOrderInfo);
                                emitter.onNext(200);
                                emitter.onComplete();
                            } catch (Throwable e) {
                                e.printStackTrace();
                                emitter.onError(e);
                            }
                        }
                    }).compose(RxTransformerUtils.mainSchedulers())
                    .to(AutoDisposeUtils.onDestroyDispose((LifecycleOwner) activityWithCheck))
                    .subscribe(new DefaultObserver<Integer>() {
                        @Override
                        protected void onSuccess(Integer integer) {
                            if (onOrderHistoryListener != null) {
                                if (integer == 200) {
                                    onOrderHistoryListener.onDeleteWaitOrderHistory(historyOrderInfo);
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (onOrderHistoryListener != null) {
                                onOrderHistoryListener.onWaitOrderHistoryError("删除数据失败");
                            }
                        }
                    });
        }
    }

    /**
     * 加载本地挂单数据
     */
    public void loadValidWaitHistoryList() {
        Activity activityWithCheck = getHolderActivityWithCheck();
        if (activityWithCheck == null) {
            return;
        }
        Observable.create(new ObservableOnSubscribe<List<WaitHistoryOrderInfo>>() {
                    @Override
                    public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<List<WaitHistoryOrderInfo>> emitter) throws Throwable {
                        try {
                            WaitHistoryOrderInfoDao goodsHistoryOrderInfoDao = daoSession.getWaitHistoryOrderInfoDao();
                            List<WaitHistoryOrderInfo> allOrderInfoList = goodsHistoryOrderInfoDao.loadAll();
                            //加载当前的挂单列表
                            List<WaitHistoryOrderInfo> validOrderInfoList = new ArrayList<>();
                            if (allOrderInfoList != null && !allOrderInfoList.isEmpty()) {
                                List<WaitHistoryOrderInfo> outDateOrderList = new ArrayList<>();
                                for (WaitHistoryOrderInfo orderInfo : allOrderInfoList) {
                                    if (orderInfo.isCurrentDayOrder()) {
                                        validOrderInfoList.add(orderInfo);
                                    } else {
                                        outDateOrderList.add(orderInfo);
                                    }
                                }
                                //移除失效的订单
                                for (WaitHistoryOrderInfo historyOrderInfo : outDateOrderList) {
                                    goodsHistoryOrderInfoDao.delete(historyOrderInfo);
                                }
                            }
                            emitter.onNext(validOrderInfoList);
                            emitter.onComplete();
                        } catch (Throwable e) {
                            e.printStackTrace();
                            emitter.onError(e);
                        }
                    }
                }).compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose((LifecycleOwner) activityWithCheck))
                .subscribe(new DefaultObserver<List<WaitHistoryOrderInfo>>() {
                    @Override
                    protected void onSuccess(List<WaitHistoryOrderInfo> orderInfoList) {
                        if (onOrderHistoryListener != null) {
                            onOrderHistoryListener.onLoadValidWaitHistoryList(orderInfoList);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (onOrderHistoryListener != null) {
                            onOrderHistoryListener.onWaitOrderHistoryError("加载数据失败");
                        }
                    }
                });
    }

    /**
     * 加载已支付订单本地数据
     */
    public void loadPayHistoryList(int offset) {
        Activity activityWithCheck = getHolderActivityWithCheck();
        if (activityWithCheck == null) {
            return;
        }
        Observable.create(new ObservableOnSubscribe<List<PayHistoryOrderInfo>>() {
                    @Override
                    public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<List<PayHistoryOrderInfo>> emitter) throws Throwable {
                        try {
                            PayHistoryOrderInfoDao historyOrderInfoDao = daoSession.getPayHistoryOrderInfoDao();
                            List<PayHistoryOrderInfo> historyOrderInfoList = historyOrderInfoDao.queryBuilder()
                                    .offset(offset)
                                    .limit(PAGE_SIZE).list();
                            emitter.onNext(historyOrderInfoList);
                            emitter.onComplete();
                        } catch (Throwable e) {
                            e.printStackTrace();
                            emitter.onError(e);
                        }
                    }
                }).compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose((LifecycleOwner) activityWithCheck))
                .subscribe(new DefaultObserver<List<PayHistoryOrderInfo>>() {
                    @Override
                    protected void onSuccess(List<PayHistoryOrderInfo> orderInfoList) {
                        if (onOrderHistoryListener != null) {
                            onOrderHistoryListener.onLoadPayHistoryList(orderInfoList);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (onOrderHistoryListener != null) {
                            onOrderHistoryListener.onPayOrderHistoryError("加载数据失败");
                        }
                    }
                });
    }

    /**
     * 添加支付成功的数据库
     */
    public void addPayOrderHistory(PayHistoryOrderInfo historyOrderInfo) {
        Activity activityWithCheck = getHolderActivityWithCheck();
        if (activityWithCheck == null) {
            return;
        }
        if (historyOrderInfo != null) {
            Observable.create(new ObservableOnSubscribe<Integer>() {
                        @Override
                        public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<Integer> emitter) throws Throwable {
                            try {
                                daoSession.getPayHistoryOrderInfoDao().insert(historyOrderInfo);
                                emitter.onNext(200);
                                emitter.onComplete();
                            } catch (Throwable e) {
                                e.printStackTrace();
                                emitter.onError(e);
                            }
                        }
                    }).compose(RxTransformerUtils.mainSchedulers())
                    .to(AutoDisposeUtils.onDestroyDispose((LifecycleOwner) activityWithCheck))
                    .subscribe(new DefaultObserver<Integer>() {
                        @Override
                        protected void onSuccess(Integer integer) {
                            if (integer == 200) {
                                onOrderHistoryListener.onAddPayOrderHistory(historyOrderInfo);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (onOrderHistoryListener != null) {
                                onOrderHistoryListener.onPayOrderHistoryError("添加数据失败");
                            }
                        }
                    });
        }
    }

    @Override
    public void onClear() {
        daoSession.clear();
        database.close();
    }

    public interface OnOrderHistoryListener {
        default void onAddWaitOrderHistory(WaitHistoryOrderInfo orderInfo) {
        }

        default void onDeleteWaitOrderHistory(WaitHistoryOrderInfo orderInfo) {
        }

        default void onLoadValidWaitHistoryList(List<WaitHistoryOrderInfo> orderInfoList) {
        }

        default void onWaitOrderHistoryError(String error) {
        }

        default void onAddPayOrderHistory(PayHistoryOrderInfo orderInfo) {
        }

        default void onLoadPayHistoryList(List<PayHistoryOrderInfo> orderInfoList) {
        }

        default void onPayOrderHistoryError(String error) {
        }
    }
}