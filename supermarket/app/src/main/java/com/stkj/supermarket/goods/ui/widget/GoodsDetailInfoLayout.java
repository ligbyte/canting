package com.stkj.supermarket.goods.ui.widget;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stkj.common.core.ActivityHolderFactory;
import com.stkj.common.core.AppManager;
import com.stkj.common.storage.StorageHelper;
import com.stkj.common.ui.activity.BaseActivity;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.linelayout.LineLinearLayout;
import com.stkj.common.ui.widget.shapelayout.ShapeEditText;
import com.stkj.common.ui.widget.shapelayout.ShapeFrameLayout;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.ui.widget.shapeselectlayout.ShapeSelectTextView;
import com.stkj.common.utils.SpanUtils;
import com.stkj.supermarket.R;
import com.stkj.supermarket.base.model.CommonExpandItem;
import com.stkj.supermarket.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.supermarket.base.ui.dialog.CommonInputDialogFragment;
import com.stkj.supermarket.base.ui.widget.CommonExpandListPopWindow;
import com.stkj.supermarket.base.upload.UploadFileHelper;
import com.stkj.supermarket.base.utils.CommonDialogUtils;
import com.stkj.supermarket.goods.callback.OnAddGoodsCateListener;
import com.stkj.supermarket.goods.callback.OnAddGoodsSpecListener;
import com.stkj.supermarket.goods.callback.OnCapturePicListener;
import com.stkj.supermarket.goods.callback.OnDelGoodsCateListener;
import com.stkj.supermarket.goods.callback.OnDelGoodsSpecListener;
import com.stkj.supermarket.goods.callback.OnGetGoodsCateListListener;
import com.stkj.supermarket.goods.callback.OnGetGoodsSpecListListener;
import com.stkj.supermarket.goods.data.GoodsConstants;
import com.stkj.supermarket.goods.helper.GoodsCateSpecHelper;
import com.stkj.supermarket.goods.model.GoodsCate;
import com.stkj.supermarket.goods.model.GoodsSaleListInfo;
import com.stkj.supermarket.goods.model.GoodsSpec;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import top.zibin.luban.Luban;

/**
 * 商品信息布局
 */
public class GoodsDetailInfoLayout extends FrameLayout {

    //商品添加模式
    public static final int LAYOUT_TYPE_ADD = 0;
    //商品详情模式
    public static final int LAYOUT_TYPE_DETAIL_INFO = 1;
    //商品详情可编辑模式
    public static final int LAYOUT_TYPE_DETAIL_EDIT = 2;
    private int layoutType = LAYOUT_TYPE_ADD;
    private int goodsType = GoodsConstants.TYPE_GOODS_STANDARD;
    //商品规格
    private GoodsSpec mGoodsSpec = new GoodsSpec();
    //商品分类
    private GoodsCate mGoodsCate = new GoodsCate();
    private LinearLayout llGoodsType;
    private ShapeSelectTextView stvStandardGoods;
    private ShapeSelectTextView stvWeighGoods;
    private TextView tvGoodsType;
    private ShapeEditText setGoodsName;
    private ShapeEditText setGoodsQrcode;
    private ShapeEditText setGoodsPrice;
    private UploadPicItemLayout uploadItemPic1;
    private UploadPicItemLayout uploadItemPic2;
    private ShapeEditText setGoodsInventory;
    private ShapeEditText setGoodsWholesalePrice;
    private ShapeTextView stvGoodsSpec;
    private ImageView ivGoodsSpecArrow;
    private ShapeTextView stvGoodsProductDate;
    private ImageView ivGoodsProductDateArrow;
    private ShapeEditText setGoodsComments;
    private FrameLayout flGoodsSpec;
    private FrameLayout flGoodsProductDate;
    private ShapeEditText setGoodsExpireDate;
    private ShapeFrameLayout flGoodsExpireDate;
    private TextView stvExpireTag;
    private ImageView ivGoodsExpireDateArrow;
    private LineLinearLayout llGoodsInventory;
    private LineLinearLayout llGoodsWholesalePrice;
    private LineLinearLayout llGoodsProductDate;
    private LineLinearLayout llGoodsExpireDate;
    private FrameLayout flGoodsCate;
    private ShapeTextView stvGoodsCate;
    private ImageView ivGoodsCateArrow;
    private LineLinearLayout llGoodsMinInventory;
    private ShapeEditText setGoodsMinInventory;
    private LineLinearLayout llGoodsLossRate;
    private ShapeEditText setGoodsLossRate;

    public GoodsDetailInfoLayout(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public GoodsDetailInfoLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public GoodsDetailInfoLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, @Nullable AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.include_goods_detail_info, this);
        int color = context.getResources().getColor(R.color.color_FF3C30);
        ShapeTextView stvGoodsTypeTitle = (ShapeTextView) findViewById(R.id.stv_goods_type_title);
        SpanUtils.with(stvGoodsTypeTitle)
                .append("*")
                .setForegroundColor(color)
                .append("商品类型").create();
        ShapeTextView stvGoodsCateTitle = (ShapeTextView) findViewById(R.id.stv_goods_cate_title);
        SpanUtils.with(stvGoodsCateTitle)
                .append("*")
                .setForegroundColor(color)
                .append("商品分类").create();
        ShapeTextView stvGoodsNameTitle = (ShapeTextView) findViewById(R.id.stv_goods_name_title);
        SpanUtils.with(stvGoodsNameTitle)
                .append("*")
                .setForegroundColor(color)
                .append("商品名称").create();
        ShapeTextView stvGoodsQrcodeTitle = (ShapeTextView) findViewById(R.id.stv_goods_qrcode_title);
        SpanUtils.with(stvGoodsQrcodeTitle)
                .append("*")
                .setForegroundColor(color)
                .append("条形码号").create();
        ShapeTextView stvGoodsPriceTitle = (ShapeTextView) findViewById(R.id.stv_goods_price_title);
        SpanUtils.with(stvGoodsPriceTitle)
                .append("*")
                .setForegroundColor(color)
                .append("零售价").create();
        ShapeTextView stvGoodsSpecTitle = (ShapeTextView) findViewById(R.id.stv_goods_spec_title);
        SpanUtils.with(stvGoodsSpecTitle)
                .append("*")
                .setForegroundColor(color)
                .append("销售规格").create();
        flGoodsCate = (FrameLayout) findViewById(R.id.fl_goods_cate);
        stvGoodsCate = (ShapeTextView) findViewById(R.id.stv_goods_cate);
        ivGoodsCateArrow = (ImageView) findViewById(R.id.iv_goods_cate_arrow);
        stvGoodsCate.setOnClickListener(goodsCateClick);
        ivGoodsCateArrow.setOnClickListener(goodsCateClick);
        llGoodsMinInventory = (LineLinearLayout) findViewById(R.id.ll_goods_min_inventory);
        setGoodsMinInventory = (ShapeEditText) findViewById(R.id.set_goods_min_inventory);
        llGoodsLossRate = (LineLinearLayout) findViewById(R.id.ll_goods_loss_rate);
        setGoodsLossRate = (ShapeEditText) findViewById(R.id.set_goods_loss_rate);
        llGoodsType = (LinearLayout) findViewById(R.id.ll_goods_type);
        stvStandardGoods = (ShapeSelectTextView) findViewById(R.id.stv_standard_goods);
        stvStandardGoods.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (goodsType == GoodsConstants.TYPE_GOODS_WEIGHT) {
                    selectGoodsStandardType();
                    //刷新规格与分类
                    resetGoodsCateSpec();
                }
            }
        });
        stvWeighGoods = (ShapeSelectTextView) findViewById(R.id.stv_weigh_goods);
        stvWeighGoods.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (goodsType == GoodsConstants.TYPE_GOODS_STANDARD) {
                    selectGoodsWeightType();
                    //刷新规格与分类
                    resetGoodsCateSpec();
                    //称重规格默认千克
                    stvGoodsSpec.setText(GoodsConstants.SPEC_WEIGHT_GOODS);
                }
            }
        });
        tvGoodsType = (TextView) findViewById(R.id.tv_goods_type);
        setGoodsName = (ShapeEditText) findViewById(R.id.set_goods_name);
        setGoodsQrcode = (ShapeEditText) findViewById(R.id.set_goods_qrcode);
        setGoodsPrice = (ShapeEditText) findViewById(R.id.set_goods_price);
        uploadItemPic1 = (UploadPicItemLayout) findViewById(R.id.upload_item_pic1);
        uploadItemPic1.setCapturePicListener(buildOnCaptureClickListener(1));
        uploadItemPic2 = (UploadPicItemLayout) findViewById(R.id.upload_item_pic2);
        uploadItemPic2.setCapturePicListener(buildOnCaptureClickListener(2));
        stvGoodsSpec = (ShapeTextView) findViewById(R.id.stv_goods_spec);
        ivGoodsSpecArrow = (ImageView) findViewById(R.id.iv_goods_spec_arrow);
        llGoodsProductDate = (LineLinearLayout) findViewById(R.id.ll_goods_product_date);
        stvGoodsProductDate = (ShapeTextView) findViewById(R.id.stv_goods_product_date);
        ivGoodsProductDateArrow = (ImageView) findViewById(R.id.iv_goods_product_date_arrow);
        llGoodsInventory = (LineLinearLayout) findViewById(R.id.ll_goods_inventory);
        setGoodsInventory = (ShapeEditText) findViewById(R.id.set_goods_inventory);
        llGoodsWholesalePrice = (LineLinearLayout) findViewById(R.id.ll_goods_wholesale_price);
        setGoodsWholesalePrice = (ShapeEditText) findViewById(R.id.set_goods_wholesale_price);
        setGoodsComments = (ShapeEditText) findViewById(R.id.set_goods_comments);
        flGoodsSpec = (FrameLayout) findViewById(R.id.fl_goods_spec);

        stvGoodsSpec.setOnClickListener(goodsSpecClick);
        ivGoodsSpecArrow.setOnClickListener(goodsSpecClick);
        flGoodsProductDate = (FrameLayout) findViewById(R.id.fl_goods_product_date);

        llGoodsExpireDate = (LineLinearLayout) findViewById(R.id.ll_goods_expire_date);
        setGoodsExpireDate = (ShapeEditText) findViewById(R.id.set_goods_expire_date);
        flGoodsExpireDate = (ShapeFrameLayout) findViewById(R.id.fl_goods_expire_date);
        stvExpireTag = (TextView) findViewById(R.id.stv_expire_tag);
        ivGoodsExpireDateArrow = (ImageView) findViewById(R.id.iv_goods_expire_date_arrow);
        stvExpireTag.setOnClickListener(expireDateClick);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GoodsDetailInfoLayout);
            layoutType = a.getInt(R.styleable.GoodsDetailInfoLayout_layout_type, LAYOUT_TYPE_ADD);
            setGoodsLayoutType(layoutType);
        }
    }

    /**
     * 刷新当前规格和分类
     */
    private void resetGoodsCateSpec() {
        mGoodsCate.reset();
        stvGoodsCate.setText("");
        mGoodsSpec.reset();
        stvGoodsSpec.setText("");
    }

    /**
     * 选中称重商品
     */
    private void selectGoodsWeightType() {
        goodsType = GoodsConstants.TYPE_GOODS_WEIGHT;
        stvWeighGoods.setShapeSelect(true);
        stvStandardGoods.setShapeSelect(false);
        setGoodsInventory.setInputType(EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
    }

    /**
     * 选中标准商品
     */
    private void selectGoodsStandardType() {
        goodsType = GoodsConstants.TYPE_GOODS_STANDARD;
        stvWeighGoods.setShapeSelect(false);
        stvStandardGoods.setShapeSelect(true);
        setGoodsInventory.setInputType(EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_VARIATION_NORMAL);
    }

    /**
     * 商品分类弹窗
     */
    private CommonExpandListPopWindow goodsCateExpandListPopWindow;

    private OnClickListener goodsCateClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            ivGoodsCateArrow.setSelected(true);
            if (goodsCateExpandListPopWindow == null) {
                goodsCateExpandListPopWindow = new CommonExpandListPopWindow(getContext());
                goodsCateExpandListPopWindow.setWidth(flGoodsCate.getWidth());
                goodsCateExpandListPopWindow.setHeight(LayoutParams.WRAP_CONTENT);
                goodsCateExpandListPopWindow.setCustomItemClickListener(new CommonExpandListPopWindow.OnCustomItemClickListener() {
                    @Override
                    public void onClickCustom() {
                        CommonInputDialogFragment.build()
                                .setTitle("自定义商品分类")
                                .setOnInputListener(new CommonInputDialogFragment.OnInputListener() {
                                    @Override
                                    public void onInputEnd(String input) {
                                        //添加商品分类
                                        if (!TextUtils.isEmpty(input)) {
                                            addGoodsCate(input);
                                        }
                                    }
                                }).show(getContext());
                    }
                });
                goodsCateExpandListPopWindow.setItemClickListener(new CommonExpandListPopWindow.OnExpandItemClickListener() {
                    @Override
                    public void onClickItem(CommonExpandItem commonExpandItem) {
                        ivGoodsCateArrow.setSelected(false);
                        stvGoodsCate.setText(commonExpandItem.getName());
                        mGoodsCate.setId(commonExpandItem.getType());
                        mGoodsCate.setName(commonExpandItem.getName());
                    }

                    @Override
                    public void onDelItem(CommonExpandItem commonExpandItem) {
                        CommonAlertDialogFragment.build()
                                .setAlertTitleTxt("提示")
                                .setAlertContentTxt("确认删除 '" + commonExpandItem.getName() + "' 分类吗?")
                                .setLeftNavTxt("删除")
                                .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                                    @Override
                                    public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                                        delGoodsCate(commonExpandItem.getType(), commonExpandItem.getName());
                                    }
                                }).setRightNavTxt("取消")
                                .show(getContext());
                    }
                });
            }
            goodsCateExpandListPopWindow.setNeedCustomItem(true);
            goodsCateExpandListPopWindow.setAllowDelItem(true);
            goodsCateExpandListPopWindow.setExpandItemList(getGoodsCateExpandList(goodsType));
            goodsCateExpandListPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    ivGoodsCateArrow.setSelected(false);
                }
            });
            goodsCateExpandListPopWindow.showAsDropDown(flGoodsCate);
        }
    };

    private CommonExpandListPopWindow goodsSpecExpandListPopWindow;

    private OnClickListener goodsSpecClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            ivGoodsSpecArrow.setSelected(true);
            if (goodsSpecExpandListPopWindow == null) {
                goodsSpecExpandListPopWindow = new CommonExpandListPopWindow(getContext());
                goodsSpecExpandListPopWindow.setWidth(flGoodsSpec.getWidth());
                goodsSpecExpandListPopWindow.setHeight(LayoutParams.WRAP_CONTENT);
                goodsSpecExpandListPopWindow.setCustomItemClickListener(new CommonExpandListPopWindow.OnCustomItemClickListener() {
                    @Override
                    public void onClickCustom() {
                        CommonInputDialogFragment.build()
                                .setTitle("自定义商品规格")
                                .setOnInputListener(new CommonInputDialogFragment.OnInputListener() {
                                    @Override
                                    public void onInputEnd(String input) {
                                        //添加商品规格
                                        if (!TextUtils.isEmpty(input)) {
                                            addGoodsSpec(input);
                                        }
                                    }
                                }).show(getContext());
                    }
                });
                goodsSpecExpandListPopWindow.setItemClickListener(new CommonExpandListPopWindow.OnExpandItemClickListener() {
                    @Override
                    public void onClickItem(CommonExpandItem commonExpandItem) {
                        ivGoodsSpecArrow.setSelected(false);
                        stvGoodsSpec.setText(commonExpandItem.getName());
                        mGoodsSpec.setId(commonExpandItem.getType());
                        mGoodsSpec.setName(commonExpandItem.getName());
                    }

                    @Override
                    public void onDelItem(CommonExpandItem commonExpandItem) {
                        CommonAlertDialogFragment.build()
                                .setAlertTitleTxt("提示")
                                .setAlertContentTxt("确认删除 '" + commonExpandItem.getName() + "' 规格吗?")
                                .setLeftNavTxt("删除")
                                .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                                    @Override
                                    public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                                        delGoodsSpec(commonExpandItem.getType(), commonExpandItem.getName());
                                    }
                                }).setRightNavTxt("取消")
                                .show(getContext());
                    }
                });
            }
            goodsSpecExpandListPopWindow.setNeedCustomItem(goodsType == GoodsConstants.TYPE_GOODS_STANDARD);
            goodsSpecExpandListPopWindow.setAllowDelItem(goodsType == GoodsConstants.TYPE_GOODS_STANDARD);
            goodsSpecExpandListPopWindow.setExpandItemList(getGoodsSpecExpandList(goodsType));
            goodsSpecExpandListPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    ivGoodsSpecArrow.setSelected(false);
                }
            });
            goodsSpecExpandListPopWindow.showAsDropDown(flGoodsSpec);
        }
    };

    /**
     * 添加会删除成功后重新获取商品分类
     */
    private void requestGoodsCateList() {
        Context context = getContext();
        if (context instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) context;
            GoodsCateSpecHelper goodsCateSpecHelper = baseActivity.getWeakRefHolder(GoodsCateSpecHelper.class);
            goodsCateSpecHelper.requestCateList(goodsType, new OnGetGoodsCateListListener() {
                @Override
                public void onGetCateListSuccess(int goodsType, List<GoodsCate> goodsCateList) {
                    baseActivity.hideLoadingDialog();
                }

                @Override
                public void onGetCateListError(int goodsType, String msg) {
                    baseActivity.hideLoadingDialog();
                }
            });
        }
    }

    /**
     * 添加会删除成功后重新获取商品规格
     */
    private void requestGoodsSpecList() {
        Context context = getContext();
        if (context instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) context;
            GoodsCateSpecHelper goodsCateSpecHelper = baseActivity.getWeakRefHolder(GoodsCateSpecHelper.class);
            goodsCateSpecHelper.requestSpecList(goodsType, new OnGetGoodsSpecListListener() {
                @Override
                public void onGetSpecListSuccess(int goodsType, List<GoodsSpec> goodsSpecList) {
                    baseActivity.hideLoadingDialog();
                }

                @Override
                public void onGetSpecListError(int goodsType, String msg) {
                    baseActivity.hideLoadingDialog();
                }
            });
        }
    }

    /**
     * 删除商品规格
     */
    private void delGoodsCate(String id, String goodsCate) {
        Context context = getContext();
        if (context instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) context;
            GoodsCateSpecHelper goodsCateSpecHelper = baseActivity.getWeakRefHolder(GoodsCateSpecHelper.class);
            baseActivity.showLoadingDialog();
            goodsCateSpecHelper.delGoodsCate(id, goodsType, goodsCate, new OnDelGoodsCateListener() {
                @Override
                public void onDelCateSuccess(String cateName) {
                    if (TextUtils.equals(id, mGoodsCate.getId())) {
                        mGoodsCate.reset();
                        stvGoodsCate.setText("");
                    }
                    AppToast.toastMsg("删除成功");
                    requestGoodsCateList();
                }

                @Override
                public void onDelCateError(String cateName, String msg) {
                    CommonDialogUtils.showTipsDialog(baseActivity, "删除分类失败: " + msg);
                    baseActivity.hideLoadingDialog();
                }
            });
        }
    }

    /**
     * 删除商品规格
     */
    private void delGoodsSpec(String id, String goodsSpec) {
        Context context = getContext();
        if (context instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) context;
            GoodsCateSpecHelper goodsCateSpecHelper = baseActivity.getWeakRefHolder(GoodsCateSpecHelper.class);
            baseActivity.showLoadingDialog();
            goodsCateSpecHelper.delGoodsSpec(id, goodsType, goodsSpec, new OnDelGoodsSpecListener() {
                @Override
                public void onDelSpecSuccess(String specName) {
                    if (TextUtils.equals(id, mGoodsSpec.getId())) {
                        mGoodsSpec.reset();
                        stvGoodsSpec.setText("");
                    }
                    AppToast.toastMsg("删除成功");
                    requestGoodsSpecList();
                }

                @Override
                public void onDelSpecError(String specName, String msg) {
                    CommonDialogUtils.showTipsDialog(baseActivity, "删除规格失败: " + msg);
                    baseActivity.hideLoadingDialog();
                }
            });
        }
    }

    /**
     * 添加商品规格
     */
    private void addGoodsSpec(String goodsSpec) {
        Context context = getContext();
        if (context instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) context;
            GoodsCateSpecHelper goodsCateSpecHelper = baseActivity.getWeakRefHolder(GoodsCateSpecHelper.class);
            baseActivity.showLoadingDialog();
            goodsCateSpecHelper.addGoodsSpec(goodsSpec, goodsType, new OnAddGoodsSpecListener() {
                @Override
                public void onAddSpecSuccess(GoodsSpec spec) {
                    mGoodsSpec = spec;
                    stvGoodsSpec.setText(spec.getName());
                    AppToast.toastMsg("添加成功");
                    requestGoodsSpecList();
                }

                @Override
                public void onAddSpecError(String specName, String msg) {
                    CommonDialogUtils.showTipsDialog(baseActivity, "添加规格失败: " + msg);
                    baseActivity.hideLoadingDialog();
                }
            });
        }
    }

    /**
     * 添加商品分类
     */
    private void addGoodsCate(String goodsCate) {
        Context context = getContext();
        if (context instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) context;
            GoodsCateSpecHelper goodsCateSpecHelper = baseActivity.getWeakRefHolder(GoodsCateSpecHelper.class);
            baseActivity.showLoadingDialog();
            goodsCateSpecHelper.addGoodsCate(goodsCate, goodsType, new OnAddGoodsCateListener() {

                @Override
                public void onAddCateSuccess(GoodsCate cate) {
                    mGoodsCate = cate;
                    stvGoodsCate.setText(cate.getName());
                    AppToast.toastMsg("添加成功");
                    requestGoodsCateList();
                }

                @Override
                public void onAddCateError(String cateName, String msg) {
                    CommonDialogUtils.showTipsDialog(baseActivity, "添加分类失败: " + msg);
                    baseActivity.hideLoadingDialog();
                }
            });
        }
    }

    /**
     * 获取商品规格
     */
    private List<CommonExpandItem> getGoodsSpecExpandList(int goodsType) {
        GoodsCateSpecHelper goodsCateSpecHelper = ActivityHolderFactory.get(GoodsCateSpecHelper.class, getContext());
        if (goodsCateSpecHelper != null) {
            List<GoodsSpec> goodsSpecByType = goodsCateSpecHelper.getGoodsSpecByType(goodsType);
            if (goodsSpecByType != null && !goodsSpecByType.isEmpty()) {
                List<CommonExpandItem> expandItemList = new ArrayList<>();
                for (int i = 0; i < goodsSpecByType.size(); i++) {
                    GoodsSpec spec = goodsSpecByType.get(i);
                    CommonExpandItem expandItem = new CommonExpandItem(spec.getId(), spec.getName());
                    expandItemList.add(expandItem);
                }
                return expandItemList;
            }
        }
        return null;
    }

    /**
     * 获取商品分类
     */
    private List<CommonExpandItem> getGoodsCateExpandList(int goodsType) {
        GoodsCateSpecHelper goodsCateSpecHelper = ActivityHolderFactory.get(GoodsCateSpecHelper.class, getContext());
        if (goodsCateSpecHelper != null) {
            List<GoodsCate> goodsCateByType = goodsCateSpecHelper.getGoodsCateByType(goodsType);
            if (goodsCateByType != null && !goodsCateByType.isEmpty()) {
                List<CommonExpandItem> expandItemList = new ArrayList<>();
                for (int i = 0; i < goodsCateByType.size(); i++) {
                    GoodsCate cate = goodsCateByType.get(i);
                    CommonExpandItem expandItem = new CommonExpandItem(cate.getId(), cate.getName());
                    expandItemList.add(expandItem);
                }
                return expandItemList;
            }
        }
        return null;
    }

    private OnClickListener expireDateClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            ivGoodsExpireDateArrow.setSelected(true);
            CommonExpandListPopWindow commonExpandListPopWindow = new CommonExpandListPopWindow(getContext());
            commonExpandListPopWindow.setWidth(flGoodsExpireDate.getWidth());
            commonExpandListPopWindow.setHeight(LayoutParams.WRAP_CONTENT);
            commonExpandListPopWindow.setItemClickListener(new CommonExpandListPopWindow.OnExpandItemClickListener() {
                @Override
                public void onClickItem(CommonExpandItem commonExpandItem) {
                    ivGoodsExpireDateArrow.setSelected(false);
                    stvExpireTag.setText(commonExpandItem.getName());
                }
            });
            commonExpandListPopWindow.setExpandItemList(GoodsConstants.getGoodsExpireSpecList());
            commonExpandListPopWindow.showAsDropDown(flGoodsExpireDate);
            commonExpandListPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    ivGoodsExpireDateArrow.setSelected(false);
                }
            });
        }
    };

    private OnCapturePicListener buildOnCaptureClickListener(int tag) {
        return new OnCapturePicListener() {
            @Override
            public void onCapturePic(Uri fileUri) {
                try {
                    List<File> fileList = Luban.with(AppManager.INSTANCE.getApplication())
                            .load(fileUri)
                            .setTargetDir(StorageHelper.getExternalShareDirPath())
                            .ignoreBy(250)
                            .get();
                    if (fileList != null && !fileList.isEmpty()) {
                        File cacheFile = fileList.get(0);
                        Context context = getContext();
                        UploadFileHelper uploadFileHelper = new UploadFileHelper((Activity) context);
                        uploadFileHelper.setUploadFileListener(new UploadFileHelper.UploadFileListener() {
                            @Override
                            public void onStart() {
                                if (context instanceof BaseActivity) {
                                    ((BaseActivity) context).showLoadingDialog();
                                }
                            }

                            @Override
                            public void onSuccess(String fileUrl) {
                                if (context instanceof BaseActivity) {
                                    ((BaseActivity) context).hideLoadingDialog();
                                }
                                if (tag == 1) {
                                    uploadItemPic1.loadGoodsPic(fileUrl);
                                } else if (tag == 2) {
                                    uploadItemPic2.loadGoodsPic(fileUrl);
                                }
                            }

                            @Override
                            public void onError(String msg) {
                                if (context instanceof BaseActivity) {
                                    ((BaseActivity) context).hideLoadingDialog();
                                }
                                CommonAlertDialogFragment.build()
                                        .setAlertTitleTxt("提示")
                                        .setAlertContentTxt("上传失败!原因: " + msg)
                                        .show(context);
                            }
                        });
                        uploadFileHelper.uploadFile(cacheFile);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public void setGoodsLayoutType(int goodsLayoutType) {
        Context context = getContext();
        layoutType = goodsLayoutType;
        if (layoutType == LAYOUT_TYPE_DETAIL_INFO) {
            //商品详情模式
            //商品规格
            tvGoodsType.setVisibility(VISIBLE);
            llGoodsType.setVisibility(GONE);
            //商品分类
            //销售规格
            stvGoodsCate.setStrokeColor(0);
            stvGoodsCate.setHint("");
            stvGoodsCate.setPadding(0, 0, 0, 0);
            ivGoodsCateArrow.setVisibility(GONE);
            stvGoodsCate.setOnClickListener(null);
            ivGoodsCateArrow.setOnClickListener(null);
            //商品名称
            setGoodsName.setEnabled(false);
            setGoodsName.setHint("");
            setGoodsName.setPadding(0, 0, 0, 0);
            setGoodsName.setStrokeColor(0);
            //条形码号
            setGoodsQrcode.setEnabled(false);
            setGoodsQrcode.setHint("");
            setGoodsQrcode.setPadding(0, 0, 0, 0);
            setGoodsQrcode.setStrokeColor(0);
            //零售价
            setGoodsPrice.setEnabled(false);
            setGoodsPrice.setHint("");
            setGoodsPrice.setPadding(0, 0, 0, 0);
            setGoodsPrice.setStrokeColor(0);
            //商品图片
            uploadItemPic1.setEditMode(false);
            uploadItemPic2.setEditMode(false);
            //销售规格
            stvGoodsSpec.setStrokeColor(0);
            stvGoodsSpec.setHint("");
            stvGoodsSpec.setPadding(0, 0, 0, 0);
            ivGoodsSpecArrow.setVisibility(GONE);
            stvGoodsSpec.setOnClickListener(null);
            ivGoodsSpecArrow.setOnClickListener(null);
            //初始库存
            llGoodsInventory.setVisibility(GONE);
            //进货价格
            llGoodsWholesalePrice.setVisibility(GONE);
            //生产日期
            llGoodsProductDate.setVisibility(GONE);
            //保质期
            llGoodsExpireDate.setVisibility(GONE);
            //最低库存
            setGoodsMinInventory.setEnabled(false);
            setGoodsMinInventory.setHint("");
            setGoodsMinInventory.setPadding(0, 0, 0, 0);
            setGoodsMinInventory.setStrokeColor(0);
            //商品损耗率
            setGoodsLossRate.setEnabled(false);
            setGoodsLossRate.setHint("");
            setGoodsLossRate.setPadding(0, 0, 0, 0);
            setGoodsLossRate.setStrokeColor(0);
            //备注
            setGoodsComments.setEnabled(false);
            setGoodsComments.setHint("");
            setGoodsComments.setPadding(0, 0, 0, 0);
            setGoodsComments.setStrokeColor(0);
        } else if (layoutType == LAYOUT_TYPE_DETAIL_EDIT) {
            //商品详情可编辑模式
            //公用圆角属性
            int strokeColor = context.getResources().getColor(R.color.color_E8EAED);
            int padding = (int) context.getResources().getDimension(com.stkj.common.R.dimen.dp_6);
            int halfPadding = (int) context.getResources().getDimension(com.stkj.common.R.dimen.dp_3);
            //商品规格
            tvGoodsType.setVisibility(GONE);
            llGoodsType.setVisibility(VISIBLE);
            //商品分类
            stvGoodsCate.setStrokeColor(strokeColor);
            stvGoodsCate.setHint("请选择");
            stvGoodsCate.setPadding(padding, 0, padding, 0);
            ivGoodsCateArrow.setVisibility(VISIBLE);
            stvGoodsCate.setOnClickListener(goodsCateClick);
            ivGoodsCateArrow.setOnClickListener(goodsCateClick);
            //商品名称
            setGoodsName.setEnabled(true);
            setGoodsName.setHint("请输入");
            setGoodsName.setPadding(padding, 0, padding, 0);
            setGoodsName.setStrokeColor(strokeColor);
            //条形码号
            setGoodsQrcode.setEnabled(true);
            setGoodsQrcode.setHint("请输入条形码");
            setGoodsQrcode.setPadding(padding, 0, padding, 0);
            setGoodsQrcode.setStrokeColor(strokeColor);
            //零售价
            setGoodsPrice.setEnabled(true);
            setGoodsPrice.setHint("请输入");
            setGoodsPrice.setPadding(padding, 0, padding, 0);
            setGoodsPrice.setStrokeColor(strokeColor);
            //商品图片
            uploadItemPic1.setEditMode(true);
            uploadItemPic2.setEditMode(true);
            //销售规格
            stvGoodsSpec.setStrokeColor(strokeColor);
            stvGoodsSpec.setHint("请选择");
            stvGoodsSpec.setPadding(padding, 0, padding, 0);
            ivGoodsSpecArrow.setVisibility(VISIBLE);
            stvGoodsSpec.setOnClickListener(goodsSpecClick);
            ivGoodsSpecArrow.setOnClickListener(goodsSpecClick);
            //初始库存
            llGoodsInventory.setVisibility(GONE);
            //进货价格
            llGoodsWholesalePrice.setVisibility(GONE);
            //生产日期
            llGoodsProductDate.setVisibility(GONE);
            //保质期
            llGoodsExpireDate.setVisibility(GONE);
            //最低库存
            setGoodsMinInventory.setEnabled(true);
            setGoodsMinInventory.setHint("请输入");
            setGoodsMinInventory.setPadding(padding, 0, padding, 0);
            setGoodsMinInventory.setStrokeColor(strokeColor);
            //商品损耗率
            setGoodsLossRate.setEnabled(true);
            setGoodsLossRate.setHint("请输入");
            setGoodsLossRate.setPadding(padding, 0, padding, 0);
            setGoodsLossRate.setStrokeColor(strokeColor);
            //备注
            setGoodsComments.setEnabled(true);
            setGoodsComments.setHint("请输入");
            setGoodsComments.setPadding(padding, halfPadding, padding, halfPadding);
            setGoodsComments.setStrokeColor(strokeColor);
        } else {
            //添加商品模式
            //公用圆角属性
            int strokeColor = context.getResources().getColor(R.color.color_E8EAED);
            int padding = (int) context.getResources().getDimension(com.stkj.common.R.dimen.dp_6);
            int halfPadding = (int) context.getResources().getDimension(com.stkj.common.R.dimen.dp_3);
            //商品规格
            tvGoodsType.setVisibility(GONE);
            llGoodsType.setVisibility(VISIBLE);
            //商品分类
            stvGoodsCate.setStrokeColor(strokeColor);
            stvGoodsCate.setHint("请选择");
            stvGoodsCate.setPadding(padding, 0, padding, 0);
            ivGoodsCateArrow.setVisibility(VISIBLE);
            stvGoodsCate.setOnClickListener(goodsCateClick);
            ivGoodsCateArrow.setOnClickListener(goodsCateClick);
            //商品名称
            setGoodsName.setEnabled(true);
            setGoodsName.setHint("请输入");
            setGoodsName.setPadding(padding, 0, padding, 0);
            setGoodsName.setStrokeColor(strokeColor);
            //条形码号
            setGoodsQrcode.setEnabled(true);
            setGoodsQrcode.setHint("请输入条形码");
            setGoodsQrcode.setPadding(padding, 0, padding, 0);
            setGoodsQrcode.setStrokeColor(strokeColor);
            //零售价
            setGoodsPrice.setEnabled(true);
            setGoodsPrice.setHint("请输入");
            setGoodsPrice.setPadding(padding, 0, padding, 0);
            setGoodsPrice.setStrokeColor(strokeColor);
            //商品图片
            uploadItemPic1.setEditMode(true);
            uploadItemPic2.setEditMode(true);
            //销售规格
            stvGoodsSpec.setStrokeColor(strokeColor);
            stvGoodsSpec.setHint("请选择");
            stvGoodsSpec.setPadding(padding, 0, padding, 0);
            ivGoodsSpecArrow.setVisibility(VISIBLE);
            stvGoodsSpec.setOnClickListener(goodsSpecClick);
            ivGoodsSpecArrow.setOnClickListener(goodsSpecClick);
            //初始库存
            llGoodsInventory.setVisibility(VISIBLE);
            setGoodsInventory.setEnabled(true);
            setGoodsInventory.setHint("请输入");
            setGoodsInventory.setPadding(padding, 0, padding, 0);
            setGoodsInventory.setStrokeColor(strokeColor);
            //进货价格
            llGoodsWholesalePrice.setVisibility(VISIBLE);
            setGoodsWholesalePrice.setEnabled(true);
            setGoodsWholesalePrice.setHint("请输入");
            setGoodsWholesalePrice.setPadding(padding, 0, padding, 0);
            setGoodsWholesalePrice.setStrokeColor(strokeColor);
            //生产日期
            llGoodsProductDate.setVisibility(VISIBLE);
            stvGoodsProductDate.setStrokeColor(strokeColor);
            stvGoodsProductDate.setHint("请选择");
            stvGoodsProductDate.setPadding(padding, 0, padding, 0);
            ivGoodsProductDateArrow.setVisibility(VISIBLE);
            flGoodsProductDate.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), DatePickerDialog.THEME_HOLO_LIGHT);
                    datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            stvGoodsProductDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                        }
                    });
                    datePickerDialog.show();
                }
            });
            //保质期
            llGoodsExpireDate.setVisibility(VISIBLE);
            //最低库存
            setGoodsMinInventory.setEnabled(true);
            setGoodsMinInventory.setHint("请输入");
            setGoodsMinInventory.setPadding(padding, 0, padding, 0);
            setGoodsMinInventory.setStrokeColor(strokeColor);
            //商品损耗率
            setGoodsLossRate.setEnabled(true);
            setGoodsLossRate.setHint("请输入");
            setGoodsLossRate.setPadding(padding, 0, padding, 0);
            setGoodsLossRate.setStrokeColor(strokeColor);
            //备注
            setGoodsComments.setEnabled(true);
            setGoodsComments.setHint("请输入");
            setGoodsComments.setPadding(padding, halfPadding, padding, halfPadding);
            setGoodsComments.setStrokeColor(strokeColor);
        }
    }

    public String getGoodsName() {
        return setGoodsName.getText().toString().trim();
    }

    // 商品类型（0 标准商品 1 称重商品)
    public int getGoodsType() {
        return goodsType;
    }

    //条码
    public String getGoodsCode() {
        return setGoodsQrcode.getText().toString().trim();
    }

    //零售价
    public String getGoodsUnitPrice() {
        return setGoodsPrice.getText().toString().trim();
    }

    //初始库存
    public String getGoodsInitStock() {
        return setGoodsInventory.getText().toString();
    }

    //生产日期
    public String getGoodsProductDate() {
        return stvGoodsProductDate.getText().toString();
    }

    //进货价格
    public String getGoodsWholesalePrice() {
        return setGoodsWholesalePrice.getText().toString();
    }

    //有效天数
    public String getExpireDays() {
        String expireDateStr = setGoodsExpireDate.getText().toString().toString();
        int totalDay = 0;
        if (!TextUtils.isEmpty(expireDateStr)) {
            try {
                float aFloat = Float.parseFloat(expireDateStr);
                String expireTag = stvExpireTag.getText().toString();
                if (TextUtils.equals("年", expireTag)) {
                    aFloat = aFloat * 365;
                } else if (TextUtils.equals("月", expireTag)) {
                    aFloat = aFloat * 30;
                }
                totalDay = (int) aFloat;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            return "";
        }
        return String.valueOf(totalDay);
    }

    //备注
    public String getGoodsNote() {
        return setGoodsComments.getText().toString().trim();
    }

    //销售规格
    public GoodsSpec getGoodsSpec() {
        return mGoodsSpec;
    }

    //商品分类
    public GoodsCate getGoodsCate() {
        return mGoodsCate;
    }

    //商品最低库存
    public String getGoodsMinInventory() {
        return setGoodsMinInventory.getText().toString();
    }

    //商品损耗率
    public String getGoodsLossRate() {
        return setGoodsLossRate.getText().toString();
    }

    //上传商品图片
    public String getUploadPicUrl() {
        String uploadPicUrl1 = uploadItemPic1.getUploadPicUrl();
        String uploadPicUrl2 = uploadItemPic2.getUploadPicUrl();
        StringBuilder builder = new StringBuilder();
        if (!TextUtils.isEmpty(uploadPicUrl1)) {
            builder.append(uploadPicUrl1).append(",");
        }
        if (!TextUtils.isEmpty(uploadPicUrl2)) {
            builder.append(uploadPicUrl2);
        }
        return builder.toString();
    }

    public void loadGoodsBaseInfo(@NonNull GoodsSaleListInfo goodsBaseInfo) {
        setGoodsLayoutType(LAYOUT_TYPE_DETAIL_INFO);
        goodsType = goodsBaseInfo.getGoodsType();
        if (goodsType == GoodsConstants.TYPE_GOODS_STANDARD) {
            selectGoodsStandardType();
        } else {
            selectGoodsWeightType();
        }
        //商品规格
        tvGoodsType.setText(GoodsConstants.getGoodsTypeFromType(goodsBaseInfo.getGoodsType()));
        mGoodsCate.reset();
        mGoodsCate.setId(goodsBaseInfo.getGoodsCategory());
        mGoodsCate.setName(goodsBaseInfo.getGoodsCategoryStr());
        //商品分类
        stvGoodsCate.setText(goodsBaseInfo.getGoodsCategoryStr());
        //商品名称
        setGoodsName.setText(goodsBaseInfo.getGoodsName());
        //条形码号
        setGoodsQrcode.setText(goodsBaseInfo.getGoodsCode());
        //零售价
        setGoodsPrice.setText(goodsBaseInfo.getGoodsUnitPrice());
        //商品图片
        String goodsImg = goodsBaseInfo.getGoodsImg();
        String[] pics = goodsImg.split(",");
        if (pics.length > 0) {
            uploadItemPic1.loadGoodsPic(pics[0]);
            if (pics.length > 1) {
                uploadItemPic2.loadGoodsPic(pics[1]);
            }
        }
        //销售规格
        mGoodsSpec.setId(goodsBaseInfo.getGoodsSpec());
        mGoodsSpec.setName(goodsBaseInfo.getGoodsSpecStr());
        stvGoodsSpec.setText(goodsBaseInfo.getGoodsSpecStr());
        //初始库存
        llGoodsInventory.setVisibility(GONE);
        //进货价格
        llGoodsWholesalePrice.setVisibility(GONE);
        //生产日期
        llGoodsProductDate.setVisibility(GONE);
        //保质期
        llGoodsExpireDate.setVisibility(GONE);
        //最低库存
        setGoodsMinInventory.setText(goodsBaseInfo.getGoodsMinStock());
        //损耗率
        setGoodsLossRate.setText(goodsBaseInfo.getGoodsLossRate());
        //备注
        setGoodsComments.setText(goodsBaseInfo.getGoodsNote());
    }

    /**
     * 重置商品可编辑和清空数据
     */
    public void resetAllLayoutAndData() {
        goodsType = GoodsConstants.TYPE_GOODS_STANDARD;
        setGoodsLayoutType(LAYOUT_TYPE_ADD);
        //添加商品模式 清空输入框数据
        //商品规格
        selectGoodsStandardType();
        //商品分类
        mGoodsCate.reset();
        stvGoodsCate.setText("");
        //商品名称
        setGoodsName.setText("");
        //条形码号
        setGoodsQrcode.setText("");
        //零售价
        setGoodsPrice.setText("");
        //销售规格
        mGoodsSpec.reset();
        stvGoodsSpec.setText("");
        //初始库存
        setGoodsInventory.setText("");
        //进货价格
        setGoodsWholesalePrice.setText("");
        //生产日期
        stvGoodsProductDate.setText("");
        //保质期
        setGoodsExpireDate.setText("");
        //备注
        setGoodsComments.setText("");
        //商品图片
        uploadItemPic1.resetUploadPic();
        uploadItemPic2.resetUploadPic();
//        setGoodsName.requestFocus();
//        KeyBoardUtils.showSoftKeyboard(getContext(), setGoodsName);
    }

    public void setGoodsName(String goodsName) {
        setGoodsName.setText(goodsName);
    }

    public void setGoodsCode(String goodsCode) {
        setGoodsQrcode.setText(goodsCode);
    }

    public void setGoodsPic(String goodsPic) {
        if (TextUtils.isEmpty(goodsPic)) {
            uploadItemPic1.resetUploadPic();
        } else {
            uploadItemPic1.loadGoodsPic(goodsPic);
        }
    }

    public void setGoodsComment(String comment) {
        setGoodsComments.setText(comment);
    }

}
