package com.enernet.eg.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.enernet.eg.R;
import com.enernet.eg.Utils;

public class ActivitySplash extends BaseActivity {

	private Intent nextIntent; // 다음 화면으로 전환하기 위한 INTENT

	// LOGO 화면을 보여주기 위한 정보들
	protected int splashTime = 2000;
	protected Thread splashThread;
	protected boolean active = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		TextView egService =findViewById(R.id.textView7);
		TextView intro = findViewById(R.id.textView11);
		//Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
		//egService.startAnimation(fadeIn);
		//intro.startAnimation(fadeIn);

		Utils.setStatusBarColor(this,Utils.StatusBarColorType.MAIN_STATUS_BAR);

		nextIntent = new Intent(this, ActivityLogin.class);
		splashThread = new Thread() {
			@Override
			public void run() {
				// MAIN 화면을 보여주기 위한 로직
				try {
					int waited = 0;
					while(waited < splashTime) {
						sleep(1000);
						waited += 1500;
					}
				} catch (InterruptedException e) {
				} finally {
					startActivity(nextIntent);
					finish();
				}
			}
		};
		splashThread.start();
	}

}
