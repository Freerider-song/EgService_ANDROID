package com.enernet.eg.activity;

import android.content.Intent;
import android.graphics.Typeface;
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
import com.enernet.eg.model.CaUsage;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ActivitySiteState2 extends BaseActivity implements IaResultHandler {

    public HorizontalBarChart m_HChart;
    public LineChart m_LChart;
    public ArrayList<CaUsage> m_alLineUsage = new ArrayList<>();
    public ArrayList<CaUsage> m_alDailyUsage = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_state2);

        prepareDrawer();

        CaApplication.m_Engine.GetUsageCurrentOfOneSite(CaApplication.m_Info.m_nSeqSite,this,this);

        initChart();

        Calendar calCurr= Calendar.getInstance();
        requestUsage(calCurr.get(Calendar.YEAR), calCurr.get(Calendar.MONTH)+1, calCurr.get(Calendar.DATE));

    }

    public void requestUsage(int nYear, int nMonth, int nDay) {

        Date today = new Date();
        SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");
        String toDay = date.format(today);

        Calendar day = Calendar.getInstance();
        day.add(Calendar.DATE , -1);
        String beforeDate = new java.text.SimpleDateFormat("yyyyMMdd").format(day.getTime());

        Calendar mon = Calendar.getInstance();
        mon.add(Calendar.MONTH , -1);
        String beforeMonth = new java.text.SimpleDateFormat("yyyyMMdd").format(mon.getTime());


        CaApplication.m_Engine.GetSiteUsageFront(CaApplication.m_Info.m_nSeqSite,
                nYear, nMonth, nDay, this, this);
        CaApplication.m_Engine.GetSiteDailyUsage(CaApplication.m_Info.m_nSeqSite, beforeMonth, beforeDate, this, this);
    }

    public void initChart()
    {
        m_HChart = findViewById(R.id.chartHorizontal);

        m_HChart.setDrawBarShadow(false);
        m_HChart.setDrawValueAboveBar(true);
        m_HChart.getDescription().setEnabled(false);
        m_HChart.setMaxVisibleValueCount(60);
        m_HChart.setDrawGridBackground(false);
        m_HChart.animateY(1000);

        m_HChart.setScaleEnabled(true);
        m_HChart.setPinchZoom(true);
        m_HChart.setDoubleTapToZoomEnabled(true);
        m_HChart.setTouchEnabled(false);

        Typeface tf = Typeface.createFromAsset(getAssets(), StringUtil.getString(this, R.string.font_open_sans_regular));

        XAxis xAxis = m_HChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(tf);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);
        xAxis.setGranularity(0.3f);

        YAxis yLeft = m_HChart.getAxisLeft();
        yLeft.setTypeface(tf);
        yLeft.setDrawAxisLine(true);
        yLeft.setDrawGridLines(true);
        xAxis.setGranularity(0.3f);

        Legend lgd = m_HChart.getLegend();
        lgd.setDrawInside(false);
        lgd.setFormSize(8f);
        lgd.setXEntrySpace(4f);

        m_LChart = findViewById(R.id.chartLine);

        m_LChart.getDescription().setEnabled(false);
        m_LChart.setMaxVisibleValueCount(60);
        m_LChart.setDrawGridBackground(false);
        m_LChart.animateY(1000);

        m_LChart.setScaleEnabled(true);
        m_LChart.setPinchZoom(true);
        m_LChart.setDoubleTapToZoomEnabled(true);
        m_LChart.setTouchEnabled(false);

        Typeface tf2 = Typeface.createFromAsset(getAssets(), StringUtil.getString(this, R.string.font_open_sans_regular));

        XAxis xAxis2 = m_LChart.getXAxis();
        xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis2.setTypeface(tf);
        xAxis2.setDrawAxisLine(true);
        xAxis2.setDrawGridLines(true);
        xAxis2.setGranularity(0.3f);

        YAxis yLeft2 = m_LChart.getAxisLeft();
        yLeft2.setTypeface(tf);
        yLeft2.setDrawAxisLine(true);
        yLeft2.setDrawGridLines(true);
        xAxis2.setGranularity(0.3f);

        Legend lgd2 = m_LChart.getLegend();
        lgd2.setDrawInside(false);
        lgd2.setFormSize(8f);
        lgd2.setXEntrySpace(4f);
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

            case R.id.usage_area_a: {
                Intent it = new Intent(this, ActivitySiteStateDetail.class);
                startActivity(it);
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
                    CaApplication.m_Info.m_strSiteUpdate = strTimeUpdate;

                    double dKwhFromMonth = jo.getDouble("site_kwh_from_month");
                    CaApplication.m_Info.m_dSiteKwhFromMonth = dKwhFromMonth;

                    double dKwhFromMonthPrevYear = jo.getDouble("site_kwh_from_month_prev_year");
                    CaApplication.m_Info.m_dSiteKwhFromMonthPrevYear = dKwhFromMonthPrevYear;

                    int nTransState=-1;

                    JSONArray jaTrans=jo.getJSONArray("trans_list");
                    for (int i=0; i<jaTrans.length(); i++) {
                        JSONObject joTrans=jaTrans.getJSONObject(i);
                        int nTransStateCurr=joTrans.getInt("trans_state");
                        if (nTransStateCurr > nTransState) nTransState=nTransStateCurr;
                    }

                    setDataEtc(strTimeUpdate, dKwhFromMonth, dKwhFromMonthPrevYear, nTransState);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            break;

            case CaEngine.CB_GET_SITE_USAGE_FRONT: {
                Log.i("ActivitySiteState", "Result of GetSiteUsageFront received...");
                try {
                    JSONObject jo = Result.object;

                    CaApplication.m_Info.m_bHoliday = (jo.getInt("is_holiday")==1) ? true: false; // yyyyMMdd

                    JSONArray ja=jo.getJSONArray("list_usage");
                    m_alLineUsage.clear();
                    for (int i=0; i<ja.length(); i++) {
                        try {
                            JSONObject joUsage = ja.getJSONObject(i);

                            CaUsage Usage = new CaUsage();
                            Usage.m_nUnit=joUsage.getInt("unit");
                            Usage.m_strHHmm=joUsage.getString("hhmm");
                            Usage.m_dUsage=joUsage.getDouble("usage");
                            Usage.m_dUsageAvgHoliday=joUsage.getDouble("usage_avg_holiday");
                            Usage.m_dUsageAvgWorkday=joUsage.getDouble("usage_avg_workday");
                            m_alLineUsage.add(Usage);
                        }
                        catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }

                    setDataChart();

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            break;

            case CaEngine.CB_GET_SITE_DAILY_USAGE: {
                Log.i("ActivitySiteState", "Result of GetSiteDailyusage received...");
                try {
                    JSONObject jo = Result.object;

                    //CaApplication.m_Info.m_bHoliday = (jo.getInt("is_holiday")==1) ? true: false; // yyyyMMdd

                    JSONArray ja=jo.getJSONArray("list_usage");
                    m_alDailyUsage.clear();
                    for (int i=0; i<ja.length(); i++) {
                        try {
                            JSONObject joUsage = ja.getJSONObject(i);

                            CaUsage Usage = new CaUsage();
                            Usage.m_nUnit=joUsage.getInt("unit");
                            Usage.m_dUsage=joUsage.getDouble("usage");

                            m_alDailyUsage.add(Usage);
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    setDataChart();

                }
                catch (JSONException e) {
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

    public ArrayList<String> getAreaCount(){
        int nCountUsage=m_alLineUsage.size();
        ArrayList<String> label = new ArrayList<>();
        for (int i = 0; i <nCountUsage; i++) {
            CaUsage Usage=m_alLineUsage.get(i);

            label.add((Usage.m_strHHmm));
        };
        return label;
    }


    public void setDataChart() {

        m_LChart.clear();


        ArrayList<Entry> yValsKwhCurr = new ArrayList<>();
        ArrayList<Entry> yValsKwhHoliday = new ArrayList<>();
        ArrayList<Entry> yValsKwhWorkday = new ArrayList<>();


        int nCountUsage=m_alLineUsage.size();
        for (int i=0; i<nCountUsage; i++) {
            CaUsage Usage=m_alLineUsage.get(i);

            yValsKwhCurr.add(new Entry(Usage.m_nUnit, (float)Usage.m_dUsage));
            yValsKwhHoliday.add(new Entry(Usage.m_nUnit, (float)Usage.m_dUsageAvgHoliday));
            yValsKwhWorkday.add(new Entry(Usage.m_nUnit, (float)Usage.m_dUsageAvgWorkday));

        }

        float fGroupSpace = 0.20f;
        float fBarSpace = 0.10f;
        float fBarWidth = 0.30f;
        ValueFormatter vfKwhWithUnit=new ValueFormatter() {

            @Override
            public String getFormattedValue(float v) {
                if (v==0) return "";
                else return CaApplication.m_Info.m_dfKwh.format(v)+" kWh";
            }
        };

        ValueFormatter vfKwh=new ValueFormatter() {

            @Override
            public String getFormattedValue(float v) {
                return CaApplication.m_Info.m_dfKwh.format(v);
            }
        };

        YAxis yLeft = m_LChart.getAxisLeft();
        yLeft.setValueFormatter(vfKwh);

        YAxis yRight = m_LChart.getAxisRight();
        yRight.setValueFormatter(vfKwh);

        XAxis xAxis = m_LChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getAreaCount()));
        xAxis.setLabelCount(m_alLineUsage.size());


        LineDataSet setKwhCurr=new LineDataSet(yValsKwhCurr, "오늘 사용량");
        setKwhCurr.setColor(getResources().getColor(R.color.eg_cyan_light));
        setKwhCurr.setValueFormatter(vfKwhWithUnit);

        LineDataSet setKwhHoliday=new LineDataSet(yValsKwhHoliday, "휴일 평균 사용량");
        setKwhHoliday.setColor(getResources().getColor(R.color.chart_light_gray));
        setKwhHoliday.setValueFormatter(vfKwhWithUnit);

        LineDataSet setKwhWorkday=new LineDataSet(yValsKwhWorkday, "근무일 평균 사용량");
        setKwhWorkday.setColor(getResources().getColor(R.color.chart_light_gray));
        setKwhWorkday.setValueFormatter(vfKwhWithUnit);

        LineData dataKwh = new LineData(setKwhCurr, setKwhHoliday, setKwhWorkday);
        dataKwh.setValueTextSize(10f);
        dataKwh.setHighlightEnabled(false);

        m_LChart.setData(dataKwh);
        m_LChart.getXAxis().setAxisMinimum(0);
        m_LChart.getXAxis().setAxisMaximum(nCountUsage);
        m_LChart.getAxisLeft().setAxisMinimum(0f);
        m_LChart.getAxisRight().setAxisMinimum(0f);
       // m_LChart.groupBars(0.0f, fGroupSpace, fBarSpace);

        m_LChart.getLegend().setEnabled(false);

    }

    public void setDataEtc(String strTimeUpdate, double dKwhMonthCurrYear, double dKwhMonthPrevYear, int nTransState) {

        try {

            TextView tvKwh=findViewById(R.id.tv_kwh);
            tvKwh.setText(CaApplication.m_Info.m_dfKwh.format(dKwhMonthCurrYear));

            //double dDeltaKwh=(dKwhMonthCurrYear - dKwhMonthPrevYear)/dKwhMonthPrevYear;
            double dDeltaKwh=(dKwhMonthCurrYear - dKwhMonthPrevYear);
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


}