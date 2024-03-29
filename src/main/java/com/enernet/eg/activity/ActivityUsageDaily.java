package com.enernet.eg.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.enernet.eg.CaApplication;
import com.enernet.eg.CaEngine;
import com.enernet.eg.CaResult;
import com.enernet.eg.EgYearMonthDayPicker;
import com.enernet.eg.IaResultHandler;
import com.enernet.eg.R;
import com.enernet.eg.StringUtil;
import com.enernet.eg.model.CaUsage;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
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

public class ActivityUsageDaily extends BaseActivity implements IaResultHandler {

    public boolean m_bShowKwh=true;
    protected HorizontalBarChart m_Chart;
    public ArrayList<CaUsage> m_alUsage=new ArrayList<>();

    private EgYearMonthDayPicker m_dlgYearMonthDayPicker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage_daily);

        // m_Drawer=new DrawerBuilder().withActivity(this).build();
        prepareDrawer();

        Log.i("ActivityUsageDaily", "onCreate called...");

        initChartDaily();

        Calendar calCurr= Calendar.getInstance();
        requestUsageDaily(calCurr.get(Calendar.YEAR), calCurr.get(Calendar.MONTH)+1, calCurr.get(Calendar.DATE));
    }

    public void requestUsageDaily(int nYear, int nMonth, int nDay) {
        CaApplication.m_Engine.GetUsageOfOneDay(CaApplication.m_Info.m_nSeqSite, CaApplication.m_Info.m_nSeqMeter,
                nYear, nMonth, nDay, this, this);
    }

    public void initChartDaily()
    {
        m_Chart = findViewById(R.id.chart_daily);

        m_Chart.setDrawBarShadow(false);
        m_Chart.setDrawValueAboveBar(true);
        m_Chart.getDescription().setEnabled(false);
        m_Chart.setMaxVisibleValueCount(60);
        m_Chart.setDrawGridBackground(false);
        m_Chart.animateY(1000);

        m_Chart.setScaleEnabled(true);
        m_Chart.setPinchZoom(true);
        m_Chart.setDoubleTapToZoomEnabled(true);
        m_Chart.setTouchEnabled(false);

        Typeface tf = Typeface.createFromAsset(getAssets(), StringUtil.getString(this, R.string.font_open_sans_regular));

        XAxis xAxis = m_Chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(tf);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);
        xAxis.setGranularity(0.3f);

        YAxis yLeft = m_Chart.getAxisLeft();
        yLeft.setTypeface(tf);
        yLeft.setDrawAxisLine(true);
        yLeft.setDrawGridLines(true);
        xAxis.setGranularity(0.3f);

        Legend lgd = m_Chart.getLegend();
        lgd.setDrawInside(false);
        lgd.setFormSize(8f);
        lgd.setXEntrySpace(4f);
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

            case R.id.btn_select_time: {

                View.OnClickListener LsnConfirmYes=new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        m_dlgYearMonthDayPicker.dismiss();

                        int nYear=m_dlgYearMonthDayPicker.m_DatePicker.getYear();
                        int nMonth=m_dlgYearMonthDayPicker.m_DatePicker.getMonth()+1;
                        int nDay=m_dlgYearMonthDayPicker.m_DatePicker.getDayOfMonth();

                        Log.i("YearMonthDatePicker", "year="+nYear+", month="+nMonth+", day="+nDay);

                        requestUsageDaily(nYear, nMonth, nDay);

                    }
                };

                View.OnClickListener LsnConfirmNo=new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("ActivityCandidate", "No button clicked...");
                        m_dlgYearMonthDayPicker.dismiss();
                    }
                };

                m_dlgYearMonthDayPicker=new EgYearMonthDayPicker(this, "조회할 날짜를 선택하세요", LsnConfirmYes, LsnConfirmNo);
                m_dlgYearMonthDayPicker.show();
            }
            break;

            case R.id.btn_show_kwh: {
                if (!m_bShowKwh) {
                    m_bShowKwh = true;
                    setDataChart();

                    Button btnShowKwh = findViewById(R.id.btn_show_kwh);
                    Button btnShowWon = findViewById(R.id.btn_show_won);

                    btnShowKwh.setTextColor(getResources().getColor(R.color.black));
                    btnShowWon.setTextColor(getResources().getColor(R.color.chart_gray));
                }
            }
            break;

            case R.id.btn_show_won: {
                if (m_bShowKwh) {
                    m_bShowKwh = false;
                    setDataChart();

                    Button btnShowKwh = findViewById(R.id.btn_show_kwh);
                    Button btnShowWon = findViewById(R.id.btn_show_won);

                    btnShowKwh.setTextColor(getResources().getColor(R.color.chart_gray));
                    btnShowWon.setTextColor(getResources().getColor(R.color.black));
                }
            }
            break;

        }
    }

    @Override
    public void onResult(CaResult Result) {

        if (Result.object==null) {
            Toast.makeText(getApplicationContext(), StringUtil.getString(this, R.string.tv_label_network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        switch (Result.m_nCallback) {
            case CaEngine.CB_GET_USAGE_OF_ONE_DAY: {
                Log.i("ActivityAck", "Result of GetUsageOfOneDay received...");

                try {
                    JSONObject jo = Result.object;

                    String strTargetTime=jo.getString("target_time"); // yyyyMMdd
                    double dSumKwhCurr=jo.getDouble("total_kwh_curr");
                    double dSumKwhPrev=jo.getDouble("total_kwh_prev");
                    double dSumKwhAvg=jo.getDouble("total_kwh_avg");

                    double dSumWonCurr=jo.getDouble("total_won_curr");
                    double dSumWonPrev=jo.getDouble("total_won_prev");
                    double dSumWonAvg=jo.getDouble("total_won_avg");

                    setDataEtc(strTargetTime, dSumKwhCurr, dSumKwhPrev, dSumKwhAvg,
                            dSumWonCurr, dSumWonPrev, dSumWonAvg);

                    JSONArray ja=jo.getJSONArray("list_usage");
                    prepareChartData(ja);
                    setDataChart();

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            break;

            default: {
                Log.i("ActivityAck", "Unknown type result received : " + Result.m_nCallback);
            }
            break;

        } // end of switch
    }


    public void prepareChartData(JSONArray ja) {
        m_alUsage.clear();

        for (int i=0; i<ja.length(); i++) {
            try {
                JSONObject jo = ja.getJSONObject(i);

                CaUsage Usage = new CaUsage();
                Usage.m_nUnit=jo.getInt("unit");
                Usage.m_dKwh=jo.getDouble("kwh_curr");
                Usage.m_dKwhPrev=jo.getDouble("kwh_prev");
                Usage.m_dKwhAvg=jo.getDouble("kwh_avg");
                Usage.m_dWon=jo.getDouble("won_curr");
                Usage.m_dWonPrev=jo.getDouble("won_prev");
                Usage.m_dWonAvg=jo.getDouble("won_avg");
                m_alUsage.add(Usage);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<String> getAreaCount(){
        int nCountUsage=m_alUsage.size();
        ArrayList<String> label = new ArrayList<>();
        for (int i = 0; i <nCountUsage; i++) {
            CaUsage Usage=m_alUsage.get(nCountUsage-1-i);

            label.add((Usage.m_nUnit+1) + " 시");
        };
        return label;
    }

    public void setDataChart() {

        m_Chart.clear();

        ArrayList<BarEntry> yValsKwhCurr = new ArrayList<>();
        ArrayList<BarEntry> yValsKwhPrev = new ArrayList<>();
        ArrayList<BarEntry> yValsWonCurr = new ArrayList<>();
        ArrayList<BarEntry> yValsWonPrev = new ArrayList<>();

        int nCountUsage=m_alUsage.size();
        for (int i=0; i<nCountUsage; i++) {
            CaUsage Usage=m_alUsage.get(nCountUsage-1-i);

            yValsKwhCurr.add(new BarEntry(Usage.m_nUnit, (float)Usage.m_dKwh));
            yValsKwhPrev.add(new BarEntry(Usage.m_nUnit, (float)Usage.m_dKwhPrev));

            yValsWonCurr.add(new BarEntry(Usage.m_nUnit, (float)Usage.m_dWon));
            yValsWonPrev.add(new BarEntry(Usage.m_nUnit, (float)Usage.m_dWonPrev));
        }

        float fGroupSpace = 0.20f;
        float fBarSpace = 0.10f;
        float fBarWidth = 0.30f;

        if (m_bShowKwh) {

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

            YAxis yLeft = m_Chart.getAxisLeft();
            yLeft.setValueFormatter(vfKwh);

            YAxis yRight = m_Chart.getAxisRight();
            yRight.setValueFormatter(vfKwh);

            XAxis xAxis = m_Chart.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(getAreaCount()));
            xAxis.setLabelCount(m_alUsage.size());

            BarDataSet setKwhCurr=new BarDataSet(yValsKwhCurr, "현재 사용량");
            setKwhCurr.setColor(getResources().getColor(R.color.eg_cyan_light));
            setKwhCurr.setValueFormatter(vfKwhWithUnit);

            BarDataSet setKwhPrev=new BarDataSet(yValsKwhPrev, "전년 동일 사용량");
            setKwhPrev.setColor(getResources().getColor(R.color.eg_yellow_dark));
            setKwhPrev.setValueFormatter(vfKwhWithUnit);

            BarData dataKwh = new BarData(setKwhCurr, setKwhPrev);
            dataKwh.setValueTextSize(10f);
            dataKwh.setBarWidth(fBarWidth);
            dataKwh.setHighlightEnabled(false);

            m_Chart.setData(dataKwh);
            m_Chart.getXAxis().setAxisMinimum(0);
            m_Chart.getXAxis().setAxisMaximum(nCountUsage);
            m_Chart.getAxisLeft().setAxisMinimum(0f);
            m_Chart.getAxisRight().setAxisMinimum(0f);
            m_Chart.groupBars(0.0f, fGroupSpace, fBarSpace);
        }
        else {

            ValueFormatter vfWonWithUnit=new ValueFormatter() {

                @Override
                public String getFormattedValue(float v) {
                    if (v==0) return "";
                    else return CaApplication.m_Info.m_dfWon.format(v)+" 원";
                }
            };

            ValueFormatter vfWon=new ValueFormatter() {

                @Override
                public String getFormattedValue(float v) {
                    return CaApplication.m_Info.m_dfWon.format(v);
                }
            };

            YAxis yLeft = m_Chart.getAxisLeft();
            yLeft.setValueFormatter(vfWon);

            YAxis yRight = m_Chart.getAxisRight();
            yRight.setValueFormatter(vfWon);

            XAxis xAxis = m_Chart.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(getAreaCount()));
            xAxis.setLabelCount(m_alUsage.size());

            BarDataSet setWonCurr=new BarDataSet(yValsWonCurr, "현재 요금");
            setWonCurr.setColor(getResources().getColor(R.color.eg_cyan_light));
            setWonCurr.setValueFormatter(vfWonWithUnit);

            BarDataSet setWonPrev=new BarDataSet(yValsWonPrev, "전년 동일 요금");
            setWonPrev.setColor(getResources().getColor(R.color.eg_yellow_dark));
            setWonPrev.setValueFormatter(vfWonWithUnit);

            BarData dataWon = new BarData(setWonCurr, setWonPrev);
            dataWon.setValueTextSize(10f);
            dataWon.setBarWidth(fBarWidth);
            dataWon.setHighlightEnabled(false);

            m_Chart.setData(dataWon);
            m_Chart.getXAxis().setAxisMinimum(0);
            m_Chart.getXAxis().setAxisMaximum(nCountUsage);
            m_Chart.getAxisLeft().setAxisMinimum(0f);
            m_Chart.getAxisRight().setAxisMinimum(0f);
            m_Chart.groupBars(0.0f, fGroupSpace, fBarSpace);
        }

        m_Chart.getLegend().setEnabled(false);

    }

    // yyyyMMdd
    public void setDataEtc(String strTargetTime, double dSumKwhCurr, double dSumKwhPrev, double dSumKwhAvg,
                           double dSumWonCurr, double dSumWonPrev, double dSumWonAvg) {

        try {
            Date dtTarget = CaApplication.m_Info.m_dfyyyyMMdd.parse(strTargetTime);
            SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");

            Button btnSelectTime=findViewById(R.id.btn_select_time);
            btnSelectTime.setText(df.format(dtTarget));

            // kwh
            TextView tvKwh=findViewById(R.id.tv_kwh);
            tvKwh.setText(CaApplication.m_Info.m_dfKwh.format(dSumKwhCurr) + " kWh");

            //문자열 일부 수정
            String content = tvKwh.getText().toString();
            SpannableString spannableString = new SpannableString(content);
            String word = "kWh";
            int start = content.indexOf(word);
            int end = start + word.length();
            spannableString.setSpan(new RelativeSizeSpan(0.5f), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvKwh.setText(spannableString);

            double dKwhDeltaPercent=100.0*(dSumKwhCurr - dSumKwhPrev)/dSumKwhPrev;
            if (dSumKwhPrev==0.0) dKwhDeltaPercent=0.0;

            TextView tvKwhPercent=findViewById(R.id.tv_kwh_percent);
            tvKwhPercent.setText(CaApplication.m_Info.m_dfKwh.format(Math.abs(dKwhDeltaPercent)) + " %");

            ImageView ivKwhPercent=findViewById(R.id.iv_kwh_percent);

            if (dKwhDeltaPercent > 0) {
                tvKwhPercent.setTextColor(getResources().getColor(R.color.red));
                ivKwhPercent.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up));
            }
            else if (dKwhDeltaPercent < 0) {
                tvKwhPercent.setTextColor(getResources().getColor(R.color.eg_cyan_dark));
                ivKwhPercent.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
            }
            else {
                tvKwhPercent.setTextColor(getResources().getColor(R.color.eg_cyan_light));
                ivKwhPercent.setImageDrawable(getResources().getDrawable(R.drawable.circle_cyan_light));
            }

            // won
            TextView tvWon=findViewById(R.id.tv_won);
            tvWon.setText(CaApplication.m_Info.m_dfWon.format(dSumWonCurr) + " 원");
            //문자열 일부 수정
            String content2 = tvWon.getText().toString();
            SpannableString spannableString2 = new SpannableString(content2);
            String word2 = "원";
            int start2 = content2.indexOf(word2);
            int end2 = start2 + word2.length();
            spannableString2.setSpan(new RelativeSizeSpan(0.5f), start2, end2, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvWon.setText(spannableString2);

            double dWonDeltaPercent=100.0*(dSumWonCurr - dSumWonPrev)/dSumWonPrev;
            if (dSumKwhPrev==0.0) dWonDeltaPercent=0.0;

            TextView tvWonPercent=findViewById(R.id.tv_won_percent);
            tvWonPercent.setText(CaApplication.m_Info.m_dfKwh.format(Math.abs(dWonDeltaPercent)) + " %");

            ImageView ivWonPercent=findViewById(R.id.iv_won_percent);

            if (dWonDeltaPercent > 0) {
                tvWonPercent.setTextColor(getResources().getColor(R.color.red));
                ivWonPercent.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up));
            }
            else if (dWonDeltaPercent < 0) {
                tvWonPercent.setTextColor(getResources().getColor(R.color.eg_cyan_dark));
                ivWonPercent.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
            }
            else {
                tvWonPercent.setTextColor(getResources().getColor(R.color.eg_cyan_light));
                ivWonPercent.setImageDrawable(getResources().getDrawable(R.drawable.circle_cyan_light));
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
