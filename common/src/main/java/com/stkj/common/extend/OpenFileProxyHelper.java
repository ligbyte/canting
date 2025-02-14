package com.stkj.common.extend;

import com.stkj.common.storage.model.CacheFileInfo;

import java.util.ArrayList;
import java.util.List;

public enum OpenFileProxyHelper {
    INSTANCE;

    private List<CacheFileInfo> cacheFileInfoList = new ArrayList<>();
    private Class mainActivityClass;

    public Class getMainActivityClass() {
        return mainActivityClass;
    }

    public void setMainActivityClass(Class mainActivityClass) {
        this.mainActivityClass = mainActivityClass;
    }

    public void putCacheFileList(List<CacheFileInfo> infoList) {
        if (infoList == null) {
            return;
        }
        cacheFileInfoList.addAll(infoList);
    }

    public List<CacheFileInfo> getCacheFileList() {
        return cacheFileInfoList;
    }

    public void clear() {
        cacheFileInfoList.clear();
    }

}