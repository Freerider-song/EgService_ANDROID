package com.enernet.eg.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.enernet.eg.CaEngine;
import com.enernet.eg.CaResult;
import com.enernet.eg.IaResultHandler;
import com.enernet.eg.R;
import com.enernet.eg.StringUtil;

public class ActivityPoint extends BaseActivity implements IaResultHandler {

    private boolean m_bShownHowToGet=true;
    private boolean m_bShownHowToUse=false;
    private boolean m_bShownCaution=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point);

        prepareDrawer();
        applyShownState();
    }

    public void applyShownState() {
        Drawable dwUp=getApplicationContext().getResources().getDrawable(R.drawable.arrow_brown_up);
        dwUp.setBounds(0, 0, 50, 50);

        Drawable dwDown=getApplicationContext().getResources().getDrawable(R.drawable.arrow_brown_down);
        dwDown.setBounds(0, 0, 50, 50);

        Button btnHowToGet=findViewById(R.id.btn_how_to_get);
        Button btnHowToUse=findViewById(R.id.btn_how_to_use);
        Button btnCaution=findViewById(R.id.btn_caution);

        TextView tvHowtoGet=findViewById(R.id.tv_how_to_get);
        TextView tvHowtoUse=findViewById(R.id.tv_how_to_use);
        TextView tvCaution=findViewById(R.id.tv_caution);

        if (m_bShownHowToGet) {
            btnHowToGet.setCompoundDrawables(null, null, dwUp, null);
            tvHowtoGet.setVisibility(View.VISIBLE);
        }
        else {
            btnHowToGet.setCompoundDrawables(null, null, dwDown, null);
            tvHowtoGet.setVisibility(View.GONE);
        }

        if (m_bShownHowToUse) {
            btnHowToUse.setCompoundDrawables(null, null, dwUp, null);
            tvHowtoUse.setVisibility(View.VISIBLE);
        }
        else {
            btnHowToUse.setCompoundDrawables(null, null, dwDown, null);
            tvHowtoUse.setVisibility(View.GONE);
        }

        if (m_bShownCaution) {
            btnCaution.setCompoundDrawables(null, null, dwUp, null);
            tvCaution.setVisibility(View.VISIBLE);
        }
        else {
            btnCaution.setCompoundDrawables(null, null, dwDown, null);
            tvCaution.setVisibility(View.GONE);
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

            case R.id.btn_how_to_get: {
                m_bShownHowToGet=!m_bShownHowToGet;
                applyShownState();
            }
            break;

            case R.id.btn_how_to_use: {
                m_bShownHowToUse=!m_bShownHowToUse;
                applyShownState();
            }
            break;

            case R.id.btn_caution: {
                m_bShownCaution=!m_bShownCaution;
                applyShownState();
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
                Log.i("UsageMonthly", "Result of GetUsageCurrentOfOneSite received...");


            }
            break;

            default: {
                Log.i("UsageYearly", "Unknown type result received : " + Result.m_nCallback);
            }
            break;

        } // end of switch
    }

}