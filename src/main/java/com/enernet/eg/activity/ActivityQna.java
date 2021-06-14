package com.enernet.eg.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.enernet.eg.CaApplication;
import com.enernet.eg.CaEngine;
import com.enernet.eg.CaResult;
import com.enernet.eg.IaResultHandler;
import com.enernet.eg.R;
import com.enernet.eg.StringUtil;
import com.enernet.eg.model.CaQna;

public class ActivityQna extends BaseActivity implements IaResultHandler {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qna);

        prepareDrawer();

        Intent it=getIntent();
        int position=it.getIntExtra("position", -1);
        if (position>=0) {
            CaQna qna= CaApplication.m_Info.m_alQna.get(position);

            TextView tvTimeQuestion=findViewById(R.id.tv_time_question);
            tvTimeQuestion.setText(qna.getTimeQuestion());

            TextView tvQuestion=findViewById(R.id.tv_question);
            tvQuestion.setText(qna.m_strQuestion);

            TextView tvTimeAnswer=findViewById(R.id.tv_time_answer);
            TextView tvAnswer=findViewById(R.id.tv_answer);

            if (qna.isAnswered()) {
                tvTimeAnswer.setText(qna.getTimeAnswer());
                tvAnswer.setText(qna.m_strAnswer);
            }
            else {
                tvTimeAnswer.setVisibility(View.GONE);
                tvAnswer.setText("답변 준비 중입니다.");
            }

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
    public void onResult(CaResult Result) {

        if (Result.object == null) {
            Toast.makeText(getApplicationContext(), StringUtil.getString(this, R.string.tv_label_network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        switch (Result.m_nCallback) {
            case CaEngine.CB_GET_USAGE_CURRENT_OF_ONE_SITE: {
                Log.i("Qna", "Result of GetUsageCurrentOfOneSite received...");


            }
            break;

            default: {
                Log.i("Qna", "Unknown type result received : " + Result.m_nCallback);
            }
            break;

        } // end of switch
    }
}
