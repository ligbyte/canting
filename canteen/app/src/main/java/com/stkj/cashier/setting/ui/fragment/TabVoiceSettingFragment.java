package com.stkj.cashier.setting.ui.fragment;

import android.view.View;
import android.widget.ImageView;

import com.stkj.cashier.R;
import com.stkj.cashier.base.tts.TTSVoiceHelper;
import com.stkj.cashier.base.ui.dialog.CommonInputDialogFragment;
import com.stkj.cashier.base.ui.widget.CommonSeekProgressBar;
import com.stkj.cashier.base.utils.EventBusUtils;
import com.stkj.cashier.setting.data.TTSSettingMMKV;
import com.stkj.cashier.setting.model.RefreshPayType;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.utils.AudioMngHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 语音设置
 */
public class TabVoiceSettingFragment extends BaseRecyclerFragment {

    private CommonSeekProgressBar seekbarVoiceSize;
    private CommonSeekProgressBar seekbarVoiceSpeed;
    private ShapeTextView stvTtsTest;
    private ImageView ivSwitchNetStatus;
    private ShapeTextView stvConsumerSuccessVoice;
    private ShapeTextView stvChangeConsumerSuccessVoice;
    private ShapeTextView stvTakeSuccessVoice;
    private ShapeTextView stvChangeTakeSuccessVoice;
    private ShapeTextView stvPayTypeVoice;
    private ShapeTextView stvChangePayTypeVoice;

    @Override
    protected void initViews(View rootView) {
        seekbarVoiceSize = (CommonSeekProgressBar) findViewById(R.id.seekbar_voice_size);
        seekbarVoiceSize.setSeekProgressListener(new CommonSeekProgressBar.OnCommonSeekProgressListener() {
            @Override
            public void onProgressFinished(int progress) {
                AudioMngHelper audioMngHelper = new AudioMngHelper(mActivity);
                audioMngHelper.setVoice100(progress);
                getTTSVoiceHelper().setVoice();
            }
        });
        seekbarVoiceSpeed = (CommonSeekProgressBar) findViewById(R.id.seekbar_voice_speed);
        seekbarVoiceSpeed.setSeekProgressListener(new CommonSeekProgressBar.OnCommonSeekProgressListener() {
            @Override
            public void onProgressFinished(int progress) {
                float speed = progress * 1.0f / 100f;
                TTSSettingMMKV.putTTSSpeakSpeed(speed);
                getTTSVoiceHelper().setSpeed(speed);
            }
        });
        stvTtsTest = (ShapeTextView) findViewById(R.id.stv_tts_test);
        stvTtsTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTTSVoiceHelper().speakByTTSVoice(TTSVoiceHelper.TEST_WORDS);
            }
        });
        ivSwitchNetStatus = (ImageView) findViewById(R.id.iv_switch_net_status);
        boolean ttsNetStatus = TTSSettingMMKV.getTTSNetStatus();
        ivSwitchNetStatus.setSelected(ttsNetStatus);
        ivSwitchNetStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean ttsNetStatus = !ivSwitchNetStatus.isSelected();
                ivSwitchNetStatus.setSelected(ttsNetStatus);
                TTSSettingMMKV.putTTSNetStatus(ttsNetStatus);
            }
        });
        stvConsumerSuccessVoice = (ShapeTextView) findViewById(R.id.stv_consumer_success_voice);
        stvChangeConsumerSuccessVoice = (ShapeTextView) findViewById(R.id.stv_change_consumer_success_voice);
        String consumeSuccessVoice = TTSSettingMMKV.getConsumeSuccessVoice();
        stvConsumerSuccessVoice.setText(consumeSuccessVoice == null ? "" : consumeSuccessVoice);
        View.OnClickListener consumerSuccessClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonInputDialogFragment.build()
                        .setTitle("修改消费成功文案")
                        .setInputContent(stvConsumerSuccessVoice.getText().toString())
                        .setOnInputListener(new CommonInputDialogFragment.OnInputListener() {
                            @Override
                            public void onInputEnd(String input) {
                                TTSSettingMMKV.putConsumeSuccessVoice(input);
                                stvConsumerSuccessVoice.setText(input);
                                AppToast.toastMsg("修改成功");
                            }
                        }).show(mActivity);
            }
        };
        stvChangeConsumerSuccessVoice.setOnClickListener(consumerSuccessClickListener);
        stvConsumerSuccessVoice.setOnClickListener(consumerSuccessClickListener);
        stvTakeSuccessVoice = (ShapeTextView) findViewById(R.id.stv_take_success_voice);
        stvChangeTakeSuccessVoice = (ShapeTextView) findViewById(R.id.stv_change_take_success_voice);
        String takeSuccessVoice = TTSSettingMMKV.getTakeSuccessVoice();
        stvTakeSuccessVoice.setText(takeSuccessVoice == null ? "" : takeSuccessVoice);
        View.OnClickListener takeSuccessClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonInputDialogFragment.build()
                        .setTitle("修改取餐成功文案")
                        .setInputContent(stvTakeSuccessVoice.getText().toString())
                        .setOnInputListener(new CommonInputDialogFragment.OnInputListener() {
                            @Override
                            public void onInputEnd(String input) {
                                TTSSettingMMKV.putTakeSuccessVoice(input);
                                stvTakeSuccessVoice.setText(input);
                                AppToast.toastMsg("修改成功");
                            }
                        }).show(mActivity);
            }
        };
        stvTakeSuccessVoice.setOnClickListener(takeSuccessClickListener);
        stvChangeTakeSuccessVoice.setOnClickListener(takeSuccessClickListener);
        stvPayTypeVoice = (ShapeTextView) findViewById(R.id.stv_pay_type_voice);
        stvChangePayTypeVoice = (ShapeTextView) findViewById(R.id.stv_change_pay_type_voice);
        String payTypeVoice = TTSSettingMMKV.getPayTypeVoice();
        stvPayTypeVoice.setText(payTypeVoice == null ? "" : payTypeVoice);
        View.OnClickListener payTypeClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonInputDialogFragment.build()
                        .setTitle("修改支付方式文案")
                        .setInputContent(stvPayTypeVoice.getText().toString())
                        .setOnInputListener(new CommonInputDialogFragment.OnInputListener() {
                            @Override
                            public void onInputEnd(String input) {
                                TTSSettingMMKV.putPayTypeVoice(input);
                                stvPayTypeVoice.setText(input);
                                AppToast.toastMsg("修改成功");
                            }
                        }).show(mActivity);
            }
        };
        stvPayTypeVoice.setOnClickListener(payTypeClickListener);
        stvChangePayTypeVoice.setOnClickListener(payTypeClickListener);
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        EventBusUtils.registerEventBus(this);
        //音量
        AudioMngHelper audioMngHelper = new AudioMngHelper(mActivity);
        int currentVolume = audioMngHelper.get100CurrentVolume();
        seekbarVoiceSize.setSeekProgress(currentVolume);
        //语速
        seekbarVoiceSpeed.setSeekProgress((int) (TTSSettingMMKV.getTTSSpeakSpeed() * 100));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshPayType(RefreshPayType refreshPayType) {
        if (stvPayTypeVoice != null) {
            String payTypeVoice = TTSSettingMMKV.getPayTypeVoice();
            stvPayTypeVoice.setText(payTypeVoice == null ? "" : payTypeVoice);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBusUtils.unRegisterEventBus(this);
    }

    private TTSVoiceHelper getTTSVoiceHelper() {
        return mActivity.getWeakRefHolder(TTSVoiceHelper.class);
    }

    @Override
    protected int getLayoutResId() {
        return com.stkj.cashier.R.layout.fragment_tab_voice_setting;
    }
}
