package com.enernet.eg.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.enernet.eg.CaApplication;
import com.enernet.eg.CaEngine;
import com.enernet.eg.CaResult;
import com.enernet.eg.EgDialog;
import com.enernet.eg.EgDialogYn;
import com.enernet.eg.IaResultHandler;
import com.enernet.eg.MySpinnerAdapter;
import com.enernet.eg.R;
import com.enernet.eg.StringUtil;
import com.enernet.eg.model.CaItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityCandidate extends BaseActivity implements IaResultHandler {

    public String m_strSiteName;
    public String m_strDongName;
    public String m_strHoName;

    public int m_nSeqSite;
    public int m_nSeqDong;

    private EditText m_etName;
    private EditText m_etPhone;

    private EgDialog m_dlgSubscribedAlready;
    private EgDialog m_dlgOccupiedByOther;
    private EgDialog m_dlgMainMemberNotSubscribed;
    private EgDialogYn m_dlgYnSubscribeAsFamily;
    private EgDialog m_dlgError;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate);

        m_etName = findViewById(R.id.et_name);
        m_etPhone = findViewById(R.id.et_phone);

        getPhoneNumberAfterCheckPermission();

        CaApplication.m_Engine.GetSiteList(this, this);

    }

    public void getPhoneNumberAfterCheckPermission() {
        Log.e("ActivityCandidate", "getPhoneNumberAfterCheckPermission called..1");

        int nRes= ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        Log.e("ActivityCandidate", "getPhoneNumberAfterCheckPermission nRes="+nRes);

        if (nRes== PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_PHONE_STATE}, 3186);
            Log.e("ActivityCandidate", "requestPermissions called");
        }
        else {
            String strPhone=CaApplication.m_Info.getPhoneNumber();
            m_etPhone.setText(strPhone);
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;

            case R.id.btn_start_auth: {
                Log.i("ActivityCandidate", "btn_start_auth clicked");

                CaApplication.m_Info.m_strMemberNameSubscribing = m_etName.getText().toString();
                String strPhoneRaw = m_etPhone.getText().toString();
                CaApplication.m_Info.m_strPhoneSubscribing = StringUtil.removeNonDigitChars(strPhoneRaw);

                String strMessage="";

                if (CaApplication.m_Info.m_nSeqAptHoSubscribing==0) {
                    strMessage = "거주하는 아파트의 동과 호를 선택해 주세요";
                }
                else {
                    if (CaApplication.m_Info.m_strMemberNameSubscribing.isEmpty()) {
                        strMessage = "이름을 입력해 주세요";
                    }
                    else {
                        if (CaApplication.m_Info.m_strPhoneSubscribing.isEmpty()) {
                            strMessage = "휴대폰 번호를 입력해 주세요";
                        }

                    }
                }

                if (strMessage.isEmpty()) {
                    CaApplication.m_Engine.GetMemberCandidateInfo(CaApplication.m_Info.m_nSeqAptHoSubscribing,
                            CaApplication.m_Info.m_strMemberNameSubscribing,
                            CaApplication.m_Info.m_strPhoneSubscribing, this, this);
                }
                else {
                    View.OnClickListener LsnConfirm=new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i("ActivityCandidate", "확인 button clicked...");
                            m_dlgError.dismiss();

                        }
                    };

                    m_dlgError=new EgDialog(this, R.layout.dialog02,strMessage, LsnConfirm);
                    m_dlgError.show();
                }
            }
            break;

        }
    }

    ///////////////////////////
    @Override
    public void onResult(CaResult Result) {
        switch (Result.m_nCallback) {
            case CaEngine.CB_GET_SITE_LIST: {
                try {

                    ArrayList<String> planets = new ArrayList<>();

                    JSONArray ja = Result.object.getJSONArray("site_list");
                    final ArrayList<CaItem> items = new ArrayList<>();

                    for (int i=0; i<ja.length(); i++) {
                        JSONObject jo=ja.getJSONObject(i);
                        CaItem item=new CaItem();
                        item.m_nSeq=jo.getInt("seq_site");
                        item.m_strName=jo.getString("name");
                        items.add(item);

                        planets.add(item.m_strName);
                    }

                    Spinner sp=findViewById(R.id.sp_site);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, planets);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    MySpinnerAdapter spa=new MySpinnerAdapter(adapter, R.layout.contact_spinner_row_nothing_selected, this);
                    spa.m_strPrompt="단지 선택";

                    sp.setAdapter(spa);
                    sp.setSelection(0, false);
                    sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

                            if (position!=0) position-=1;
                            m_strSiteName = items.get(position).m_strName;
                            CaApplication.m_Info.m_strSiteName=m_strSiteName;
                            m_nSeqSite = items.get(position).m_nSeq;
                            Log.i("ActivityCandidate", "SiteSpinner selected="+m_strSiteName + " (" + m_nSeqSite + "), position=" + position);
                            ((TextView)parent.getChildAt(0)).setTextColor(Color.WHITE);
                            CaApplication.m_Info.m_nSeqAptHoSubscribing = 0; // to prevent mismatch on submission

                            CaApplication.m_Engine.GetAptDongList(m_nSeqSite, ActivityCandidate.this, ActivityCandidate.this);

                        }


                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {

                        }
                    });

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            break;

            case CaEngine.CB_GET_APT_DONG_LIST: {
                try {
                    Log.i("ActivityCandidate", "Dong list received");

                    ArrayList<String> planets = new ArrayList<>();

                    JSONArray ja = Result.object.getJSONArray("dong_list");
                    final ArrayList<CaItem> items = new ArrayList<>();

                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject jo = ja.getJSONObject(i);
                        CaItem item = new CaItem();
                        item.m_nSeq = jo.getInt("seq_dong");
                        item.m_strName = jo.getString("dong_name");
                        items.add(item);

                        planets.add(item.m_strName);
                    }

                    Spinner sp=findViewById(R.id.sp_dong);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, planets);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    MySpinnerAdapter spa=new MySpinnerAdapter(adapter, R.layout.contact_spinner_row_nothing_selected, this);
                    spa.m_strPrompt="동 선택";

                    sp.setAdapter(spa);
                    sp.setSelection(0, false);
                    sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

                            if (position!=0) position-=1;
                            m_strDongName = items.get(position).m_strName;
                            CaApplication.m_Info.m_strAptDongName=m_strDongName;
                            m_nSeqDong = items.get(position).m_nSeq;
                            Log.i("ActivityCandidate", "DongSpinner selected="+m_strDongName + " (" + m_nSeqDong + "), position=" + position);
                            ((TextView)parent.getChildAt(0)).setTextColor(Color.WHITE);
                            CaApplication.m_Info.m_nSeqAptHoSubscribing = 0; // to prevent mismatch on submission

                            CaApplication.m_Engine.GetAptHoList(m_nSeqSite, m_nSeqDong, ActivityCandidate.this, ActivityCandidate.this);

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            break;

            case CaEngine.CB_GET_APT_HO_LIST: {
                try {
                    Log.i("ActivityCandidate", "Ho list received");

                    ArrayList<String> planets = new ArrayList<>();

                    JSONArray ja = Result.object.getJSONArray("ho_list");
                    final ArrayList<CaItem> items = new ArrayList<>();

                    for (int i=0; i<ja.length(); i++) {
                        JSONObject jo=ja.getJSONObject(i);
                        CaItem item=new CaItem();
                        item.m_nSeq=jo.getInt("seq_ho");
                        item.m_strName=jo.getString("ho_name");
                        items.add(item);

                        planets.add(item.m_strName);
                    }

                    Spinner sp=findViewById(R.id.sp_ho);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, planets);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    MySpinnerAdapter spa=new MySpinnerAdapter(adapter, R.layout.contact_spinner_row_nothing_selected, this);
                    spa.m_strPrompt="호 선택";

                    sp.setAdapter(spa);
                    sp.setSelection(0, false);
                    sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

                            if (position!=0) position-=1;
                            m_strHoName = items.get(position).m_strName;
                            CaApplication.m_Info.m_strAptHoName=m_strHoName;
                            CaApplication.m_Info.m_nSeqAptHoSubscribing = items.get(position).m_nSeq;
                            ((TextView)parent.getChildAt(0)).setTextColor(Color.WHITE);
                            Log.i("ActivityCandidate", "HoSpinner selected="+m_strHoName
                                    + " (" + CaApplication.m_Info.m_nSeqAptHoSubscribing + "), position=" + position);

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {

                        }
                    });
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            break;

            case CaEngine.CB_GET_MEMBER_CANDIDATE_INFO: {
                try {
                    JSONObject jo = Result.object;
                    boolean bCandidate = (jo.getInt("is_candidate")==1);
                    int nHoState=jo.getInt("ho_state");

                    if (bCandidate) {
                        if (nHoState==1) {
                            Log.i("ActivityCandidate", "동의서에 명시된 세대 대표이며 아직 미등록 상태");

                            CaApplication.m_Info.m_bSubscribingAsMainMember=true;
                            CaApplication.m_Info.m_nAuthType=CaEngine.AUTH_TYPE_SUBSCRIBE;

                            finish();
                            Intent nextIntent = new Intent(this, ActivityAuth.class);
                            startActivity(nextIntent);
                        }
                        else if (nHoState==2) {
                            Log.i("ActivityCandidate", "동일 정보로 이미 등록됨. 로그인 창으로.");

                            View.OnClickListener LsnConfirm=new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.i("ActivityCandidate", "확인 button clicked...");
                                    m_dlgSubscribedAlready.dismiss();

                                    finish();
                                }
                            };

                            m_dlgSubscribedAlready=new EgDialog(this, R.layout.dialog02,"세대 대표로 이미 등록되어 있습니다. 로그인하세요.", LsnConfirm);
                            m_dlgSubscribedAlready.show();

                        }
                        else if (nHoState==3) {
                            Log.i("ActivityCandidate", "다른분이 세대 대표로 이미 등록되어 있슴. 관리실 문의");

                            View.OnClickListener LsnConfirm=new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.i("ActivityCandidate", "확인 button clicked...");
                                    m_dlgOccupiedByOther.dismiss();

                                }
                            };

                            m_dlgOccupiedByOther=new EgDialog(this, R.layout.dialog02,"다른분이 세대 대표로 이미 등록되어 있습니다. 관리실에 문의하세요.", LsnConfirm);
                            m_dlgOccupiedByOther.show();
                        }
                        else {
                            Log.i("ActivityCandidate", "Unknown nHoState = " + nHoState);
                        }
                    }
                    else {
                        if (nHoState==1) {
                            Log.i("ActivityCandidate", "세대 대표 먼저 등록해야함");

                            View.OnClickListener LsnConfirm=new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.i("ActivityCandidate", "확인 button clicked...");
                                    m_dlgMainMemberNotSubscribed.dismiss();

                                }
                            };

                            m_dlgMainMemberNotSubscribed=new EgDialog(this, R.layout.dialog02,"세대 대표자의 가입 이후에 가입하시기 바랍니다.", LsnConfirm);
                            m_dlgMainMemberNotSubscribed.show();
                        }
                        else if (nHoState==3) {
                            Log.i("ActivityCandidate", "세대원으로 가입 진행");

                            CaApplication.m_Info.m_bSubscribingAsMainMember=false;
                            CaApplication.m_Info.m_nAuthType=CaEngine.AUTH_TYPE_SUBSCRIBE;

                            View.OnClickListener LsnConfirmYes=new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.i("ActivityCandidate", "Yes button clicked...");
                                    m_dlgYnSubscribeAsFamily.dismiss();

                                    finish();

                                    final Context Ctx=getApplicationContext();
                                    final Class Clazz=ActivityAuth.class;

                                    Intent nextIntent = new Intent(Ctx, Clazz);
                                    startActivity(nextIntent);

                                }
                            };

                            View.OnClickListener LsnConfirmNo=new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.i("ActivityCandidate", "No button clicked...");
                                    m_dlgYnSubscribeAsFamily.dismiss();
                                }
                            };

                            m_dlgYnSubscribeAsFamily=new EgDialogYn(this, R.layout.dialog01yn,"본인인증을 하고 세대원으로 가입하시겠습니까?", LsnConfirmYes, LsnConfirmNo);
                            m_dlgYnSubscribeAsFamily.show();


                        }
                        else {

                        }


                    }

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            break;



        } // end if switch

    }

}
