package com.stkj.infocollect.card.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.stkj.common.glide.GlideApp;
import com.stkj.common.ui.widget.shapelayout.ShapeEditText;
import com.stkj.common.ui.widget.shapelayout.ShapeFrameLayout;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.utils.KeyBoardUtils;
import com.stkj.common.utils.SpanUtils;
import com.stkj.infocollect.R;
import com.stkj.infocollect.card.model.UserCardInfo;

/**
 * 餐卡信息公用布局
 */
public class CardAccountInfoLayout extends FrameLayout {

    private TextView tvUserName;
    private TextView tvIdCard;
    private TextView tvParentName;
    private TextView tvParentPhone;
    private ShapeEditText setUserName;
    private ShapeEditText setIdCard;
    private ShapeEditText setParentName;
    private ShapeEditText setParentPhone;
    private LinearLayout llAccount;
    private TextView tvAccount;
    private ShapeTextView stvAccount;
    private LinearLayout llCardState;
    private TextView tvCardState;
    private ShapeTextView stvCardState;
    private ShapeFrameLayout sllCameraUpload;
    private LinearLayout llAddCameraFace;
    private ImageView ivAvatar;
    private String mAvatarUrl;
    private boolean mCanEdit = true;
    private OnClickListener avatarClickListener;

    public CardAccountInfoLayout(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public CardAccountInfoLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CardAccountInfoLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        boolean canEdit = true;
        if (attributeSet != null) {
            TypedArray array = context.obtainStyledAttributes(attributeSet, R.styleable.CardAccountInfoLayout);
            canEdit = array.getBoolean(R.styleable.CardAccountInfoLayout_cail_can_edit, true);
            array.recycle();
        }
        LayoutInflater.from(context).inflate(R.layout.include_card_account_info, this);
        llAddCameraFace = (LinearLayout) findViewById(R.id.ll_add_camera_face);
        tvUserName = (TextView) findViewById(R.id.tv_user_name);
        tvIdCard = (TextView) findViewById(R.id.tv_id_card);
        tvParentName = (TextView) findViewById(R.id.tv_parent_name);
        tvParentPhone = (TextView) findViewById(R.id.tv_parent_phone);
        setUserName = (ShapeEditText) findViewById(R.id.set_userName);
        setIdCard = (ShapeEditText) findViewById(R.id.set_id_card);
        setParentName = (ShapeEditText) findViewById(R.id.set_parent_name);
        setParentPhone = (ShapeEditText) findViewById(R.id.set_parent_phone);
        llAccount = (LinearLayout) findViewById(R.id.ll_account);
        tvAccount = (TextView) findViewById(R.id.tv_account);
        stvAccount = (ShapeTextView) findViewById(R.id.stv_account);
        llCardState = (LinearLayout) findViewById(R.id.ll_card_state);
        tvCardState = (TextView) findViewById(R.id.tv_card_state);
        stvCardState = (ShapeTextView) findViewById(R.id.stv_card_state);
        sllCameraUpload = (ShapeFrameLayout) findViewById(R.id.sll_camera_upload);
        ivAvatar = (ImageView) findViewById(R.id.iv_avatar);
        sllCameraUpload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCanEdit && avatarClickListener != null) {
                    avatarClickListener.onClick(v);
                }
            }
        });
        setCanEdit(canEdit);
    }

    private OnFocusChangeListener buildFocusChangeListener(ShapeEditText shapeEditText) {
        return new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    shapeEditText.setStrokeWidth((int) getResources().getDimension(com.stkj.common.R.dimen.dp_0_5));
                } else {
                    shapeEditText.setStrokeWidth(0);
                }
            }
        };
    }

    public void setAvatarClickListener(OnClickListener avatarClickListener) {
        this.avatarClickListener = avatarClickListener;
    }

    public void setCanEdit(boolean canEdit) {
        mCanEdit = canEdit;
        if (mCanEdit) {
            int redColor = Color.parseColor("#fa5151");
            SpanUtils.with(tvUserName)
                    .append("姓名")
                    .append("*")
                    .setForegroundColor(redColor)
                    .append(":").create();
            SpanUtils.with(tvIdCard)
                    .append("身份证号")
                    .append("*")
                    .setForegroundColor(redColor)
                    .append(":").create();
            SpanUtils.with(tvParentName)
                    .append("家长姓名")
                    .append("*")
                    .setForegroundColor(redColor)
                    .append(":").create();
            SpanUtils.with(tvParentPhone)
                    .append("家长手机号")
                    .append("*")
                    .setForegroundColor(redColor)
                    .append(":").create();
            setUserName.setOnFocusChangeListener(buildFocusChangeListener(setUserName));
            setIdCard.setOnFocusChangeListener(buildFocusChangeListener(setIdCard));
            setParentName.setOnFocusChangeListener(buildFocusChangeListener(setParentName));
            setParentPhone.setOnFocusChangeListener(buildFocusChangeListener(setParentPhone));
            int editColor = Color.WHITE;
            setUserName.setSolidColor(editColor);
            setIdCard.setSolidColor(editColor);
            setParentName.setSolidColor(editColor);
            setParentPhone.setSolidColor(editColor);
            int textColor = getResources().getColor(com.stkj.common.R.color.color_333333);
            setUserName.setTextColor(textColor);
            setIdCard.setTextColor(textColor);
            setParentName.setTextColor(textColor);
            setParentPhone.setTextColor(textColor);
            setUserName.setEnabled(true);
            setIdCard.setEnabled(true);
            setParentName.setEnabled(true);
            setParentPhone.setEnabled(true);
            llAddCameraFace.setVisibility(VISIBLE);
            llAccount.setVisibility(GONE);
        } else {
            tvUserName.setText("姓名:");
            tvIdCard.setText("身份证号:");
            tvParentName.setText("家长姓名:");
            tvParentPhone.setText("家长手机号:");
            setUserName.setOnFocusChangeListener(null);
            setIdCard.setOnFocusChangeListener(null);
            setParentName.setOnFocusChangeListener(null);
            setParentPhone.setOnFocusChangeListener(null);
            int noEditColor = getResources().getColor(R.color.color_E0E7F1);
            setUserName.setSolidColor(noEditColor);
            setIdCard.setSolidColor(noEditColor);
            setParentName.setSolidColor(noEditColor);
            setParentPhone.setSolidColor(noEditColor);
            int textColor = getResources().getColor(R.color.color_666666);
            setUserName.setTextColor(textColor);
            setIdCard.setTextColor(textColor);
            setParentName.setTextColor(textColor);
            setParentPhone.setTextColor(textColor);
            setUserName.setEnabled(false);
            setIdCard.setEnabled(false);
            setParentName.setEnabled(false);
            setParentPhone.setEnabled(false);
            llAddCameraFace.setVisibility(GONE);
            llAccount.setVisibility(VISIBLE);
        }
    }

    public String getUserName() {
        return setUserName.getText().toString();
    }

    public void setUserName(String userName) {
        setUserName.setText(userName);
    }

    public String getIDCard() {
        return setIdCard.getText().toString();
    }

    public void setIDCard(String IDCard) {
        setIdCard.setText(IDCard);
    }

    public String getParentName() {
        return setParentName.getText().toString();
    }

    public void setParentName(String parentName) {
        setParentName.setText(parentName);
    }

    public String getParentPhone() {
        return setParentPhone.getText().toString();
    }

    public void setParentPhone(String parentPhone) {
        setParentPhone.setText(parentPhone);
    }

    public void setAccount(String account) {
        stvAccount.setText(account);
    }

    public void setCardState(String cardState) {
        llCardState.setVisibility(VISIBLE);
        stvCardState.setVisibility(VISIBLE);
        stvCardState.setText(cardState);
    }

    public void setUserAvatar(String avatarUrl) {
        mAvatarUrl = avatarUrl;
        GlideApp.with(this).load(avatarUrl)
                .transform(new RoundedCorners(getResources().getDimensionPixelSize(com.stkj.common.R.dimen.dp_4))).placeholder(R.mipmap.icon_no_person_mini).into(ivAvatar);
    }

    public String getAvatarUrl() {
        return mAvatarUrl == null ? "" : mAvatarUrl;
    }

    public void setCardUserInfo(UserCardInfo userInfo) {
        setUserName(userInfo.getName());
        setIDCard(userInfo.getIdCardNumber());
        setParentName(userInfo.getEmergencyContact());
        setParentPhone(userInfo.getEmergencyPhone());
        setAccount(userInfo.getCustomerNo());
        setUserAvatar(userInfo.getFaceImg());
    }

    public void clearEditFocus() {
        setUserName.clearFocus();
        setIdCard.clearFocus();
        setParentName.clearFocus();
        setParentPhone.clearFocus();
        KeyBoardUtils.hideSoftKeyboard(getContext(), setParentPhone);
    }
}
