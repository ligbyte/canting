package com.stkj.supermarketmini.goods.helper;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.stkj.common.core.ActivityWeakRefHolder;
import com.stkj.supermarketmini.goods.model.GoodsBaseInfo;

public class GoodsExportHelper extends ActivityWeakRefHolder {
    public GoodsExportHelper(@NonNull Activity activity) {
        super(activity);
    }

    public void addGoods(GoodsBaseInfo goodsBaseInfo) {

    }

    @Override
    public void onClear() {

    }
}
