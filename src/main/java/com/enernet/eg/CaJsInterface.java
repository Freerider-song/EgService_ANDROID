package com.enernet.eg;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;

import com.enernet.eg.activity.ActivityAuth;
import com.enernet.eg.activity.ActivityCandidate;
import com.enernet.eg.activity.ActivitySubscribe;

import org.json.JSONException;
import org.json.JSONObject;

public class CaJsInterface {
    ActivityAuth m_ActivityAuth;
    private EgDialog m_dlgAuthFailed;

    public CaJsInterface(ActivityAuth a) {
        m_ActivityAuth=a;
    }

    @JavascriptInterface
    public void transferAuthSuccess(String str) {
        System.out.println("JsInterface transferAuthSuccess=" + str);

        try {
            JSONObject jo = new JSONObject(str);

            String strName=jo.getString("name");
            String strPhone=jo.getString("phone");

            System.out.println("auth_result = " + jo.getInt("auth_result"));
            System.out.println("result_code = " + jo.getInt("result_code"));
            System.out.println("result_message = " + jo.getString("result_message"));
            System.out.println("name = " + strName);
            System.out.println("phone = " + strPhone);
            System.out.println("birth_date = " + jo.getString("birth_date"));
            System.out.println("gender = " + jo.getString("gender"));
            System.out.println("nationality = " + jo.getString("nationality"));

            //m_ActivityAuth.finish();

            switch (CaApplication.m_Info.m_nAuthType) {
                case CaEngine.AUTH_TYPE_SUBSCRIBE: {
                    Intent it = new Intent(m_ActivityAuth, ActivitySubscribe.class);
                    m_ActivityAuth.startActivity(it);
                }
                break;

                case CaEngine.AUTH_TYPE_CHANGE_PASSWORD: {
                    Log.i("CaJsInterface", "call GetMemberIdSeq with Name=" + strName + ", Phone="+strPhone);
                    CaApplication.m_Engine.GetMemberIdSeq(strName, strPhone, m_ActivityAuth, m_ActivityAuth);

                 //   Intent it = new Intent(m_ActivityAuth, ActivityChangePasswordInput.class);
                 //   m_ActivityAuth.startActivity(it);
                }
                break;

                default: {
                    Log.i("CaJsInterface", "Unknown Auth Type..." + CaApplication.m_Info.m_nAuthType);
                }
                break;

            }

            CaApplication.m_Info.m_nAuthType=CaEngine.AUTH_TYPE_UNKNOWN;


        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void transferAuthFail(String str) {
        System.out.println("JsInterface transferAuthFail=" + str);

        try {
            JSONObject jo = new JSONObject(str);
            System.out.println("auth_result = " + jo.getInt("auth_result"));
            System.out.println("result_code = " + jo.getInt("result_code"));
            System.out.println("result_message = " + jo.getString("result_message"));

            View.OnClickListener LsnConfirm=new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("CaJsInterface", "확인 button clicked...");
                    m_dlgAuthFailed.dismiss();

                    m_ActivityAuth.finish();
                    Intent it = new Intent(m_ActivityAuth, ActivityCandidate.class);
                    m_ActivityAuth.startActivity(it);

                }
            };

            m_dlgAuthFailed=new EgDialog(m_ActivityAuth, R.layout.dialog02,"본인인증에 실패했습니다", LsnConfirm);
            m_dlgAuthFailed.show();

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
