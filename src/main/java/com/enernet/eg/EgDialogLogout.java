package com.enernet.eg;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class EgDialogLogout extends Dialog {

    private Button m_BtnYes;
    private Button m_BtnNo;

    private View.OnClickListener m_ClickListenerYes;
    private View.OnClickListener m_ClickListenerNo;

    public EgDialogLogout(Context ctx, View.OnClickListener ClickListenerYes, View.OnClickListener ClickListenerNo) {
        super(ctx, android.R.style.Theme_Translucent_NoTitleBar);

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

        setContentView(R.layout.dialog_logout);

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
