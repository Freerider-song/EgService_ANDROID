package com.enernet.eg;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.enernet.eg.model.CaAlarm;

public class EgDialogFeedback extends Dialog {

    private CaAlarm m_Alarm;
    private View.OnClickListener m_ClickListenerYes;
    private View.OnClickListener m_ClickListenerNo;


    public EgDialogFeedback(Context ctx, CaAlarm alarm,  View.OnClickListener ClickListenerYes, View.OnClickListener ClickListenerNo) {
        super(ctx, android.R.style.Theme_Translucent_NoTitleBar);

        m_Alarm=alarm;
        m_ClickListenerYes = ClickListenerYes;
        m_ClickListenerNo = ClickListenerNo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setCancelable(false);
        getWindow().setGravity(Gravity.CENTER);

        setContentView(R.layout.dialog_alarm_ack);

        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvTimeCreated = findViewById(R.id.tv_time_created);
        TextView tvContent = findViewById(R.id.tv_content);
        ListView lvFeedback = findViewById(R.id.lv_feedback);
        tvTitle.setText(m_Alarm.m_strTitle);
        tvTimeCreated.setText(m_Alarm.getTimeCreated());
        tvContent.setText(m_Alarm.m_strContent);

        Button btnYes = findViewById(R.id.btn_yes);
        Button btnNo = findViewById(R.id.btn_no);

        if (m_ClickListenerYes != null) {
            btnYes.setOnClickListener(m_ClickListenerYes);
        }

        if (m_ClickListenerNo != null) {
            btnNo.setOnClickListener(m_ClickListenerNo);
        }

    }

}
