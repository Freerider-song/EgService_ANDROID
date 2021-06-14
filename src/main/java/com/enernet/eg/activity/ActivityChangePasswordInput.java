package com.enernet.eg.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.enernet.eg.CaApplication;
import com.enernet.eg.CaEngine;
import com.enernet.eg.CaPref;
import com.enernet.eg.CaResult;
import com.enernet.eg.EgDialog;
import com.enernet.eg.IaResultHandler;
import com.enernet.eg.R;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityChangePasswordInput extends BaseActivity implements IaResultHandler {

    private int m_nSeqMember=0;
    private String m_strMemberId="";
    private EgDialog m_dlgInputError;
    private EgDialog m_dlgChangePassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_input);

        m_nSeqMember=getIntent().getIntExtra("seq_member", 0);
        m_strMemberId=getIntent().getStringExtra("member_id");

        EditText etMemberId=findViewById(R.id.et_member_id);
        etMemberId.setText(m_strMemberId);

        //// for the next login com.enernet.eg.activity
        CaPref pref = new CaPref(getApplicationContext());
        pref.setValue(CaPref.PREF_MEMBER_ID, m_strMemberId);

        ////
        Log.i("ChangePasswordInput", "SeqMember="+m_nSeqMember+", MemberId="+m_strMemberId);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_change_password: {
                Log.i("ChangePassword", "btn_change_password clicked");

                EditText etPasswordNew=findViewById(R.id.et_password_new);
                EditText etPasswordConfirm=findViewById(R.id.et_password_confirm);

                String strPasswordNew=etPasswordNew.getText().toString();
                String strPasswordConfirm=etPasswordConfirm.getText().toString();

                String strMessage="";

                if (strPasswordNew.isEmpty() || strPasswordConfirm.isEmpty()) {
                    strMessage="새 비밀번호와 확인 비밀번호를 모두 입력해 주세요";
                }
                else {
                    if (!strPasswordNew.equals(strPasswordConfirm)) {
                        strMessage="새 비밀번호와 확인 비밀번호가 서로 다릅니다. 다시 입력해 주세요";
                    }
                }

                if (strMessage.isEmpty()) {
                    CaApplication.m_Engine.ChangePassword(m_nSeqMember, strPasswordNew, this, this);
                }
                else {
                    View.OnClickListener LsnConfirm=new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i("ChangePasswordInput", "확인 button clicked...");
                            m_dlgInputError.dismiss();

                        }
                    };

                    m_dlgInputError=new EgDialog(this, R.layout.dialog02, strMessage, LsnConfirm);
                    m_dlgInputError.show();
                }

                Log.i("ChangePasswordInput", "PasswordNew=["+strPasswordNew+"], PasswordConfirm=["+strPasswordConfirm+"]");
            }
            break;

        }
    }

    @Override
    public void onResult(CaResult Result) {
        switch (Result.m_nCallback) {
            case CaEngine.CB_CHANGE_PASSWORD: {

                try {
                    JSONObject jo = Result.object;
                    int nSeqMember = jo.getInt("seq_member");

                    Log.i("ChangePasswordInput", "ChangePassword : " + jo.toString());

                    if (nSeqMember == 0) {

                        View.OnClickListener LsnConfirm = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.i("ActivitySubscribe", "확인 button clicked...");
                                m_dlgChangePassword.dismiss();

                            }
                        };

                        m_dlgChangePassword = new EgDialog(this, R.layout.dialog01, "비밀번호 변경에 실패했습니다", LsnConfirm);
                        m_dlgChangePassword.show();

                    }
                    else {
                        View.OnClickListener LsnConfirm = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.i("ActivitySubscribe", "확인 button clicked...");
                                m_dlgChangePassword.dismiss();

                                finish();

                                CaPref pref = new CaPref(getApplicationContext());

                                //pref.setValue(CaPref.PREF_MEMBER_ID, "");
                                pref.setValue(CaPref.PREF_PASSWORD, "");

                                Intent nextIntent = new Intent(getApplicationContext(), ActivityLogin.class);
                                startActivity(nextIntent);

                            }
                        };

                        m_dlgChangePassword = new EgDialog(this, R.layout.dialog01, "비밀번호 변경에 성공했습니다. 로그인해서 서비스를 이용하세요", LsnConfirm);
                        m_dlgChangePassword.show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
            break;
        }
    }

}
