package com.stkj.supermarketmini.base.utils;

import com.stkj.common.log.LogHelper;

import org.greenrobot.eventbus.EventBus;

public class EventBusUtils {

    public static void registerEventBus(Object object) {
        LogHelper.print("--EventBusUtils-registerEventBus-object: " + object.getClass().getName());
        try {
            if (!EventBus.getDefault().isRegistered(object)) {
                EventBus.getDefault().register(object);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void unRegisterEventBus(Object object) {
        LogHelper.print("--EventBusUtils-unRegisterEventBus-object: " + object.getClass().getName());
        try {
            EventBus.getDefault().unregister(object);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
