package com.enernet.eg.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class ActivityAck extends BaseActivity implements IaResultHandler {

    private TextView m_tvMessage;
    private Button m_btnRequestAck;
    private Button m_btnExit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ack);

        m_tvMessage=findViewById(R.id.tv_message);
        m_btnRequestAck=findViewById(R.id.btn_request_ack);
        m_btnExit=findViewById(R.id.btn_exit);

        Intent it=getIntent();
        int nAckRequestTodayCount=it.getIntExtra("ack_request_today_count", 0);
        int nAckResponseCodeLatest=it.getIntExtra("ack_response_code_latest", -1);
   //     int nSeqMemberSub=it.getIntExtra("seq_member_sub", 0);

    //    if (nSeqMemberSub!=0) {
    //        Log.i("ActivityAck", "SeqMemberSub will be set to "+nSeqMemberSub);
    //        CaApplication.m_Info.m_nSeqMember=nSeqMemberSub;
    //    }

        Log.i("ActivityAck", "nAckRequestTodayCount="+nAckRequestTodayCount);
        Log.i("ActivityAck", "nAckResponseCodeLatest="+nAckResponseCodeLatest);
      //  Log.i("ActivityAck", "nSeqMemberSub="+nSeqMemberSub);

        switch (nAckResponseCodeLatest) {
            case 0: {

                if (nAckRequestTodayCount<2) {
                    m_tvMessage.setText("승인 대기중입니다. 승인 요청을 다시 보낼수 있습니다.");
                }
                else {
                    m_tvMessage.setText("승인 대기중입니다. 오늘의 승인요청 횟수를 초과하여 승인요청을 다시 보내려면 내일 시도하십시요.");
                    m_btnRequestAck.setEnabled(false);
                }

            }
            break;

            case 1: {

            }
            break;

            case 2: {
                if (nAckRequestTodayCount<2) {
                    m_tvMessage.setText("승인이 거절되었습니다. 승인 요청을 다시 보낼수 있습니다.");
                }
                else {
                    m_tvMessage.setText("승인 거절되었습니다. 오늘의 승인요청 횟수를 초과하여 승인요청을 다시 보내려면 내일 시도하십시요.");
                    m_btnRequestAck.setEnabled(false);
                }
            }
            break;

            case 3: {
                if (nAckRequestTodayCount<2) {
                    m_tvMessage.setText("승인이 철회되었습니다. 승인 요청을 다시 보낼수 있습니다.");
                }
                else {
                    m_tvMessage.setText("승인 철회되었습니다. 오늘의 승인요청 횟수를 초과하여 승인요청을 다시 보내려면 내일 시도하십시요.");
                    m_btnRequestAck.setEnabled(false);
                }
            }
            break;

            default: {
                m_tvMessage.setText("앱을 사용하시려면 승인 요청을 보내서 세대 대표의 승인을 받아야 합니다.");
            }
            break;
        }

    }

    /*
    @Override
    public void onNewIntent(Intent intent) {

        int nSeqMemberSub=intent.getIntExtra("seq_member_sub", 0);
        Log.i("ActivityAck", "onNewIntent called. SeqMemberSub="+nSeqMemberSub);

        if (nSeqMemberSub!=0) {
            Log.i("ActivityAck", "SeqMemberSub will be set to "+nSeqMemberSub);
            CaApplication.m_Info.m_nSeqMember=nSeqMemberSub;
        }
    }
*/
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_request_ack: {
                CaApplication.m_Engine.RequestAckMember(CaApplication.m_Info.m_nSeqMember, this, this);
            }
            break;

            case R.id.btn_exit: {
                // kill app
                finishAffinity();
                System.runFinalization();
                System.exit(0);
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
            case CaEngine.CB_REQUEST_ACK_MEMBER: {
                Log.i("ActivityAck", "Result of RequestAckMember received...");

                try {
                    JSONObject jo = Result.object;

                    int nAckRequestTodayCount=jo.getInt("ack_request_today_count");

                    if (nAckRequestTodayCount>=2) {
                        m_tvMessage.setText("승인 대기중입니다. 오늘의 승인요청 횟수를 초과하여 승인요청을 다시 보내려면 내일 시도하십시요.");
                        m_btnRequestAck.setEnabled(false);
                    }

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
}
