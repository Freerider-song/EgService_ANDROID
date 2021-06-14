package com.enernet.eg.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.enernet.eg.R;

public class ActivitySubscribedMain extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribed_main);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                Intent itt = new Intent(this, ActivityLogin.class);
                startActivity(itt);

                finish();
                break;

        }
    }
}
