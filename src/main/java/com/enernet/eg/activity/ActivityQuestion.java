package com.enernet.eg.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.enernet.eg.CaApplication;
import com.enernet.eg.CaEngine;
import com.enernet.eg.CaResult;
import com.enernet.eg.EgDialogYn;
import com.enernet.eg.IaResultHandler;
import com.enernet.eg.R;
import com.enernet.eg.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ActivityQuestion extends BaseActivity implements IaResultHandler {
    EgDialogYn m_dlgYnCancel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        prepareDrawer();
    }

    @Override
    public void onBackPressed() {
        if (m_Drawer.isDrawerOpen()) {
            m_Drawer.closeDrawer();
        }
        else {
            promptForCancel();
        }

    }

    public void promptForCancel() {
        EditText etQuestion=findViewById(R.id.et_question);
        String strQuestion=etQuestion.getText().toString();

        if (strQuestion.isEmpty()) {
            finish();
            return;
        }

        View.OnClickListener LsnConfirmYes=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Setting", "Yes button clicked...");
                m_dlgYnCancel.dismiss();

                finish();
            }
        };

        View.OnClickListener LsnConfirmNo=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Setting", "No button clicked...");
                m_dlgYnCancel.dismiss();
            }
        };

        m_dlgYnCancel=new EgDialogYn(this, R.layout.dialog01yn,"문의를 취소하시겠습니까?", LsnConfirmYes, LsnConfirmNo);
        m_dlgYnCancel.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode==KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    return true;
                }
                return false;
            }
        });

        m_dlgYnCancel.show();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
            case R.id.btn_cancel: {
                promptForCancel();
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

            case R.id.btn_submit: {
                EditText etQuestion=findViewById(R.id.et_question);
                String strQuestion=etQuestion.getText().toString();

                CaApplication.m_Engine.CreateQuestion(CaApplication.m_Info.m_nSeqMember, strQuestion, this, this);
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
            case CaEngine.CB_CREATE_QUESTION: {
                Log.i("Question", "Result of CreateQuestion received...");

                try {
                    JSONObject jo = Result.object;
                    JSONArray jaQuestion = jo.getJSONArray("qna_list");
                    CaApplication.m_Info.setQnaList(jaQuestion);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

                finish();

            }
            break;

            default: {
                Log.i("Question", "Unknown type result received : " + Result.m_nCallback);
            }
            break;

        } // end of switch
    }
}
