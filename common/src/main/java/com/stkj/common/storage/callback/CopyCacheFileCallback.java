package com.stkj.common.storage.callback;

import java.util.List;

import com.stkj.common.storage.model.CacheFileInfo;

public interface CopyCacheFileCallback {
    void onSuccess(List<CacheFileInfo> cacheFileInfoList);

    void onError(String errorMsg);
}
