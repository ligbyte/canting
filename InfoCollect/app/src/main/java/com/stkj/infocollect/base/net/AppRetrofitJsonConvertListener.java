package com.stkj.infocollect.base.net;

import com.stkj.infocollect.base.model.BaseNetResponse;
import com.stkj.infocollect.base.model.BaseResponse;
import com.stkj.infocollect.login.helper.LoginHelper;
import com.stkj.common.core.MainThreadHolder;
import com.stkj.common.net.callback.RetrofitConvertJsonListener;

/**
 * 系统json解析监听
 */
public class AppRetrofitJsonConvertListener implements RetrofitConvertJsonListener {
    @Override
    public void onConvertJson(Object o) {
        if (o instanceof BaseNetResponse) {
            BaseNetResponse baseNetResponse = (BaseNetResponse) o;
            //判断token过期
            if (baseNetResponse.isTokenInvalid() && !LoginHelper.INSTANCE.isHandleLoginValid()) {
                setNeedHandleLoginValid();
            }
        } else if (o instanceof BaseResponse) {
            BaseResponse baseResponse = (BaseResponse) o;
            //判断token过期
            if (baseResponse.isTokenInvalid() && !LoginHelper.INSTANCE.isHandleLoginValid()) {
                setNeedHandleLoginValid();
            }
        }
    }

    private void setNeedHandleLoginValid() {
        LoginHelper.INSTANCE.setNeedHandleLoginValid();
        //主线程处理登录失效弹窗
        MainThreadHolder.post(new Runnable() {
            @Override
            public void run() {
                LoginHelper.INSTANCE.handleLoginValid(true);
            }
        });
    }
}
