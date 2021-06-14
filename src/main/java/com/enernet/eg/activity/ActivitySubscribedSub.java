package com.enernet.eg.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.enernet.eg.CaApplication;
import com.enernet.eg.R;

public class ActivitySubscribedSub extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribed_sub);

        TextView tvInfo=findViewById(R.id.tv_info);
        String strApt=CaApplication.m_Info.m_strSiteName + " " + CaApplication.m_Info.m_strAptDongName + "동 " + CaApplication.m_Info.m_strAptHoName+"호";
        String strInfo="회원가입이 완료되었으며 ["+strApt+"] 대표자님께 승인 요청을 보냈습니다. 대표자님께서 승인하신 후 로그인하시면 서비스를 이용하실 수 있습니다.";

        tvInfo.setText(strInfo);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                Intent itt = new Intent(this, ActivityLogin.class);
                startActivity(itt);

                finish();
                break;

            case R.id.btn_exit: {
                // kill app
                finishAffinity();
                System.runFinalization();
                System.exit(0);
            }
            break;
        }
    }
}
