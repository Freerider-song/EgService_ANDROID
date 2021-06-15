package com.enernet.eg.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.enernet.eg.CaApplication;
import com.enernet.eg.CaEngine;
import com.enernet.eg.CaResult;
import com.enernet.eg.EgDialogYn;
import com.enernet.eg.IaResultHandler;
import com.enernet.eg.R;
import com.enernet.eg.ServiceMonitor;
import com.enernet.eg.StringUtil;
import com.enernet.eg.model.CaDiscount;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ActivityUsage extends BaseActivity implements IaResultHandler {

    EgDialogYn m_dlgYnExit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage);

        prepareDrawer();

        TextView tvSiteName=findViewById(R.id.tv_site_name);
        tvSiteName.setText(CaApplication.m_Info.m_strSiteName);

        String strDongHoName="[" + CaApplication.m_Info.m_strAptDongName + "동 " + CaApplication.m_Info.m_strAptHoName + "호]";
        TextView tvDongHoName=findViewById(R.id.tv_dong_ho_name);
        tvDongHoName.setText(strDongHoName);

        TextView tvMemberName=findViewById(R.id.tv_member_name);
        tvMemberName.setText(CaApplication.m_Info.m_strMemberName + " 님");

        requestUsage();

    }

    public void requestUsage() {
        Date dtTo=new Date(System.currentTimeMillis());

        Calendar calCurr= Calendar.getInstance();
        Calendar calFrom=new GregorianCalendar(calCurr.get(Calendar.YEAR), calCurr.get(Calendar.MONTH), 1, 0, 0, 0);
        Date dtFrom=calFrom.getTime();

        String strFrom=CaApplication.m_Info.m_dfyyyyMMdd.format(dtFrom);
        String strTo=CaApplication.m_Info.m_dfyyyyMMddhhmm.format(dtTo);

        Log.i("ActivityUsage", "from=" + strFrom + ", to="+strTo);

        CaApplication.m_Engine.GetUsageOfOneMeter(CaApplication.m_Info.m_nSeqSite, CaApplication.m_Info.m_nSeqMeter, strFrom, strTo, this, this);
    }

    @Override
    public void onResume() {
        super.onResume();

        String strAlarmAll;
        if (CaApplication.m_Info.m_bNotiAll) strAlarmAll="전체 알림 (○)";
        else strAlarmAll="전체 알림 (Ⅹ)";

        TextView tvAlarmAll=findViewById(R.id.tv_alarm_all);
        tvAlarmAll.setText(strAlarmAll);

        String strNotiDesc=CaApplication.m_Info.getNotiDesc();

        TextView tvNoti=findViewById(R.id.tv_noti);
        tvNoti.setText(strNotiDesc);

        CaDiscount dsFamily=CaApplication.m_Info.findDiscountFamily(CaApplication.m_Info.m_nDiscountFamily);
        if (dsFamily != null) {
            TextView tvDiscountFamily=findViewById(R.id.tv_discount_family);
            tvDiscountFamily.setText("대가족/생명유지장치 할인\n["+dsFamily.m_strDiscountName+"]");
        }

        CaDiscount dsSocial=CaApplication.m_Info.findDiscountSocial(CaApplication.m_Info.m_nDiscountSocial);
        if (dsSocial != null) {
            TextView tvDiscountSocial=findViewById(R.id.tv_discount_social);
            tvDiscountSocial.setText("복지 할인\n["+dsSocial.m_strDiscountName+"]");
        }
    }

    public void promptAppExit() {
        View.OnClickListener LsnConfirmYes=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Setting", "Yes button clicked...");
                m_dlgYnExit.dismiss();

                // kill app
                finishAffinity();
                System.runFinalization();
                System.exit(0);
            }
        };

        View.OnClickListener LsnConfirmNo=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Setting", "No button clicked...");
                m_dlgYnExit.dismiss();
            }
        };

        m_dlgYnExit=new EgDialogYn(this, R.layout.dialog01yn,"앱을 종료하시겠습니까?", LsnConfirmYes, LsnConfirmNo);
        m_dlgYnExit.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode==KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    return true;
                }
                return false;
            }
        });

        m_dlgYnExit.show();
    }

    @Override
    public void onBackPressed() {
        if (m_Drawer.isDrawerOpen()) {
            m_Drawer.closeDrawer();
        }
        else {
            promptAppExit();
        }
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back: {
                promptAppExit();
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
                Log.i("Usage", "Refresh button clicked...");
                requestUsage();
            }
            break;

            case R.id.btn_usage_daily: {
                Intent it = new Intent(this, ActivityUsageDaily.class);
                startActivity(it);
            }
            break;

            case R.id.btn_usage_monthly: {
                Intent it = new Intent(this, ActivityUsageMonthly.class);
                startActivity(it);
            }
            break;

            case R.id.btn_usage_yearly: {
                Intent it = new Intent(this, ActivityUsageYearly.class);
                startActivity(it);
            }
            break;

            case R.id.btn_set_alarm_info: {
                Intent it = new Intent(this, ActivitySetting.class);
                it.putExtra("to", "alarm_setting");
                startActivity(it);
            }
            break;

            case R.id.btn_set_discount_info: {
                Intent it = new Intent(this, ActivitySetting.class);
                it.putExtra("to", "discount_setting");
                startActivity(it);
            }
            break;

            case R.id.btn_clause_service: {
                Intent it = new Intent(this, ActivityWeb.class);
                it.putExtra("title", "서비스 이용약관");
                it.putExtra("url", "https://www.egservice.co.kr/EgService/EG_이용약관_2019_11_04.htm");
                startActivity(it);
            }
            break;

            case R.id.btn_clause_personal_info: {
                Log.i("Subscribe", "btn_clause_2 clicked");
                Intent it = new Intent(this, ActivityWeb.class);
                it.putExtra("title", "개인정보 수집이용");
                it.putExtra("url", "https://www.egservice.co.kr/EgService/EG_개인정보_2019_11_04.htm");
                startActivity(it);
            }
            break;

            case R.id.cl4: {
                Intent it = new Intent(this, ActivitySiteState.class);
                it.putExtra("to", "trans_state");
                startActivity(it);
            }
            break;

            case R.id.usage_area_a: {
                Intent it = new Intent(this, ActivityUsageDetail.class);
                startActivity(it);
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
            case CaEngine.CB_GET_USAGE_OF_ONE_METER: {
                Log.i("ActivityAck", "Result of GetUsageOfOneMeter received...");

                try {
                    JSONObject jo = Result.object;

                    double dWon=jo.getDouble("won_curr");
                    CaApplication.m_Info.m_wonCurr = dWon;
                    TextView tvWon=findViewById(R.id.tv_won);
                    tvWon.setText(CaApplication.m_Info.m_dfWon.format(dWon));

                    double dKwh=jo.getDouble("kwh_curr");
                    CaApplication.m_Info.m_kwhCurr= dKwh;
                    TextView tvKwh=findViewById(R.id.tv_kwh);
                    tvKwh.setText("[사용량 : " + CaApplication.m_Info.m_dfKwh.format(dKwh) + " kWh]");

                    String strTimeUpdate=jo.getString("to_curr");
                    Date dtUpdate=CaApplication.m_Info.m_dfyyyyMMddhhmm.parse(strTimeUpdate);

                    TextView tvUpdate=findViewById(R.id.tv_time_update);
                    tvUpdate.setText(CaApplication.m_Info.m_dfyyyyMMddhhmm_ampm.format(dtUpdate));
                    CaApplication.m_Info.m_dtUpdate = CaApplication.m_Info.m_dfyyyyMMddhhmm_ampm.format(dtUpdate);

                    double dWonExpected=jo.getDouble("won_expected");
                    CaApplication.m_Info.m_wonExpected = dWonExpected;
                    TextView tvWonExpected=findViewById(R.id.tv_won_expected);
                    tvWonExpected.setText(CaApplication.m_Info.m_dfWon.format(dWonExpected));

                    double dWonPrevMonth=jo.getDouble("won_prev_month");
                    double dWonPrevYear=jo.getDouble("won_prev_year");
                    CaApplication.m_Info.m_wonPrevMonth = dWonPrevMonth;
                    CaApplication.m_Info.m_wonPrevYear = dWonPrevYear;

                    setDeltaWonPrevMonth(dWon - dWonPrevMonth);
                    setDeltaWonPrevYear(dWon - dWonPrevYear);

                    TextView tvPriceLevel=findViewById(R.id.tv_price_level);
                    tvPriceLevel.setText(jo.getInt("price_level")+"구간");

                    TextView tvDayNextPriceLevel=findViewById(R.id.tv_day_next_price_level);
                    tvDayNextPriceLevel.setText(jo.getString("next_price_level_info"));

                    double dKwhDailyAvg=jo.getDouble("kwh_daily_avg");
                    TextView tvKwhDailyAvg=findViewById(R.id.tv_kwh_daily_avg);
                    tvKwhDailyAvg.setText(CaApplication.m_Info.m_dfKwh.format(dKwhDailyAvg));

                    CaApplication.m_Info.m_nTransState=jo.getInt("trans_state");
                    setTransState(CaApplication.m_Info.m_nTransState);


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

    void setDeltaWonPrevMonth(double dDeltaWon) {

        TextView tv=findViewById(R.id.tv_delta_won_prev_month);
        tv.setText(CaApplication.m_Info.m_dfWon.format(Math.abs(dDeltaWon)));

        ImageView iv=findViewById(R.id.iv_delta_won_prev_month);
        if (dDeltaWon>0) {
            iv.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up));
        }
        else if (dDeltaWon<0) {
            iv.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
        }
        else {
            iv.setImageDrawable(getResources().getDrawable(R.drawable.circle_cyan_light));
        }
    }

    void setDeltaWonPrevYear(double dDeltaWon) {

        TextView tv=findViewById(R.id.tv_delta_won_prev_year);
        tv.setText(CaApplication.m_Info.m_dfWon.format(Math.abs(dDeltaWon)));

        ImageView iv=findViewById(R.id.iv_delta_won_prev_year);
        if (dDeltaWon>0) {
            iv.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up));
        }
        else if (dDeltaWon<0){
            iv.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
        }
        else {
            iv.setImageDrawable(getResources().getDrawable(R.drawable.circle_cyan_light));
        }
    }

    void setTransState(int nTransState) {
        ImageView iv=findViewById(R.id.iv_trans_state);

        switch (nTransState) {
            case 0: {
                iv.setImageDrawable(getResources().getDrawable(R.drawable.trans_normal));
            }
            break;

            case 1: {
                iv.setImageDrawable(getResources().getDrawable(R.drawable.trans_warning));
            }
            break;

            case 2: {
                iv.setImageDrawable(getResources().getDrawable(R.drawable.trans_danger));
            }
            break;

            default: {
                iv.setImageDrawable(getResources().getDrawable(R.drawable.trans_unknown));
            }
            break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (ServiceMonitor.s_Intent != null) {
            stopService(ServiceMonitor.s_Intent);
            ServiceMonitor.s_Intent=null;
        }
    }
}
