package com.stkj.common.log;

import android.text.TextUtils;

import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.flattener.ClassicFlattener;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.backup.BackupStrategy2;
import com.elvishew.xlog.printer.file.backup.FileSizeBackupStrategy2;
import com.elvishew.xlog.printer.file.clean.FileLastModifiedCleanStrategy;
import com.stkj.common.constants.AppCommonConstants;
import com.stkj.common.storage.StorageHelper;

import java.io.File;

public class LogHelper {

    private static boolean logEnable;
    private final static LogFileNameGenerator logFileNameGenerator = new LogFileNameGenerator();

    public static String getLogDirPath() {
        return StorageHelper.getExternalCustomDirPath("androidLog");
    }

    public static File getCurrentLogFile() {
        String currentLogFileName = logFileNameGenerator.getCurrentLogFileName();
        if (!TextUtils.isEmpty(currentLogFileName)) {
            return new File(getLogDirPath(), currentLogFileName);
        }
        return null;
    }

    public static void init() {
        init("");
    }

    public static void init(String logPrefix) {
        logFileNameGenerator.setLogPrefix(logPrefix);

        LogConfiguration config = new LogConfiguration.Builder()
                .logLevel(LogLevel.ALL)
                .build();

        AndroidPrinter androidPrinter = new AndroidPrinter(true);
        FilePrinter filePrinter = new FilePrinter
                .Builder(getLogDirPath())
                .fileNameGenerator(logFileNameGenerator)
                .backupStrategy(new FileSizeBackupStrategy2(5 * 1024 * 1024, BackupStrategy2.NO_LIMIT))
                .cleanStrategy(new FileLastModifiedCleanStrategy(10L * 24L * 60L * 60L * 1000L))
                .flattener(new ClassicFlattener())
                .build();

        XLog.init(config, androidPrinter, filePrinter);
    }

    public static void setLogEnable(boolean enable) {
        logEnable = enable;
    }

    public static void print(String tag, String msg) {
        if (logEnable) {
            XLog.i(AppCommonConstants.APP_PREFIX_TAG + " || " + tag + msg);
        }
    }

    public static void print(String msg) {
        if (logEnable) {
            XLog.i(AppCommonConstants.APP_PREFIX_TAG + " || " + msg);
        }
    }

}
