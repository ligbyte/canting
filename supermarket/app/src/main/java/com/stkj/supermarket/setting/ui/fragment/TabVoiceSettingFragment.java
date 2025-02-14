package com.stkj.supermarket.setting.ui.fragment;

import android.view.View;
import android.widget.ImageView;

import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.utils.AudioMngHelper;
import com.stkj.supermarket.R;
import com.stkj.supermarket.base.tts.TTSVoiceHelper;
import com.stkj.supermarket.base.ui.widget.CommonSeekProgressBar;
import com.stkj.supermarket.setting.data.PaymentSettingMMKV;
import com.stkj.supermarket.setting.data.TTSSettingMMKV;

/**
 * 语音设置
 */
public class TabVoiceSettingFragment extends BaseRecyclerFragment {

    private CommonSeekProgressBar seekbarVoiceSize;
    private CommonSeekProgressBar seekbarVoiceSpeed;
    private ShapeTextView stvTtsTest;
    private ImageView ivSwitchNetStatus;

    @Override
    protected void initViews(View rootView) {
        AudioMngHelper audioMngHelper = new AudioMngHelper(mActivity);
        int currentVolume = audioMngHelper.get100CurrentVolume();
        seekbarVoiceSize = (CommonSeekProgressBar) findViewById(R.id.seekbar_voice_size);
        seekbarVoiceSize.setSeekProgress(currentVolume);
        seekbarVoiceSize.setSeekProgressListener(new CommonSeekProgressBar.OnCommonSeekProgressListener() {
            @Override
            public void onProgressFinished(int progress) {
                audioMngHelper.setVoice100(progress);
                getTTSVoiceHelper().setVoice();
            }
        });
        seekbarVoiceSpeed = (CommonSeekProgressBar) findViewById(R.id.seekbar_voice_speed);
        seekbarVoiceSpeed.setSeekProgress((int) (TTSSettingMMKV.getTTSSpeakSpeed() * 100));
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
    }

    private TTSVoiceHelper getTTSVoiceHelper() {
        return mActivity.getWeakRefHolder(TTSVoiceHelper.class);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_tab_voice_setting;
    }
}
