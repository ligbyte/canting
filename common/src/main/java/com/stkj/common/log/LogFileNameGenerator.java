package com.stkj.common.log;

import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator;

/**
 * log文件名称生成器
 */
public class LogFileNameGenerator extends DateFileNameGenerator {

    private String logPrefix;
    private String currentLogFileName;

    public LogFileNameGenerator() {
    }

    public void setLogPrefix(String logPrefix) {
        this.logPrefix = logPrefix;
    }

    public LogFileNameGenerator(String logPrefix) {
        this.logPrefix = logPrefix;
    }

    public String getCurrentLogFileName() {
        return currentLogFileName;
    }

    @Override
    public String generateFileName(int logLevel, long timestamp) {
        String generateFileName = super.generateFileName(logLevel, timestamp) + ".log";
        currentLogFileName = logPrefix != null ? (logPrefix + generateFileName) : generateFileName;
        return currentLogFileName;
    }
}
