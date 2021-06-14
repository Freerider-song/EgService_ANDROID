package com.enernet.eg;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.enernet.eg.model.CaAlarm;

public class EgDialogAlarm extends Dialog {

    private CaAlarm m_Alarm;
    private View.OnClickListener m_ClickListener;

    public EgDialogAlarm(Context ctx, CaAlarm alarm, View.OnClickListener ClickListener) {
        super(ctx, android.R.style.Theme_Translucent_NoTitleBar);

        m_Alarm=alarm;
        m_ClickListener = ClickListener;
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

        setContentView(R.layout.dialog_alarm);

        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvTimeCreated = findViewById(R.id.tv_time_created);
        TextView tvContent = findViewById(R.id.tv_content);
        TextView tvAckResponse = findViewById(R.id.tv_ack_response);

        tvTitle.setText(m_Alarm.m_strTitle);
        tvTimeCreated.setText(m_Alarm.getTimeCreated());
        tvContent.setText(m_Alarm.m_strContent);

        if (m_Alarm.isRequestAck()) {
            if (m_Alarm.m_nResponse==1) tvAckResponse.setText("승인함");
            else if (m_Alarm.m_nResponse==2) tvAckResponse.setText("거절함");
            else tvAckResponse.setText("미정");
        }
        else {
            tvAckResponse.setVisibility(View.INVISIBLE);
        }

        Button btnConfirm = findViewById(R.id.btn_confirm);

        if (m_ClickListener != null) {
            btnConfirm.setOnClickListener(m_ClickListener);
        }

    }

}
