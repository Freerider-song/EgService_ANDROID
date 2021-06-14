package com.enernet.eg.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.enernet.eg.CaAes256;
import com.enernet.eg.CaApplication;
import com.enernet.eg.CaEngine;
import com.enernet.eg.CaResult;
import com.enernet.eg.EgDialog;
import com.enernet.eg.EgDialogYn;
import com.enernet.eg.IaResultHandler;
import com.enernet.eg.R;
import com.enernet.eg.ServiceMonitor;
import com.enernet.eg.StringUtil;
import com.enernet.eg.CaPref;
import com.enernet.eg.model.CaDiscount;
import com.enernet.eg.model.CaFamily;
import com.enernet.eg.model.CaPrice;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;

public class ActivityLogin extends BaseActivity implements IaResultHandler {

	private static final String TAG = "ActivityLogin";
	private EditText m_etUserId;
	private EditText m_etPassword;
	
	private Context m_Context;
	private CaPref m_Pref;
	
	String m_strMemberId;
	String m_strPassword;
	String m_strDeviceId;

	private EgDialog m_dlgError;
	private EgDialog m_dlgIdPasswordFailed;
	private EgDialog m_dlgNewVersionAvailable;
	EgDialogYn m_dlgYnExit;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		m_Context = getApplicationContext();
		m_Pref = new CaPref(m_Context);

		m_etUserId = findViewById(R.id.input_id);
		m_etPassword = findViewById(R.id.input_pw);
		
		TextView basicVer = findViewById(R.id.basic_version) ;
		basicVer.setText(getVersion());

		String savedLoginId = m_Pref.getValue(CaPref.PREF_MEMBER_ID, "");
		if (!savedLoginId.equals("")) {
			m_etUserId.setText(savedLoginId);
		}

		String savedPassword = m_Pref.getValue(CaPref.PREF_PASSWORD, "");
		if (!savedPassword.equals("")) {
			m_etPassword.setText(savedPassword);
		}

		// test
		double dKwh=12345.6789;
		String strKwh=CaApplication.m_Info.m_dfKwh.format(dKwh);

		double dWon=87654.321;
		String strWon=CaApplication.m_Info.m_dfWon.format(dWon);

		Log.i("TAG", "Kwh="+strKwh+", Won="+strWon);

		getPushId();


	}
	
	private String getVersion()
	{
		String version = "";
		try {
			PackageInfo packageInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
			version = packageInfo.versionName;
			version += " ";
		}
		catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		System.out.println("version : " + version);
		return version;
	}

	@Override
	public void onBackPressed() {
		Log.i("Login", "onBackPressed called...");

		promptAppExit();

	}

	public void promptAppExit() {
		View.OnClickListener LsnConfirmYes=new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("Setting", "Yes button clicked...");
				m_dlgYnExit.dismiss();

				// kill app
				finishAffinity();
				System.runFinalization();
				System.exit(0);
			}
		};

		View.OnClickListener LsnConfirmNo=new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("Setting", "No button clicked...");
				m_dlgYnExit.dismiss();
			}
		};

		m_dlgYnExit=new EgDialogYn(this, R.layout.dialog01yn,"앱을 종료하시겠습니까?", LsnConfirmYes, LsnConfirmNo);
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

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_btn: {

			m_strMemberId = m_etUserId.getText().toString();
			m_strPassword = m_etPassword.getText().toString();
			m_strDeviceId = m_Pref.getValue(CaPref.PREF_DEVICE_ID, "");

			String strPwdEnc= CaAes256.encrypt(m_strPassword);
			String strPwdDec= CaAes256.decrypt(strPwdEnc);

			Log.i("AES256", "enc="+strPwdEnc);
			Log.i("AES256", "dec="+strPwdDec);

			if (m_strMemberId.isEmpty() || m_strPassword.isEmpty()) {
				View.OnClickListener LsnConfirm = new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						m_dlgError.dismiss();
					}
				};

				m_dlgError = new EgDialog(this, R.layout.dialog01, "아이디와 비밀번호를 입력해 주세요", LsnConfirm);
				m_dlgError.show();
			}
			else {
				CaApplication.m_Engine.CheckLogin(m_strMemberId, m_strPassword, this, this);
			}
		}
		break;

		case R.id.btn_join: {

			// for test
			//Intent nextIntent = new Intent(this, ActivityAuth.class);
			//startActivity(nextIntent);

			// for test
			//CaApplication.m_Info.m_bSubscribingAsMainMember = false;
			//CaApplication.m_Info.m_nSeqAptHoSubscribing = 128;
			//CaApplication.m_Info.m_strMemberNameSubscribing = "박개동";
			//CaApplication.m_Info.m_strPhoneSubscribing = "01026007859";

			// for test
			//Intent nextIntent = new Intent(this, ActivitySubscribe.class);
			//startActivity(nextIntent);

			// for test
			//		Intent nextIntent = new Intent(this, ActivitySubscribedMain.class);
			//		startActivity(nextIntent);

			// for test
			//		Intent nextIntent = new Intent(this, ActivitySubscribedSub.class);
			//		startActivity(nextIntent);

			// for test
			//Intent nextIntent = new Intent(this, ActivityAck.class);
			//startActivity(nextIntent);

			// real process
			Intent it = new Intent(this, ActivityCandidate.class);
			startActivity(it);
		}
		break;

		case R.id.btn_change_password: {

			// for test
			//Intent nextIntent = new Intent(this, ActivityChangePasswordInput.class);
			//startActivity(nextIntent);

			// real process
			Intent it = new Intent(this, ActivityChangePasswordAuth.class);
			startActivity(it);
		}
		break;

		}
	}

	@Override
	public void onResult(CaResult Result) {

		if (Result.object==null) {
			Toast.makeText(m_Context, StringUtil.getString(this, R.string.tv_label_network_error), Toast.LENGTH_SHORT).show();
			return;
		}

		switch (Result.m_nCallback) {
			case CaEngine.CB_CHECK_LOGIN: {

				try {
					JSONObject jo = Result.object;
					boolean bIdPasswordOk = jo.getBoolean("is_id_password_ok");

					if (bIdPasswordOk) {
						m_Pref.setValue(CaPref.PREF_MEMBER_ID, m_strMemberId);
						m_Pref.setValue(CaPref.PREF_PASSWORD, m_strPassword);

						boolean bNewVersionAvailable=jo.getBoolean("is_new_version_available");
						boolean bMainMember=jo.getBoolean("is_main_member");
						boolean bSystemMember=jo.getBoolean("is_system_member");
						int nSeqMemberMain=jo.getInt("seq_member_main");
						int nSeqMemberSub=jo.getInt("seq_member_sub");

						CaApplication.m_Info.m_strMemberId = m_strMemberId;
						CaApplication.m_Info.m_nSeqMember=jo.getInt("seq_member");
						CaApplication.m_Info.m_strPassword = m_strPassword;
						CaApplication.m_Info.m_nSeqSite=jo.getInt("seq_site");
						CaApplication.m_Info.m_bMainMember = bMainMember;
						CaApplication.m_Info.m_bSystemMember = bSystemMember;

						CaApplication.m_Info.m_dtAuthRequested=parseDate(jo.getString("time_ack_request"));
						String strTimeAckRequested=CaApplication.m_Info.m_dfStd.format(CaApplication.m_Info.m_dtAuthRequested);

						CaApplication.m_Info.m_dtAuthResponsed=parseDate(jo.getString("time_ack_response"));
						String strTimeAckResponsed=CaApplication.m_Info.m_dfStd.format(CaApplication.m_Info.m_dtAuthResponsed);

						int nAckRequestTodayCount=jo.getInt("ack_request_today_count");
						int nAckResponseCodeLatest=jo.getInt("ack_response_code_latest");

						Log.i("TAG", "<< CheckLogin Results>>");
						Log.i("TAG", "IsNewVersionAvailable="+bNewVersionAvailable);
						Log.i("TAG", "IsMainMember="+bMainMember);
						Log.i("TAG", "IsSystemMember="+bSystemMember);
						Log.i("TAG", "SeqMemberMain="+nSeqMemberMain);
						Log.i("TAG", "SeqMemberSub="+nSeqMemberSub);
						Log.i("TAG", "TimeAckRequested="+strTimeAckRequested);
						Log.i("TAG", "TimeAckResponsed="+strTimeAckResponsed);
						Log.i("TAG", "AckRequestTodayCount="+nAckRequestTodayCount);
						Log.i("TAG", "AckResponseCodeLatest="+nAckResponseCodeLatest);

						if (bMainMember) {
							if (bNewVersionAvailable){
								showNewVersionAvailableDialog();
							}
							else {
								CaApplication.m_Engine.GetMemberInfo(CaApplication.m_Info.m_nSeqMember, this, this);
							}
						}
						else {
							if (nAckResponseCodeLatest==1) {
								if (bNewVersionAvailable){
									showNewVersionAvailableDialog();
								}
								else {
									CaApplication.m_Engine.GetMemberInfo(CaApplication.m_Info.m_nSeqMember, this, this);
								}
							}
							else {
								finish();

								Intent it = new Intent(this, ActivityAck.class);
								it.putExtra("ack_request_today_count", nAckRequestTodayCount);
								it.putExtra("ack_response_code_latest", nAckResponseCodeLatest);

								startActivity(it);
							}
						}

						if (bSystemMember) {
							startServiceMonitor();
						}

					}
					else {

						View.OnClickListener LsnConfirm=new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								m_dlgIdPasswordFailed.dismiss();
							}
						};

						m_dlgIdPasswordFailed=new EgDialog(this, R.layout.dialog02,
							"아이디와 비밀번호를 확인해 주세요.", LsnConfirm);
						m_dlgIdPasswordFailed.show();
					}
				}
				catch (JSONException e) {
					e.printStackTrace();
				}
			}
			break;
			
			case CaEngine.CB_GET_MEMBER_INFO: {
				Log.i(TAG, "Result of GetMemberInfo received...");

				try {
					JSONObject jo = Result.object;
					JSONObject joMemberInfo=jo.getJSONObject("member_info");

					CaApplication.m_Info.m_dtCreated=parseDate(joMemberInfo.getString("time_created"));
					CaApplication.m_Info.m_dtModified=parseDate(joMemberInfo.getString("time_modified"));
					CaApplication.m_Info.m_dtLasLogin=parseDate(joMemberInfo.getString("time_last_login"));
					CaApplication.m_Info.m_dtChangePassword=parseDate(joMemberInfo.getString("time_change_password"));

					CaApplication.m_Info.m_bMainMember = (joMemberInfo.getInt("member_main") == 1);
					CaApplication.m_Info.m_nSeqSite = joMemberInfo.getInt("seq_site");
					CaApplication.m_Info.m_nSeqMember = joMemberInfo.getInt("seq_member");
					CaApplication.m_Info.m_strMemberName = joMemberInfo.getString("member_name");
					CaApplication.m_Info.m_strSiteName = joMemberInfo.getString("site_name");
					CaApplication.m_Info.m_strSiteAddress = joMemberInfo.getString("site_address");
					CaApplication.m_Info.m_nSiteBuiltYear = joMemberInfo.getInt("site_built_year");
					CaApplication.m_Info.m_nSiteBuiltMonth = joMemberInfo.getInt("site_built_month");
					CaApplication.m_Info.m_dxSite = joMemberInfo.getDouble("site_x");
					CaApplication.m_Info.m_dySite = joMemberInfo.getDouble("site_y");
					CaApplication.m_Info.m_nSeqAptDong = joMemberInfo.getInt("seq_apt_dong");
					CaApplication.m_Info.m_strAptDongName = joMemberInfo.getString("apt_dong_name");
					CaApplication.m_Info.m_nSeqAptHo = joMemberInfo.getInt("seq_apt_ho");
					CaApplication.m_Info.m_strAptHoName = joMemberInfo.getString("apt_ho_name");
					CaApplication.m_Info.m_nAptHoArea = joMemberInfo.getInt("apt_ho_area");
					CaApplication.m_Info.m_strPhone = joMemberInfo.getString("phone_num");
					CaApplication.m_Info.m_strMail = joMemberInfo.getString("mail");
					CaApplication.m_Info.m_bNotiAll = (joMemberInfo.getInt("noti_all") == 1);
					CaApplication.m_Info.m_bNotiKwh = (joMemberInfo.getInt("noti_kwh") == 1);
					CaApplication.m_Info.m_bNotiWon = (joMemberInfo.getInt("noti_won") == 1);
					CaApplication.m_Info.m_bNotiPriceLevel = (joMemberInfo.getInt("noti_price_level") == 1);
					CaApplication.m_Info.m_bNotiUsageAtTime = (joMemberInfo.getInt("noti_usage_at_time") == 1);
					CaApplication.m_Info.m_dThresholdKwh = joMemberInfo.getDouble("threshold_kwh");
					CaApplication.m_Info.m_dThresholdWon = joMemberInfo.getDouble("threshold_won");
					CaApplication.m_Info.m_nNextPriceLevelPercent=joMemberInfo.getInt("next_price_level_percent");
					CaApplication.m_Info.m_nUsageNotiType=joMemberInfo.getInt("usage_noti_type");
					CaApplication.m_Info.m_nUsageNotiHour=joMemberInfo.getInt("usage_noti_hour");
					CaApplication.m_Info.m_nSiteReadDay = joMemberInfo.getInt("site_read_day");
					CaApplication.m_Info.m_nSeqMeter = joMemberInfo.getInt("seq_meter");
					CaApplication.m_Info.m_strMid = joMemberInfo.getString("meter_id");
					CaApplication.m_Info.m_strMeterMac = joMemberInfo.getString("meter_mac_address");
					CaApplication.m_Info.m_strMeterMaker = joMemberInfo.getString("meter_maker");
					CaApplication.m_Info.m_strMeterModel = joMemberInfo.getString("meter_model");
					CaApplication.m_Info.m_nMeterState = joMemberInfo.getInt("meter_state");
					CaApplication.m_Info.m_nMeterType = joMemberInfo.getInt("meter_type");
					CaApplication.m_Info.m_nMeterValidInstr = joMemberInfo.getInt("meter_valid_instr_integer");
					CaApplication.m_Info.m_dtMeterInstalled=parseDate(joMemberInfo.getString("time_meter_installed"));
					CaApplication.m_Info.m_dtPriceModified=parseDate(joMemberInfo.getString("time_price_modified"));
					CaApplication.m_Info.m_strPriceComment = joMemberInfo.getString("price_comment");
					CaApplication.m_Info.m_nDiscountFamily=joMemberInfo.getInt("discount_id_family");
                    CaApplication.m_Info.m_nDiscountSocial=joMemberInfo.getInt("discount_id_social");
                    CaApplication.m_Info.m_nMonthlyWonMethod=joMemberInfo.getInt("monthly_won_method");

                    // family list
                    JSONArray jaFamilyList=jo.getJSONArray("family_list");

                    Log.i("Login", "FamilyCount="+jaFamilyList.length());

                    CaApplication.m_Info.m_alFamily.clear();

                    for (int i=0; i<jaFamilyList.length(); i++) {
						CaFamily family=new CaFamily();

						JSONObject joFamily=jaFamilyList.getJSONObject(i);

						family.m_nSeqMember=joFamily.getInt("seq_member");
						family.m_bMain=(joFamily.getInt("main_member") == 1);
						family.m_strName=joFamily.getString("member_name");
						family.m_strPhone=joFamily.getString("member_phone");

						String strTimeLastLogin=joFamily.getString("time_last_login");
						if (strTimeLastLogin=="null") family.m_dtLastLogin=null;
						else family.m_dtLastLogin=parseDate(strTimeLastLogin);

						if (family.m_nSeqMember==CaApplication.m_Info.m_nSeqMember) continue;

						CaApplication.m_Info.m_alFamily.add(family);
					}

					// price_list
					CaApplication.m_Info.m_alPrice.clear();

					JSONArray ja=Result.object.getJSONArray("price_list");
					for (int i=0; i<ja.length(); i++) {
						JSONObject joPrice=ja.getJSONObject(i);

						CaPrice Price=new CaPrice();
						Price.m_nLevel=joPrice.getInt("pTerm");
						Price.m_nFrom=joPrice.getInt("nFrom");
						Price.m_nTo=joPrice.getInt("nTo");
						Price.m_dBase=joPrice.getDouble("pBasic");
						Price.m_dHeight=joPrice.getDouble("fHeight");
						Price.m_dRate=joPrice.getDouble("pValue");
						Price.m_strInterval=joPrice.getString("Interval");

						CaApplication.m_Info.m_alPrice.add(Price);
					}

					// discount_family
					CaApplication.m_Info.m_alDiscountFamily.clear();

					ja=Result.object.getJSONArray("discount_family_list");
					for (int i=0; i<ja.length(); i++) {
						JSONObject joDiscountFamily=ja.getJSONObject(i);

						CaDiscount Discount=new CaDiscount();
						Discount.m_nDiscountId=joDiscountFamily.getInt("discount_id");
						Discount.m_strDiscountName=joDiscountFamily.getString("discount_name");

						CaApplication.m_Info.m_alDiscountFamily.add(Discount);
					}

					// discount_social
					CaApplication.m_Info.m_alDiscountSocial.clear();

					ja=Result.object.getJSONArray("discount_social_list");
					for (int i=0; i<ja.length(); i++) {
						JSONObject joDiscountSocial=ja.getJSONObject(i);

						CaDiscount Discount=new CaDiscount();
						Discount.m_nDiscountId=joDiscountSocial.getInt("discount_id");
						Discount.m_strDiscountName=joDiscountSocial.getString("discount_name");

						CaApplication.m_Info.m_alDiscountSocial.add(Discount);
					}

					// alarm
					ja = jo.getJSONArray("alarm_list");
					CaApplication.m_Info.setAlarmList(ja);

					// notice
					JSONArray jaTop = jo.getJSONArray("notice_top_list");
					JSONArray jaNormal = jo.getJSONArray("notice_list");

					CaApplication.m_Info.setNoticeList(jaTop, jaNormal);

					// qna
					ja = jo.getJSONArray("qna_list");
					CaApplication.m_Info.setQnaList(ja);
				}
				catch (JSONException e) {
					e.printStackTrace();
				}

				finish();

				String strStartedBy=getIntent().getStringExtra("started_by");

				if ((strStartedBy!=null) && strStartedBy.equals("noti")) {
					Intent it = new Intent(this, ActivityAlarm.class);
					startActivity(it);
				}
				else {
					Intent it = new Intent(this, ActivityUsage.class);
					startActivity(it);
				}

			}
				break;

			default: {
				Log.i(TAG, "Unknown type result received");
			}
			break;

		} // end of switch
	}

	public void getPushId() {

		FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( ActivityLogin.this,  new OnSuccessListener<InstanceIdResult>() {
			@Override
			public void onSuccess(InstanceIdResult instanceIdResult) {
				String strToken = instanceIdResult.getToken();
				//Log.e("newToken", strToken);

				CaApplication.m_Info.m_strPushId=strToken;

			}
		});
	}

	public void showNewVersionAvailableDialog() {

		final Context Ctx=getApplicationContext();
		final ActivityLogin This=this;

		View.OnClickListener LsnConfirm=new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				m_dlgNewVersionAvailable.dismiss();

				CaApplication.m_Engine.GetMemberInfo(CaApplication.m_Info.m_nSeqMember, Ctx, This);
			}
		};

		m_dlgNewVersionAvailable=new EgDialog(this, R.layout.dialog01,
				"새 버전으로 업그레이드 해주세요.", LsnConfirm);
		m_dlgNewVersionAvailable.show();
	}

	public void startServiceMonitor() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			PowerManager pm = (PowerManager) getApplicationContext().getSystemService(POWER_SERVICE);
			boolean isWhiteListing = pm.isIgnoringBatteryOptimizations(getApplicationContext().getPackageName());

			if (!isWhiteListing) {
				Intent intent = new Intent();
				intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
				intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
				startActivity(intent);
			}
		}

		if (ServiceMonitor.s_Intent==null) {
			ServiceMonitor.s_Intent = new Intent(this, ServiceMonitor.class);
			startService(ServiceMonitor.s_Intent);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

//		if (ServiceMonitor.s_Intent != null) {
//			stopService(ServiceMonitor.s_Intent);
//			ServiceMonitor.s_Intent=null;
//		}
	}
}
