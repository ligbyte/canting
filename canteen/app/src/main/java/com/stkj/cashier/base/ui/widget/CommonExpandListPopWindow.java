package com.stkj.cashier.base.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.stkj.cashier.R;
import com.stkj.cashier.base.model.CommonExpandItem;
import com.stkj.cashier.base.model.CustomExpandItem;
import com.stkj.cashier.base.ui.adapter.CommonExpandItemViewHolder;
import com.stkj.cashier.base.ui.adapter.CustomExpandItemViewHolder;
import com.stkj.common.ui.adapter.CommonRecyclerAdapter;
import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;
import com.stkj.common.ui.popupwindow.CommonPopupWindow;

import java.util.ArrayList;
import java.util.List;

public class CommonExpandListPopWindow extends CommonPopupWindow {

    private CommonRecyclerAdapter commonRecyclerAdapter;
    private boolean needCustomItem;
    private boolean allowEditItem;
    private boolean allowDelItem;

    public CommonExpandListPopWindow(Context context) {
        super(context);
    }

    public CommonExpandListPopWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommonExpandListPopWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CommonExpandListPopWindow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected int getLayoutResId() {
        return com.stkj.cashier.R.layout.pop_common_expand_list;
    }

    @Override
    protected void initViews(View rootView) {
        RecyclerView recyclerView = rootView.findViewById(R.id.rv_weight_goods_content);
        commonRecyclerAdapter = new CommonRecyclerAdapter(false);
        commonRecyclerAdapter.addViewHolderFactory(new CommonExpandItemViewHolder.Factory());
        commonRecyclerAdapter.addViewHolderFactory(new CustomExpandItemViewHolder.Factory());
        commonRecyclerAdapter.addItemEventListener(new CommonRecyclerAdapter.OnItemEventListener() {
            @Override
            public void onCustomItemEvent(CommonRecyclerAdapter commonRecyclerAdapter, CommonRecyclerViewHolder viewHolder, int eventId, Object obj) {
                if (eventId == CommonExpandItemViewHolder.EVENT_CLICK) {
                    if (obj instanceof CommonExpandItem) {
                        if (itemClickListener != null) {
                            itemClickListener.onClickItem((CommonExpandItem) obj);
                        }
                    } else if (obj instanceof CustomExpandItem) {
                        if (customItemClickListener != null) {
                            customItemClickListener.onClickCustom();
                        }
                    }
                } else if (eventId == CommonExpandItemViewHolder.EVENT_EDIT) {
                    if (obj instanceof CommonExpandItem) {
                        if (itemClickListener != null) {
                            itemClickListener.onEditItem((CommonExpandItem) obj);
                        }
                    }
                } else if (eventId == CommonExpandItemViewHolder.EVENT_DEL) {
                    if (obj instanceof CommonExpandItem) {
                        if (itemClickListener != null) {
                            itemClickListener.onDelItem((CommonExpandItem) obj);
                        }
                    }
                }
                dismiss();
            }
        });
        recyclerView.setAdapter(commonRecyclerAdapter);
    }

    @Override
    protected ViewGroup.LayoutParams getLayoutParams() {
        return null;
    }

    public void setNeedCustomItem(boolean needCustomItem) {
        this.needCustomItem = needCustomItem;
    }

    public void setAllowEditItem(boolean allowEditItem) {
        this.allowEditItem = allowEditItem;
    }

    public void setAllowDelItem(boolean allowDelItem) {
        this.allowDelItem = allowDelItem;
    }

    private OnExpandItemClickListener itemClickListener;

    public void setItemClickListener(OnExpandItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    private OnCustomItemClickListener customItemClickListener;

    public void setCustomItemClickListener(OnCustomItemClickListener customItemClickListener) {
        this.customItemClickListener = customItemClickListener;
    }

    public void setExpandItemList(List expandItemList) {
        if (expandItemList != null) {
            commonRecyclerAdapter.removeAllData();
            if (needCustomItem) {
                expandItemList.add(0, new CustomExpandItem());
            }
            for (int i = 0; i < expandItemList.size(); i++) {
                Object o = expandItemList.get(i);
                if (o instanceof CommonExpandItem) {
                    CommonExpandItem item = (CommonExpandItem) o;
                    item.setAllowEdit(allowEditItem);
                    item.setAllowDel(allowDelItem);
                }
            }
            commonRecyclerAdapter.addDataList(expandItemList);
        } else {
            commonRecyclerAdapter.removeAllData();
            if (needCustomItem) {
                commonRecyclerAdapter.addData(new CustomExpandItem());
            }
        }
    }

    /**
     * 添加自定义的item
     *
     * @param customExpandItem
     */
    public void addCustomItem(CommonExpandItem customExpandItem) {
        List<Object> dataList = commonRecyclerAdapter.getDataList();
        if (dataList != null) {
            List<Object> newDataList = new ArrayList<>();
            newDataList.add(customExpandItem);
            for (int i = 0; i < dataList.size(); i++) {
                Object o = dataList.get(i);
                if (o instanceof CommonExpandItem) {
                    newDataList.add(o);
                }
            }
            setExpandItemList(newDataList);
        }
    }

    public interface OnExpandItemClickListener {
        void onClickItem(CommonExpandItem commonExpandItem);

        default void onEditItem(CommonExpandItem commonExpandItem) {

        }

        default void onDelItem(CommonExpandItem commonExpandItem) {
        }
    }

    public interface OnCustomItemClickListener {
        void onClickCustom();
    }
}
