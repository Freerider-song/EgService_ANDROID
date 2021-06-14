package com.enernet.eg;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EgUsageAlarmOption extends Dialog {

    private TextView m_tvTitle;
    private Button m_BtnYes;
    private Button m_BtnNo;
    private String m_strTitle;

    private Spinner m_spAlarmType;
    private Spinner m_spAlarmHour;

    private View.OnClickListener m_ClickListenerYes;
    private View.OnClickListener m_ClickListenerNo;

    public int m_nUsageNotiType=CaApplication.m_Info.m_nUsageNotiType;
    public int m_nUsageNotiHour=CaApplication.m_Info.m_nUsageNotiHour;

    public EgUsageAlarmOption(Context ctx, String strTitle, View.OnClickListener ClickListenerYes, View.OnClickListener ClickListenerNo) {
        super(ctx, android.R.style.Theme_Translucent_NoTitleBar);

        m_strTitle=strTitle;
        m_ClickListenerYes = ClickListenerYes;
        m_ClickListenerNo = ClickListenerNo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setCancelable(false);
        getWindow().setGravity(Gravity.CENTER);

        setContentView(R.layout.eg_usage_alarm_option);

        m_tvTitle = findViewById(R.id.tv_title);
        m_tvTitle.setText(m_strTitle);

        /////////////////
        m_spAlarmType=findViewById(R.id.sp_alarm_type);

        final List<String> alAlarmType = new ArrayList<>();
        alAlarmType.add("매일");
        alAlarmType.add("매주 일요일");
        alAlarmType.add("매주 월요일");
        alAlarmType.add("매달 1일");
        alAlarmType.add("매달 검침일");

        ArrayAdapter<String> AdapterUsageType = new ArrayAdapter<>(getContext(), R.layout.eg_spinner_style, alAlarmType);
        m_spAlarmType.setAdapter(AdapterUsageType);
        AdapterUsageType.setDropDownViewResource(R.layout.eg_spinner_item_style);
        m_spAlarmType.setSelection(CaApplication.m_Info.m_nUsageNotiType);

        m_spAlarmType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("UsageAlarmType", "Selected="+alAlarmType.get(position)+", position="+position+", id="+id);

                m_nUsageNotiType=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /////////////
        m_spAlarmHour=findViewById(R.id.sp_alarm_hour);

        final List<String> alAlarmHour = new ArrayList<>();

        for (int i=0; i<24; i++) {
            alAlarmHour.add(Integer.toString(i)+ " 시");
        }

        ArrayAdapter<String> AdapterUsageHour = new ArrayAdapter<>(getContext(), R.layout.eg_spinner_style, alAlarmHour);
        m_spAlarmHour.setAdapter(AdapterUsageHour);
        AdapterUsageHour.setDropDownViewResource(R.layout.eg_spinner_item_style);
        m_spAlarmHour.setSelection(CaApplication.m_Info.m_nUsageNotiHour);

        m_spAlarmHour.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("UsageAlarmHour", "Selected="+alAlarmHour.get(position)+", position="+position+", id="+id);
                m_nUsageNotiHour=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /////////////

        m_BtnYes = findViewById(R.id.btn_yes);
        m_BtnNo = findViewById(R.id.btn_no);

        if (m_ClickListenerYes != null) {
            m_BtnYes.setOnClickListener(m_ClickListenerYes);
        }

        if (m_ClickListenerNo != null) {
            m_BtnNo.setOnClickListener(m_ClickListenerNo);
        }

    }

}
