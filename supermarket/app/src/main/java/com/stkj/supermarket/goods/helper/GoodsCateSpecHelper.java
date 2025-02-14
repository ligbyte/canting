package com.stkj.supermarket.goods.helper;

import android.app.Activity;
import android.text.TextUtils;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.stkj.common.core.ActivityWeakRefHolder;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.supermarket.base.model.BaseResponse;
import com.stkj.supermarket.goods.callback.OnAddGoodsCateListener;
import com.stkj.supermarket.goods.callback.OnAddGoodsSpecListener;
import com.stkj.supermarket.goods.callback.OnDelGoodsCateListener;
import com.stkj.supermarket.goods.callback.OnDelGoodsSpecListener;
import com.stkj.supermarket.goods.callback.OnGetGoodsCateListListener;
import com.stkj.supermarket.goods.callback.OnGetGoodsSpecListListener;
import com.stkj.supermarket.goods.callback.OnGoodsCateSpecListener;
import com.stkj.supermarket.goods.data.GoodsConstants;
import com.stkj.supermarket.goods.model.AddGoodsCateParams;
import com.stkj.supermarket.goods.model.AddGoodsSpecParams;
import com.stkj.supermarket.goods.model.DelGoodsCateParams;
import com.stkj.supermarket.goods.model.DelGoodsSpecParams;
import com.stkj.supermarket.goods.model.GoodsCate;
import com.stkj.supermarket.goods.model.GoodsSpec;
import com.stkj.supermarket.goods.service.GoodsService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import retrofit2.Response;

/**
 * 商品分类和规格数据帮助类
 */
public class GoodsCateSpecHelper extends ActivityWeakRefHolder {

    private SparseArray<List<GoodsCate>> goodsCateSA = new SparseArray<>();
    private SparseArray<List<GoodsSpec>> goodsSpecSA = new SparseArray<>();

    public GoodsCateSpecHelper(@NonNull Activity activity) {
        super(activity);
    }

    /**
     * 获取商品分类 (标准商品、称重商品)
     */
    public List<GoodsCate> getGoodsCateByType(int goodsType) {
        return goodsCateSA.get(goodsType, new ArrayList<>());
    }

    public List<GoodsSpec> getGoodsSpecByType(int goodsType) {
        return goodsSpecSA.get(goodsType, new ArrayList<>());
    }

    /**
     * 请求商品所有分类\所有规格
     */
    public void requestAllList(OnGoodsCateSpecListener goodsCateSpecListener) {
        Activity activityWithCheck = getHolderActivityWithCheck();
        if (activityWithCheck == null) {
            return;
        }
        Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<Integer> emitter) throws Throwable {
                        try {
                            for (int goodsType : GoodsConstants.GOODS_TYPE_LIST) {
                                //获取商品分类
                                Response<BaseResponse<List<GoodsCate>>> goodsCateListSync = RetrofitManager.INSTANCE.getDefaultRetrofit().create(GoodsService.class).getGoodsCateListSync(goodsType).execute();
                                if (goodsCateListSync.isSuccessful()) {
                                    BaseResponse<List<GoodsCate>> body = goodsCateListSync.body();
                                    if (body != null) {
                                        List<GoodsCate> goodsCateList = body.getData();
                                        runUIThreadWithCheck(new Runnable() {
                                            @Override
                                            public void run() {
                                                goodsCateSA.put(goodsType, goodsCateList);
                                            }
                                        });
                                    }
                                }
                                //获取商品规格(称重商品默认只有千克)
                                if (goodsType == GoodsConstants.TYPE_GOODS_WEIGHT) {
                                    runUIThreadWithCheck(new Runnable() {
                                        @Override
                                        public void run() {
                                            List<GoodsSpec> goodsSpecList = new ArrayList<>();
                                            goodsSpecList.add(new GoodsSpec("", GoodsConstants.SPEC_WEIGHT_GOODS));
                                            goodsSpecSA.put(goodsType, goodsSpecList);
                                        }
                                    });
                                } else {
                                    Response<BaseResponse<List<GoodsSpec>>> goodsSpecListSync = RetrofitManager.INSTANCE.getDefaultRetrofit().create(GoodsService.class).getGoodsSpecListSync(goodsType).execute();
                                    if (goodsSpecListSync.isSuccessful()) {
                                        BaseResponse<List<GoodsSpec>> body = goodsSpecListSync.body();
                                        if (body != null) {
                                            List<GoodsSpec> goodsSpecList = body.getData();
                                            runUIThreadWithCheck(new Runnable() {
                                                @Override
                                                public void run() {
                                                    goodsSpecSA.put(goodsType, goodsSpecList);
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                            emitter.onNext(200);
                            emitter.onComplete();
                        } catch (Throwable e) {
                            e.printStackTrace();
                            emitter.onError(e);
                            emitter.onComplete();
                        }

                    }
                }).compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose((LifecycleOwner) activityWithCheck)).subscribe(new DefaultObserver<Integer>() {
                    @Override
                    protected void onSuccess(Integer integer) {
                        if (goodsCateSpecListener != null) {
                            goodsCateSpecListener.onGetCateSpecListEnd();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (goodsCateSpecListener != null) {
                            goodsCateSpecListener.onGetCateSpecError(e.getMessage());
                        }
                    }
                });
    }

    /**
     * 请求商品分类
     */
    public void requestCateList(int goodsType, OnGetGoodsCateListListener getGoodsCateListListener) {
        Activity activityWithCheck = getHolderActivityWithCheck();
        if (activityWithCheck == null) {
            return;
        }
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(GoodsService.class)
                .getGoodsCateList(goodsType)
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose((LifecycleOwner) activityWithCheck))
                .subscribe(new DefaultObserver<BaseResponse<List<GoodsCate>>>() {
                    @Override
                    protected void onSuccess(BaseResponse<List<GoodsCate>> listBaseResponse) {
                        if (listBaseResponse.isSuccess()) {
                            List<GoodsCate> goodsCateList = listBaseResponse.getData();
                            goodsCateSA.put(goodsType, goodsCateList);
                            if (getGoodsCateListListener != null) {
                                getGoodsCateListListener.onGetCateListSuccess(goodsType, goodsCateList);
                            }
                        } else {
                            if (getGoodsCateListListener != null) {
                                getGoodsCateListListener.onGetCateListError(goodsType, listBaseResponse.getMsg());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getGoodsCateListListener != null) {
                            getGoodsCateListListener.onGetCateListError(goodsType, e.getMessage());
                        }
                    }
                });
    }

    /**
     * 请求商品规格
     */
    public void requestSpecList(int goodsType, OnGetGoodsSpecListListener getGoodsSpecListListener) {
        Activity activityWithCheck = getHolderActivityWithCheck();
        if (activityWithCheck == null) {
            return;
        }
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(GoodsService.class)
                .getGoodsSpecList(goodsType)
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose((LifecycleOwner) activityWithCheck))
                .subscribe(new DefaultObserver<BaseResponse<List<GoodsSpec>>>() {
                    @Override
                    protected void onSuccess(BaseResponse<List<GoodsSpec>> listBaseResponse) {
                        if (listBaseResponse.isSuccess()) {
                            List<GoodsSpec> goodsSpecList = listBaseResponse.getData();
                            goodsSpecSA.put(goodsType, goodsSpecList);
                            if (getGoodsSpecListListener != null) {
                                getGoodsSpecListListener.onGetSpecListSuccess(goodsType, goodsSpecList);
                            }
                        } else {
                            if (getGoodsSpecListListener != null) {
                                getGoodsSpecListListener.onGetSpecListError(goodsType, listBaseResponse.getMsg());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getGoodsSpecListListener != null) {
                            getGoodsSpecListListener.onGetSpecListError(goodsType, e.getMessage());
                        }
                    }
                });

    }

    /**
     * 添加商品分类
     */
    public void addGoodsCate(String cateName, int goodsType, OnAddGoodsCateListener addGoodsCateListener) {
        Activity activityWithCheck = getHolderActivityWithCheck();
        if (activityWithCheck == null) {
            return;
        }
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(GoodsService.class)
                .addGoodsCate(new AddGoodsCateParams(cateName, goodsType))
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose((LifecycleOwner) activityWithCheck))
                .subscribe(new DefaultObserver<BaseResponse<String>>() {
                    @Override
                    protected void onSuccess(BaseResponse<String> response) {
                        if (response.isSuccess()) {
                            String cateId = response.getData();
                            if (!TextUtils.isEmpty(cateId)) {
                                if (addGoodsCateListener != null) {
                                    addGoodsCateListener.onAddCateSuccess(new GoodsCate(cateId, cateName));
                                }
                            } else {
                                if (addGoodsCateListener != null) {
                                    addGoodsCateListener.onAddCateError(cateName, "数据为空");
                                }
                            }
                        } else {
                            if (addGoodsCateListener != null) {
                                addGoodsCateListener.onAddCateError(cateName, response.getMsg());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (addGoodsCateListener != null) {
                            addGoodsCateListener.onAddCateError(cateName, e.getMessage());
                        }
                    }
                });
    }

    /**
     * 添加商品规格
     */
    public void addGoodsSpec(String specName, int goodsType, OnAddGoodsSpecListener addGoodsSpecListener) {
        Activity activityWithCheck = getHolderActivityWithCheck();
        if (activityWithCheck == null) {
            return;
        }
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(GoodsService.class)
                .addGoodsSpec(new AddGoodsSpecParams(specName, goodsType))
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose((LifecycleOwner) activityWithCheck))
                .subscribe(new DefaultObserver<BaseResponse<String>>() {
                    @Override
                    protected void onSuccess(BaseResponse<String> response) {
                        if (response.isSuccess()) {
                            String specId = response.getData();
                            if (!TextUtils.isEmpty(specId)) {
                                if (addGoodsSpecListener != null) {
                                    addGoodsSpecListener.onAddSpecSuccess(new GoodsSpec(specId, specName));
                                }
                            } else {
                                if (addGoodsSpecListener != null) {
                                    addGoodsSpecListener.onAddSpecError(specName, "数据为空");
                                }
                            }
                        } else {
                            if (addGoodsSpecListener != null) {
                                addGoodsSpecListener.onAddSpecError(specName, response.getMsg());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (addGoodsSpecListener != null) {
                            addGoodsSpecListener.onAddSpecError(specName, e.getMessage());
                        }
                    }
                });
    }

    /**
     * 添加商品分类
     */
    public void delGoodsCate(String id, int goodsType, String cateName, OnDelGoodsCateListener delGoodsCateListener) {
        Activity activityWithCheck = getHolderActivityWithCheck();
        if (activityWithCheck == null) {
            return;
        }
        List<DelGoodsCateParams> delGoodsCateParamsList = new ArrayList<>();
        delGoodsCateParamsList.add(new DelGoodsCateParams(id, goodsType));
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(GoodsService.class)
                .delGoodsCate(delGoodsCateParamsList)
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose((LifecycleOwner) activityWithCheck))
                .subscribe(new DefaultObserver<BaseResponse<String>>() {
                    @Override
                    protected void onSuccess(BaseResponse<String> response) {
                        if (response.isSuccess()) {
                            if (delGoodsCateListener != null) {
                                delGoodsCateListener.onDelCateSuccess(cateName);
                            }
                        } else {
                            if (delGoodsCateListener != null) {
                                delGoodsCateListener.onDelCateError(cateName, response.getMsg());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (delGoodsCateListener != null) {
                            delGoodsCateListener.onDelCateError(cateName, e.getMessage());
                        }
                    }
                });
    }

    /**
     * 添加商品规格
     */
    public void delGoodsSpec(String id, int goodsType, String specName, OnDelGoodsSpecListener delGoodsSpecListener) {
        Activity activityWithCheck = getHolderActivityWithCheck();
        if (activityWithCheck == null) {
            return;
        }
        List<DelGoodsSpecParams> delGoodsSpecParamsList = new ArrayList<>();
        delGoodsSpecParamsList.add(new DelGoodsSpecParams(id, goodsType));
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(GoodsService.class)
                .delGoodsSpec(delGoodsSpecParamsList)
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose((LifecycleOwner) activityWithCheck))
                .subscribe(new DefaultObserver<BaseResponse<String>>() {
                    @Override
                    protected void onSuccess(BaseResponse<String> response) {
                        if (response.isSuccess()) {
                            if (delGoodsSpecListener != null) {
                                delGoodsSpecListener.onDelSpecSuccess(specName);
                            }
                        } else {
                            if (delGoodsSpecListener != null) {
                                delGoodsSpecListener.onDelSpecError(specName, response.getMsg());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (delGoodsSpecListener != null) {
                            delGoodsSpecListener.onDelSpecError(specName, e.getMessage());
                        }
                    }
                });
    }

    @Override
    public void onClear() {

    }
}
