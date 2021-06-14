package com.enernet.eg.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.enernet.eg.CaApplication;
import com.enernet.eg.CaEngine;
import com.enernet.eg.CaResult;
import com.enernet.eg.EgDialogAlarm;
import com.enernet.eg.EgDialogAlarmAck;
import com.enernet.eg.IaResultHandler;
import com.enernet.eg.R;
import com.enernet.eg.StringUtil;
import com.enernet.eg.model.CaAlarm;
import com.enernet.eg.model.CaFamily;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class ActivityAlarm extends BaseActivity implements IaResultHandler {

    private EgDialogAlarm m_dlgAlarm;
    private EgDialogAlarmAck m_dlgAlarmAck;

    private AlarmAdapter m_AlarmAdapter;

    private class AlarmViewHolder {
        public ConstraintLayout m_clAreaRoot;
        public TextView m_tvTitle;
        public TextView m_tvContent;
        public TextView m_tvTimeCreated;
        public TextView m_tvAckResponse;
        public ImageView m_ivRightArrow;
        public ImageView m_ivNew;
    }

    private class AlarmAdapter extends BaseAdapter {

        public AlarmAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return CaApplication.m_Info.m_alAlarm.size();
        }

        @Override
        public Object getItem(int position) {
            return CaApplication.m_Info.m_alAlarm.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AlarmViewHolder holder;
            if (convertView == null) {
                holder = new AlarmViewHolder();

                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.eg_list_item_alarm, null);

                holder.m_clAreaRoot = convertView.findViewById(R.id.area_root);
                holder.m_tvTitle = convertView.findViewById(R.id.tv_title);
                holder.m_tvContent = convertView.findViewById(R.id.tv_content);
                holder.m_tvTimeCreated=convertView.findViewById(R.id.tv_time_created);
                holder.m_tvAckResponse=convertView.findViewById(R.id.tv_ack_response);
                holder.m_ivRightArrow=convertView.findViewById(R.id.iv_right_arrow);
                holder.m_ivRightArrow.setImageDrawable(getDrawable(R.drawable.right_triangle));
                holder.m_ivNew=convertView.findViewById(R.id.iv_new);
                holder.m_ivNew.setImageDrawable(getDrawable(R.drawable.new_icon));

                convertView.setTag(holder);
            }
            else {
                holder = (AlarmViewHolder) convertView.getTag();
            }

            final CaAlarm alarm = CaApplication.m_Info.m_alAlarm.get(position);

            switch (alarm.m_nAlarmType) {
                case CaEngine.ALARM_TYPE_NOTI_KWH:
                case CaEngine.ALARM_TYPE_NOTI_WON:
                case CaEngine.ALARM_TYPE_NOTI_PRICE_LEVEL:
                case CaEngine.ALARM_TYPE_NOTI_USAGE:
                    holder.m_clAreaRoot.setBackground(getDrawable(R.drawable.shape_round_corner_filled_yellow_a));
                    break;

                case CaEngine.ALARM_TYPE_REQUEST_ACK_MEMBER:
                case CaEngine.ALARM_TYPE_RESPONSE_ACK_MEMBER_ACCEPTED:
                case CaEngine.ALARM_TYPE_RESPONSE_ACK_MEMBER_REJECTED:
                case CaEngine.ALARM_TYPE_RESPONSE_ACK_MEMBER_CANCELED:
                case CaEngine.ALARM_TYPE_NOTI_TRANS:
                    holder.m_clAreaRoot.setBackground(getDrawable(R.drawable.shape_round_corner_filled_yellow_b));
                    break;
            }

            holder.m_tvTitle.setText(alarm.m_strTitle);
            holder.m_tvContent.setText(alarm.m_strContent);
            holder.m_tvTimeCreated.setText(alarm.getTimeCreated());

            if (alarm.m_bRead) holder.m_ivNew.setVisibility(View.INVISIBLE);
            else holder.m_ivNew.setVisibility(View.VISIBLE);

            //Log.i("Alarm", "m_bRead="+alarm.m_bRead);

            if (alarm.isRequestAck()) {
                switch (alarm.m_nResponse) {
                    case 0:
                        holder.m_tvAckResponse.setText("승인 대기중");
                        break;

                    case 1:
                        holder.m_tvAckResponse.setText("승인함");
                        break;

                    case 2:
                        holder.m_tvAckResponse.setText("거절함");
                        break;

                    default:
                        holder.m_tvAckResponse.setText("미정");
                        break;
                }
            }
            else {
                holder.m_tvAckResponse.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }
    }

    public void ResponseAckMemberInActivityAlarm(int nSeqMemberSub, int nAck) {
        CaApplication.m_Engine.ResponseAckMember(nSeqMemberSub, nAck, true, this, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        if (CaApplication.m_Info.m_nSeqMember==0) {
            Intent it = new Intent(this, ActivityLogin.class);
            it.putExtra("started_by", "noti");
            startActivity(it);

            finish();
        }

        prepareDrawer();

        ListView lv=findViewById(R.id.lv_alarm_list);

        TextView tvEmpty=findViewById(R.id.tv_empty);
        lv.setEmptyView(tvEmpty);

        final ActivityAlarm This=this;

        m_AlarmAdapter=new AlarmAdapter();
        lv.setAdapter(m_AlarmAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("ListAlarm", "Item clicked, pos="+position);

                CaAlarm alarm=CaApplication.m_Info.m_alAlarm.get(position);
                alarm.m_bRead=true;
                alarm.m_bReadStateChanged=true;
                alarm.m_dtRead= Calendar.getInstance().getTime();
                m_AlarmAdapter.notifyDataSetChanged();

                final int nSeqMemberAckRequester=alarm.m_nSeqMemberAckRequester;

                if (alarm.isRequestAck() && alarm.m_nResponse==0) {
                    View.OnClickListener LsnYes = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i("Setting", "승인 button clicked...");
                            m_dlgAlarmAck.dismiss();

                           ResponseAckMemberInActivityAlarm(nSeqMemberAckRequester, 1);
                        }
                    };

                    View.OnClickListener LsnNo = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i("Setting", "거절 button clicked...");
                            m_dlgAlarmAck.dismiss();

                            ResponseAckMemberInActivityAlarm(nSeqMemberAckRequester, 2);
                        }
                    };

                    m_dlgAlarmAck = new EgDialogAlarmAck(This, alarm, LsnYes, LsnNo);
                    m_dlgAlarmAck.setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode==KeyEvent.KEYCODE_BACK) {
                                dialog.dismiss();
                                return true;
                            }
                            return false;
                        }
                    });

                    m_dlgAlarmAck.show();
                }
                else {
                    View.OnClickListener LsnConfirm = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i("Setting", "확인 button clicked...");

                            m_dlgAlarm.dismiss();

                        }
                    };

                    m_dlgAlarm = new EgDialogAlarm(This, alarm, LsnConfirm);
                    m_dlgAlarm.show();
                }


            }
        });

    }

    public void setAlarmReadStateToDb() {
        String strSeqAlarmList=CaApplication.m_Info.getAlarmReadListString();
        if (strSeqAlarmList.isEmpty()) {
            finish();
        }
        else {
            CaApplication.m_Engine.SetAlarmListAsRead(CaApplication.m_Info.m_nSeqMember, strSeqAlarmList, this, this);
        }

    }

    @Override
    public void onBackPressed() {
        if (m_Drawer.isDrawerOpen()) {
            m_Drawer.closeDrawer();
        }
        else {
            setAlarmReadStateToDb();
        }

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back: {
                setAlarmReadStateToDb();
            }
            break;

            case R.id.btn_menu: {
                m_Drawer.openDrawer();
            }
            break;

            case R.id.btn_alarm_setting: {
                Intent it = new Intent(this, ActivitySetting.class);
                it.putExtra("to", "alarm_setting");
                startActivity(it);
            }
            break;

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        m_AlarmAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResult(CaResult Result) {

        if (Result.object==null) {
            Toast.makeText(getApplicationContext(), StringUtil.getString(this, R.string.tv_label_network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        switch (Result.m_nCallback) {
            case CaEngine.CB_RESPONSE_ACK_MEMBER: {

                Log.i("Alarm", "Result of ResponseAckMember received...");

                try {
                    JSONObject jo = Result.object;

                    int nResultCode = jo.getInt("result_code");

                    if (nResultCode == 1) {
                        int nAck=jo.getInt("ack_type"); // 1=승인, 2=거절, 3=철회
                        int nSeqMemberSub=jo.getInt("seq_member_sub");
                        int nSeqAlarmInserted=jo.getInt("seq_alarm_inserted");
                        String strNameSub=jo.getString("name_sub");
                        String strPhoneSub=jo.getString("phone_sub");

                        CaApplication.m_Info.setResponseCodeForMemberSub(nSeqMemberSub, nAck);
                        m_AlarmAdapter.notifyDataSetChanged();

                        if (nAck==1) {
                            CaFamily family = new CaFamily();
                            family.m_nSeqMember = nSeqMemberSub;
                            family.m_strName = strNameSub;
                            family.m_strPhone = strPhoneSub;
                            family.m_bMain = false;

                            CaApplication.m_Info.m_alFamily.add(family);
                        }
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            break;

            case CaEngine.CB_SET_ALARM_LIST_AS_READ: {
                Log.i("Alarm", "Result of SetAlarmListAsRead received...");
                finish();
            }
            break;

            default: {
                Log.i("Alarm", "Unknown type result received : " + Result.m_nCallback);
            }
            break;

        } // end of switch
    }
}
