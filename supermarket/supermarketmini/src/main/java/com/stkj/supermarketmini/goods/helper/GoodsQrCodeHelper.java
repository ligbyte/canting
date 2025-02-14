package com.stkj.supermarketmini.goods.helper;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.supermarketmini.base.model.BaseResponse;
import com.stkj.supermarketmini.base.utils.JacksonUtils;
import com.stkj.supermarketmini.goods.callback.OnRequestQrCodeDetailListener;
import com.stkj.supermarketmini.goods.model.GoodsQrCodeDetail;
import com.stkj.supermarketmini.goods.service.GoodsService;

/**
 * 商品条码帮助类
 */
public class GoodsQrCodeHelper {

    public static void requestCodeDetail(String qrcode, LifecycleOwner lifecycleOwner, OnRequestQrCodeDetailListener requestQrCodeDetailListener) {
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(GoodsService.class)
                .requestCodeDetail(qrcode)
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(lifecycleOwner))
                .subscribe(new DefaultObserver<BaseResponse<String>>() {
                    @Override
                    protected void onSuccess(BaseResponse<String> detailBaseResponse) {
                        String data = detailBaseResponse.getData();
                        if (data != null) {
                            GoodsQrCodeDetail goodsQrCodeDetail = JacksonUtils.convertJsonObject(data, GoodsQrCodeDetail.class);
                            if (goodsQrCodeDetail != null) {
                                if (requestQrCodeDetailListener != null) {
                                    requestQrCodeDetailListener.onRequestDetailSuccess(goodsQrCodeDetail);
                                }
                            } else {
                                requestQrCodeDetailListener.onRequestDetailError(qrcode, "请求数据为空");
                            }
                        } else {
                            requestQrCodeDetailListener.onRequestDetailError(qrcode, "请求数据为空");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (requestQrCodeDetailListener != null) {
                            requestQrCodeDetailListener.onRequestDetailError(qrcode, e.getMessage());
                        }
                    }
                });

    }

}
