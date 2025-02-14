package com.stkj.infocollect.setting.ui.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stkj.infocollect.BuildConfig;
import com.stkj.infocollect.R;
import com.stkj.infocollect.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.infocollect.base.ui.widget.CommonSeekProgressBar;
import com.stkj.infocollect.setting.callback.FacePassSettingCallback;
import com.stkj.infocollect.setting.data.FacePassDateBaseMMKV;
import com.stkj.infocollect.setting.model.ResumeFacePassDetect;
import com.stkj.cbgfacepass.CBGFacePassHandlerHelper;
import com.stkj.cbgfacepass.data.CBGFacePassConfigMMKV;
import com.stkj.common.log.LogHelper;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.shapeselectlayout.ShapeSelectTextView;
import com.stkj.common.utils.AndroidUtils;
import com.stkj.common.utils.FragmentUtils;

import org.greenrobot.eventbus.EventBus;

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
    private CommonSeekProgressBar seekbarDetectFaceMinThreshold;
    private TextView tvDetectFaceMinThresholdTips;
    private CommonSeekProgressBar seekbarAddFaceMinThreshold;
    private CommonSeekProgressBar seekbarPoseThreshold;
    private FacePassSettingCallback facePassSettingCallback;
    private ShapeSelectTextView stvDetectMinThreshold50;
    private ShapeSelectTextView stvDetectMinThreshold80;
    private ShapeSelectTextView stvDetectMinThreshold100;


    public void setFacePassSettingCallback(FacePassSettingCallback facePassSettingCallback) {
        this.facePassSettingCallback = facePassSettingCallback;
    }

    @Override
    protected int getLayoutResId() {
        return com.stkj.infocollect.R.layout.fragment_facepass_setting_alert_dialog;
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
                //恢复人脸识别功能
                EventBus.getDefault().post(new ResumeFacePassDetect());
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
        tvDetectFaceMinThresholdTips = (TextView) findViewById(R.id.tv_detect_faceMinThreshold_tips);
        stvDetectMinThreshold50 = (ShapeSelectTextView) findViewById(R.id.stv_detect_MinThreshold50);
        stvDetectMinThreshold80 = (ShapeSelectTextView) findViewById(R.id.stv_detect_MinThreshold80);
        stvDetectMinThreshold100 = (ShapeSelectTextView) findViewById(R.id.stv_detect_MinThreshold100);
        stvDetectMinThreshold50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unSelectAllFaceMinThreshold();
                stvDetectMinThreshold50.setShapeSelect(true);
                CBGFacePassConfigMMKV.putDetectFaceLevel(CBGFacePassConfigMMKV.DETECT_FACE_LEVEL50);
                int cm50FaceMinThreshold = FacePassDateBaseMMKV.DEFAULT_DETECT_FACE_LEVEL50;
                seekbarDetectFaceMinThreshold.setSeekProgress(cm50FaceMinThreshold);
                //转换成旷视sdk的识别距离设置
                int maxProgress = seekbarDetectFaceMinThreshold.getMaxProgress();
                int cbgFaceMinThreshold = maxProgress - cm50FaceMinThreshold;
                CBGFacePassConfigMMKV.putDetectFaceMinThreshold(cbgFaceMinThreshold);
                updateFacePassConfig();
            }
        });
        stvDetectMinThreshold80.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unSelectAllFaceMinThreshold();
                stvDetectMinThreshold80.setShapeSelect(true);
                CBGFacePassConfigMMKV.putDetectFaceLevel(CBGFacePassConfigMMKV.DETECT_FACE_LEVEL80);
                int cm80FaceMinThreshold = FacePassDateBaseMMKV.DEFAULT_DETECT_FACE_LEVEL80;
                seekbarDetectFaceMinThreshold.setSeekProgress(cm80FaceMinThreshold);
                //转换成旷视sdk的识别距离设置
                int maxProgress = seekbarDetectFaceMinThreshold.getMaxProgress();
                int cbgFaceMinThreshold = maxProgress - cm80FaceMinThreshold;
                CBGFacePassConfigMMKV.putDetectFaceMinThreshold(cbgFaceMinThreshold);
                updateFacePassConfig();
            }
        });
        stvDetectMinThreshold100 = (ShapeSelectTextView) findViewById(R.id.stv_detect_MinThreshold100);
        stvDetectMinThreshold100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unSelectAllFaceMinThreshold();
                stvDetectMinThreshold100.setShapeSelect(true);
                CBGFacePassConfigMMKV.putDetectFaceLevel(CBGFacePassConfigMMKV.DETECT_FACE_LEVEL100);
                int cm100FaceMinThreshold = FacePassDateBaseMMKV.DEFAULT_DETECT_FACE_LEVEL100;
                seekbarDetectFaceMinThreshold.setSeekProgress(cm100FaceMinThreshold);
                //转换成旷视sdk的识别距离设置
                int maxProgress = seekbarDetectFaceMinThreshold.getMaxProgress();
                int cbgFaceMinThreshold = maxProgress - cm100FaceMinThreshold;
                CBGFacePassConfigMMKV.putDetectFaceMinThreshold(cbgFaceMinThreshold);
                updateFacePassConfig();
            }
        });
        //识别距离阈值
        seekbarDetectFaceMinThreshold = (CommonSeekProgressBar) findViewById(R.id.seekbar_detect_faceMinThreshold);
        seekbarDetectFaceMinThreshold.setSeekProgressListener(new CommonSeekProgressBar.OnCommonSeekProgressListener() {
            @Override
            public void onProgressFinished(int progress) {
                unSelectAllFaceMinThreshold();
                CBGFacePassConfigMMKV.putDetectFaceLevel(0);
                //转换成旷视sdk的识别距离设置
                int maxProgress = seekbarDetectFaceMinThreshold.getMaxProgress();
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
        ivSwitchLiveness.setSelected(CBGFacePassConfigMMKV.getLivenessEnabled());
        ivSwitchOcclusionMode.setSelected(CBGFacePassConfigMMKV.getOcclusionMode());
        ivSwitchGaThreshold.setSelected(CBGFacePassConfigMMKV.getGaThresholdEnabled());
        seekbarSearchThreshold.setSeekProgress((int) CBGFacePassConfigMMKV.getSearchThreshold());
        seekbarGaThreshold.setSeekProgress((int) CBGFacePassConfigMMKV.getGaThreshold());
        seekbarLivenessThreshold.setSeekProgress((int) CBGFacePassConfigMMKV.getLivenessThreshold());
        //识别距离阈值
        int faceMinThresholdLevel = CBGFacePassConfigMMKV.getDetectFaceLevel();
        unSelectAllFaceMinThreshold();
        if (faceMinThresholdLevel == CBGFacePassConfigMMKV.DETECT_FACE_LEVEL50) {
            stvDetectMinThreshold50.setShapeSelect(true);
            int cm50FaceMinThreshold = FacePassDateBaseMMKV.DEFAULT_DETECT_FACE_LEVEL50;
            seekbarDetectFaceMinThreshold.setSeekProgress(cm50FaceMinThreshold);
        } else if (faceMinThresholdLevel == CBGFacePassConfigMMKV.DETECT_FACE_LEVEL80) {
            stvDetectMinThreshold80.setShapeSelect(true);
            int cm80FaceMinThreshold = FacePassDateBaseMMKV.DEFAULT_DETECT_FACE_LEVEL80;
            seekbarDetectFaceMinThreshold.setSeekProgress(cm80FaceMinThreshold);
        } else if (faceMinThresholdLevel == CBGFacePassConfigMMKV.DETECT_FACE_LEVEL100) {
            stvDetectMinThreshold100.setShapeSelect(true);
            int cm100FaceMinThreshold = FacePassDateBaseMMKV.DEFAULT_DETECT_FACE_LEVEL100;
            seekbarDetectFaceMinThreshold.setSeekProgress(cm100FaceMinThreshold);
        } else {
            //为了本地调节方便，进行反向设置识别阈值 (512 - cbgFaceMinThreshold)
            int maxProgress = seekbarDetectFaceMinThreshold.getMaxProgress();
            int seekBarFaceMinThreshold = maxProgress - CBGFacePassConfigMMKV.getDetectFaceMinThreshold();
            seekbarDetectFaceMinThreshold.setSeekProgress(seekBarFaceMinThreshold);
        }
        if (BuildConfig.DEBUG) {
            seekbarDetectFaceMinThreshold.setVisibility(View.VISIBLE);
            tvDetectFaceMinThresholdTips.setVisibility(View.VISIBLE);
        } else {
            seekbarDetectFaceMinThreshold.setVisibility(View.GONE);
            tvDetectFaceMinThresholdTips.setVisibility(View.GONE);
        }
        //人脸入库阈值
        int addFaceMinThreshold = CBGFacePassConfigMMKV.getAddFaceMinThreshold();
        seekbarAddFaceMinThreshold.setSeekProgress(addFaceMinThreshold);
        //人脸角度阈值
        int poseThreshold = CBGFacePassConfigMMKV.getPoseThreshold();
        seekbarPoseThreshold.setSeekProgress(poseThreshold);
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
                        //恢复人脸识别功能
                        EventBus.getDefault().post(new ResumeFacePassDetect());
                        FragmentUtils.safeRemoveFragment(getParentFragmentManager(), FacePassSettingAlertFragment.this);
                        if (facePassSettingCallback != null) {
                            facePassSettingCallback.needUpdateFacePass();
                        }
                    }
                })
                .setRightNavTxt("稍后更新")
                .show(mActivity);
    }

    private void unSelectAllFaceMinThreshold() {
        stvDetectMinThreshold50.setShapeSelect(false);
        stvDetectMinThreshold80.setShapeSelect(false);
        stvDetectMinThreshold100.setShapeSelect(false);
    }

    /**
     * 更新人脸识别设置
     */
    private void updateFacePassConfig() {
        CBGFacePassHandlerHelper facePassHandlerHelper = mActivity.getWeakRefHolder(CBGFacePassHandlerHelper.class);
        facePassHandlerHelper.setCBGFacePassConfig(CBGFacePassConfigMMKV.getFacePassConfig(false));
        AppToast.toastMsg("设置已生效");
    }

    /**
     * 更新人脸入库设置
     */
    private void updateAddFacePassConfig() {
        CBGFacePassHandlerHelper facePassHandlerHelper = mActivity.getWeakRefHolder(CBGFacePassHandlerHelper.class);
        facePassHandlerHelper.setCBGAddFacePassConfig(CBGFacePassConfigMMKV.getFacePassConfig(false));
        AppToast.toastMsg("设置已生效");
    }
}
