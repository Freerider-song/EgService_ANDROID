package com.enernet.eg.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.enernet.eg.CaApplication;
import com.enernet.eg.CaEngine;
import com.enernet.eg.CaInfo;
import com.enernet.eg.CaPref;
import com.enernet.eg.CaResult;
import com.enernet.eg.EgDialog;
import com.enernet.eg.EgDialogLogout;
import com.enernet.eg.EgDialogYn;
import com.enernet.eg.EgUsageAlarmOption;
import com.enernet.eg.IaResultHandler;
import com.enernet.eg.R;
import com.enernet.eg.StringUtil;
import com.enernet.eg.model.CaDiscount;
import com.enernet.eg.model.CaFamily;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivitySetting extends BaseActivity implements IaResultHandler {

    private  boolean m_bFinishWhenChangeSaved=false;

    private EgDialogLogout m_dlgLogout;
    private EgDialogYn m_dlgYnSettingChanged;
    private EgDialogYn m_dlgYnAckCancel;
    private EgDialog m_dlgInfo;
    private EgUsageAlarmOption m_dlgUsageAlarmOption;

    private boolean m_bAlarmAll=CaApplication.m_Info.m_bNotiAll;
    private boolean m_bAlarmKwh=CaApplication.m_Info.m_bNotiKwh;
    private boolean m_bAlarmWon=CaApplication.m_Info.m_bNotiWon;
    private boolean m_bAlarmPriceLevel=CaApplication.m_Info.m_bNotiPriceLevel;
    private boolean m_bAlarmUsageAtTime=CaApplication.m_Info.m_bNotiUsageAtTime;

    private int m_nDiscountFamily=CaApplication.m_Info.m_nDiscountFamily;
    private int m_nDiscountSocial=CaApplication.m_Info.m_nDiscountSocial;

    private double m_dThresholdKwh=CaApplication.m_Info.m_dThresholdKwh;
    private double m_dThresholdWon=CaApplication.m_Info.m_dThresholdWon;
    private int m_nNextPriceLevelPercent=CaApplication.m_Info.m_nNextPriceLevelPercent;
    private int m_nUsageNotiType=CaApplication.m_Info.m_nUsageNotiType;
    private int m_nUsageNotiHour=CaApplication.m_Info.m_nUsageNotiHour;

    private Spinner m_spDiscountFamily;
    private Spinner m_spDiscountSocial;

    private FamilyAdapter m_FamilyAdapter;

    private class ViewHolder {
        public int m_nSeqMember;
        public String m_strName;
        public TextView m_tvName;
        public TextView m_tvPhone;
        public TextView m_tvLastLogin;
        public Button m_btnReject;
    }

    private class FamilyAdapter extends BaseAdapter {

        public FamilyAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return CaApplication.m_Info.m_alFamily.size();
        }

        @Override
        public Object getItem(int position) {
            return CaApplication.m_Info.m_alFamily.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.eg_list_item_family, null);

                holder.m_tvName = convertView.findViewById(R.id.tv_name);
                holder.m_tvPhone = convertView.findViewById(R.id.tv_phone);
                holder.m_tvLastLogin=convertView.findViewById(R.id.tv_time_last_login);
                holder.m_btnReject=convertView.findViewById(R.id.btn_reject);

                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            final CaFamily family = CaApplication.m_Info.m_alFamily.get(position);

            holder.m_nSeqMember=family.m_nSeqMember;
            holder.m_strName=family.m_strName;
            holder.m_tvName.setText(family.m_strName);

            String strPhone=CaInfo.getDecoPhoneNumber(family.m_strPhone);
            holder.m_tvPhone.setText(strPhone);
            holder.m_tvLastLogin.setText(family.getLastLoginTime());

            if (CaApplication.m_Info.m_bMainMember) {
                holder.m_btnReject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.i("Setting", "Reject Button clicked...SeqMember="+family.m_nSeqMember+", Name="+family.m_strName);

                        processAckCancel(family.m_nSeqMember, family.m_strName);
                    }
                });

            }
            else {
                holder.m_btnReject.setVisibility(View.INVISIBLE);
            }


            return convertView;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("Setting", "onCreate called...");

        setContentView(R.layout.activity_setting);

        prepareDrawer();

        TextView tvId=findViewById(R.id.tv_1_id);
        TextView tvDateChangePassword=findViewById(R.id.tv_1_date_password_change);
        TextView tvName=findViewById(R.id.tv_1_name);
        TextView tvPhone=findViewById(R.id.tv_1_phone);
        TextView tvSite=findViewById(R.id.tv_1_site);
        TextView tvDong=findViewById(R.id.tv_1_dong);
        TextView tvHo=findViewById(R.id.tv_1_ho);
        EditText etThresholdKwh=findViewById(R.id.et_alarm_kwh);
        EditText etThresholdWon=findViewById(R.id.et_alarm_won);
        EditText etPriceLevel=findViewById(R.id.et_alarm_price_level);
        Button btnAlarmUsage=findViewById(R.id.btn_alarm_usage);

        tvId.setText(CaApplication.m_Info.m_strMemberId);
        tvDateChangePassword.setText(CaApplication.m_Info.m_dfyyyyMMddhhmmStd.format(CaApplication.m_Info.m_dtChangePassword));
        tvName.setText(CaApplication.m_Info.m_strMemberName);

        String strPhone= CaInfo.getDecoPhoneNumber(CaApplication.m_Info.m_strPhone);
        tvPhone.setText(strPhone);
        tvSite.setText(CaApplication.m_Info.m_strSiteName);
        tvDong.setText(CaApplication.m_Info.m_strAptDongName);
        tvHo.setText(CaApplication.m_Info.m_strAptHoName);
        etThresholdKwh.setText(CaApplication.m_Info.m_dfKwh.format(CaApplication.m_Info.m_dThresholdKwh));
        etThresholdWon.setText(CaApplication.m_Info.m_dfWon.format(CaApplication.m_Info.m_dThresholdWon));
        etPriceLevel.setText(Integer.toString(CaApplication.m_Info.m_nNextPriceLevelPercent));
        btnAlarmUsage.setText(CaApplication.m_Info.getAtAlarmDesc());

        setAlarmInfo();

        // discount family
        m_spDiscountFamily=findViewById(R.id.sp_discount_family);

        final List<String> alDiscountNameFamily = new ArrayList<>();

        for (int i=0; i<CaApplication.m_Info.m_alDiscountFamily.size(); i++) {
            CaDiscount ds=CaApplication.m_Info.m_alDiscountFamily.get(i);
            alDiscountNameFamily.add(ds.m_strDiscountName);
        }

        ArrayAdapter<String> AdapterFamily;
        if (CaApplication.m_Info.m_bMainMember) {
            AdapterFamily=new ArrayAdapter<>(this, R.layout.eg_spinner_style, alDiscountNameFamily);
            m_spDiscountFamily.setEnabled(true);
        }
        else {
            AdapterFamily=new ArrayAdapter<>(this, R.layout.eg_spinner_style_disabled, alDiscountNameFamily);
            m_spDiscountFamily.setEnabled(false);
        }

        m_spDiscountFamily.setAdapter(AdapterFamily);
        AdapterFamily.setDropDownViewResource(R.layout.eg_spinner_item_style);
        m_spDiscountFamily.setSelection(CaApplication.m_Info.m_nDiscountFamily);

        m_spDiscountFamily.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                m_nDiscountFamily=position;
                Log.i("DiscountFamily", "Selected="+alDiscountNameFamily.get(position)+", position="+position+", id="+id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // discount social
        m_spDiscountSocial=findViewById(R.id.sp_discount_social);

        final List<String> alDiscountNameSocial = new ArrayList<>();

        for (int i=0; i<CaApplication.m_Info.m_alDiscountSocial.size(); i++) {
            CaDiscount ds=CaApplication.m_Info.m_alDiscountSocial.get(i);
            alDiscountNameSocial.add(ds.m_strDiscountName);
        }

        ArrayAdapter<String> AdapterSocial;
        if (CaApplication.m_Info.m_bMainMember) {
            AdapterSocial=new ArrayAdapter<>(this, R.layout.eg_spinner_style, alDiscountNameSocial);
            m_spDiscountSocial.setEnabled(true);
        }
        else {
            AdapterSocial=new ArrayAdapter<>(this, R.layout.eg_spinner_style_disabled, alDiscountNameSocial);
            m_spDiscountSocial.setEnabled(false);
        }

        m_spDiscountSocial.setAdapter(AdapterSocial);
        AdapterSocial.setDropDownViewResource(R.layout.eg_spinner_item_style);
        m_spDiscountSocial.setSelection(CaApplication.m_Info.m_nDiscountSocial);

        m_spDiscountSocial.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                m_nDiscountSocial=position;
                Log.i("DiscountSocial", "Selected="+alDiscountNameSocial.get(position)+", position="+position+", id="+id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //////////////////
        String strSiteName="단지명 : "+CaApplication.m_Info.m_strSiteName;
        TextView tvSiteName=findViewById(R.id.tv_site_name);
        tvSiteName.setText(strSiteName);

        String strDesc="우리 아파트 검침일 : 매달 "+CaApplication.m_Info.m_nSiteReadDay+"일";
        TextView tvReadDay=findViewById(R.id.tv_read_day);
        tvReadDay.setText(strDesc);

        if (CaApplication.m_Info.m_nSiteReadDay==1) strDesc="* 요금계산 기간 : 지난달 1일 ~ 지난달 말일";
        else strDesc="* 요금계산 기간 : 지난달 "+ CaApplication.m_Info.m_nSiteReadDay +"일 ~ 이번달 " + (CaApplication.m_Info.m_nSiteReadDay-1)+"일";

        TextView tvReadPeriod=findViewById(R.id.tv_read_period);
        tvReadPeriod.setText(strDesc);

        // family list view

        ListView lv=findViewById(R.id.lv_family);

        m_FamilyAdapter=new FamilyAdapter();

        lv.setAdapter(m_FamilyAdapter);

        /*
        // not working because of the button in the list view item
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("ListFamily", "Item clicked, pos="+position);
            }
        });
        */
        final View header = getLayoutInflater().inflate(R.layout.eg_list_header_family, null, false);
        lv.addHeaderView(header);
        //////////////////////////////////////
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.i("Setting", "onStart called...");
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i("Setting", "onResume called...");

    }

    @Override
    public void onPostResume() {
        super.onPostResume();
        Log.i("Setting", "onPostResume called...");
    }

    @Override
    public void onRestart() {
        super.onRestart();

        Log.i("Setting", "onRestart called...");
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.i("Setting", "onPause called...");
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.i("Setting", "onStop called...");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i("Setting", "onDestroy called...");
    }

    @Override
    public void onBackPressed() {
        if (m_Drawer.isDrawerOpen()) {
            m_Drawer.closeDrawer();
        }
        else {
            if (isSettingChanged()) {
                m_bFinishWhenChangeSaved=true;
                processSettingChange();
            }
            else {
                finish();
            }
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

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back: {
                if (isSettingChanged()) {
                    m_bFinishWhenChangeSaved=true;
                    processSettingChange();
                }
                else {
                    finish();
                }
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

            case R.id.btn_save: {
                if (isSettingChanged()) {
                    m_bFinishWhenChangeSaved=false;
                    processSettingChange();
                }
                else {
                    ////////////////////////////
                    View.OnClickListener LsnConfirm = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i("Setting", "확인 button clicked...");
                            m_dlgInfo.dismiss();

                        }
                    };

                    m_dlgInfo = new EgDialog(this, R.layout.dialog01, "변경된 설정값이 없습니다.", LsnConfirm);
                    m_dlgInfo.show();
                    ////////////////////////////
                }

            }
            break;

            case R.id.btn_change_password_setting: {
                Intent it = new Intent(this, ActivityChangePasswordAuth.class);
                startActivity(it);
            }
            break;

            case R.id.btn_logout: {

                final Context Ctx=getApplicationContext();

                View.OnClickListener LsnConfirmYes=new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                          m_dlgLogout.dismiss();

                        CaPref pref = new CaPref(Ctx);

                        pref.setValue(CaPref.PREF_MEMBER_ID, "");
                        pref.setValue(CaPref.PREF_PASSWORD, "");

                        final Class Clazz=ActivityLogin.class;

                        Intent nextIntent = new Intent(Ctx, Clazz);
                        startActivity(nextIntent);
                    }
                };

                View.OnClickListener LsnConfirmNo=new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        m_dlgLogout.dismiss();
                    }
                };

                m_dlgLogout=new EgDialogLogout(this, LsnConfirmYes, LsnConfirmNo);

                // setOnCancelListener not working
                /*
                m_dlgLogout.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        m_dlgLogout.dismiss();
                    }
                });
                */

                m_dlgLogout.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode==KeyEvent.KEYCODE_BACK) {
                            dialog.dismiss();
                            return true;
                        }
                        return false;
                    }
                });

                m_dlgLogout.show();
            }
            break;

            case R.id.ib_alarm_all: {
                m_bAlarmAll=!m_bAlarmAll;
                setAlarmInfo();
            }
            break;

            case R.id.ib_alarm_kwh: {
                m_bAlarmKwh=!m_bAlarmKwh;
                setAlarmInfo();
            }
            break;

            case R.id.ib_alarm_won: {
                m_bAlarmWon=!m_bAlarmWon;
                setAlarmInfo();
            }
            break;

            case R.id.ib_alarm_price_level: {
                m_bAlarmPriceLevel=!m_bAlarmPriceLevel;
                setAlarmInfo();
            }
            break;

            case R.id.ib_alarm_usage_at_time: {
                m_bAlarmUsageAtTime=!m_bAlarmUsageAtTime;
                setAlarmInfo();
            }
            break;

            case R.id.btn_alarm_usage: {

                View.OnClickListener LsnConfirmYes=new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        m_dlgUsageAlarmOption.dismiss();

                        m_nUsageNotiType=m_dlgUsageAlarmOption.m_nUsageNotiType;
                        m_nUsageNotiHour=m_dlgUsageAlarmOption.m_nUsageNotiHour;

                        String strUsageDesc=CaApplication.m_Info.getAtAlarmDesc(m_nUsageNotiType, m_nUsageNotiHour);
                        Button btnAlarmUsage=findViewById(R.id.btn_alarm_usage);
                        btnAlarmUsage.setText(strUsageDesc);
                    }
                };

                View.OnClickListener LsnConfirmNo=new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        m_dlgUsageAlarmOption.dismiss();
                    }
                };

                m_dlgUsageAlarmOption=new EgUsageAlarmOption(this, "사용량 알림 방식을 선택하세요", LsnConfirmYes, LsnConfirmNo);
                m_dlgUsageAlarmOption.show();
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
            case CaEngine.CB_CHANGE_MEMBER_SETTINS: {
                Log.i("Setting", "Result of ChangeMemberSettings received...");

                try {
                    JSONObject jo = Result.object;
                    int nSeqMember = jo.getInt("seq_member");

                    if (nSeqMember == 0) {

                        View.OnClickListener LsnConfirm = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.i("Setting", "확인 button clicked...");
                                m_dlgInfo.dismiss();

                                if (m_bFinishWhenChangeSaved) finish();
                            }
                        };

                        m_dlgInfo = new EgDialog(this, R.layout.dialog01, "설정값 저장에 실패했습니다", LsnConfirm);
                        m_dlgInfo.show();

                    }
                    else {
                        View.OnClickListener LsnConfirm = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.i("Setting", "확인 button clicked...");
                                m_dlgInfo.dismiss();

                                CaApplication.m_Info.m_bNotiAll=m_bAlarmAll;
                                CaApplication.m_Info.m_bNotiKwh=m_bAlarmKwh;
                                CaApplication.m_Info.m_bNotiWon=m_bAlarmWon;
                                CaApplication.m_Info.m_bNotiPriceLevel=m_bAlarmPriceLevel;
                                CaApplication.m_Info.m_bNotiUsageAtTime=m_bAlarmUsageAtTime;
                                CaApplication.m_Info.m_nDiscountFamily=m_nDiscountFamily;
                                CaApplication.m_Info.m_nDiscountSocial=m_nDiscountSocial;
                                CaApplication.m_Info.m_dThresholdKwh=m_dThresholdKwh;
                                CaApplication.m_Info.m_dThresholdWon=m_dThresholdWon;
                                CaApplication.m_Info.m_nNextPriceLevelPercent=m_nNextPriceLevelPercent;
                                CaApplication.m_Info.m_nUsageNotiType=m_nUsageNotiType;
                                CaApplication.m_Info.m_nUsageNotiHour=m_nUsageNotiHour;

                                if (m_bFinishWhenChangeSaved) finish();
                            }
                        };

                        m_dlgInfo = new EgDialog(this, R.layout.dialog01, "설정값 저장에 성공했습니다", LsnConfirm);
                        m_dlgInfo.show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            break;

            case CaEngine.CB_RESPONSE_ACK_MEMBER: {
                Log.i("Setting", "Result of ResponseAckMember received...");

                try {
                    JSONObject jo = Result.object;

                    int nResultCode = jo.getInt("result_code");
                    if (nResultCode == 1) {
                        int nAckType=jo.getInt("ack_type"); // 1=승인, 2=거절, 3=철회

                        if (nAckType==3) {
                            int nSeqMemberSub = jo.getInt("seq_member_sub");
                         //   String strNameSub = jo.getString("name_sub");

                            if (CaApplication.m_Info.removeFamilyMember(nSeqMemberSub)) {
                                m_FamilyAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                } catch(JSONException e){
                        e.printStackTrace();
                }

            }
            break;

            default: {
                Log.i("Setting", "Unknown type result received : " + Result.m_nCallback);
            }
            break;

        } // end of switch
    }

    private void setAlarmInfo() {
        ImageButton ibAlarmAll=findViewById(R.id.ib_alarm_all);
        ImageButton ibAlarmKwh=findViewById(R.id.ib_alarm_kwh);
        ImageButton ibAlarmWon=findViewById(R.id.ib_alarm_won);
        ImageButton ibAlarmPriceLevel=findViewById(R.id.ib_alarm_price_level);
        ImageButton ibAlarmUsageAtTime=findViewById(R.id.ib_alarm_usage_at_time);

        if (m_bAlarmAll) {

            ibAlarmAll.setImageResource(R.drawable.check_yes);

            if (m_bAlarmKwh) ibAlarmKwh.setImageResource(R.drawable.check_yes);
            else ibAlarmKwh.setImageResource(R.drawable.check_no);

            if (m_bAlarmWon) ibAlarmWon.setImageResource(R.drawable.check_yes);
            else ibAlarmWon.setImageResource(R.drawable.check_no);

            if (m_bAlarmPriceLevel) ibAlarmPriceLevel.setImageResource(R.drawable.check_yes);
            else ibAlarmPriceLevel.setImageResource(R.drawable.check_no);

            if (m_bAlarmUsageAtTime) ibAlarmUsageAtTime.setImageResource(R.drawable.check_yes);
            else ibAlarmUsageAtTime.setImageResource(R.drawable.check_no);
        }
        else {
            ibAlarmAll.setImageResource(R.drawable.check_no);

            if (m_bAlarmKwh) ibAlarmKwh.setImageResource(R.drawable.check_disabled_yes);
            else ibAlarmKwh.setImageResource(R.drawable.check_disabled_no);

            if (m_bAlarmWon) ibAlarmWon.setImageResource(R.drawable.check_disabled_yes);
            else ibAlarmWon.setImageResource(R.drawable.check_disabled_no);

            if (m_bAlarmPriceLevel) ibAlarmPriceLevel.setImageResource(R.drawable.check_disabled_yes);
            else ibAlarmPriceLevel.setImageResource(R.drawable.check_disabled_no);

            if (m_bAlarmUsageAtTime) ibAlarmUsageAtTime.setImageResource(R.drawable.check_disabled_yes);
            else ibAlarmUsageAtTime.setImageResource(R.drawable.check_disabled_no);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        Log.i("Setting", "onWindowFocusChanged called...hasFocus="+hasFocus);

        if (hasFocus) {
            String strTo=getIntent().getStringExtra("to");
            if (strTo!=null) {
                if (strTo.equals("alarm_setting")) {
                    View view=findViewById(R.id.tv_area_2);
                    scrollToView(view);
                }
                else if (strTo.equals("discount_setting")) {
                    View view=findViewById(R.id.tv_area_3);
                    scrollToView(view);
                }
            }

        }
    }

    private boolean isSettingChanged() {
        if (m_bAlarmAll != CaApplication.m_Info.m_bNotiAll) return true;
        if (m_bAlarmKwh != CaApplication.m_Info.m_bNotiKwh) return true;
        if (m_bAlarmWon != CaApplication.m_Info.m_bNotiWon) return true;
        if (m_bAlarmPriceLevel != CaApplication.m_Info.m_bNotiPriceLevel) return true;
        if (m_bAlarmUsageAtTime != CaApplication.m_Info.m_bNotiUsageAtTime) return true;

        if (m_nDiscountFamily != CaApplication.m_Info.m_nDiscountFamily) return true;
        if (m_nDiscountSocial != CaApplication.m_Info.m_nDiscountSocial) return true;

        EditText etThresholdKwh=findViewById(R.id.et_alarm_kwh);
        String strThresholdKwh=etThresholdKwh.getText().toString();
        m_dThresholdKwh=Double.parseDouble(strThresholdKwh);

        EditText etThresholdWon=findViewById(R.id.et_alarm_won);
        String strThresholdWon=etThresholdWon.getText().toString();
        m_dThresholdWon=Double.parseDouble(StringUtil.removeNonDigitChars(strThresholdWon));

        EditText etPriceLevel=findViewById(R.id.et_alarm_price_level);
        String strPriceLevel=etPriceLevel.getText().toString();
        m_nNextPriceLevelPercent=Integer.parseInt(strPriceLevel);

        if (Math.abs(m_dThresholdKwh - CaApplication.m_Info.m_dThresholdKwh)>0.1) return true;
        if (Math.abs(m_dThresholdWon - CaApplication.m_Info.m_dThresholdWon)>0.1) return true;
        if (m_nNextPriceLevelPercent != CaApplication.m_Info.m_nNextPriceLevelPercent) return true;

        if (m_nUsageNotiType != CaApplication.m_Info.m_nUsageNotiType) return true;
        if (m_nUsageNotiHour != CaApplication.m_Info.m_nUsageNotiHour) return true;

        return false;
    }

    public void requestChangeMemberSettings() {
        CaApplication.m_Engine.ChangeMemberSettins(CaApplication.m_Info.m_nSeqMember,
                m_bAlarmAll, m_bAlarmKwh, m_bAlarmWon, m_bAlarmPriceLevel, m_bAlarmUsageAtTime,
                m_nDiscountFamily, m_nDiscountSocial,
                m_dThresholdKwh, m_dThresholdWon, m_nNextPriceLevelPercent, m_nUsageNotiType, m_nUsageNotiHour,
                this, this);
    }

    public void processSettingChange() {

        View.OnClickListener LsnConfirmYes=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Setting", "Yes button clicked...");
                m_dlgYnSettingChanged.dismiss();

                requestChangeMemberSettings();
            }
        };

        View.OnClickListener LsnConfirmNo=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Setting", "No button clicked...");
                m_dlgYnSettingChanged.dismiss();

                if (m_bFinishWhenChangeSaved) finish();
            }
        };

        m_dlgYnSettingChanged=new EgDialogYn(this, R.layout.dialog01yn,"변경한 설정값을 저장하시겠습니까?", LsnConfirmYes, LsnConfirmNo);

        m_dlgYnSettingChanged.show();
    }

    public void requestAckCancel(int nSeqMemberSub) {
        CaApplication.m_Engine.ResponseAckMember(nSeqMemberSub, 3, true, this, this);
    }

    public void processAckCancel(final int nSeqMemberSub, String strNameSub) {

        View.OnClickListener LsnConfirmYes=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Setting", "Yes button clicked...");
                m_dlgYnAckCancel.dismiss();

                requestAckCancel(nSeqMemberSub);
            }
        };

        View.OnClickListener LsnConfirmNo=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Setting", "No button clicked...");
                m_dlgYnAckCancel.dismiss();
            }
        };

        m_dlgYnAckCancel=new EgDialogYn(this, R.layout.dialog01yn,strNameSub + " 님의 승인을 철회하시겠습니까?", LsnConfirmYes, LsnConfirmNo);
        m_dlgYnAckCancel.show();
    }
}
