package com.stkj.supermarket.goods.service;

import com.stkj.supermarket.base.model.BaseResponse;
import com.stkj.supermarket.goods.model.AddGoodsCateParams;
import com.stkj.supermarket.goods.model.AddGoodsSpecParams;
import com.stkj.supermarket.goods.model.DelGoodsCateParams;
import com.stkj.supermarket.goods.model.DelGoodsSpecParams;
import com.stkj.supermarket.goods.model.GoodsBaseInfo;
import com.stkj.supermarket.goods.model.GoodsBatchListResponse;
import com.stkj.supermarket.goods.model.GoodsCate;
import com.stkj.supermarket.goods.model.GoodsEditBaseInfo;
import com.stkj.supermarket.goods.model.GoodsIdBaseListInfo;
import com.stkj.supermarket.goods.model.GoodsSaleListInfo;
import com.stkj.supermarket.goods.model.GoodsSaleListResponse;
import com.stkj.supermarket.goods.model.GoodsSaveStorageInfo;
import com.stkj.supermarket.goods.model.GoodsSpec;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface GoodsService {

    //添加商品
    @POST("/api/webapp/service/spgoodsinfo/add")
    Observable<BaseResponse<String>> addGoods(@Body GoodsBaseInfo requestParams);

    //编辑商品
    @POST("/api/webapp/service/spgoodsinfo/edit")
    Observable<BaseResponse<String>> editGoods(@Body GoodsEditBaseInfo requestParams);

    //获取商品详情
    @GET("/api/webapp/service/spgoodsinfo/detail")
    Observable<BaseResponse<GoodsSaleListInfo>> getGoodsDetail(@Query("id") String requestParams);

    //删除商品
    @POST("/api/webapp/service/spgoodsinfo/delete")
    Observable<BaseResponse<String>> deleteGoods(@Body List<Map<String, String>> requestParams);

    //查询销售商品列表
    @GET("/api/webapp/service/spgoodsinfo/page")
    Observable<BaseResponse<GoodsSaleListResponse>> queryGoodsSaleList(@QueryMap Map<String, Object> requestParams);

    //快速查找商品列表
    @GET("/api/webapp/service/spgoodsinfo/listTree")
    Observable<BaseResponse<List<GoodsIdBaseListInfo>>> searchGoodsList(@Query("searchKey") String requestParams);

    //商品入库
    @POST("/api/webapp/service/spgoodsinrecords/add")
    Observable<BaseResponse<String>> saveGoodsStorage(@Body List<GoodsSaveStorageInfo> requestParams);

    //商品入库记录
    @GET("/api/webapp/service/spgoodsinrecords/page")
    Observable<BaseResponse<GoodsBatchListResponse>> getGoodsBatchList(@QueryMap Map<String, Object> requestParams);

    //查找商品（全量）
    @GET("/api/webapp/service/spgoodsinfo/all")
    Observable<BaseResponse<List<GoodsSaleListInfo>>> getAllGoodsList(@Query("goodsType") String requestParams);

    //查询商品明细通过扫码
    @GET("/api/webapp/service/spgoodsinfo/base")
    Observable<BaseResponse<String>> requestCodeDetail(@Query("goodsCode") String requestParams);

    //请求商品分类
    @GET("/api/webapp/service/spgoodscategory/list")
    Observable<BaseResponse<List<GoodsCate>>> getGoodsCateList(@Query("type") int goodsType);

    @GET("/api/webapp/service/spgoodscategory/list")
    Call<BaseResponse<List<GoodsCate>>> getGoodsCateListSync(@Query("type") int goodsType);

    //请求商品规格
    @GET("/api/webapp/service/spgoodsspec/list")
    Observable<BaseResponse<List<GoodsSpec>>> getGoodsSpecList(@Query("type") int goodsType);

    //请求商品规格
    @GET("/api/webapp/service/spgoodsspec/list")
    Call<BaseResponse<List<GoodsSpec>>> getGoodsSpecListSync(@Query("type") int goodsType);

    //添加商品分类
    @POST("/api/webapp/service/spgoodscategory/add")
    Observable<BaseResponse<String>> addGoodsCate(@Body AddGoodsCateParams addGoodsCateParams);

    //添加商品规格
    @POST("/api/webapp/service/spgoodsspec/add")
    Observable<BaseResponse<String>> addGoodsSpec(@Body AddGoodsSpecParams addGoodsSpecParams);

    //删除商品分类
    @POST("/api/webapp/service/spgoodscategory/delete")
    Observable<BaseResponse<String>> delGoodsCate(@Body List<DelGoodsCateParams> delGoodsCateParams);

    //删除商品规格
    @POST("/api/webapp/service/spgoodsspec/delete")
    Observable<BaseResponse<String>> delGoodsSpec(@Body List<DelGoodsSpecParams> delGoodsSpecParams);

}
