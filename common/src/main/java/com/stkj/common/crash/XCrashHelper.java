package com.stkj.common.crash;

import android.content.Context;
import android.os.Environment;

import java.io.File;

import xcrash.XCrash;

/**
 * 崩溃日志帮助类
 */
public class XCrashHelper {

    /**
     * 初始崩溃帮助类
     */
    public static void init(Context context) {
        try {
            XCrash.init(context);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static final String SD_LOG_PATH = "XCrash_log";

    public static File getLogDir() {
        File logDirFile = new File(Environment.getExternalStorageDirectory(), SD_LOG_PATH);
        logDirFile.mkdir();
        return logDirFile;
    }

}
