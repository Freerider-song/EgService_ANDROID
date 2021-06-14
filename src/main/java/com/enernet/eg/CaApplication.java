package com.enernet.eg;

import android.app.Application;
import android.content.Context;

public class CaApplication extends Application {

	public static CaEngine m_Engine = null;
	public static CaUser m_User = null;
	public static CaInfo m_Info = null;
	public static Context m_Context = null;


	public static Context getContext() {
		return m_Context;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		m_Context = this;
		m_Engine = new CaEngine();
		m_User = new CaUser();
		m_Info=new CaInfo();

		m_User.load();
	}


}