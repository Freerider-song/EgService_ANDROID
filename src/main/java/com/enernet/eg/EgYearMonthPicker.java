package com.enernet.eg;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.Calendar;

public class EgYearMonthPicker extends Dialog {
    //public DatePicker m_DatePicker;
    private TextView m_tvTitle;
    private Button m_BtnYes;
    private Button m_BtnNo;
    private String m_strTitle;

    public NumberPicker m_npYear;
    public NumberPicker m_npMonth;

    private View.OnClickListener m_ClickListenerYes;
    private View.OnClickListener m_ClickListenerNo;

    public EgYearMonthPicker(Context ctx, String strTitle, View.OnClickListener ClickListenerYes, View.OnClickListener ClickListenerNo) {
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

        setContentView(R.layout.eg_year_month_picker);

      //  m_DatePicker=findViewById(R.id.date_picker);
        Calendar cal = Calendar.getInstance();
        m_npYear=findViewById(R.id.picker_year);
        m_npYear.setMinValue(2010);
        m_npYear.setMaxValue(2050);
        m_npYear.setValue(cal.get(Calendar.YEAR));

        m_npMonth=findViewById(R.id.picker_month);
        m_npMonth.setMinValue(1);
        m_npMonth.setMaxValue(12);
        m_npMonth.setValue(cal.get(Calendar.MONTH)+1);

        m_tvTitle = findViewById(R.id.dialog_text);
        m_tvTitle.setText(m_strTitle);

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
