package com.enernet.eg.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.enernet.eg.CaApplication;
import com.enernet.eg.CaEngine;
import com.enernet.eg.CaResult;
import com.enernet.eg.IaResultHandler;
import com.enernet.eg.R;
import com.enernet.eg.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ActivityUsageDetail extends BaseActivity implements IaResultHandler {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage_detail);

       viewSetting();
    }

    public void viewSetting() {

        TextView tvWon=findViewById(R.id.tv_won_detai);
        tvWon.setText(CaApplication.m_Info.m_dfWon.format(CaApplication.m_Info.m_wonCurr));

        TextView tvKwh=findViewById(R.id.tv_kwh_detail);
        tvKwh.setText(CaApplication.m_Info.m_dfKwh.format(CaApplication.m_Info.m_kwhCurr));


        TextView tvUpdate=findViewById(R.id.tv_time_update3);
        tvUpdate.setText(CaApplication.m_Info.m_dtUpdate);

        TextView tvWonExpected=findViewById(R.id.tv_won_expected_detail);
        tvWonExpected.setText(CaApplication.m_Info.m_dfWon.format(CaApplication.m_Info.m_wonExpected));

        TextView tvWonMonth = findViewById(R.id.tv_won_percent_month);
        TextView tvWonYear = findViewById(R.id.tv_won_percent_year);
        TextView tvWonMonthTitle = findViewById(R.id.tv_won_month_title);
        TextView tvWonYearTitle = findViewById(R.id.tv_won_year_title);

        tvWonMonth.setText(Integer.toString((int)(100 * Double.parseDouble(CaApplication.m_Info.m_dfPercent.format(Math.abs(CaApplication.m_Info.m_wonCurr - CaApplication.m_Info.m_wonPrevMonth)/CaApplication.m_Info.m_wonCurr))))+"%");
        if(CaApplication.m_Info.m_wonCurr - CaApplication.m_Info.m_wonPrevMonth>0){
            tvWonMonth.setTextColor(getResources().getColor(R.color.bright_red));
            tvWonMonthTitle.setText("증가");
            tvWonMonth.setTextColor(getResources().getColor(R.color.bright_red));
        }
        else{
            tvWonMonth.setTextColor(getResources().getColor(R.color.bright_blue));
            tvWonMonthTitle.setText("감소");
            tvWonMonth.setTextColor(getResources().getColor(R.color.bright_blue));
        }

        tvWonYear.setText(Integer.toString((int)(100 * Double.parseDouble(CaApplication.m_Info.m_dfPercent.format(Math.abs(CaApplication.m_Info.m_wonCurr - CaApplication.m_Info.m_wonPrevYear)/CaApplication.m_Info.m_wonCurr))))+"%");
        if(CaApplication.m_Info.m_wonCurr - CaApplication.m_Info.m_wonPrevYear>0){
            tvWonYear.setTextColor(getResources().getColor(R.color.bright_red));
            tvWonYearTitle.setText("증가");
            tvWonYear.setTextColor(getResources().getColor(R.color.bright_red));
        }
        else{
            tvWonYear.setTextColor(getResources().getColor(R.color.bright_blue));
            tvWonYearTitle.setText("감소");
            tvWonYear.setTextColor(getResources().getColor(R.color.bright_blue));
        }
    }

    public void requestUsage() {
        Date dtTo=new Date(System.currentTimeMillis());

        Calendar calCurr= Calendar.getInstance();
        Calendar calFrom=new GregorianCalendar(calCurr.get(Calendar.YEAR), calCurr.get(Calendar.MONTH), 1, 0, 0, 0);
        Date dtFrom=calFrom.getTime();

        String strFrom= CaApplication.m_Info.m_dfyyyyMMdd.format(dtFrom);
        String strTo=CaApplication.m_Info.m_dfyyyyMMddhhmm.format(dtTo);

        Log.i("ActivityUsage", "from=" + strFrom + ", to="+strTo);

        CaApplication.m_Engine.GetUsageOfOneMeter(CaApplication.m_Info.m_nSeqSite, CaApplication.m_Info.m_nSeqMeter, strFrom, strTo, this, this);
    }

    public void onClick(View v) {
        switch (v.getId()) {


            case R.id.btn_refresh3:
            case R.id.tv_time_update3: {
                Log.i("Usage", "Refresh button clicked...");
                requestUsage();
            }
            break;

            case R.id.cl_usage_detail: {
                finish();
            }


        }
    }

    @Override
    public void onResult(CaResult Result) {

        if (Result.object==null) {
            Toast.makeText(getApplicationContext(), StringUtil.getString(this, R.string.tv_label_network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        switch (Result.m_nCallback) {
            case CaEngine.CB_GET_USAGE_OF_ONE_METER: {
                Log.i("ActivityAck", "Result of GetUsageOfOneMeter received...");

                try {
                    JSONObject jo = Result.object;

                    double dWon=jo.getDouble("won_curr");
                    TextView tvWon=findViewById(R.id.tv_won_detai);
                    tvWon.setText(CaApplication.m_Info.m_dfWon.format(dWon));

                    /*
                    double dKwh=jo.getDouble("kwh_curr");
                    TextView tvKwh=findViewById(R.id.tv_kwh);
                    tvKwh.setText("[사용량 : " + CaApplication.m_Info.m_dfKwh.format(dKwh) + " kWh]");

                     */

                    String strTimeUpdate=jo.getString("to_curr");
                    Date dtUpdate=CaApplication.m_Info.m_dfyyyyMMddhhmm.parse(strTimeUpdate);

                    TextView tvUpdate=findViewById(R.id.tv_time_update3);
                    tvUpdate.setText(CaApplication.m_Info.m_dfyyyyMMddhhmm_ampm.format(dtUpdate));

                    double dWonExpected=jo.getDouble("won_expected");
                    TextView tvWonExpected=findViewById(R.id.tv_won_expected_detail);
                    tvWonExpected.setText(CaApplication.m_Info.m_dfWon.format(dWonExpected));

                    double dWonPrevMonth=jo.getDouble("won_prev_month");
                    double dWonPrevYear=jo.getDouble("won_prev_year");

                    TextView tvWonMonth = findViewById(R.id.tv_won_percent_month);
                    TextView tvWonYear = findViewById(R.id.tv_won_percent_year);
                    TextView tvWonMonthTitle = findViewById(R.id.tv_won_month_title);
                    TextView tvWonYearTitle = findViewById(R.id.tv_won_year_title);

                    tvWonMonth.setText(Integer.toString((int)(100 * Double.parseDouble(CaApplication.m_Info.m_dfPercent.format(Math.abs(dWon - dWonPrevMonth)/dWon))))+"%");
                    if(dWon - dWonPrevMonth>0){
                        tvWonMonth.setTextColor(getResources().getColor(R.color.bright_red));
                        tvWonMonthTitle.setText("증가");
                        tvWonMonth.setTextColor(getResources().getColor(R.color.bright_red));
                    }
                    else{
                        tvWonMonth.setTextColor(getResources().getColor(R.color.bright_blue));
                        tvWonMonthTitle.setText("감소");
                        tvWonMonth.setTextColor(getResources().getColor(R.color.bright_blue));
                    }

                    tvWonYear.setText(Integer.toString((int)(100 * Double.parseDouble(CaApplication.m_Info.m_dfPercent.format(Math.abs(dWon - dWonPrevYear)/dWon))))+"%");
                    if(dWon - dWonPrevYear>0){
                        tvWonYear.setTextColor(getResources().getColor(R.color.bright_red));
                        tvWonYearTitle.setText("증가");
                        tvWonYear.setTextColor(getResources().getColor(R.color.bright_red));
                    }
                    else{
                        tvWonYear.setTextColor(getResources().getColor(R.color.bright_blue));
                        tvWonYearTitle.setText("감소");
                        tvWonYear.setTextColor(getResources().getColor(R.color.bright_blue));
                    }
                    //int actRatio = (int) (100 * Double.parseDouble(CaApplication.m_Info.m_dfPercent.format((double)CaApplication.m_Info.m_nActCountWithHistory / (double)CaApplication.m_Info.m_nActCount)))


                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                catch (ParseException e) {
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

}