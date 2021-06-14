package com.enernet.eg.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.enernet.eg.CaApplication;
import com.enernet.eg.CaEngine;
import com.enernet.eg.CaResult;
import com.enernet.eg.IaResultHandler;
import com.enernet.eg.R;
import com.enernet.eg.StringUtil;
import com.enernet.eg.model.CaNotice;

public class ActivityNotice extends BaseActivity implements IaResultHandler {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        prepareDrawer();

        Intent it=getIntent();
        int position=it.getIntExtra("position", -1);
        if (position>=0) {
            CaNotice notice= CaApplication.m_Info.m_alNotice.get(position);

            TextView tvTitle=findViewById(R.id.tv_title);
            tvTitle.setText(notice.m_strTitle);

            TextView tvWriter=findViewById(R.id.tv_writer);
            ImageView ivNoticeType=findViewById(R.id.iv_noti_type);

            if (notice.m_nWriterType==1) {
                tvWriter.setText("관리사무실");
                ivNoticeType.setImageDrawable(getDrawable(R.drawable.notice_site));
            }
            else {
                tvWriter.setText("EG 서비스 운영자");
                ivNoticeType.setImageDrawable(getDrawable(R.drawable.notice_eg));
            }

            TextView tvTimeCreated=findViewById(R.id.tv_time_created);
            tvTimeCreated.setText(notice.getTimeCreated());

           // TextView tvContent=findViewById(R.id.tv_content);
           // tvContent.setText(notice.m_strContent);

            WebView wvContent=findViewById(R.id.wv_content);
            wvContent.loadData(notice.m_strContent, "text/html; charset=utf-8", "UTF-8");

        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back: {
                //setAlarmReadStateToDb();
                finish();
            }
            break;

            case R.id.btn_alarm: {
                Intent it = new Intent(this, ActivityAlarm.class);
                startActivity(it);
            }
            break;

            case R.id.btn_menu: {
                m_Drawer.openDrawer();
            }
            break;
        }
    }

    @Override
    public void onBackPressed() {
        if (m_Drawer.isDrawerOpen()) {
            m_Drawer.closeDrawer();
        }
        else {
            finish();
        }
    }

    @Override
    public void onResult(CaResult Result) {

        if (Result.object == null) {
            Toast.makeText(getApplicationContext(), StringUtil.getString(this, R.string.tv_label_network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        switch (Result.m_nCallback) {
            case CaEngine.CB_GET_USAGE_CURRENT_OF_ONE_SITE: {
                Log.i("Notice", "Result of GetUsageCurrentOfOneSite received...");


            }
            break;

            default: {
                Log.i("Notice", "Unknown type result received : " + Result.m_nCallback);
            }
            break;

        } // end of switch
    }

}
