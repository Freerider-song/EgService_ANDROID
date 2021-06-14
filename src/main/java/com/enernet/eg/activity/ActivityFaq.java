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
import com.enernet.eg.model.CaFaq;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ActivityFaq extends BaseActivity implements IaResultHandler {

    private FaqAdapter m_FaqAdapter;

    private class FaqViewHolder {
        public ImageView m_ivFaqHeader;
        public TextView m_tvQuestion;
        public TextView m_tvAnswer;
        public ImageView m_ivUpDown;
    }

    private class FaqAdapter extends BaseAdapter {

        public FaqAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return CaApplication.m_Info.m_alFaq.size();
        }

        @Override
        public Object getItem(int position) {
            return CaApplication.m_Info.m_alFaq.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FaqViewHolder holder;
            if (convertView == null) {
                holder = new FaqViewHolder();

                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.eg_list_item_faq, null);

                holder.m_ivFaqHeader=convertView.findViewById(R.id.iv_faq_head);
                holder.m_tvQuestion = convertView.findViewById(R.id.tv_question);
                holder.m_tvAnswer = convertView.findViewById(R.id.tv_answer);
                holder.m_ivUpDown=convertView.findViewById(R.id.iv_up_down);

                convertView.setTag(holder);
            }
            else {
                holder = (FaqViewHolder) convertView.getTag();
            }

            final CaFaq faq = CaApplication.m_Info.m_alFaq.get(position);

            holder.m_ivFaqHeader.setImageDrawable(getDrawable(R.drawable.faq_head));
            holder.m_tvQuestion.setText(faq.m_strQuestion);

            if (faq.m_bShowAnswer) {
                holder.m_ivUpDown.setImageDrawable(getDrawable(R.drawable.arrow_brown_up));
                holder.m_tvAnswer.setVisibility(View.VISIBLE);
                holder.m_tvAnswer.setText(faq.m_strAnswer);
            }
            else {
                holder.m_ivUpDown.setImageDrawable(getDrawable(R.drawable.arrow_brown_down));
                holder.m_tvAnswer.setVisibility(View.GONE);
            }

            return convertView;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        prepareDrawer();

        ListView lv=findViewById(R.id.lv_faq_list);

        TextView tvEmpty=findViewById(R.id.tv_empty);
        lv.setEmptyView(tvEmpty);

//////////////////////
        final ActivityFaq This=this;

        m_FaqAdapter=new FaqAdapter();
        lv.setAdapter(m_FaqAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("ListFaq", "Item clicked, pos="+position);

                CaFaq faq=CaApplication.m_Info.m_alFaq.get(position);
                faq.m_bShowAnswer=(!faq.m_bShowAnswer);
                This.m_FaqAdapter.notifyDataSetChanged();
            }
        });

        ///////////////////////

        CaApplication.m_Engine.GetFaqList(CaApplication.m_Info.m_nSeqSite, this, this);
    }

    @Override
    public void onBackPressed() {
        if (m_Drawer.isDrawerOpen()) {
            m_Drawer.closeDrawer();
        }
        else {
            finish();
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back: {
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

        }
    }

    @Override
    public void onResult(CaResult Result) {

        if (Result.object==null) {
            Toast.makeText(getApplicationContext(), StringUtil.getString(this, R.string.tv_label_network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        switch (Result.m_nCallback) {
            case CaEngine.CB_GET_FAQ_LIST: {
                Log.i("Faq", "Result of GetFaqList received...");

                try {
                    JSONObject jo = Result.object;
                    JSONArray jaRes = jo.getJSONArray("faq_list");

                    CaApplication.m_Info.m_alFaq.clear();

                    for (int i=0; i<jaRes.length(); i++) {
                        JSONObject joRes=jaRes.getJSONObject(i);
                        CaFaq faq=new CaFaq();
                        faq.m_nSeqFaq=joRes.getInt("seq_faq");
                        faq.m_strQuestion=joRes.getString("question");
                        faq.m_strAnswer=joRes.getString("answer");
                        faq.m_dtCreated=parseDate(joRes.getString("time_created"));

                        CaApplication.m_Info.m_alFaq.add(faq);
                    }

                    m_FaqAdapter.notifyDataSetChanged();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            break;

            default: {
                Log.i("Faq", "Unknown type result received : " + Result.m_nCallback);
            }
            break;

        } // end of switch
    }
}
