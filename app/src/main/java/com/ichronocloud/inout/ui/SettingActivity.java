package com.ichronocloud.inout.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.SettingInjectorService;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.ichronocloud.inout.Config;
import com.ichronocloud.inout.R;

/**
 * Created by lxl on 2014/5/6.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = getClass().getCanonicalName();
    private Spinner mInOutSpinner;
    private Spinner mOutSpinner;
    private Spinner mReturnSpinner;
    private Spinner mTestBadnessSpinner;
    private Button mNextBtn;

    private ArrayAdapter mInOutAdapter;
    private ArrayAdapter mOutAdapter;
    private ArrayAdapter mReturnAdapter;
    private ArrayAdapter mBadnessAdapter;

    private String[] mInOutNameArray;
    private String[] mOutNameArray;
    private String[] mReturnNameArray;
    private String[] mBadnessNameArray;

    private int[] mInOutCodeArray;
    private int[] mOutCodeArray;
    private int[] mReturnCodeArray;
    private int[] mBadnessCodeArray;

    private String mInOutName;
    private String mOutName;
    private String mReturnName;
    private String mBadnessName;

    private int mInOutCode = 0;
    private int mOutCode = 0;
    private int mReturnCode = 0;
    private int mBadnessCode = 0;

    private boolean isExit = false;

    private SharedPreferences mSharedPreferences;



    public static boolean isForeground = false;

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            isExit = false;
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);
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
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_btn:
                saveConfigs();
                startActivity(new Intent(SettingActivity.this, CaptureActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void initData() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(SettingActivity.this);
        setArrays();

        mInOutAdapter = ArrayAdapter.createFromResource(this, R.array.inOut_name,
                android.R.layout.simple_spinner_item);
        mOutAdapter = ArrayAdapter.createFromResource(this, R.array.out_name,
                android.R.layout.simple_spinner_item);

        mReturnAdapter = ArrayAdapter.createFromResource(this, R.array.return_name,
                android.R.layout.simple_spinner_item);
        mBadnessAdapter = ArrayAdapter.createFromResource(this, R.array.badness_name,
                android.R.layout.simple_spinner_item);

        mInOutAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mOutAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mReturnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBadnessAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    }

    private void initView() {
        mInOutSpinner = (Spinner) findViewById(R.id.spinner_inOut);
        mOutSpinner = (Spinner) findViewById(R.id.spinner_out);
        mReturnSpinner = (Spinner) findViewById(R.id.spinner_return);
        mTestBadnessSpinner = (Spinner) findViewById(R.id.spinner_badness);

        mNextBtn = (Button) findViewById(R.id.next_btn);

        mInOutSpinner.setAdapter(mInOutAdapter);
        mOutSpinner.setAdapter(mOutAdapter);
        mReturnSpinner.setAdapter(mReturnAdapter);
        mTestBadnessSpinner.setAdapter(mBadnessAdapter);

        mNextBtn.setOnClickListener(this);

        mInOutSpinner.setOnItemSelectedListener(InOutSelectedListener);
        mOutSpinner.setOnItemSelectedListener(OutSelectedListener);
        mReturnSpinner.setOnItemSelectedListener(ReturnSelectedListener);
        mTestBadnessSpinner.setOnItemSelectedListener(BadnessSelectedListener);


    }

    /**
     * 连续点击返回按钮退出程序
     */
    public void exit(){
        if (!isExit) {
            isExit = true;
            makeToast(getResources().getString(R.string.main_exit));
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            System.exit(0);
        }
    }

    /**
     * 设置映射相关数组
     */
    private void setArrays() {
        mInOutNameArray = getResources().getStringArray(R.array.inOut_name);
        mOutNameArray = getResources().getStringArray(R.array.out_name);
        mReturnNameArray = getResources().getStringArray(R.array.return_name);
        mBadnessNameArray = getResources().getStringArray(R.array.badness_name);

        mInOutCodeArray = getResources().getIntArray(R.array.inOut_code);
        mOutCodeArray = getResources().getIntArray(R.array.out_code);
        mReturnCodeArray = getResources().getIntArray(R.array.return_code);
        mBadnessCodeArray = getResources().getIntArray(R.array.badness_code);
    }

    /**
     * 保存相关设置
     */
    private void saveConfigs() {
        mSharedPreferences.edit().putString(Config.EXTRAS_INOUT_NAME, mInOutName).commit();
        mSharedPreferences.edit().putInt(Config.EXTRAS_INOUT_CODE, mInOutCode).commit();
        mSharedPreferences.edit().putString(Config.EXTRAS_OUT_NAME, mOutName).commit();
        mSharedPreferences.edit().putInt(Config.EXTRAS_OUT_CODE, mOutCode).commit();
        mSharedPreferences.edit().putString(Config.EXTRAS_RETURN_NAME, mReturnName).commit();
        mSharedPreferences.edit().putInt(Config.EXTRAS_RETURN_CODE, mReturnCode).commit();
        mSharedPreferences.edit().putString(Config.EXTRAS_BADNESS_NAME, mBadnessName).commit();
        mSharedPreferences.edit().putInt(Config.EXTRAS_BADNESS_CODE, mBadnessCode).commit();

    }

    private AdapterView.OnItemSelectedListener InOutSelectedListener =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String InOutSpinner = mInOutAdapter.getItem(position).toString();
                    mInOutCode = mInOutCodeArray[position];
                    mInOutName = InOutSpinner;
                    if (InOutSpinner.equals("出库")) {
                        mOutSpinner.setVisibility(View.VISIBLE);
                        mOutSpinner.setSelection(0);
                        mReturnSpinner.setVisibility(View.GONE);
//                        mOutCode = 0;
                    } else if (InOutSpinner.equals("退货")) {
                        mOutSpinner.setVisibility(View.GONE);
                        mReturnSpinner.setVisibility(View.VISIBLE);
                        mReturnSpinner.setSelection(0);
//                        mReturnCode = 0;
                    } else {
                        mOutSpinner.setVisibility(View.GONE);
                        mReturnSpinner.setVisibility(View.GONE);
                    }


                    mTestBadnessSpinner.setVisibility(View.GONE);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            };

    private AdapterView.OnItemSelectedListener OutSelectedListener =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String outSpinnerName = mOutAdapter.getItem(position).toString();
                    mOutName = outSpinnerName;
                    mOutCode = mOutCodeArray[position];
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            };

    private AdapterView.OnItemSelectedListener ReturnSelectedListener =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String returnSpinnerName = mReturnAdapter.getItem(position).toString();

                    if (returnSpinnerName.equals("测试不良")) {
                        mTestBadnessSpinner.setVisibility(View.VISIBLE);
                        mTestBadnessSpinner.setSelection(0);
                    } else {
                        mTestBadnessSpinner.setVisibility(View.GONE);
                    }
                    mReturnCode = mReturnCodeArray[position];
                    mReturnName = returnSpinnerName;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            };

    private AdapterView.OnItemSelectedListener BadnessSelectedListener =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String badnessSpinnerName = mBadnessAdapter.getItem(position).toString();
                    mBadnessName = badnessSpinnerName;
                    mBadnessCode = mBadnessCodeArray[position];

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            };


}
