package com.enernet.eg.activity;

import android.content.Context;
import android.content.Intent;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.enernet.eg.CaApplication;
import com.enernet.eg.CaEngine;
import com.enernet.eg.CaInfo;
import com.enernet.eg.CaResult;
import com.enernet.eg.IaResultHandler;
import com.enernet.eg.ListViewInfinite;
import com.enernet.eg.R;
import com.enernet.eg.StringUtil;
import com.enernet.eg.model.CaNotice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

public class ActivityNoticeList extends BaseActivity implements IaResultHandler, ListViewInfinite.ListenerInfinite {

    private ListViewInfinite m_lvNotice;

    private NoticeAdapter m_NoticeAdapter;

    private class NoticeViewHolder {
        public ConstraintLayout m_clAreaRoot;
        public TextView m_tvTitle;
        public TextView m_tvTimeCreated;
        public ImageView m_ivNoticeType;
        public ImageView m_ivRightArrow;
        public ImageView m_ivNew;
    }

    private class NoticeAdapter extends BaseAdapter {

        public NoticeAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return CaApplication.m_Info.m_alNotice.size();
        }

        @Override
        public Object getItem(int position) {
            return CaApplication.m_Info.m_alNotice.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            NoticeViewHolder holder;
            if (convertView == null) {
                holder = new NoticeViewHolder();

                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.eg_list_item_notice, null);

                holder.m_clAreaRoot = convertView.findViewById(R.id.area_root);
                holder.m_tvTitle = convertView.findViewById(R.id.tv_title);
                holder.m_tvTimeCreated=convertView.findViewById(R.id.tv_time_created);
                holder.m_ivNoticeType=convertView.findViewById(R.id.iv_notice_type);
                holder.m_ivRightArrow=convertView.findViewById(R.id.iv_right_arrow);
                holder.m_ivRightArrow.setImageDrawable(getDrawable(R.drawable.arrow_right_hollow));
                holder.m_ivNew=convertView.findViewById(R.id.iv_new);
                holder.m_ivNew.setImageDrawable(getDrawable(R.drawable.new_icon));

                convertView.setTag(holder);
            }
            else {
                holder = (NoticeViewHolder) convertView.getTag();
            }

            final CaNotice notice = CaApplication.m_Info.m_alNotice.get(position);

            if (notice.m_bTop) {
                holder.m_clAreaRoot.setBackground(getDrawable(R.drawable.shape_round_corner_notice_top));
            }
            else {
                holder.m_clAreaRoot.setBackground(getDrawable(R.drawable.shape_round_corner_notice_normal));
            }

            if (notice.m_nWriterType==1) {
                holder.m_ivNoticeType.setImageDrawable(getDrawable(R.drawable.notice_site));
            }
            else {
                holder.m_ivNoticeType.setImageDrawable(getDrawable(R.drawable.notice_eg));
            }

            holder.m_tvTitle.setText(notice.m_strTitle);
            holder.m_tvTimeCreated.setText(notice.getTimeCreated());

            if (notice.m_bRead) holder.m_ivNew.setVisibility(View.INVISIBLE);
            else holder.m_ivNew.setVisibility(View.VISIBLE);

            return convertView;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_list);

        prepareDrawer();

        m_lvNotice=findViewById(R.id.lv_notice_list);

        TextView tvEmpty=findViewById(R.id.tv_empty);
        m_lvNotice.setEmptyView(tvEmpty);

        final ActivityNoticeList This=this;

        m_lvNotice.setLoadingView(R.layout.loading_layout);
        m_lvNotice.setListener(this);

        m_NoticeAdapter=new NoticeAdapter();
        m_lvNotice.setAdapter(m_NoticeAdapter);

        m_lvNotice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("ListNotice", "Item clicked, pos="+position);

                CaNotice notice=CaApplication.m_Info.m_alNotice.get(position);
                notice.m_bRead=true;
                notice.m_bReadStateChanged=true;
                notice.m_dtRead= Calendar.getInstance().getTime();
                m_NoticeAdapter.notifyDataSetChanged();

                Intent it = new Intent(This, ActivityNotice.class);
                it.putExtra("position", position);

                startActivity(it);
            }
        });

    }

    public void setNoticeReadStateToDb() {
        String strSeqNoticeList = CaApplication.m_Info.getNoticeReadListString();
        if (strSeqNoticeList.isEmpty()) {
            finish();
        }
        else {
            CaApplication.m_Engine.SetNoticeListAsRead(CaApplication.m_Info.m_nSeqMember, strSeqNoticeList, this, this);
        }
    }

    @Override
    public void onBackPressed() {
        if (m_Drawer.isDrawerOpen()) {
            m_Drawer.closeDrawer();
        }
        else {
            setNoticeReadStateToDb();
            finish();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        setNoticeCount();

        m_NoticeAdapter.notifyDataSetChanged();
    }

    public void setNoticeCount() {
        TextView tvNoticeCount=findViewById(R.id.tv_notice_count);
        String strNoticeCount="* 총 "+CaApplication.m_Info.m_alNotice.size()+" 건";
        tvNoticeCount.setText(strNoticeCount);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back: {
                setNoticeReadStateToDb();
                finish();
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
                //Date dtNow=new Date(System.currentTimeMillis());
                CaApplication.m_Info.m_alNotice.clear();

                Date dtNow=new Date();
                String strTimeMax=CaApplication.m_Info.m_dfyyyyMMddhhmmss.format(dtNow);
                Log.i("NoticeList", "btn_refresh clicked...strTimeMax="+strTimeMax);

                CaApplication.m_Engine.GetNoticeList(CaApplication.m_Info.m_nSeqMember, strTimeMax, CaInfo.DEFAULT_REQUEST_NOTICE_COUNT, this, this);
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
            case CaEngine.CB_GET_NOTICE_LIST: {
                Log.i("NoticeList", "Result of GetNoticeList received...");

                try {
                    JSONObject jo = Result.object;
                    JSONArray jaTop = jo.getJSONArray("notice_top_list");
                    JSONArray jaNormal = jo.getJSONArray("notice_list");

                    Log.i("NoticeList", "jaNormal length="+jaNormal.length());

                    if (jaNormal.length()==0) {
                        m_lvNotice.m_bNoMoreData=true;
                    }

                    CaApplication.m_Info.setNoticeList(jaTop, jaNormal);
                    setNoticeCount();

                    m_lvNotice.onDataAppended();

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            break;

            default: {
                Log.i("NoticeList", "Unknown type result received : " + Result.m_nCallback);
            }
            break;

        } // end of switch
    }

    @Override
    public void onNeedLoadData() {
        String strTimeMax=CaApplication.m_Info.m_dfyyyyMMddhhmmss.format(CaApplication.m_Info.m_dtNoticeCreatedMaxForNextRequest);

        Log.i("NoticeList", "onNeedLoadData called...strTimeMax="+strTimeMax);

        CaApplication.m_Engine.GetNoticeList(CaApplication.m_Info.m_nSeqMember, strTimeMax, CaInfo.DEFAULT_REQUEST_NOTICE_COUNT, this, this);
    }
}
