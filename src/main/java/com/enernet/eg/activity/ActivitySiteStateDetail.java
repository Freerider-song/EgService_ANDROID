package com.enernet.eg.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.enernet.eg.CaResult;
import com.enernet.eg.IaResultHandler;
import com.enernet.eg.R;

public class ActivitySiteStateDetail extends BaseActivity implements IaResultHandler {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_state_detail);
    }

    @Override
    public void onResult(CaResult mdlResult) {
        
    }
}