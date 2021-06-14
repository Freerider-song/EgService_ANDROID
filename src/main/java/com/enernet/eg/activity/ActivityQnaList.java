package com.enernet.eg.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.enernet.eg.IaResultHandler;
import com.enernet.eg.R;
import com.enernet.eg.StringUtil;
import com.enernet.eg.model.CaQna;

import java.util.Calendar;

public class ActivityQnaList extends BaseActivity implements IaResultHandler {

    private QnaAdapter m_QnaAdapter;

    private class QnaViewHolder {
        public TextView m_tvQnaState;
        public ImageView m_ivNew;
        public TextView m_tvQuestion;
        public TextView m_tvTimeQna;
    }

    private class QnaAdapter extends BaseAdapter {

        public QnaAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return CaApplication.m_Info.m_alQna.size();
        }

        @Override
        public Object getItem(int position) {
            return CaApplication.m_Info.m_alQna.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            QnaViewHolder holder;
            if (convertView == null) {
                holder = new QnaViewHolder();

                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.eg_list_item_qna, null);

                holder.m_tvQnaState = convertView.findViewById(R.id.tv_qna_state);

                holder.m_ivNew=convertView.findViewById(R.id.iv_new);
                holder.m_ivNew.setImageDrawable(getDrawable(R.drawable.new_icon));

                holder.m_tvQuestion = convertView.findViewById(R.id.tv_question);
                holder.m_tvTimeQna=convertView.findViewById(R.id.tv_time_qna);

                convertView.setTag(holder);
            }
            else {
                holder = (QnaViewHolder) convertView.getTag();
            }

            final CaQna qna = CaApplication.m_Info.m_alQna.get(position);

            holder.m_tvQnaState.setText(qna.getQnaState());
            holder.m_tvQuestion.setText(qna.m_strQuestion);
            holder.m_tvTimeQna.setText(qna.getTimeQna());

            if (qna.isAnswered() && !qna.isAnswerRead()) holder.m_ivNew.setVisibility(View.VISIBLE);
            else holder.m_ivNew.setVisibility(View.INVISIBLE);

            return convertView;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qna_list);

        prepareDrawer();

        ////////////////////
        ListView lv=findViewById(R.id.lv_qna_list);

        TextView tvEmpty=findViewById(R.id.tv_empty);
        lv.setEmptyView(tvEmpty);

        final ActivityQnaList This=this;

        m_QnaAdapter=new QnaAdapter();
        lv.setAdapter(m_QnaAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("ListQna", "Item clicked, pos="+position);

                CaQna qna=CaApplication.m_Info.m_alQna.get(position);
                qna.m_bReadStateChanged=true;
                qna.m_dtAnswerRead= Calendar.getInstance().getTime();
                m_QnaAdapter.notifyDataSetChanged();

                Intent it = new Intent(This, ActivityQna.class);
                it.putExtra("position", position);

                startActivity(it);
            }
        });

        ///////////////////
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back: {
                setQnaReadStateToDb();
                finish();
            }
            break;

            case R.id.btn_menu: {
                m_Drawer.openDrawer();
            }
            break;

            case R.id.btn_alarm: {
                Intent it = new Intent(this, ActivityAlarm.class);
                startActivity(it);
            }
            break;

            case R.id.btn_create_question: {
                Intent it = new Intent(this, ActivityQuestion.class);
                startActivity(it);
            }
            break;

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        m_QnaAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (m_Drawer.isDrawerOpen()) {
            m_Drawer.closeDrawer();
        }
        else {
            setQnaReadStateToDb();
            finish();
        }
    }

    public void setQnaReadStateToDb() {
        String strSeqQnaList = CaApplication.m_Info.getQnaReadListString();
        if (strSeqQnaList.isEmpty()) {
            finish();
        }
        else {
            CaApplication.m_Engine.SetQnaListAsRead(CaApplication.m_Info.m_nSeqMember, strSeqQnaList, this, this);
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
                Log.i("Alarm", "Result of RequestAckMember received...");


            }
            break;

            default: {
                Log.i("Alarm", "Unknown type result received : " + Result.m_nCallback);
            }
            break;

        } // end of switch
    }
}
