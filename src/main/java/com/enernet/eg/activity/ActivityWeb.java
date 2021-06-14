package com.enernet.eg.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.enernet.eg.CaResult;
import com.enernet.eg.IaResultHandler;
import com.enernet.eg.R;
import com.enernet.eg.StringUtil;

public class ActivityWeb extends BaseActivity implements IaResultHandler {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        TextView tvTitle=findViewById(R.id.tv_title);
        WebView wvContent=findViewById(R.id.wv_content);
        //wvContent.loadUrl("https://www.egservice.co.kr/EgService/EG_이용약관_2019_11_04.htm");

        Intent it=getIntent();
        String strTitle=it.getStringExtra("title");
        String strUrl=it.getStringExtra("url");

        tvTitle.setText(strTitle);
        wvContent.loadUrl(strUrl);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back: {
                finish();
            }
            break;

        }
    }

    @Override
    public void onBackPressed() {
            finish();
    }

    @Override
    public void onResult(CaResult Result) {

        if (Result.object==null) {
            Toast.makeText(getApplicationContext(), StringUtil.getString(this, R.string.tv_label_network_error), Toast.LENGTH_SHORT).show();
            return;
        }


    }

}
