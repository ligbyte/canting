package com.stkj.common.contact.callback;

import androidx.annotation.Nullable;

import com.stkj.common.contact.model.ContactModel;

public interface OnGetContactListener {

    void onGetContact(@Nullable ContactModel contactModel);

    void onError(String message);
}
