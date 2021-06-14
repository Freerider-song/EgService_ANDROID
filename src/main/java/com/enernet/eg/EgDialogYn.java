package com.enernet.eg;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class EgDialogYn extends Dialog {

    public int m_nResourceLayout;
    private TextView m_tvContent;
    private Button m_BtnYes;
    private Button m_BtnNo;
    private String m_strContent;
    private View.OnClickListener m_ClickListenerYes;
    private View.OnClickListener m_ClickListenerNo;

    public EgDialogYn(Context ctx, int nResoureId, String strContent, View.OnClickListener ClickListenerYes, View.OnClickListener ClickListenerNo) {
        super(ctx, android.R.style.Theme_Translucent_NoTitleBar);

        m_nResourceLayout=nResoureId;
        m_strContent=strContent;
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

        setContentView(m_nResourceLayout);

        m_tvContent = findViewById(R.id.dialog_text);
        m_tvContent.setText(m_strContent);

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
