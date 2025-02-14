package com.stkj.common.web.fragment;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.stkj.common.utils.ClipboardUtils;

import com.stkj.common.R;
import com.stkj.common.picker.FilePickerHelper;
import com.stkj.common.picker.SimpleFilePickerDialog;
import com.stkj.common.router.RouterManager;
import com.stkj.common.ui.dialog.AppAlertDialog;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.web.callback.WebViewHolder;
import com.stkj.common.web.callback.WebViewListener;
import com.stkj.common.web.widget.WebTitleLayout;

public class CommonWebFragment extends BaseRecyclerFragment implements WebViewListener {

    public static final String EXTRA_WEB_URL = "web_url";
    public static final String EXTRA_WEB_X5 = "web_view_x5";
    public static final String EXTRA_HIDE_TITLE_LAYOUT = "hide_title_layout";

    private static final String TAG_FRAGMENT_CONTENT = "tag_content_fragment";

    private Fragment contentFragment;
    private @Nullable
    WebViewHolder webViewHolder;
    private String mWebUrl;
    private boolean mHideTitleLayout;

    private WebTitleLayout wtlTitle;
    private OnWebListener onWebListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mWebUrl = arguments.getString(EXTRA_WEB_URL);
            mHideTitleLayout = arguments.getBoolean(EXTRA_HIDE_TITLE_LAYOUT, false);
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_common_web;
    }

    @Override
    protected void initViews(View rootView) {
        wtlTitle = (WebTitleLayout) rootView.findViewById(R.id.wtl_title);
        wtlTitle.setOnTitleClickListener(new WebTitleLayout.OnTitleClickListener() {
            @Override
            public void onClick(int type, View view) {
                switch (type) {
                    case WebTitleLayout.TYPE_BACK:
                        boolean goBack = false;
                        if (webViewHolder != null) {
                            goBack = webViewHolder.goBack();
                        }
                        if (!goBack && onWebListener != null) {
                            onWebListener.onWebClose();
                        }
                        break;
                    case WebTitleLayout.TYPE_CLOSE:
                        if (onWebListener != null) {
                            onWebListener.onWebClose();
                        }
                        break;
                    case WebTitleLayout.TYPE_REFRESH:
                        wtlTitle.setRefreshClickable(false);
                        if (webViewHolder != null) {
                            webViewHolder.reload();
                        }
                        break;
                }
            }

            @Override
            public void onLongClickTitle(View view) {
                showCurrentPageInfo();
            }
        });
        if (mHideTitleLayout) {
            wtlTitle.setVisibility(View.GONE);
        } else {
            wtlTitle.setVisibility(View.VISIBLE);
        }
        wtlTitle.setRefreshClickable(false);
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        if (isFirstOnResume) {
            if (contentFragment == null) {
                BaseWebFragment baseWebFragment = BaseWebFragment.newInstance(mWebUrl, true);
                baseWebFragment.setWebViewListener(this);
                contentFragment = baseWebFragment;
                webViewHolder = baseWebFragment;
                Log.d("CommonWebFragment", "add system WebView");
            }
            try {
                FragmentManager fragmentManager = getChildFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fl_content, contentFragment, TAG_FRAGMENT_CONTENT).commitNow();
            } catch (Exception e) {
                Log.d("CommonWebFragment", "add web error");
            }
        }
    }

    @Override
    protected void initData(boolean isFirstInit, @Nullable Bundle savedInstanceState) {
        FragmentManager fragmentManager = getChildFragmentManager();
        if (savedInstanceState != null) {
            contentFragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_CONTENT);
        }
    }

    //--------------WebViewListener start-----------

    @Override
    public void onReceiveTitle(String title) {
        wtlTitle.setTitleText(title);
    }

    @Override
    public boolean onHandleOverrideUrl(String requestUrl) {
        //三方路由协议跳转
        if (!requestUrl.startsWith("http://") && !requestUrl.startsWith("https://")) {
            RouterManager.startThirdPartySchemaIntent(requestUrl);
            return true;
        }
        return false;
    }

    @Override
    public void doUpdateVisitedHistory(String url, boolean isReload) {
        wtlTitle.setRefreshClickable(true);
        if (webViewHolder != null) {
            wtlTitle.setShowClose(webViewHolder.canGoBack());
        }
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {

    }

    @Override
    public void onPageFinished(String url) {
        wtlTitle.setRefreshClickable(true);
    }

    @Override
    public boolean onShowFileChooser(String fileType, boolean isCaptureEnabled) {
        FilePickerHelper.OnPickFileListener pickFileListener = new FilePickerHelper.OnPickFileListener() {
            @Override
            public void onPickFile(@Nullable Uri[] uri, int pickType) {
                if (webViewHolder != null) {
                    webViewHolder.setFileChooserResult(uri);
                }
            }

            @Override
            public void onPickCancel(int pickType) {
                if (webViewHolder != null) {
                    webViewHolder.setFileChooserResult(null);
                }
            }
        };
        if (TextUtils.isEmpty(fileType)) {
            SimpleFilePickerDialog.build()
                    .setShowAllPickOperate(true)
                    .setAllowPickMultiFile(true)
                    .setPickFileListener(pickFileListener)
                    .show(mActivity);
        } else {
            if (fileType.startsWith("image/")) {
                if (isCaptureEnabled) {
                    SimpleFilePickerDialog.build()
                            .setAutoStartPickType(FilePickerHelper.PICK_TYPE_IMAGE_CAPTURE)
                            .setPickFileListener(pickFileListener)
                            .show(mActivity);
                } else {
                    SimpleFilePickerDialog.build()
                            .setAutoStartPickType(FilePickerHelper.PICK_TYPE_GALLERY)
                            .setPickFileListener(pickFileListener)
                            .show(mActivity);
                }
            } else if (fileType.startsWith("video/")) {
                if (isCaptureEnabled) {
                    SimpleFilePickerDialog.build()
                            .setAutoStartPickType(FilePickerHelper.PICK_TYPE_VIDEO_CAPTURE)
                            .setPickFileListener(pickFileListener)
                            .show(mActivity);
                } else {
                    SimpleFilePickerDialog.build()
                            .setShowVideoCapture(true)
                            .setShowFilePick(true)
                            .setPickFileListener(pickFileListener)
                            .show(mActivity);
                }
            } else {
                SimpleFilePickerDialog.build()
                        .setShowAllPickOperate(true)
                        .setPickFileListener(pickFileListener)
                        .show(mActivity);
            }
        }
        return true;
    }

    //--------------WebViewListener end-----------

    public static CommonWebFragment newInstance(String webUrl, boolean isX5WebView) {
        return newInstance(webUrl, isX5WebView, false);
    }

    public static CommonWebFragment newInstance(String webUrl, boolean isX5WebView, boolean hideTitleLayout) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_WEB_URL, webUrl);
        bundle.putBoolean(EXTRA_WEB_X5, isX5WebView);
        bundle.putBoolean(EXTRA_HIDE_TITLE_LAYOUT, hideTitleLayout);
        CommonWebFragment commonWebFragment = new CommonWebFragment();
        commonWebFragment.setArguments(bundle);
        return commonWebFragment;
    }

    private void showCurrentPageInfo() {
        //测试长按标题栏 获取当前web页信息
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("页面链接: ")
                .append(webViewHolder == null ? "null" : webViewHolder.getCurrentPageLink())
                .append("\n")
                .append("内核信息: ")
                .append(webViewHolder == null ? "null" : webViewHolder.getWebViewInfo())
                .append("\n页面类型: 普通类型");
        String content = contentBuilder.toString();
        AppAlertDialog.build()
                .setTitleText("页面信息")
                .setContentText(content)
                .setConfirmText("确定")
                .setCancelText("复制")
                .setCancelClickListener(new AppAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(AppAlertDialog appAlertDialog) {
                        ClipboardUtils.copyClipboardInfo(content);
                        AppToast.toastMsg("信息已复制!");
                    }
                }).show(mActivity);
    }

    public void setOnWebListener(OnWebListener onWebListener) {
        this.onWebListener = onWebListener;
    }

    public interface OnWebListener {
        void onWebClose();
    }

    public boolean goBack() {
        if (webViewHolder != null) {
            return webViewHolder.goBack();
        }
        return false;
    }

    public WebViewHolder getWebViewHolder() {
        return webViewHolder;
    }
}
