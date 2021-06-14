package com.enernet.eg.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.enernet.eg.CaApplication;
import com.enernet.eg.CaEngine;
import com.enernet.eg.CaResult;
import com.enernet.eg.EgDialog;
import com.enernet.eg.IaResultHandler;
import com.enernet.eg.R;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivitySubscribe extends BaseActivity implements IaResultHandler {

    private EditText m_etMemberId;
    private EditText m_etPasswordNew;
    private EditText m_etPasswordConfirm;

    private EgDialog m_dlgCheckPassword;
    private EgDialog m_dlgMemberMainCreated;
    private EgDialog m_dlgMemberSubCreated;
    private EgDialog m_dlgIdDuplication;
    private EgDialog m_dlgUnknownErrorCreatingMemberMain;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_member: {

                CheckBox cbClause1=findViewById(R.id.cb_clause_1);
                boolean bClause1=cbClause1.isChecked();

                CheckBox cbClause2=findViewById(R.id.cb_clause_2);
                boolean bClause2=cbClause2.isChecked();

                EditText etMemberId = findViewById(R.id.member_id);
                String strMemberId = etMemberId.getText().toString();

                EditText etPasswordNew = findViewById(R.id.member_password_new);
                String strPasswordNew = etPasswordNew.getText().toString();

                EditText etPasswordConfirm = findViewById(R.id.member_password_confirm);
                String strPasswordConfirm = etPasswordConfirm.getText().toString();

                String strMessage="";

                if (strMemberId.isEmpty()) {
                    strMessage = "아이디를 입력해 주세요";
                }
                else {

                    if (strPasswordNew.isEmpty() || strPasswordConfirm.isEmpty()) {
                        strMessage = "새 비밀번호와 확인 비밀번호를 모두 입력해 주세요";
                    }
                    else {
                        if (!strPasswordNew.equals(strPasswordConfirm)) {
                            strMessage = "새 비밀번호와 확인 비밀번호가 서로 다릅니다. 다시 입력해 주세요";
                        }
                        else {
                            if (!bClause1 || !bClause2) {
                                strMessage="모든 약관에 동의해 주세요";
                            }
                        }
                    }
                }

                if (strMessage.isEmpty()) {

                    if (CaApplication.m_Info.m_bSubscribingAsMainMember) {
                        CaApplication.m_Engine.CreateMemberMain(CaApplication.m_Info.m_nSeqAptHoSubscribing,
                                CaApplication.m_Info.m_strMemberNameSubscribing, CaApplication.m_Info.m_strPhoneSubscribing,
                                strMemberId, strPasswordNew, this, this);
                    }
                    else {
                        CaApplication.m_Engine.CreateMemberSub(CaApplication.m_Info.m_nSeqAptHoSubscribing,
                                CaApplication.m_Info.m_strMemberNameSubscribing, CaApplication.m_Info.m_strPhoneSubscribing,
                                strMemberId, strPasswordNew, this, this);
                    }

                }
                else {
                    View.OnClickListener LsnConfirm = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i("ActivitySubscribe", "확인 button clicked...");
                            m_dlgCheckPassword.dismiss();
                        }
                    };

                    m_dlgCheckPassword = new EgDialog(this, R.layout.dialog02, strMessage, LsnConfirm);
                    m_dlgCheckPassword.show();
                }

            }
            break;

            case R.id.btn_clause_1: {
                Log.i("Subscribe", "btn_clause_1 clicked");

                Intent it = new Intent(this, ActivityWeb.class);
                it.putExtra("title", "서비스 이용약관");
                it.putExtra("url", "https://www.egservice.co.kr/EgService/EG_이용약관_2019_11_04.htm");
                startActivity(it);
            }
            break;

            case R.id.btn_clause_2: {
                Log.i("Subscribe", "btn_clause_2 clicked");
                Intent it = new Intent(this, ActivityWeb.class);
                it.putExtra("title", "개인정보 수집이용");
                it.putExtra("url", "https://www.egservice.co.kr/EgService/EG_개인정보_2019_11_04.htm");
                startActivity(it);
            }
            break;

            default: {

            }
            break;

        }
    }

    @Override
    public void onResult(CaResult Result) {

        if (Result.object == null) {
           // Toast.makeText(m_Context, StringUtil.getString(this, R.string.tv_label_network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        switch (Result.m_nCallback) {
            case CaEngine.CB_CREATE_MEMBER_MAIN: {

                try {
                    JSONObject jo = Result.object;
                    int nSeqMember = jo.getInt("seq_member");
                    int nResultCode = jo.getInt("result_code");

                    Log.i("ActivitySubscribe", "CreateMemberMain : " + jo.toString());

                    if (nResultCode==0) {
                        Intent nextIntent = new Intent(this, ActivitySubscribedMain.class);
                        startActivity(nextIntent);

                    }
                    else if (nResultCode==1) {
                        View.OnClickListener LsnConfirm = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.i("ActivitySubscribe", "확인 button clicked...");
                                m_dlgIdDuplication.dismiss();
                            }
                        };

                        m_dlgIdDuplication = new EgDialog(this, R.layout.dialog01, "이미 존재하는 아이디입니다. 다른 아이디를 입력해 주세요.", LsnConfirm);
                        m_dlgIdDuplication.show();

                    }
                    else {
                        View.OnClickListener LsnConfirm = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.i("ActivitySubscribe", "확인 button clicked...");
                                m_dlgUnknownErrorCreatingMemberMain.dismiss();
                            }
                        };

                        m_dlgUnknownErrorCreatingMemberMain = new EgDialog(this, R.layout.dialog01, "계정 생성에 실패했습니다.", LsnConfirm);
                        m_dlgUnknownErrorCreatingMemberMain.show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            break;

            case CaEngine.CB_CREATE_MEMBER_SUB: {

                try {
                    JSONObject jo = Result.object;
                    final int nSeqMember = jo.getInt("seq_member");
                    int nResultCode = jo.getInt("result_code");

                    Log.i("ActivitySubscribe", "CreateMemberSub : " + jo.toString());

                    if (nResultCode==0) {

                        CaApplication.m_Engine.RequestAckMember(nSeqMember, this, this);

                        Intent nextIntent = new Intent(this, ActivitySubscribedSub.class);
                        startActivity(nextIntent);

                    }
                    else if (nResultCode==1) {
                        View.OnClickListener LsnConfirm = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.i("ActivitySubscribe", "확인 button clicked...");
                                m_dlgIdDuplication.dismiss();
                            }
                        };

                        m_dlgIdDuplication = new EgDialog(this, R.layout.dialog01, "이미 존재하는 아이디입니다. 다른 아이디를 입력해 주세요.", LsnConfirm);
                        m_dlgIdDuplication.show();

                    }
                    else {
                        View.OnClickListener LsnConfirm = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.i("ActivitySubscribe", "확인 button clicked...");
                                m_dlgUnknownErrorCreatingMemberMain.dismiss();
                            }
                        };

                        m_dlgUnknownErrorCreatingMemberMain = new EgDialog(this, R.layout.dialog01, "계정 생성에 실패했습니다.", LsnConfirm);
                        m_dlgUnknownErrorCreatingMemberMain.show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            break;

            case CaEngine.CB_REQUEST_ACK_MEMBER: {
                Log.e("ActivitySubscribe", "Response of Request Ack Member");
            }
            break;

        } // end of switch
    }

}