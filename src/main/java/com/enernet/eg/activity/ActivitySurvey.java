package com.enernet.eg.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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
import com.enernet.eg.model.CaItem;
import com.enernet.eg.model.CaNotice;
import com.enernet.eg.model.CaQuestion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ActivitySurvey extends BaseActivity implements IaResultHandler {

    public Context Ctx;
    public int position;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        prepareDrawer();

        Ctx = this;

        Intent it = getIntent();
        position=it.getIntExtra("position", -1);
        if(position >= 0){
            final CaNotice survey = CaApplication.m_Info.m_alNotice.get(position);

            TextView tvTitle = findViewById(R.id.tv_title);
            tvTitle.setText(survey.m_strTitle);

            TextView tvPeriod = findViewById(R.id.tv_period);
            tvPeriod.setText(survey.getPeriod());

            TextView tvExplain = findViewById(R.id.tv_explain);
            tvExplain.setText(survey.m_strExplain);

            CaApplication.m_Engine.GetSurveyInfo(survey.m_nSeqSurvey, this, this);

            Button btnStart = findViewById(R.id.btn_start);
            btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(Ctx, ActivitySurveyContent.class);
                    it.putExtra("position", position);

                    finish();

                    startActivity(it);
                }
            });
        }

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back: {
                //setAlarmReadStateToDb();
                finish();
            }
            break;

            case R.id.btn_menu: {
                m_Drawer.openDrawer();
            }
            break;
        }
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

    @Override
    public void onResult(CaResult Result) {

        if (Result.object == null) {
            Toast.makeText(getApplicationContext(), StringUtil.getString(this, R.string.tv_label_network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        switch (Result.m_nCallback) {
            case CaEngine.KS_GET_SURVEY_INFO: {
                try {
                    JSONObject jo = Result.object;
                    JSONArray ja = jo.getJSONArray("list_question");

                    CaNotice survey = new CaNotice();
                    survey.m_nSeqSurvey = jo.getInt("seq_survey");
                    survey.m_strTitle = jo.getString("survey_title");
                    survey.m_strExplain = jo.getString("survey_content");
                    survey.m_dtStart = parseDate(jo.getString("time_begin"));
                    survey.m_dtEnd = parseDate(jo.getString("time_end"));

                    Log.i("Survey", "ja length=" + ja.length());

                    for(int i=0; i<ja.length(); i++){
                        JSONObject joQuestion = ja.getJSONObject(i);

                        CaQuestion question = new CaQuestion();
                        question.m_nSeqQuestion = joQuestion.getInt("seq_question");
                        question.m_strQuestionTitle = joQuestion.getString("question_title");
                        question.m_nQuestionType = joQuestion.getInt("question_type");
                        question.m_bEssential = joQuestion.getBoolean("essential");

                        if(question.m_nQuestionType != 3){
                            JSONArray jaQuestionItem = joQuestion.getJSONArray("list_item");

                            Log.i("Survey", "jaItem length=" + jaQuestionItem.length());
                            for(int j=0; j<jaQuestionItem.length(); j++){
                                JSONObject joQuestionItem = jaQuestionItem.getJSONObject(j);

                                CaItem item = new CaItem();
                                item.m_nSeq = joQuestionItem.getInt("seq_item");
                                item.m_strName = joQuestionItem.getString("item_content");

                                question.m_alItem.add(item);
                            }
                        }

                        survey.m_alQuestion.add(question);
                    }

                    CaApplication.m_Info.m_alNotice.set(position, survey);

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
            break;

            default: {
                Log.i("Notice", "Unknown type result received : " + Result.m_nCallback);
            }
            break;

        } // end of switch
    }


}