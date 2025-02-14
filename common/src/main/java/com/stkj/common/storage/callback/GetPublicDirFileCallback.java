package com.stkj.common.storage.callback;

import java.util.List;

import com.stkj.common.storage.model.PublicDirFileInfo;

public interface GetPublicDirFileCallback {
    void onSuccess(List<PublicDirFileInfo> publicDirFileInfoList);

    void onError(String message);
}
