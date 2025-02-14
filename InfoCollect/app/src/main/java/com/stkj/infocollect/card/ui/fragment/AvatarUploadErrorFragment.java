package com.stkj.infocollect.card.ui.fragment;

import android.view.View;

import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.utils.FragmentUtils;
import com.stkj.infocollect.R;

/**
 * 人脸上传失败
 */
public class AvatarUploadErrorFragment extends BaseRecyclerFragment {

    private ShapeTextView stvRetryCamera;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_avatar_upload_error;
    }

    @Override
    protected void initViews(View rootView) {
        stvRetryCamera = (ShapeTextView) findViewById(R.id.stv_retry_camera);
        stvRetryCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.addContentPlaceHolderFragment(new AvatarCameraFragment());
                FragmentUtils.safeRemoveFragment(getParentFragmentManager(), AvatarUploadErrorFragment.this);
            }
        });
    }
}
