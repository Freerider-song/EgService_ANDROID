package com.enernet.eg.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.enernet.eg.CaApplication;
import com.enernet.eg.CaEngine;
import com.enernet.eg.CaInfo;
import com.enernet.eg.CaResult;
import com.enernet.eg.IaResultHandler;
import com.enernet.eg.R;
import com.enernet.eg.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;

public class ActivitySiteState extends BaseActivity implements IaResultHandler {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_state);

        prepareDrawer();

        CaApplication.m_Engine.GetUsageCurrentOfOneSite(CaApplication.m_Info.m_nSeqSite,this,this);
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

            case R.id.btn_refresh: {
                Log.i("SiteState", "Refresh button clicked...");
                CaApplication.m_Engine.GetUsageCurrentOfOneSite(CaApplication.m_Info.m_nSeqSite,this,this);
            }
            break;
        }
    }

    public void scrollToView(View v) {

        ScrollView sv=findViewById(R.id.scroll);
        int y=sv.getScrollY();
        int[] locScroll={0, 0};
        sv.getLocationInWindow(locScroll);

        int[] locView={0, 0};
        v.getLocationOnScreen(locView);

        sv.scrollTo(0, y+locView[1]-locScroll[1]);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        Log.i("Setting", "onWindowFocusChanged called...hasFocus="+hasFocus);

        if (hasFocus) {
            String strTo=getIntent().getStringExtra("to");
            if (strTo!=null) {
                if (strTo.equals("trans_state")) {
                    View view=findViewById(R.id.usage_area_b);
                    scrollToView(view);
                }

            }

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

                try {
                    JSONObject jo = Result.object;

                    String strTimeUpdate = jo.getString("time_curr");

                    double dKwhFromYear = jo.getDouble("site_kwh_from_year");
                    double dKwhFromMonth = jo.getDouble("site_kwh_from_month");
                    double dKwhFromWeek = jo.getDouble("site_kwh_from_week");
                    double dKwhFromDay = jo.getDouble("site_kwh_from_day");

                    double dKwhFromYearPrevYear = jo.getDouble("site_kwh_from_year_prev_year");
                    double dKwhFromMonthPrevYear = jo.getDouble("site_kwh_from_month_prev_year");
                    double dKwhFromWeekPrevYear = jo.getDouble("site_kwh_from_week_prev_year");
                    double dKwhFromDayPrevYear = jo.getDouble("site_kwh_from_day_prev_year");

                    int nTransState=-1;

                    JSONArray jaTrans=jo.getJSONArray("trans_list");
                    for (int i=0; i<jaTrans.length(); i++) {
                        JSONObject joTrans=jaTrans.getJSONObject(i);
                        int nTransStateCurr=joTrans.getInt("trans_state");
                        if (nTransStateCurr > nTransState) nTransState=nTransStateCurr;
                    }

                    setDataEtc(strTimeUpdate, dKwhFromMonth, dKwhFromMonthPrevYear, nTransState);
                    setTransState(nTransState);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            break;

            default: {
                Log.i("UsageYearly", "Unknown type result received : " + Result.m_nCallback);
            }
            break;

        } // end of switch
    }

    public void setDataEtc(String strTimeUpdate, double dKwhMonthCurrYear, double dKwhMonthPrevYear, int nTransState) {

        try {

            TextView tvKwh=findViewById(R.id.tv_kwh);
            tvKwh.setText(CaApplication.m_Info.m_dfKwh.format(dKwhMonthCurrYear));

            double dDeltaKwh=(dKwhMonthCurrYear - dKwhMonthPrevYear)/dKwhMonthPrevYear;
            if (dKwhMonthPrevYear==0.0) dDeltaKwh=0.0;

            double dDeltaGas=dDeltaKwh * CaInfo.KWH_TO_GAS;

            TextView tvDeltaKwh=findViewById(R.id.tv_delta_kwh);
            tvDeltaKwh.setText(CaApplication.m_Info.m_dfKwh.format(Math.abs(dDeltaKwh)) + " kWh");

            TextView tvDeltaGas=findViewById(R.id.tv_delta_gas);
            tvDeltaGas.setText(CaApplication.m_Info.m_dfKwh.format(Math.abs(dDeltaGas)) + " kg");

            ImageView ivDeltaKwh=findViewById(R.id.iv_delta_kwh);
            ImageView ivDeltaGas=findViewById(R.id.iv_delta_gas);

            if (dDeltaKwh > 0) {
                tvDeltaKwh.setTextColor(getResources().getColor(R.color.red));
                ivDeltaKwh.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up));

                tvDeltaGas.setTextColor(getResources().getColor(R.color.red));
                ivDeltaGas.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up));
            }
            else if (dDeltaKwh < 0) {
                tvDeltaKwh.setTextColor(getResources().getColor(R.color.bright_blue));
                ivDeltaKwh.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));

                tvDeltaGas.setTextColor(getResources().getColor(R.color.bright_blue));
                ivDeltaGas.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
            }
            else {
                tvDeltaKwh.setTextColor(getResources().getColor(R.color.eg_cyan_light));
                ivDeltaKwh.setImageDrawable(getResources().getDrawable(R.drawable.circle_cyan_light));

                tvDeltaGas.setTextColor(getResources().getColor(R.color.eg_cyan_light));
                ivDeltaGas.setImageDrawable(getResources().getDrawable(R.drawable.circle_cyan_light));
            }

            TextView tvUpdate=findViewById(R.id.tv_time_update);
            Date dtUpdate = CaApplication.m_Info.m_dfStd.parse(strTimeUpdate);
            tvUpdate.setText(CaApplication.m_Info.m_dfyyyyMMddhhmm_ampm.format(dtUpdate));

        }
        catch (ParseException e) {
            e.printStackTrace();
        }

    }

    void setTransState(int nTransState) {
        ImageView iv=findViewById(R.id.iv_trans_state);

        String strTransState="";

        switch (nTransState) {
            case 0: {
                iv.setImageDrawable(getResources().getDrawable(R.drawable.trans_normal));
                strTransState="우리 아파트의 변압기 온도와 상태가 안정적입니다";
            }
            break;

            case 1: {
                iv.setImageDrawable(getResources().getDrawable(R.drawable.trans_warning));
                strTransState="현재 우리 아파트의 전기 사용이 많아서 주의가 필요입니다";
            }
            break;

            case 2: {
                iv.setImageDrawable(getResources().getDrawable(R.drawable.trans_danger));
                strTransState="현재 우리 아파트의 전기 사용이 과도하여 변압기 상태가 위험합니다";
            }
            break;

            default: {
                iv.setImageDrawable(getResources().getDrawable(R.drawable.trans_unknown));
                strTransState="우리 아파트의 변압기 상태 정보가 수집되지 않고 있어 확인중입니다";
            }
            break;
        }

        TextView tvTransState=findViewById(R.id.tv_trans_state);
        tvTransState.setText(strTransState);
    }
}