package com.stkj.supermarket.setting.ui.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stkj.cbgfacepass.CBGFacePassHandlerHelper;
import com.stkj.cbgfacepass.data.CBGFacePassConfigMMKV;
import com.stkj.common.log.LogHelper;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.utils.AndroidUtils;
import com.stkj.common.utils.FragmentUtils;
import com.stkj.supermarket.BuildConfig;
import com.stkj.supermarket.R;
import com.stkj.supermarket.base.device.DeviceManager;
import com.stkj.supermarket.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.supermarket.base.ui.widget.CommonSeekProgressBar;
import com.stkj.supermarket.base.utils.CommonDialogUtils;
import com.stkj.supermarket.setting.callback.FacePassSettingCallback;

/**
 * 人脸识别设置弹窗
 */
public class FacePassSettingAlertFragment extends BaseRecyclerFragment {
    private LinearLayout llAddFaceSetting;
    private TextView tvAddFaceSetting;
    private ImageView ivClose;
    private TextView tvLiveness;
    private TextView tvDualCamera;
    private ImageView ivSwitchDualCamera;
    private ImageView ivSwitchLiveness;
    private ImageView ivSwitchOcclusionMode;
    private ImageView ivSwitchGaThreshold;
    private CommonSeekProgressBar seekbarSearchThreshold;
    private CommonSeekProgressBar seekbarGaThreshold;
    private CommonSeekProgressBar seekbarLivenessThreshold;
    private CommonSeekProgressBar seekbarFaceMinThreshold;
    private CommonSeekProgressBar seekbarAddFaceMinThreshold;
    private CommonSeekProgressBar seekbarResultSearchScoreThreshold;
    private CommonSeekProgressBar seekbarPoseThreshold;
    private FacePassSettingCallback facePassSettingCallback;

    public void setFacePassSettingCallback(FacePassSettingCallback facePassSettingCallback) {
        this.facePassSettingCallback = facePassSettingCallback;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_facepass_setting_alert_dialog;
    }

    @Override
    protected void initViews(View rootView) {
        llAddFaceSetting = (LinearLayout) findViewById(R.id.ll_add_face_setting);
        tvAddFaceSetting = (TextView) findViewById(R.id.tv_add_face_setting);
        if (BuildConfig.DEBUG) {
            llAddFaceSetting.setVisibility(View.VISIBLE);
            tvAddFaceSetting.setVisibility(View.VISIBLE);
        } else {
            llAddFaceSetting.setVisibility(View.GONE);
            tvAddFaceSetting.setVisibility(View.GONE);
        }
        ivClose = (ImageView) findViewById(R.id.iv_close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtils.safeRemoveFragment(getParentFragmentManager(), FacePassSettingAlertFragment.this);
            }
        });
        ivSwitchLiveness = (ImageView) findViewById(R.id.iv_switch_liveness);
        ivSwitchLiveness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean selected = !ivSwitchLiveness.isSelected();
                ivSwitchLiveness.setSelected(selected);
                CBGFacePassConfigMMKV.putLivenessEnabled(selected);
                updateFacePassConfig();
            }
        });
        tvLiveness = (TextView) findViewById(R.id.tv_liveness);
        tvDualCamera = (TextView) findViewById(R.id.tv_dual_camera);
        ivSwitchDualCamera = (ImageView) findViewById(R.id.iv_switch_dual_camera);
        boolean supportDualCamera = DeviceManager.INSTANCE.getDeviceInterface().isSupportDualCamera();
        if (supportDualCamera) {
            tvDualCamera.setVisibility(View.VISIBLE);
            ivSwitchDualCamera.setVisibility(View.VISIBLE);
            ivSwitchDualCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonAlertDialogFragment.build()
                            .setAlertTitleTxt("提示")
                            .setAlertContentTxt("打开/关闭双目检测需要重启App")
                            .setLeftNavTxt("确认重启")
                            .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                                @Override
                                public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                                    boolean selected = !ivSwitchDualCamera.isSelected();
                                    ivSwitchLiveness.setSelected(selected);
                                    CBGFacePassConfigMMKV.setOpenDualCamera(selected);
                                    DeviceManager.INSTANCE.getDeviceInterface().release();
                                    AndroidUtils.restartApp();
                                }
                            }).setRightNavTxt("取消")
                            .show(mActivity);
                }
            });
            //如果开启双目检测 不需要活体开关
            if (CBGFacePassConfigMMKV.isOpenDualCamera()) {
                tvLiveness.setVisibility(View.GONE);
                ivSwitchLiveness.setVisibility(View.GONE);
                ivSwitchDualCamera.setSelected(true);
            } else {
                tvLiveness.setVisibility(View.VISIBLE);
                ivSwitchLiveness.setVisibility(View.VISIBLE);
            }
        } else {
            tvDualCamera.setVisibility(View.GONE);
            ivSwitchDualCamera.setVisibility(View.GONE);
        }
        ivSwitchOcclusionMode = (ImageView) findViewById(R.id.iv_switch_occlusionMode);
        ivSwitchOcclusionMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean selected = !ivSwitchOcclusionMode.isSelected();
                ivSwitchOcclusionMode.setSelected(selected);
                CBGFacePassConfigMMKV.putOcclusionMode(selected);
                updateFacePassConfig();
            }
        });
        ivSwitchGaThreshold = (ImageView) findViewById(R.id.iv_switch_GaThreshold);
        ivSwitchGaThreshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean selected = !ivSwitchGaThreshold.isSelected();
                ivSwitchGaThreshold.setSelected(selected);
                CBGFacePassConfigMMKV.putGaThresholdEnabled(selected);
                updateFacePassConfig();
            }
        });
        seekbarSearchThreshold = (CommonSeekProgressBar) findViewById(R.id.seekbar_searchThreshold);
        seekbarSearchThreshold.setSeekProgressListener(new CommonSeekProgressBar.OnCommonSeekProgressListener() {
            @Override
            public void onProgressFinished(int progress) {
                CBGFacePassConfigMMKV.putSearchThreshold(progress);
                updateFacePassConfig();
            }
        });
        seekbarGaThreshold = (CommonSeekProgressBar) findViewById(R.id.seekbar_GaThreshold);
        seekbarGaThreshold.setSeekProgressListener(new CommonSeekProgressBar.OnCommonSeekProgressListener() {
            @Override
            public void onProgressFinished(int progress) {
                CBGFacePassConfigMMKV.putGaThreshold(progress);
                updateFacePassConfig();
            }
        });
        seekbarLivenessThreshold = (CommonSeekProgressBar) findViewById(R.id.seekbar_livenessThreshold);
        seekbarLivenessThreshold.setSeekProgressListener(new CommonSeekProgressBar.OnCommonSeekProgressListener() {
            @Override
            public void onProgressFinished(int progress) {
                CBGFacePassConfigMMKV.putLivenessThreshold(progress);
                updateFacePassConfig();
            }
        });
        //角度识别阈值
        seekbarPoseThreshold = (CommonSeekProgressBar) findViewById(R.id.seekbar_poseThreshold);
        seekbarPoseThreshold.setSeekProgressListener(new CommonSeekProgressBar.OnCommonSeekProgressListener() {
            @Override
            public void onProgressFinished(int progress) {
                CBGFacePassConfigMMKV.putPoseThreshold(progress);
                updateFacePassConfig();
            }
        });
        //识别距离阈值
        seekbarFaceMinThreshold = (CommonSeekProgressBar) findViewById(R.id.seekbar_faceMinThreshold);
        seekbarFaceMinThreshold.setSeekProgressListener(new CommonSeekProgressBar.OnCommonSeekProgressListener() {
            @Override
            public void onProgressFinished(int progress) {
                int maxProgress = seekbarFaceMinThreshold.getMaxProgress();
                int faceMinThreshold = maxProgress - progress;
                CBGFacePassConfigMMKV.putDetectFaceMinThreshold(faceMinThreshold);
                LogHelper.print("---seekbarFaceMinThreshold----new faceMinThreshold: " + faceMinThreshold);
                updateFacePassConfig();
            }
        });
        //人脸入库阈值
        seekbarAddFaceMinThreshold = (CommonSeekProgressBar) findViewById(R.id.seekbar_add_faceMinThreshold);
        seekbarAddFaceMinThreshold.setSeekProgressListener(new CommonSeekProgressBar.OnCommonSeekProgressListener() {
            @Override
            public void onProgressFinished(int progress) {
                CBGFacePassConfigMMKV.putAddFaceMinThreshold(progress);
                LogHelper.print("---seekbarFaceMinThreshold----new add faceMinThreshold: " + progress);
                updateAddFacePassConfig();
                showNeedUpdateFacePass();
            }
        });
        //人脸识别结果分数阈值
        seekbarResultSearchScoreThreshold = (CommonSeekProgressBar) findViewById(R.id.seekbar_resultSearchScoreThreshold);
        seekbarResultSearchScoreThreshold.setSeekProgressListener(new CommonSeekProgressBar.OnCommonSeekProgressListener() {
            @Override
            public void onProgressFinished(int progress) {
                //不能低于识别阈值
                float searchThreshold = CBGFacePassConfigMMKV.getSearchThreshold();
                if (progress < searchThreshold) {
                    progress = (int) searchThreshold;
                    seekbarResultSearchScoreThreshold.setSeekProgress(progress);
                    CommonDialogUtils.showTipsDialog(mActivity, "识别结果分数不能低于当前识别阈值");
                }
                CBGFacePassConfigMMKV.putResultSearchScoreThreshold(progress);
                LogHelper.print("---seekbarFaceMinThreshold----new resultSearchScoreThreshold: " + progress);
                updateFacePassConfig();
            }
        });
        ivSwitchLiveness.setSelected(CBGFacePassConfigMMKV.getLivenessEnabled());
        ivSwitchOcclusionMode.setSelected(CBGFacePassConfigMMKV.getOcclusionMode());
        ivSwitchGaThreshold.setSelected(CBGFacePassConfigMMKV.getGaThresholdEnabled());
        seekbarSearchThreshold.setSeekProgress((int) CBGFacePassConfigMMKV.getSearchThreshold());
        seekbarGaThreshold.setSeekProgress((int) CBGFacePassConfigMMKV.getGaThreshold());
        seekbarLivenessThreshold.setSeekProgress((int) CBGFacePassConfigMMKV.getLivenessThreshold());
        //识别距离阈值
        int maxProgress = seekbarFaceMinThreshold.getMaxProgress();
        int faceMinThreshold = maxProgress - CBGFacePassConfigMMKV.getDetectFaceMinThreshold();
        seekbarFaceMinThreshold.setSeekProgress(faceMinThreshold);
        //人脸入库阈值
        int addFaceMinThreshold = CBGFacePassConfigMMKV.getAddFaceMinThreshold();
        seekbarAddFaceMinThreshold.setSeekProgress(addFaceMinThreshold);
        //人脸角度阈值
        int poseThreshold = CBGFacePassConfigMMKV.getPoseThreshold();
        seekbarPoseThreshold.setSeekProgress(poseThreshold);
        //人脸识别分数
        int resultSearchScoreThreshold = CBGFacePassConfigMMKV.getResultSearchScoreThreshold();
        seekbarResultSearchScoreThreshold.setSeekProgress(resultSearchScoreThreshold);
    }

    /**
     * 显示需要更新人脸库弹窗
     */
    private void showNeedUpdateFacePass() {
        CommonAlertDialogFragment.build()
                .setAlertTitleTxt("提示")
                .setAlertContentTxt("修改此值需要更新人脸库,确认更新?")
                .setLeftNavTxt("现在更新")
                .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                    @Override
                    public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                        FragmentUtils.safeRemoveFragment(getParentFragmentManager(), FacePassSettingAlertFragment.this);
                        if (facePassSettingCallback != null) {
                            facePassSettingCallback.needUpdateFacePass();
                        }
                    }
                })
                .setRightNavTxt("稍后更新")
                .show(mActivity);
    }

    /**
     * 更新人脸识别设置
     */
    private void updateFacePassConfig() {
        CBGFacePassHandlerHelper facePassHandlerHelper = mActivity.getWeakRefHolder(CBGFacePassHandlerHelper.class);
        boolean supportDualCamera = DeviceManager.INSTANCE.getDeviceInterface().isSupportDualCamera();
        facePassHandlerHelper.setCBGFacePassConfig(CBGFacePassConfigMMKV.getFacePassConfig(supportDualCamera));
    }

    /**
     * 更新人脸入库设置
     */
    private void updateAddFacePassConfig() {
        CBGFacePassHandlerHelper facePassHandlerHelper = mActivity.getWeakRefHolder(CBGFacePassHandlerHelper.class);
        boolean supportDualCamera = DeviceManager.INSTANCE.getDeviceInterface().isSupportDualCamera();
        facePassHandlerHelper.setCBGAddFacePassConfig(CBGFacePassConfigMMKV.getFacePassConfig(supportDualCamera));
    }
}
