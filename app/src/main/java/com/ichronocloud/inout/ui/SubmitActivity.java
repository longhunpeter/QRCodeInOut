package com.ichronocloud.inout.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ichronocloud.inout.Config;
import com.ichronocloud.inout.R;
import com.ichronocloud.inout.data.GetOrderData;
import com.ichronocloud.inout.data.InOutStorageData;
import com.ichronocloud.inout.data.ListData;
import com.ichronocloud.inout.network.QRCodeRestClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by lxl on 2014/5/6.
 */
public class SubmitActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = getClass().getCanonicalName();
    private Button mBackBtn;
    private Button mSubmitBtn;
    private TextView mConfigText;
    private TextView mCodeText;
    private TextView mSubmitStatus;
    private RelativeLayout mSelectOrder;
    private ProgressBar mBar;
    private TextView mAgainLoad;

    private ListView mListView;

    private String mInOutName;
    private String mOutName;
    private String mReturnName;
    private String mBadnessName;

    private String scanQrCode;

    private int mInOutCode = -1;
    private int mOutCode = -1;
    private int mReturnCode = -1;
    private int mBadnessCode = -1;
    private String mOrderNo;

    private List<ListData> mOrderList;

    private SharedPreferences mSharedPreferences;

    private enum SubmitStatus {
        IN, OUT, RETURN, RETURN_BADNESS
    }

    private SubmitStatus status;

    private final int CHRONOCLOUD_CODE = 1;

    //TODO
    public static final int IN_CODE_INDEX = 1;
    public static final int OUT_CODE_INDEX = 2;
    public static final int RETURN_CODE_INDEX = 3;
    public static final int BADNESS_CODE_INDEX = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.submit_layout);
        initData();
        initView();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.ok_btn:

                switch (status) {
                    case IN:
                        mSubmitStatus.setText(getResources().getString(R.string.submit_toast));
//                        Log.i(TAG, "in mInoutCode is " + mInOutCode);
                        submitInOut(mInOutCode + "", CHRONOCLOUD_CODE + "", "");
                        break;
                    case OUT:
//                        Log.i(TAG, "OUT mInoutCode is " + mInOutCode + " mOutCode is " + mOutCode);
                        if (TextUtils.isEmpty(mOrderNo)) {
                            makeToast(getResources().getString(R.string.orderNo_isEmpty));
                            return;
                        }
                        Log.i(TAG, "mOrderNo is : " + mOrderNo);
                        mSubmitStatus.setText(getResources().getString(R.string.submit_toast));
                        submitInOut(mInOutCode + "", mOutCode + "", mOrderNo);
                        break;
                    case RETURN:
                        mSubmitStatus.setText(getResources().getString(R.string.submit_toast));
//                        Log.i(TAG, "RETURN mReturnCode is ---------------------- " + mReturnCode);
                        submitReturn(mReturnCode + "", "");
                        break;
                    case RETURN_BADNESS:
                        mSubmitStatus.setText(getResources().getString(R.string.submit_toast));
//                        Log.i(TAG, "RETURN_BADNESS mReturnCode is ---------------------------------- " + mReturnCode + " mBadnessCode is " + mBadnessCode);
                        submitReturn(mReturnCode + "", mBadnessCode + "");
                        break;
                    default:
                        break;

                }

                break;
            case R.id.again_load:
                //TODO
                mBar.setVisibility(View.VISIBLE);
                mAgainLoad.setVisibility(View.GONE);
                getOutOrder();
            default:
                break;

        }
    }

    private void initData() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(SubmitActivity.this);
        scanQrCode = getIntent().getStringExtra(Config.GET_RESULT);
        getConfigs();
    }

    private void initView() {

        mSelectOrder = (RelativeLayout) findViewById(R.id.select_order);
        mBar = (ProgressBar) findViewById(R.id.get_order_bar);
        mBackBtn = (Button) findViewById(R.id.back_btn);
        mSubmitBtn = (Button) findViewById(R.id.ok_btn);
        mConfigText = (TextView) findViewById(R.id.setting_info);
        mCodeText = (TextView) findViewById(R.id.code_text);
        mAgainLoad = (TextView) findViewById(R.id.again_load);
        mListView = (ListView) findViewById(R.id.order_list);
        mSubmitStatus = (TextView) findViewById(R.id.submit_status);
        mAgainLoad.setOnClickListener(this);
        mListView.setOnItemClickListener(itemClickListener);
        mBackBtn.setOnClickListener(this);
        mSubmitBtn.setOnClickListener(this);
        if (scanQrCode == null || scanQrCode.equals(""))
            mCodeText.setText(getResources().getString(R.string.qrCode_null));
        mCodeText.setText(scanQrCode);
        configSelect();
    }

    private void getConfigs() {
        mInOutName = mSharedPreferences.getString(Config.EXTRAS_INOUT_NAME, "");
        mInOutCode = mSharedPreferences.getInt(Config.EXTRAS_INOUT_CODE, -1);
        mOutName = mSharedPreferences.getString(Config.EXTRAS_OUT_NAME, "");
        mOutCode = mSharedPreferences.getInt(Config.EXTRAS_OUT_CODE, -1);
        mReturnName = mSharedPreferences.getString(Config.EXTRAS_RETURN_NAME, "");
        mReturnCode = mSharedPreferences.getInt(Config.EXTRAS_RETURN_CODE, -1);
        mBadnessName = mSharedPreferences.getString(Config.EXTRAS_BADNESS_NAME, "");
        mBadnessCode = mSharedPreferences.getInt(Config.EXTRAS_BADNESS_CODE, -1);

    }

    /**
     * 设置判定
     */
    private void configSelect() {
        if (mInOutCode == IN_CODE_INDEX) {
            status = SubmitStatus.IN;
            mConfigText.setText(mInOutName);
        } else if (mInOutCode == OUT_CODE_INDEX) {
            status = SubmitStatus.OUT;
            mConfigText.setText(mInOutName + "  " + mOutName);
            mSelectOrder.setVisibility(View.VISIBLE);
            mSubmitStatus.setText(getResources().getString(R.string.getOrder_success));
            getOutOrder();
//            myAdapter.notifyDataSetChanged();

        } else if (mInOutCode == RETURN_CODE_INDEX) {

            if (mReturnCode == BADNESS_CODE_INDEX) {
                status = SubmitStatus.RETURN_BADNESS;
                mConfigText.setText(mInOutName + "  " + mReturnName + "  " + mBadnessName);
            } else {
                status = SubmitStatus.RETURN;
                mConfigText.setText(mInOutName + "  " + mReturnName);
            }

        }
    }

    /**
     * 获取订单号
     */
    private void getOutOrder() {
        final String invokeTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        QRCodeRestClient.newInstance(this, Config.URL_TWO).executeGetOrder("", invokeTime, new Callback<GetOrderData>() {
            @Override
            public void success(GetOrderData getOrderData, Response response) {
                if (getOrderData != null && getOrderData.result.equals(Config.SUBMIT_SUCCESS)) {
                    if (getOrderData.dataList != null) {
                        mOrderList = getOrderData.dataList;
                        mSubmitStatus.setText(getResources().getString(R.string.getOrder_success));
                        MyAdapter myAdapter = new MyAdapter(mOrderList);
                        mListView.setAdapter(myAdapter);
                    } else {
                        mSubmitStatus.setText(getResources().getString(R.string.getOrder_fail));
                        mAgainLoad.setVisibility(View.VISIBLE);
                    }

                    mBar.setVisibility(View.GONE);
                    return;
                } else {
                    String errorMsg = getOrderData.errorMsg;
                    if (errorMsg == null) {
                        mSubmitStatus.setText(getResources().getString(R.string.submit_fail));
                        return;
                    }
                    mSubmitStatus.setText(errorMsg);
                    mBar.setVisibility(View.GONE);
                    mAgainLoad.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                mBar.setVisibility(View.GONE);
                mAgainLoad.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 提交订单
     *
     * @param flag
     * @param stock
     */
    private void submitInOut(String flag, String stock, String orderNo) {
        final String actionTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        QRCodeRestClient.newInstance(this, Config.URL_ONE).executeScanInStorage(scanQrCode, flag, stock, orderNo, actionTime, new Callback<InOutStorageData>() {
            @Override
            public void success(InOutStorageData inOutStorageData, Response response) {
                if (inOutStorageData != null && inOutStorageData.result.equals(Config.SUBMIT_SUCCESS)) {
                    mSubmitStatus.setText(getResources().getString(R.string.submit_success));
                    return;
                } else {
                    String errorMsg = inOutStorageData.errorMsg;
                    if (errorMsg == null) {
                        mSubmitStatus.setText(getResources().getString(R.string.submit_fail));
                        return;
                    }
                    mSubmitStatus.setText(errorMsg);

                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                mSubmitStatus.setText(getResources().getString(R.string.submit_fail));
            }
        });
    }

    /**
     * 退货提交
     *
     * @param returnCode
     * @param badnessCode
     */
    private void submitReturn(String returnCode, String badnessCode) {
        final String actionTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        QRCodeRestClient.newInstance(this, Config.URL_ONE).executeReturn(scanQrCode, actionTime, returnCode, badnessCode, new Callback<InOutStorageData>() {
            @Override
            public void success(InOutStorageData inOutStorageData, Response response) {
                if (inOutStorageData != null && inOutStorageData.result.equals(Config.SUBMIT_SUCCESS)) {
                    mSubmitStatus.setText(getResources().getString(R.string.submit_success));
                    return;
                } else {
                    String errorMsg = inOutStorageData.errorMsg;
                    if (errorMsg == null) {
                        mSubmitStatus.setText(getResources().getString(R.string.submit_fail));
                        return;
                    }
                    mSubmitStatus.setText(errorMsg);

                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                mSubmitStatus.setText(getResources().getString(R.string.submit_fail));
            }
        });
    }

    /**
     * listView响应
     */
    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mOrderNo = mOrderList.get(position).orderNo;
            if (mOrderNo == null) {
                Log.e(TAG, "select orderNo is error!!!");
                return;
            }
            mConfigText.setText(mInOutName + "  " + mOutName + "  " + mOrderNo);
        }
    };

    /**
     * 适配器
     */
    class MyAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<ListData> mList;

        public MyAdapter(List<ListData> list) {
            this.mList = list;
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mList != null ? mList.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item, null);
                viewHolder = new ViewHolder();
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.mOrderName = (TextView) convertView.findViewById(R.id.order_name);
            viewHolder.mOrderNumber = (TextView) convertView.findViewById(R.id.order_number);
            viewHolder.mOrderCount = (TextView) convertView.findViewById(R.id.order_count);
            viewHolder.mOrderAmount = (TextView) convertView.findViewById(R.id.order_amount);
            viewHolder.mOrderDealer = (TextView) convertView.findViewById(R.id.order_dealer);
            String name = mList.get(position).name;
            String number = mList.get(position).orderNo;
            String count = mList.get(position).quantity;
            String amount = mList.get(position).amout;
            String dealer = mList.get(position).dealer;
            if (TextUtils.isEmpty(name)) {
                viewHolder.mOrderName.setText("--");
            } else {
                viewHolder.mOrderName.setText(name);
            }
            if (TextUtils.isEmpty(amount)) {
                viewHolder.mOrderAmount.setText("-- ￥");
            } else {
                viewHolder.mOrderAmount.setText(amount + " ￥");
            }

            if (TextUtils.isEmpty(dealer)) {
                viewHolder.mOrderDealer.setText("--");
            } else {
                viewHolder.mOrderDealer.setText(dealer);
            }

            viewHolder.mOrderNumber.setText(number);
            viewHolder.mOrderCount.setText(count + " 件");

            return convertView;
        }


        class ViewHolder {
            private TextView mOrderName;
            private TextView mOrderNumber;
            private TextView mOrderCount;
            private TextView mOrderAmount;
            private TextView mOrderDealer;
        }
    }
}
