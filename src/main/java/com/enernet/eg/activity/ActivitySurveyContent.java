package com.enernet.eg.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.enernet.eg.CaApplication;
import com.enernet.eg.CaResult;
import com.enernet.eg.EgDialog;
import com.enernet.eg.EgDialogYn;
import com.enernet.eg.IaResultHandler;
import com.enernet.eg.R;
import com.enernet.eg.StringUtil;
import com.enernet.eg.model.CaAnswer;
import com.enernet.eg.model.CaNotice;
import com.enernet.eg.model.CaQuestion;

import java.util.ArrayList;

public class ActivitySurveyContent extends BaseActivity implements IaResultHandler {

    public int nQuestion = 1;
    public int nQuestionTotal = 0;

    public TextView tvQuestionNum;

    public ArrayList<CaAnswer> alAnswer = new ArrayList<>();

    public CaNotice survey = new CaNotice();

    public ViewPager viewPager;

    public Context Ctx;

    private EgDialogYn m_dlgYnExit;
    private EgDialog m_dlgFinish;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_content);

        Ctx = this;

        Intent it = getIntent();
        final int position=it.getIntExtra("position", -1);

        Log.i("Content", "Position: " + position);

        if(position >= 0) {
            survey = CaApplication.m_Info.m_alNotice.get(position);
        }

        nQuestionTotal = survey.m_alQuestion.size();

        tvQuestionNum = findViewById(R.id.tv_question_num);
        tvQuestionNum.setText(nQuestion + " / " + nQuestionTotal);

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        //ViewPager Adapter 연결
        viewPager.setAdapter(new ViewPagerAdapter(getLayoutInflater()));
        viewPager.setCurrentItem(0);

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back: {
                //setAlarmReadStateToDb();
                View.OnClickListener LsnConfirmYes=new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("Setting", "Yes button clicked...");
                        m_dlgYnExit.dismiss();

                        finish();
                    }
                };

                View.OnClickListener LsnConfirmNo=new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("Setting", "No button clicked...");
                        m_dlgYnExit.dismiss();
                    }
                };

                m_dlgYnExit=new EgDialogYn(this, R.layout.dialog01yn,"설문을 중단하시겠습니까? \n (기록된 모든 응답은 저장되지 않습니다.)", LsnConfirmYes, LsnConfirmNo);
                m_dlgYnExit.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode==KeyEvent.KEYCODE_BACK) {
                            dialog.dismiss();
                            return true;
                        }
                        return false;
                    }
                });

                m_dlgYnExit.show();
            }
            break;

        }
    }

    @Override
    public void onBackPressed() {
        View.OnClickListener LsnConfirmYes=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Setting", "Yes button clicked...");
                m_dlgYnExit.dismiss();

                finish();
            }
        };

        View.OnClickListener LsnConfirmNo=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Setting", "No button clicked...");
                m_dlgYnExit.dismiss();
            }
        };

        m_dlgYnExit=new EgDialogYn(this, R.layout.dialog01yn,"설문을 중단하시겠습니까? \n (기록된 모든 응답은 저장되지 않습니다.)", LsnConfirmYes, LsnConfirmNo);
        m_dlgYnExit.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode==KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    return true;
                }
                return false;
            }
        });

        m_dlgYnExit.show();

    }

    //이전버튼 클릭
    public View.OnClickListener mPrevButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            nQuestion -= 1;
            tvQuestionNum.setText(nQuestion + " / " + nQuestionTotal);
            viewPager.setCurrentItem(viewPager.getCurrentItem()-1, true);
        }
    };

    //다음버튼 클릭
    public View.OnClickListener mNextButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CaAnswer answer = new CaAnswer();
            CaQuestion question = survey.m_alQuestion.get(viewPager.getCurrentItem());

            answer.m_nSeqQuestion = question.m_nSeqQuestion;

            if(question.m_nQuestionType == 1) {
                //질문 답변 유형이 Radio일 경우 체크된 값 가져와서 저장
                ListView view = viewPager.findViewWithTag(viewPager.getCurrentItem());
                answer.m_nQuestionType = 1;
                //응답이 없을 경우
                if(view.getCheckedItemPosition() == -1) {
                    //필수 응답 여부 확인
                    if (question.m_bEssential == true) {
                        Toast.makeText(Ctx,"필수 응답 항목입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    } else{
                        Log.i("survey", "Question: " + answer.m_nSeqQuestion + " No answer");
                    }
                } else {
                    //응답이 있을 경우
                    answer.m_alAnswer.add(question.m_alItem.get(view.getCheckedItemPosition()).m_nSeq);
                    Log.i("survey", "Question: " + answer.m_nSeqQuestion + " answer: " + answer.m_alAnswer.get(0));
                }
            } else if(question.m_nQuestionType == 2){
                //질문 답변 유형이 Checkbox일 경우 체크된 값들 가져와서 저장
                ListView view = viewPager.findViewWithTag(viewPager.getCurrentItem());
                answer.m_nQuestionType = 2;
                SparseBooleanArray booleans = view.getCheckedItemPositions();
                for(int i=0; i<question.m_alItem.size(); i++){
                    if(booleans.get(i)){
                        answer.m_alAnswer.add(question.m_alItem.get(i).m_nSeq);
                        Log.i("survey", "Question: " + answer.m_nSeqQuestion + " answer: " + question.m_alItem.get(i).m_nSeq);
                    }
                }
                if(answer.m_alAnswer.size() == 0){
                    if (question.m_bEssential == true) {
                        Toast.makeText(Ctx,"필수 응답 항목입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    } else{
                        Log.i("survey", "Question: " + answer.m_nSeqQuestion + " No answer");
                    }
                }

                Log.i("survey", "Question: " + answer.m_nSeqQuestion + " answer: " + answer.m_alAnswer.toString());
            } else if(question.m_nQuestionType == 3){
                //질문 답변 유형 주관식일 경우 입력된 값 가져와서 저장
                View view = viewPager.findViewWithTag(viewPager.getCurrentItem());
                EditText editText = view.findViewById(R.id.et_content);
                answer.m_strAnswer = editText.getText().toString();
                answer.m_nQuestionType = 3;

                if (answer.m_strAnswer.length() == 0){
                    if (question.m_bEssential == true) {
                        Toast.makeText(Ctx,"필수 응답 항목입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    } else{
                        Log.i("survey", "Question: " + answer.m_nSeqQuestion + " No answer");
                    }
                }

                Log.i("survey", "Question: " + answer.m_nSeqQuestion + " answer: " + answer.m_strAnswer);
            }

            //이미 응답된 값 있을 경우 삭제 후 새로운 응답 저장
            for(int i=0; i<alAnswer.size(); i++){
                if(alAnswer.get(i).m_nSeqQuestion == answer.m_nSeqQuestion){
                    alAnswer.remove(i);
                }
            }

            alAnswer.add(answer);

            nQuestion += 1;
            tvQuestionNum.setText(nQuestion + " / " + nQuestionTotal);

            viewPager.setCurrentItem(viewPager.getCurrentItem()+1, true);
        }
    };

    //마지막 Finish 버튼 클릭
    public View.OnClickListener mFinishButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CaAnswer answer = new CaAnswer();
            CaQuestion question = survey.m_alQuestion.get(viewPager.getCurrentItem());

            answer.m_nSeqQuestion = question.m_nSeqQuestion;

            if(question.m_nQuestionType == 1) {
                //질문 답변 유형이 Radio일 경우 체크된 값 가져와서 저장
                ListView view = viewPager.findViewWithTag(viewPager.getCurrentItem());
                answer.m_nQuestionType = 1;
                if(view.getCheckedItemPosition() == -1) {
                    if (question.m_bEssential == true) {
                        Toast.makeText(Ctx,"필수 응답 항목입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    } else{
                        Log.i("survey", "Question: " + answer.m_nSeqQuestion + " No answer");
                    }
                } else {
                    answer.m_alAnswer.add(question.m_alItem.get(view.getCheckedItemPosition()).m_nSeq);
                    Log.i("survey", "Question: " + answer.m_nSeqQuestion + " answer: " + answer.m_alAnswer.get(0));
                }
            } else if(question.m_nQuestionType == 2){
                //질문 답변 유형이 Checkbox일 경우 체크된 값들 가져와서 저장
                ListView view = viewPager.findViewWithTag(viewPager.getCurrentItem());
                answer.m_nQuestionType = 2;
                SparseBooleanArray booleans = view.getCheckedItemPositions();
                for(int i=0; i<question.m_alItem.size(); i++){
                    if(booleans.get(i)){
                        answer.m_alAnswer.add(question.m_alItem.get(i).m_nSeq);
                        Log.i("survey", "Question: " + answer.m_nSeqQuestion + " answer: " + question.m_alItem.get(i).m_nSeq);
                    }
                }
                if(answer.m_alAnswer.size() == 0){
                    if (question.m_bEssential == true) {
                        Toast.makeText(Ctx,"필수 응답 항목입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    } else{
                        Log.i("survey", "Question: " + answer.m_nSeqQuestion + " No answer");
                    }
                }
            } else if(question.m_nQuestionType == 3){
                //질문 답변 유형 주관식일 경우 입력된 값 가져와서 저장
                View view = viewPager.findViewWithTag(viewPager.getCurrentItem());
                EditText editText = view.findViewById(R.id.et_content);
                answer.m_strAnswer = editText.getText().toString();
                answer.m_nQuestionType = 3;

                if (answer.m_strAnswer.length() == 0){
                    if (question.m_bEssential == true) {
                        Toast.makeText(Ctx,"필수 응답 항목입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    } else{
                        Log.i("survey", "Question: " + answer.m_nSeqQuestion + " No answer");
                    }
                }

                Log.i("survey", "Question: " + answer.m_nSeqQuestion + " answer: " + answer.m_strAnswer);
            }

            for(int i=0; i<alAnswer.size(); i++){
                if(alAnswer.get(i).m_nSeqQuestion == answer.m_nSeqQuestion){
                    alAnswer.remove(i);
                }
            }

            alAnswer.add(answer);

            InsertAnswer();
        }
    };

    @Override
    public void onResult(CaResult Result) {

        if (Result.object == null) {
            Toast.makeText(getApplicationContext(), StringUtil.getString(this, R.string.tv_label_network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        switch (Result.m_nCallback) {

            default: {
                Log.i("Notice", "Unknown type result received : " + Result.m_nCallback);
            }
            break;

        } // end of switch
    }

    public class ViewPagerAdapter extends PagerAdapter {
        private LayoutInflater mInflater;

        public ViewPagerAdapter(LayoutInflater inflater)
        {
            this.mInflater = inflater;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View)object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position)
        {
            View view;
            ListView listView = null;

            CaQuestion question = survey.m_alQuestion.get(position);

            //질문 응답 유형 주관식일 경우 View 변경
            if(question.m_nQuestionType == 3){
                view = mInflater.inflate(R.layout.question_layout_text, null);
                view.setTag(position);
            }else {
                view = mInflater.inflate(R.layout.question_layout, null);
                listView = (ListView)view.findViewById(R.id.lv_ans);
                listView.setTag(position);
            }

            TextView textView = (TextView)view.findViewById(R.id.tv_title);
            //필수 응답 질문일 경우 별 추가
            if(question.m_bEssential ==true){
                textView.setText(Html.fromHtml("<font color='black'>" + question.m_strQuestionTitle  + "</font>" + "<font color='red'> *</font>"), TextView.BufferType.SPANNABLE);
            } else {
                textView.setText(question.m_strQuestionTitle);
            }

            //첫 번째 질문일 경우 prev버튼 안 보이게 변경
            if(position == 0){
                Button prev = (Button)view.findViewById(R.id.btn_prev);
                prev.setVisibility(View.INVISIBLE);
            } else {
                view.findViewById(R.id.btn_prev).setOnClickListener(mPrevButtonClick);
            }

            //마지막 질문일 경우 버튼 변경
            if(position+1 == survey.m_alQuestion.size()){
                Button next = (Button)view.findViewById(R.id.btn_next);
                next.setText("Finish");
                next.setOnClickListener(mFinishButtonClick);
            } else {
                view.findViewById(R.id.btn_next).setOnClickListener(mNextButtonClick);
            }

            //질문 응답 유형에 맞게 adapter 유형 변경
            switch(question.m_nQuestionType)
            {
                //단답형
                case 1:
                    ArrayAdapter arrayAdapter1 = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_single_choice, question.m_alItem);
                    listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                    listView.setAdapter(arrayAdapter1);
                    break;

                //다중 선택
                case 2:
                    ArrayAdapter arrayAdapter2 = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice, question.m_alItem);
                    listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    listView.setAdapter(arrayAdapter2);
                    break;
            }
            container.addView(view);
            return view;
        }

        //ViewPager Size 리턴
        @Override
        public int getCount()
        {
            return survey.m_alQuestion.size();
        }
    }

    public void InsertAnswer(){
        for(int i=0; i<alAnswer.size(); i++){
            CaAnswer answer = alAnswer.get(i);
            if(answer.m_nQuestionType == 3){
                CaApplication.m_Engine.InsertAnswerString(CaApplication.m_Info.m_nSeqMember, answer.m_nSeqQuestion, answer.m_strAnswer, this, this);
            } else if (answer.m_nQuestionType == 1 || answer.m_nQuestionType == 2){
                CaApplication.m_Engine.InsertAnswerItemList(CaApplication.m_Info.m_nSeqMember, answer.m_nSeqQuestion, answer.m_alAnswer, this, this);
            }
        }

        View.OnClickListener LsnOk = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

                m_dlgFinish.dismiss();
            }
        };

        m_dlgFinish=new EgDialog(this, R.layout.dialog02,"설문 응답이 완료되었습니다.", LsnOk);

        m_dlgFinish.show();
    }
}