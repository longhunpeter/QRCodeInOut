package com.ichronocloud.inout.network;

import android.content.Context;
import android.util.Log;

import com.ichronocloud.inout.Config;
import com.ichronocloud.inout.data.GetOrderData;
import com.ichronocloud.inout.data.InOutStorageData;
import com.ichronocloud.inout.util.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by lxl on 14-4-11.
 */
public class QRCodeRestClient {

    private final String TAG = getClass().getCanonicalName();
    private String url;
    public static QRCodeRestClient mQRCodeRestClient;
    private final RestAdapter mRestAdapter;
    public static Context mContext;

    /**
     * @param context
     */
    public QRCodeRestClient(Context context, int urlIndex) {
        if (urlIndex == Config.URL_ONE) {
            url = Config.BASE_URL;
        } else if (urlIndex == Config.URL_TWO) {
            url = Config.BASE_URL_TWO;
        }
        this.mRestAdapter = new RestAdapter.Builder().
                setEndpoint(url)
                .setClient(new QRCodeClient(context))
                .setLog(new RestAdapter.Log() {
                    @Override
                    public void log(String s) {
                        Log.e(TAG, s);
                    }
                })
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();


    }

    /**
     * 该类初始化
     *
     * @param context
     * @return
     */
    public static QRCodeRestClient newInstance(Context context, int urlIndex) {
        mContext = context;
//        if (mQRCodeRestClient == null) {
//        }
        mQRCodeRestClient = new QRCodeRestClient(context, urlIndex);
        return mQRCodeRestClient;
    }

    /**
     * 出入库
     *
     * @param stock
     * @param dimCode
     * @param flag
     * @param actionTime
     * @param callback
     */
    public void executeScanInStorage(
            String dimCode,
            String flag,
            String stock,
            String orderNo,
            String actionTime,
            Callback<InOutStorageData> callback


    ) {
        final String deviceType = Config.ANDROID_DEVICE_TYPE;
        String appVer = Utils.getIMEI(mContext);
        String deviceCode = Utils.getVersionName(mContext);
        String reqTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis()));
        String sign = Utils.encryption(stock + dimCode + flag + actionTime + Config.CONVENTIOS_STR);
//        Log.i(TAG, "executeScanInStorage default params is : " + deviceType + " " + appVer + " " + deviceCode + " " + reqTime + " sign  " + sign);
        ScanInOutStorageExecutor executor = mRestAdapter.create(ScanInOutStorageExecutor.class);
        executor.execute(deviceType, appVer, deviceCode, reqTime, dimCode, flag, stock, orderNo, actionTime, sign, callback);
    }

    /**
     * 退货接口
     *
     * @param dimCode
     * @param actionTime
     * @param reason
     * @param result
     * @param callback
     */
    public void executeReturn(String dimCode,
                              String actionTime,
                              String reason,
                              String result,

                              Callback<InOutStorageData> callback) {
        final String deviceType = Config.ANDROID_DEVICE_TYPE;
        final String appVer = Utils.getIMEI(mContext);
        final String deviceCode = Utils.getVersionName(mContext);
        final String reqTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis()));
        final String sign = Utils.encryption(dimCode + actionTime + reason + Config.CONVENTIOS_STR);
//        Log.i(TAG, "executeReturn default params is : " + actionTime + " " + reason + "  --------- " + result + " sign " + sign);

        ReturnExecutor executor = mRestAdapter.create(ReturnExecutor.class);
        executor.execute(deviceType, appVer, deviceCode, reqTime, dimCode, actionTime, reason, result, sign, callback);
    }

    /**
     * 获取出库订单列表接口
     *
     * @param orderNo
     * @param callback
     */
    public void executeGetOrder(
            String orderNo,
            String invokeTime,
            Callback<GetOrderData> callback

    ) {
        final String deviceType = Config.ANDROID_DEVICE_TYPE;
        final String appVer = Utils.getIMEI(mContext);
        final String deviceCode = Utils.getVersionName(mContext);
        final String reqTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis()));
        final String sign = Utils.encryption(invokeTime + Config.CONVENTIOS_STR);

        GetOrderExecutor executor = mRestAdapter.create(GetOrderExecutor.class);
        executor.execute(deviceType, appVer, deviceCode, reqTime, orderNo, invokeTime, sign, callback);

    }


    interface ScanInOutStorageExecutor {
        @FormUrlEncoded
        @POST("/bodyscaleStorage.htm")
        void execute(
                @Field("deviceType") String deviceType,
                @Field("appVer") String appVer,
                @Field("deviceCode") String deviceCode,
                @Field("reqTime") String reqTime,

                @Field("dimCode") String dimCode,
                @Field("flag") String flag,
                @Field("stock") String stock,
                @Field("orderNo") String orderNo,
                @Field("actionTime") String actionTime,
                @Field("sign") String sign, Callback<InOutStorageData> callback);

    }

    interface ReturnExecutor {
        @FormUrlEncoded
        @POST("/bodyscaleReturn.htm")
        void execute(
                @Field("deviceType") String deviceType,
                @Field("appVer") String appVer,
                @Field("deviceCode") String deviceCode,
                @Field("reqTime") String reqTime,

                @Field("dimCode") String dimCode,
                @Field("actionTime") String actionTime,
                @Field("reason") String reason,
                @Field("result") String result,
                @Field("sign") String sign,

                Callback<InOutStorageData> callback

        );
    }

    interface GetOrderExecutor {
        @FormUrlEncoded
        @POST("/appGetOrder.htm")
        void execute(
                @Field("deviceType") String deviceType,
                @Field("appVer") String appVer,
                @Field("deviceCode") String deviceCode,
                @Field("reqTime") String reqTime,

                @Field("orderNo") String orderNo,
                @Field("invokeTime") String invokeTime,
                @Field("sign") String sign,
                Callback<GetOrderData> callback
        );
    }


}
