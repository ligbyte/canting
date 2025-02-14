package com.stkj.cashier.setting.ui.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.stkj.cashier.R;
import com.stkj.cashier.base.model.CommonExpandItem;
import com.stkj.cashier.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.cashier.base.ui.widget.AppSmartRefreshLayout;
import com.stkj.cashier.base.ui.widget.CommonExpandListPopWindow;
import com.stkj.cashier.base.utils.CommonDialogUtils;
import com.stkj.cashier.setting.callback.FacePassSettingCallback;
import com.stkj.cashier.setting.data.FacePassDateBaseMMKV;
import com.stkj.cashier.setting.helper.FacePassHelper;
import com.stkj.cashier.setting.model.FacePassPeopleInfo;
import com.stkj.cashier.setting.model.PauseFacePassDetect;
import com.stkj.cashier.setting.model.SearchFacePassPeopleParams;
import com.stkj.cashier.setting.ui.adapter.FacePassPeopleViewHolder;
import com.stkj.common.ui.adapter.CommonRecyclerAdapter;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.ui.widget.shapeselectlayout.ShapeSelectTextView;
import com.stkj.common.utils.FragmentUtils;
import com.stkj.common.utils.SpanUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 人脸识别设置
 */
public class TabFacePassSettingFragment extends BaseRecyclerFragment implements FacePassHelper.OnFacePassListener, FacePassSettingCallback {
    private LinearLayout llFaceSetting;
    private ShapeTextView stvFacepassSetting;
    private TextView stvFacepassCount;
    private ShapeSelectTextView sstvFacepassDetail;
    private ShapeSelectTextView sstvFacepassDelAll;
    private ShapeSelectTextView sstvFacepassUpdate;
    private ImageView ivFaceDataBack;
    private LinearLayout llFaceData;
    private FrameLayout flAllPeople;
    private ShapeTextView stvAllPeople;
    private ImageView ivAllPeople;
    private FrameLayout flAllDepartment;
    private ShapeTextView stvAllDepartment;
    private ImageView ivAllDepartment;
    private EditText etPeopleSearch;
    private ShapeTextView tvPeopleSearch;
    private RecyclerView rvFacePass;
    private CommonRecyclerAdapter facePassAdapter;
    private ProgressBar ivFacePassLoading;
    private TextView tvFacePassLoading;
    private AppSmartRefreshLayout srlFacePassList;
    private TextView tvFacePassEmpty;
    private SearchFacePassPeopleParams searchFacePassParams = new SearchFacePassPeopleParams();

    @Override
    protected int getLayoutResId() {
        return com.stkj.cashier.R.layout.fragment_tab_facepass_setting;
    }

    @Override
    protected void initViews(View rootView) {
        tvFacePassEmpty = (TextView) findViewById(R.id.tv_face_pass_empty);
        tvFacePassLoading = (TextView) findViewById(R.id.tv_face_pass_loading);
        ivFacePassLoading = (ProgressBar) findViewById(R.id.iv_face_pass_loading);
        llFaceSetting = (LinearLayout) findViewById(R.id.ll_face_setting);
        stvFacepassSetting = (ShapeTextView) findViewById(R.id.stv_facepass_setting);
        stvFacepassSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //正在更新人脸库，不可以设置
                FacePassHelper facePassHelper = getFacePassHelper();
                if (facePassHelper.isAddOrDeleteLocalFacePass()) {
                    CommonDialogUtils.showTipsDialog(mActivity, "人脸库正在更新中,不可操作,请稍等~");
                    return;
                }
                //暂停人脸识别功能
                EventBus.getDefault().post(new PauseFacePassDetect());
                FacePassSettingAlertFragment alertFragment = new FacePassSettingAlertFragment();
                alertFragment.setFacePassSettingCallback(TabFacePassSettingFragment.this);
                FragmentUtils.safeReplaceFragment(getParentFragmentManager(), alertFragment, R.id.fl_setting_second_content);
            }
        });
        stvFacepassCount = (TextView) findViewById(R.id.stv_facepass_count);
        sstvFacepassDetail = (ShapeSelectTextView) findViewById(R.id.sstv_facepass_detail);
        sstvFacepassDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivFaceDataBack.setVisibility(View.VISIBLE);
                llFaceData.setVisibility(View.VISIBLE);
                searchFacePassParams.resetParams();
                initSearchFacePeopleLay();
                getFacePassPeopleList(true);
                llFaceSetting.setVisibility(View.GONE);
            }
        });
        sstvFacepassDelAll = (ShapeSelectTextView) findViewById(R.id.sstv_facepass_del_all);
        sstvFacepassDelAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonAlertDialogFragment.build()
                        .setAlertTitleTxt("提示")
                        .setAlertContentTxt("确定清空人脸数据库吗")
                        .setLeftNavTxt("确定")
                        .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                            @Override
                            public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                                deleteAllFacePass();
                            }
                        })
                        .setRightNavTxt("取消")
                        .show(mActivity);
            }
        });
        sstvFacepassUpdate = (ShapeSelectTextView) findViewById(R.id.sstv_facepass_update);
        sstvFacepassUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonAlertDialogFragment.build()
                        .setAlertTitleTxt("提示")
                        .setAlertContentTxt("全量更新将删除本地数据库?")
                        .setLeftNavTxt("确定")
                        .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                            @Override
                            public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                                requestAllFacePass();
                            }
                        })
                        .setRightNavTxt("取消").show(mActivity);
            }
        });
        ivFaceDataBack = (ImageView) findViewById(R.id.iv_face_data_back);
        ivFaceDataBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facePassAdapter.removeAllData();
                ivFaceDataBack.setVisibility(View.GONE);
                llFaceData.setVisibility(View.GONE);
                llFaceSetting.setVisibility(View.VISIBLE);
            }
        });
        llFaceData = (LinearLayout) findViewById(R.id.ll_face_data);
        flAllPeople = (FrameLayout) findViewById(R.id.fl_all_people);
        stvAllPeople = (ShapeTextView) findViewById(R.id.stv_all_people);
        ivAllPeople = (ImageView) findViewById(R.id.iv_all_people);
        flAllDepartment = (FrameLayout) findViewById(R.id.fl_all_department);
        stvAllDepartment = (ShapeTextView) findViewById(R.id.stv_all_department);
        ivAllDepartment = (ImageView) findViewById(R.id.iv_all_department);
        etPeopleSearch = (EditText) findViewById(R.id.et_people_search);
        tvPeopleSearch = (ShapeTextView) findViewById(R.id.tv_people_search);
        rvFacePass = (RecyclerView) findViewById(R.id.rv_face_pass);
        facePassAdapter = new CommonRecyclerAdapter(false);
        facePassAdapter.addViewHolderFactory(new FacePassPeopleViewHolder.Factory());
        rvFacePass.setAdapter(facePassAdapter);
        srlFacePassList = (AppSmartRefreshLayout) findViewById(R.id.srl_face_pass_list);
        srlFacePassList.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getFacePassPeopleList(false);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getFacePassPeopleList(true);
            }
        });
    }

    /**
     * 初始化人脸库搜索布局
     */
    private void initSearchFacePeopleLay() {
        Set<String> departmentList = FacePassDateBaseMMKV.getDepartmentList();
        Set<String> accountTypeList = FacePassDateBaseMMKV.getAccountTypeList();
        if (accountTypeList.isEmpty() && departmentList.isEmpty()) {
            tvFacePassEmpty.setVisibility(View.VISIBLE);
            rvFacePass.setVisibility(View.GONE);
            flAllPeople.setOnClickListener(null);
            stvAllPeople.setText("无数据");
            flAllDepartment.setOnClickListener(null);
            stvAllDepartment.setText("无数据");
            tvPeopleSearch.setOnClickListener(null);
        } else {
            tvFacePassEmpty.setVisibility(View.GONE);
            rvFacePass.setVisibility(View.VISIBLE);
            List<CommonExpandItem> accountExpandList = new ArrayList<>();
            accountExpandList.add(new CommonExpandItem(0, "全部人员"));
            for (String s : accountTypeList) {
                accountExpandList.add(new CommonExpandItem(0, s));
            }
            stvAllPeople.setText("全部人员");
            flAllPeople.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ivAllPeople.setSelected(true);
                    CommonExpandListPopWindow commonExpandListPopWindow = new CommonExpandListPopWindow(mActivity);
                    commonExpandListPopWindow.setWidth(flAllPeople.getWidth());
                    commonExpandListPopWindow.setHeight(FrameLayout.LayoutParams.WRAP_CONTENT);
                    commonExpandListPopWindow.setItemClickListener(new CommonExpandListPopWindow.OnExpandItemClickListener() {
                        @Override
                        public void onClickItem(CommonExpandItem commonExpandItem) {
                            ivAllPeople.setSelected(false);
                            stvAllPeople.setText(commonExpandItem.getName());
                            getFacePassPeopleList(true);
                        }
                    });
                    commonExpandListPopWindow.setExpandItemList(accountExpandList);
                    commonExpandListPopWindow.showAsDropDown(flAllPeople);
                    commonExpandListPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            ivAllPeople.setSelected(false);
                        }
                    });
                }
            });
            List<CommonExpandItem> departmentExpandList = new ArrayList<>();
            departmentExpandList.add(new CommonExpandItem(0, "全部部门"));
            for (String s : departmentList) {
                departmentExpandList.add(new CommonExpandItem(0, s));
            }
            stvAllDepartment.setText("全部部门");
            flAllDepartment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ivAllDepartment.setSelected(true);
                    CommonExpandListPopWindow commonExpandListPopWindow = new CommonExpandListPopWindow(mActivity);
                    commonExpandListPopWindow.setWidth(flAllDepartment.getWidth());
                    commonExpandListPopWindow.setHeight(FrameLayout.LayoutParams.WRAP_CONTENT);
                    commonExpandListPopWindow.setItemClickListener(new CommonExpandListPopWindow.OnExpandItemClickListener() {
                        @Override
                        public void onClickItem(CommonExpandItem commonExpandItem) {
                            ivAllDepartment.setSelected(false);
                            stvAllDepartment.setText(commonExpandItem.getName());
                            getFacePassPeopleList(true);
                        }
                    });
                    commonExpandListPopWindow.setExpandItemList(departmentExpandList);
                    commonExpandListPopWindow.showAsDropDown(flAllDepartment);
                    commonExpandListPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            ivAllDepartment.setSelected(false);
                        }
                    });
                }
            });
            tvPeopleSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String string = etPeopleSearch.getText().toString();
                    searchFacePassParams.setSearchKey(string);
                    getFacePassPeopleList(true);
                }
            });
        }
    }

    /**
     * 获取人脸识别库人员列表
     */
    private void getFacePassPeopleList(boolean isFirstPage) {
        if (isFirstPage) {
            searchFacePassParams.setRequestOffset(0);
        } else {
            searchFacePassParams.setRequestOffset(facePassAdapter.getItemCount());
        }
        searchFacePassParams.setSearchKey(etPeopleSearch.getText().toString());
        searchFacePassParams.setAccountType(stvAllPeople.getText().toString());
        searchFacePassParams.setDepartment(stvAllDepartment.getText().toString());
        FacePassHelper facePassHelper = getFacePassHelper();
        facePassHelper.queryLocalFacePass(searchFacePassParams, new FacePassHelper.OnQueryLocalFacePassListener() {
            @Override
            public void onQueryLocalFacePassSuccess(List<FacePassPeopleInfo> facePassPeopleInfoList) {
                srlFacePassList.finishRefresh();
                srlFacePassList.finishLoadMore();
                if (isFirstPage) {
                    facePassAdapter.removeAllData();
                }
                if (facePassPeopleInfoList != null && !facePassPeopleInfoList.isEmpty()) {
                    facePassAdapter.addDataList(facePassPeopleInfoList);
                } else {
                    AppToast.toastMsg("没更多数据了");
                }
            }

            @Override
            public void onQueryLocalFacePassError(String msg) {
                srlFacePassList.finishRefresh();
                srlFacePassList.finishLoadMore();
                CommonDialogUtils.showTipsDialog(mActivity, "查询人脸数据库失败:" + msg);
            }
        });
    }

    private void setFacePassOperateEnable(boolean enable) {
        if (enable) {
            sstvFacepassDetail.setShapeSelect(true);
            sstvFacepassDetail.setClickable(true);
            sstvFacepassDelAll.setShapeSelect(true);
            sstvFacepassDelAll.setClickable(true);
            sstvFacepassUpdate.setShapeSelect(true);
            sstvFacepassUpdate.setClickable(true);
            ivFacePassLoading.setVisibility(View.GONE);
            tvFacePassLoading.setVisibility(View.GONE);
        } else {
            //置灰不可操作
            sstvFacepassDetail.setShapeSelect(false);
            sstvFacepassDetail.setClickable(false);
            sstvFacepassDelAll.setShapeSelect(false);
            sstvFacepassDelAll.setClickable(false);
            sstvFacepassUpdate.setShapeSelect(false);
            sstvFacepassUpdate.setClickable(false);
            ivFacePassLoading.setVisibility(View.VISIBLE);
            tvFacePassLoading.setVisibility(View.VISIBLE);
        }

    }

    private void requestAllFacePass() {
        setFacePassOperateEnable(false);
        getFacePassHelper().deleteAllFaceGroup(true);
    }

    private void deleteAllFacePass() {
        setFacePassOperateEnable(false);
        getFacePassHelper().deleteAllFaceGroup();
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        FacePassHelper facePassHelper = getFacePassHelper();
        facePassHelper.addOnFacePassListener(this);
        //正在操作本地人脸库时，按钮不可操作
        if (!facePassHelper.isAddOrDeleteLocalFacePass()) {
            setFacePassOperateEnable(true);
            facePassHelper.getFacePassLocalCount();
        } else {
            setFacePassOperateEnable(false);
        }
    }

    @Override
    public void onDetach() {
        FacePassHelper facePassHelper = getFacePassHelper();
        facePassHelper.removeOnFacePassListener(this);
        super.onDetach();
    }

    @Override
    public void onLoadFacePassGroupStart() {
        setFacePassOperateEnable(false);
    }

    @Override
    public void onLoadFacePassGroupEnd(List<FacePassPeopleInfo> facePassPeopleInfoList, String msg, boolean isError) {
        FacePassHelper.OnFacePassListener.super.onLoadFacePassGroupEnd(facePassPeopleInfoList, msg, isError);
        //更新人脸库完成
        if (facePassPeopleInfoList == null) {
            getFacePassHelper().getFacePassLocalCount();
            setFacePassOperateEnable(true);
            if (isError && !TextUtils.isEmpty(msg)) {
                CommonDialogUtils.showTipsDialog(mActivity, "更新人脸库出错:" + msg);
            }
        }
    }

    @Override
    public void onDeleteAllFacePassSuccess(boolean needRequestAllFace) {
        FacePassHelper.OnFacePassListener.super.onDeleteAllFacePassSuccess(needRequestAllFace);
        handleDeleteAllFacePass(needRequestAllFace);
    }

    @Override
    public void onDeleteAllFacePassError(boolean needRequestAllFace, String msg) {
        FacePassHelper.OnFacePassListener.super.onDeleteAllFacePassError(needRequestAllFace, msg);
        handleDeleteAllFacePass(needRequestAllFace);
    }

    /**
     * 统一处理删除人脸库成功和失败
     */
    private void handleDeleteAllFacePass(boolean needRequestAllFace) {
        refreshFacePassTotalCount(0);
        if (!needRequestAllFace) {
            setFacePassOperateEnable(true);
        }
    }

    @Override
    public void onGetFacePassLocalCount(long totalCount) {
        FacePassHelper.OnFacePassListener.super.onGetFacePassLocalCount(totalCount);
        refreshFacePassTotalCount(totalCount);
    }

    @Override
    public void onGetFacePassLocalCountError(String msg) {
        FacePassHelper.OnFacePassListener.super.onGetFacePassLocalCountError(msg);
//        refreshFacePassTotalCount(0);
    }

    private void refreshFacePassTotalCount(long count) {
        SpanUtils.with(stvFacepassCount)
                .append(String.valueOf(count))
                .setForegroundColor(mResources.getColor(R.color.color_3489F5))
                .append("人已入库")
                .create();
    }

    private FacePassHelper getFacePassHelper() {
        return mActivity.getWeakRefHolder(FacePassHelper.class);
    }

    @Override
    public void needUpdateFacePass() {
        FacePassHelper facePassHelper = getFacePassHelper();
        if (!facePassHelper.isAddOrDeleteLocalFacePass()) {
            requestAllFacePass();
        }
    }
}
