package com.stkj.supermarket.goods.helper;

import androidx.fragment.app.Fragment;

import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.supermarket.base.model.BaseResponse;
import com.stkj.supermarket.base.utils.JacksonUtils;
import com.stkj.supermarket.goods.callback.OnRequestQrCodeDetailListener;
import com.stkj.supermarket.goods.model.GoodsQrCodeDetail;
import com.stkj.supermarket.goods.service.GoodsService;

/**
 * 商品条码帮助类
 */
public class GoodsQrCodeHelper {

    public static void requestCodeDetail(String qrcode, Fragment fragment, OnRequestQrCodeDetailListener requestQrCodeDetailListener) {
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(GoodsService.class)
                .requestCodeDetail(qrcode)
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(fragment))
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
