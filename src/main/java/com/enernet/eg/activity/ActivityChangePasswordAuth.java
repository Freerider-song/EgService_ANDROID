package com.enernet.eg.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.enernet.eg.CaApplication;
import com.enernet.eg.CaEngine;
import com.enernet.eg.R;

public class ActivityChangePasswordAuth extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_auth);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_change_password_auth: {
                finish();

                CaApplication.m_Info.m_nAuthType= CaEngine.AUTH_TYPE_CHANGE_PASSWORD;

                Intent nextIntent = new Intent(this, ActivityAuth.class);
                startActivity(nextIntent);

            }
            break;

            case R.id.btn_change_password_cancel: {
                finish();
            }
            break;
        }
    }

}
