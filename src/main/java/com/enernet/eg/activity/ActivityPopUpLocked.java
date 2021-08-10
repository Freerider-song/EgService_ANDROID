package com.enernet.eg.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.enernet.eg.R;
import com.enernet.eg.ServicePush;
import com.mikepenz.fastadapter.listeners.OnClickListener;

public class ActivityPopUpLocked extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //PopUp의 Title을 제거
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pop_up_locked);
        // 화면이 잠겨있을 때 보여주기

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED

                // 키잠금 해제하기

                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD

                // 화면 켜기

                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);



        TextView tvContent = (TextView)findViewById(R.id.tv_push_content);
        tvContent.setText(getIntent().getStringExtra("content"));
        ImageView ivPush = (ImageView)findViewById(R.id.iv_push_image);
        Glide.with(this).load(getIntent().getStringExtra("image")).into(ivPush);

        //ivPush.setImageBitmap(ServicePush.getImageFromURL(getIntent().getStringExtra("image")));
        Log.i("PopUpLocked", "image url = " + getIntent().getStringExtra("image"));


        Button btnNo = (Button)findViewById(R.id.btn_close);
        Button btnYes = (Button)findViewById(R.id.btn_check);



    }

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_check: {
                Log.i("Usage", "Refresh button clicked...");
                Intent intent = new Intent(ActivityPopUpLocked.this, ActivityAlarm.class);

                startActivity(intent);

                finish();
            }
            break;

            case R.id.btn_close: {
                finish();
            }


        }
    }
}