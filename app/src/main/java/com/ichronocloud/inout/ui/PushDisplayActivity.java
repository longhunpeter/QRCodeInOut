package com.ichronocloud.inout.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ichronocloud.inout.Config;
import com.ichronocloud.inout.R;
import com.ichronocloud.inout.server.MyReceiver;

/**
 * Created by lxl on 2014/6/16.
 */
public class PushDisplayActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = getClass().getCanonicalName();
    private Button mBackBtn;
    private TextView mDisplayContent;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_layout);
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
            case R.id.push_back_btn:
                finish();
                break;
            default:
                break;
        }
    }

    private void initData() {
        pushMessage = getIntent().getStringExtra(Config.PUSH_EXTRAS);

    }

    private void initView() {
        mBackBtn = (Button) findViewById(R.id.push_back_btn);
        mBackBtn.setOnClickListener(this);
        mDisplayContent = (TextView) findViewById(R.id.display_text);
        if (!TextUtils.isEmpty(pushMessage)) {
            mDisplayContent.setText(pushMessage);
        }
    }




}
