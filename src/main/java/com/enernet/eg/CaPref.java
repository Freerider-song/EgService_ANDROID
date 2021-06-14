package com.enernet.eg;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class CaPref {

    private final String PREF_NAME = "TIDE_PREF";  //저장될 파일 이름
    public final static int MODE = Activity.MODE_PRIVATE; //만약 모드를 바꾸고 싶다면!! 디폴트 : Activity.MODE_PRIVATE

    public static final String PREF 				= "MOBILE_DEVICE_LOCK";
	public static final String PREF_PHONENUM 		= "PHONENUM";
	public static final String PREF_DEVICE_ID		= "deviceId";
	
	public static final String PREF_MEMBER_ID		= "MEMBER_ID";
	public static final String PREF_PASSWORD		= "PASSWORD";
	
	public static final String PREF_METER_ID			= "meterId";
	public static final String PREF_SITE_SEQ			= "siteSeq";
	public static final String PREF_APP_USER_SEQ		= "appUserSeq";
	public static final String PREF_RESIDENT_NAME		= "residentName";
	public static final String PREF_RESIDENT_PHONENUM	= "residentPhoneNum";
	public static final String PREF_RESIDENT_EMAIL		= "residentEmail";
	public static final String PREF_RESIDENT_ADDR_1		= "address_1";
	public static final String PREF_RESIDENT_ADDR_2		= "address_2";
	public static final String PREF_SITE_NAME			= "siteName";
	public static final String PREF_USE_OVER_YN			= "useOverYn";
	public static final String PREF_USE_OVER_VALUE		= "useOverValue";
	public static final String PREF_CHARGE_OVER_YN		= "useChargeYn";
	public static final String PREF_CHARGE_OVER_VALUE	= "usechargeValue";
	public static final String PREF_READ_DAY			= "readDay";

	///////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	

    static Context mContext;

    public CaPref(Context c) {
        mContext = c;
    }

    public void setValue(String key, String value) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,MODE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(key, value);
        editor.commit();
    }

    public void setValue(String key, boolean value) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putBoolean(key, value);
        editor.commit();
    }

    public void setValue(String key, int value) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putInt(key, value);
        editor.commit();
    }

    public String getValue(String key, String dftValue) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,Activity.MODE_PRIVATE);

        try {
            return pref.getString(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }

    }

    public int getValue(String key, int dftValue) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,Activity.MODE_PRIVATE);

        try {
            return pref.getInt(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }

    }

    public boolean getValue(String key, boolean dftValue) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,Activity.MODE_PRIVATE);

        try {
            return pref.getBoolean(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }
    }
    
    public void clear() {
    	 SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,Activity.MODE_PRIVATE);
    	 pref.edit().clear();
	}
    
    public void remove(String key) {
    	 SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,Activity.MODE_PRIVATE);
    	 pref.edit().remove(key);
	}
}
