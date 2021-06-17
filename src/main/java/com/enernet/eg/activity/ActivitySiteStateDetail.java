package com.enernet.eg.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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

public class ActivitySiteStateDetail extends BaseActivity implements IaResultHandler {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_state_detail);

        setDataEtc(CaApplication.m_Info.m_strSiteUpdate, CaApplication.m_Info.m_dSiteKwhFromMonth, CaApplication.m_Info.m_dSiteKwhFromMonthPrevYear);
    }

    public void setDataEtc(String strTimeUpdate, double dKwhMonthCurrYear, double dKwhMonthPrevYear) {

        try {

            TextView tvKwh=findViewById(R.id.tv_site_state_kwh);
            tvKwh.setText(CaApplication.m_Info.m_dfKwh.format(dKwhMonthCurrYear));


            double dDeltaKwh = Math.abs(dKwhMonthCurrYear - dKwhMonthPrevYear);
            if (dKwhMonthPrevYear==0.0) dDeltaKwh=0.0;

            double dDeltaGas=dDeltaKwh * CaInfo.KWH_TO_GAS;

            TextView tvDeltaKwh=findViewById(R.id.tv_kwh_percent);
            TextView tvDeltaKwhText = findViewById(R.id.tv_kwh_delta);
            tvDeltaKwh.setText(Integer.toString((int)(100 * Double.parseDouble(CaApplication.m_Info.m_dfPercent.format(
                    dDeltaKwh/dKwhMonthCurrYear)))));

            TextView tvDeltaGas=findViewById(R.id.tv_gas_percent);
            TextView tvDeltaGasText = findViewById(R.id.tv_gas_delta);
            tvDeltaGas.setText(CaApplication.m_Info.m_dfKwh.format(Math.abs(dDeltaGas)));

            if (dDeltaKwh > 0) {
                tvDeltaKwh.setTextColor(getResources().getColor(R.color.red));
                tvDeltaKwhText.setText(" % 증가");
                tvDeltaKwhText.setTextColor(getResources().getColor(R.color.red));

                tvDeltaGas.setTextColor(getResources().getColor(R.color.red));
                tvDeltaGasText.setText(" kg 증가");
                tvDeltaGasText.setTextColor(getResources().getColor(R.color.red));
            }
            else if (dDeltaKwh < 0) {
                tvDeltaKwh.setTextColor(getResources().getColor(R.color.bright_blue));
                tvDeltaKwhText.setText(" % 감소");
                tvDeltaKwhText.setTextColor(getResources().getColor(R.color.bright_blue));

                tvDeltaGas.setTextColor(getResources().getColor(R.color.bright_blue));
                tvDeltaGasText.setText(" kg 감소");
                tvDeltaGasText.setTextColor(getResources().getColor(R.color.bright_blue));
            }
            else {
                tvDeltaKwh.setTextColor(getResources().getColor(R.color.eg_cyan_light));
                tvDeltaKwhText.setText(" %");
                tvDeltaKwhText.setTextColor(getResources().getColor(R.color.eg_cyan_light));

                tvDeltaGas.setTextColor(getResources().getColor(R.color.eg_cyan_light));
                tvDeltaGasText.setText(" kg");
                tvDeltaGasText.setTextColor(getResources().getColor(R.color.eg_cyan_light));
            }

            TextView tvUpdate=findViewById(R.id.tv_time_update3);
            Date dtUpdate = CaApplication.m_Info.m_dfStd.parse(strTimeUpdate);
            tvUpdate.setText(CaApplication.m_Info.m_dfyyyyMMddhhmm_ampm.format(dtUpdate));

        }
        catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_refresh3: {
                Log.i("SiteState", "Refresh button clicked...");
                CaApplication.m_Engine.GetUsageCurrentOfOneSite(CaApplication.m_Info.m_nSeqSite,this,this);
            }
            break;

            case R.id.tv_time_update3: {

                CaApplication.m_Engine.GetUsageCurrentOfOneSite(CaApplication.m_Info.m_nSeqSite,this,this);
            }
            break;

            case R.id.cl_site_state_detail: {
                finish();
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

                try {
                    JSONObject jo = Result.object;

                    String strTimeUpdate = jo.getString("time_curr");
                    CaApplication.m_Info.m_strSiteUpdate = strTimeUpdate;

                    double dKwhFromYear = jo.getDouble("site_kwh_from_year");
                    double dKwhFromMonth = jo.getDouble("site_kwh_from_month");
                    CaApplication.m_Info.m_dSiteKwhFromMonth = dKwhFromMonth;
                    double dKwhFromWeek = jo.getDouble("site_kwh_from_week");
                    double dKwhFromDay = jo.getDouble("site_kwh_from_day");

                    double dKwhFromYearPrevYear = jo.getDouble("site_kwh_from_year_prev_year");
                    double dKwhFromMonthPrevYear = jo.getDouble("site_kwh_from_month_prev_year");
                    CaApplication.m_Info.m_dSiteKwhFromMonthPrevYear = dKwhFromMonthPrevYear;
                    double dKwhFromWeekPrevYear = jo.getDouble("site_kwh_from_week_prev_year");
                    double dKwhFromDayPrevYear = jo.getDouble("site_kwh_from_day_prev_year");


                    setDataEtc(strTimeUpdate, dKwhFromMonth, dKwhFromMonthPrevYear);

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
}