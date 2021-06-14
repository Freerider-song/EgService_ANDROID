package com.enernet.eg;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class EgDialog extends Dialog {

    public int m_nResourceLayout;
    private TextView m_tvContent;
    private Button m_BtnConfirm;
    private String m_strContent;
    private View.OnClickListener m_ClickListener;

    public EgDialog(Context ctx, int nResoureId, String strContent, View.OnClickListener ClickListener) {
        super(ctx, android.R.style.Theme_Translucent_NoTitleBar);

        m_nResourceLayout=nResoureId;
        m_strContent=strContent;
        m_ClickListener = ClickListener;
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

        if (!m_strContent.isEmpty()) {
            m_tvContent = findViewById(R.id.dialog_text);
            m_tvContent.setText(m_strContent);
        }

        m_BtnConfirm = findViewById(R.id.dialog_btn);

        if (m_ClickListener != null) {
            m_BtnConfirm.setOnClickListener(m_ClickListener);
        }

    }

}
