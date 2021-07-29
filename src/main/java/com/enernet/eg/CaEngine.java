package com.enernet.eg;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.util.Log;

public class CaEngine {

	public static final int CB_NONE = 0;
	public static final int CB_CHECK_LOGIN = 1001;
	public static final int CB_GET_MEMBER_INFO = 1002;
	public static final int CB_POST_FILE = 1003;
	public static final int CB_GET_USAGE_CURRENT = 1004;
	public static final int CB_GET_USAGE_MONTHLY = 1005;
	public static final int CB_GET_USAGE_WEEKLY = 1006;
	public static final int CB_GET_USAGE_DAILY = 1007;
	public static final int CB_GET_SITE_LIST = 1008;
	public static final int CB_GET_APT_DONG_LIST = 1009;
	public static final int CB_GET_APT_HO_LIST = 1010;
	public static final int CB_SET_MEMBER_INFO = 1011;
	public static final int CB_IS_EXISTING_MEMBER_ID = 1012;
	public static final int CB_SET_ALARM_INFO = 1013;
	public static final int CB_SET_PASSWORD = 1014;
	public static final int CB_GET_NOTICE_LIST = 1015;
	public static final int CB_GET_OVERED_KWH = 1017;
	public static final int CB_GET_OVERED_WON = 1018;
	public static final int CB_UNSUBSCRIBE = 1019;
	public static final int CB_GET_MEMBER_CANDIDATE_INFO = 1020;
	public static final int CB_CREATE_MEMBER_MAIN = 1022;
	public static final int CB_CREATE_MEMBER_SUB = 1023;
    public static final int CB_REQUEST_ACK_MEMBER = 1024;
	public static final int CB_RESPONSE_ACK_MEMBER = 1025;
	public static final int CB_GET_MEMBER_ID_SEQ = 1026;
	public static final int CB_CHANGE_PASSWORD = 1027;
	public static final int CB_GET_USAGE_OF_ONE_DAY = 1028;
	public static final int CB_GET_USAGE_OF_ONE_MONTH = 1029;
	public static final int CB_GET_USAGE_OF_ONE_YEAR = 1030;
	public static final int CB_GET_USAGE_OF_ONE_METER = 1031;
	public static final int CB_GET_USAGE_CURRENT_OF_ONE_SITE = 1032;
	public static final int CB_CHANGE_MEMBER_SETTINS = 1033;
	public static final int CB_GET_ALARM_LIST = 1034;
	public static final int CB_SET_ALARM_LIST_AS_READ = 1035;
	public static final int CB_SET_NOTICE_LIST_AS_READ = 1036;
	public static final int CB_GET_FAQ_LIST = 1037;
	public static final int CB_GET_QNA_LIST = 1038;
	public static final int CB_SET_QNA_LIST_AS_READ = 1039;
	public static final int CB_CREATE_QUESTION = 1040;
	public static final int CB_GET_MINUTE_SINCE_LAST_LP = 1041;
	public static final int CB_GET_SITE_USAGE_FRONT = 1042;
	public static final int CB_GET_SITE_DAILY_USAGE = 1043;

	public static final String[] NO_CMD_ARGS = new String[]{};

	public static final int ALARM_TYPE_UNKNOWN = 0;
	public static final int ALARM_TYPE_REQUEST_ACK_MEMBER = 1001;
	public static final int ALARM_TYPE_RESPONSE_ACK_MEMBER_ACCEPTED = 1002;
	public static final int ALARM_TYPE_RESPONSE_ACK_MEMBER_REJECTED = 1003;
	public static final int ALARM_TYPE_RESPONSE_ACK_MEMBER_CANCELED = 1004;
	public static final int ALARM_TYPE_NOTI_KWH = 1101;
	public static final int ALARM_TYPE_NOTI_WON = 1102;
	public static final int ALARM_TYPE_NOTI_PRICE_LEVEL = 1103;
	public static final int ALARM_TYPE_NOTI_USAGE = 1104;
	public static final int ALARM_TYPE_NOTI_TRANS = 1110; // 변압기

	public static final int AUTH_TYPE_UNKNOWN = 1000;
	public static final int AUTH_TYPE_SUBSCRIBE = 1001;
	public static final int AUTH_TYPE_CHANGE_PASSWORD = 1002;

	public static final int MENU_USAGE = 100;
	public static final int MENU_USAGE_DAILY = 101;
	public static final int MENU_USAGE_MONTHLY = 102;
	public static final int MENU_USAGE_YEARLY = 103;
	public static final int MENU_SITE_STATE = 200;
	public static final int MENU_POINT = 300;
	public static final int MENU_ELEC_INFO_SAVE = 401;
	public static final int MENU_ELEC_INFO_PRICE = 402;
	public static final int MENU_ALARM = 500;
	public static final int MENU_NOTICE = 600;
	public static final int MENU_FAQ = 710;
	public static final int MENU_QNA = 720;
	public static final int MENU_SETTING = 800;
	public static final int MENU_LOGOUT = 900;

	public CaEngine() {

	}

	public final CaResult executeCommand(CaArg Arg, final int nCallMethod, final boolean bSync, final boolean bShowWaitDialog, Context Ctx, IaResultHandler ResultHandler) {

		CaTask Task = new CaTask(nCallMethod, bShowWaitDialog, Ctx, ResultHandler);
		Task = (CaTask) Task.execute(Arg);

		CaResult Result = null;

		if (bSync) {
			try {
				Result = Task.get();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return Result;
	}

	public void GetMemberInfo(final int nSeqMember, Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("GetMemberInfo", NO_CMD_ARGS, null);
		Arg.addArg("SeqMember", nSeqMember);

		executeCommand(Arg, CB_GET_MEMBER_INFO, false, true, Ctx, ResultHandler);
	}

	public void GetUsageCurrent(final int nSeqSite, final int nSeqMeter, Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("GetUsageCurrent", NO_CMD_ARGS, null);
		Arg.addArg("SeqSite", nSeqSite);
		Arg.addArg("SeqMeter", nSeqMeter);

		executeCommand(Arg, CB_GET_USAGE_CURRENT, false, true, Ctx, ResultHandler);
	}

	public void GetUsageMonthly(final int nSeqSite, final int nSeqMeter, Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("GetUsageMonthly", NO_CMD_ARGS, null);
		Arg.addArg("SeqSite", nSeqSite);
		Arg.addArg("SeqMeter", nSeqMeter);

		executeCommand(Arg, CB_GET_USAGE_MONTHLY, false, true, Ctx, ResultHandler);
	}

	public void GetUsageWeekly(final int nSeqSite, final int nSeqMeter, Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("GetUsageWeekly", NO_CMD_ARGS, null);
		Arg.addArg("SeqSite", nSeqSite);
		Arg.addArg("SeqMeter", nSeqMeter);

		executeCommand(Arg, CB_GET_USAGE_WEEKLY, false, true, Ctx, ResultHandler);
	}

	public void GetSiteList(Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("GetSiteList", NO_CMD_ARGS, null);

		executeCommand(Arg, CB_GET_SITE_LIST, false, true, Ctx, ResultHandler);
	}

	public void GetAptDongList(final int nSeqSite, Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("GetAptDongList", NO_CMD_ARGS, null);
		Arg.addArg("SeqSite", nSeqSite);

		executeCommand(Arg, CB_GET_APT_DONG_LIST, false, true, Ctx, ResultHandler);
	}

	public void GetAptHoList(final int nSeqSite, final int nSeqDong, Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("GetAptHoList", NO_CMD_ARGS, null);
		Arg.addArg("SeqSite", nSeqSite);
		Arg.addArg("SeqDong", nSeqDong);

		executeCommand(Arg, CB_GET_APT_HO_LIST, false, true, Ctx, ResultHandler);
	}

	public void SetMemberInfo(final String residentId, final String residentPw, final String usedValue, final String chargeValue, final String residentSeq, final String usedYn, final String chargeYn,
							  final String residentName, final String residentPhoneNum, final String residentEmail, final String os, Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("/siteInfo/SetMemberInfo.json", NO_CMD_ARGS, null);
		Arg.addArg("residentId", residentId);
		Arg.addArg("residentPw", residentPw);
		Arg.addArg("usedValue", usedValue);
		Arg.addArg("chargeValue", chargeValue);
		Arg.addArg("residentSeq", residentSeq);
		Arg.addArg("usedYn", usedYn);
		Arg.addArg("chargeYn", chargeYn);
		Arg.addArg("residentName", residentName);
		Arg.addArg("residentPhoneNum", residentPhoneNum);
		Arg.addArg("residentEmail", residentEmail);
		Arg.addArg("OS", os);

		executeCommand(Arg, CB_SET_MEMBER_INFO, false, true, Ctx, ResultHandler);
	}

	public void CheckLogin(final String strMemberId, final String strPassword, Context Ctx, IaResultHandler ResultHandler) {
		Log.i("ENGINE", "MemberId=" + strMemberId + ", Password=" + strPassword);

		Calendar calToday = Calendar.getInstance();
		calToday.add(Calendar.DATE, 0);
		SimpleDateFormat myyyyMMddFormat = new SimpleDateFormat("yyMMdd");
		String m_dtToday = myyyyMMddFormat.format(calToday.getTime())+"1";

		CaArg Arg = new CaArg("CheckLogin", NO_CMD_ARGS, null);
		Arg.addArg("MemberId", strMemberId);
		Arg.addArg("Password", strPassword);
		Arg.addArg("DeviceId", CaApplication.m_Info.m_strPushId);
		Arg.addArg("Os", "ANDROID");
		Arg.addArg("Version", m_dtToday);

		executeCommand(Arg, CB_CHECK_LOGIN, false, true, Ctx, ResultHandler);
	}

	public void IsExistingMemberId(final String residentId, Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("/siteInfo/IsExistingMemberId.json", NO_CMD_ARGS, null);
		Arg.addArg("residentId", residentId);

		executeCommand(Arg, CB_IS_EXISTING_MEMBER_ID, false, true, Ctx, ResultHandler);
	}

	public void SetAlarmInfo(final int nSeqMember, final double dThresholdKwh, final double dThresholdWon,
							 final boolean bNotiKwh, final boolean bNotiWon, Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("SetAlarmInfo", NO_CMD_ARGS, null);
		Arg.addArg("SeqMember", nSeqMember);
		Arg.addArg("ThresholdKwh", dThresholdKwh);
		Arg.addArg("ThresholdWon", dThresholdWon);
		Arg.addArg("NotiKwh", bNotiKwh);
		Arg.addArg("NotiWon", bNotiWon);

		executeCommand(Arg, CB_SET_ALARM_INFO, false, true, Ctx, ResultHandler);
	}

	public void SetPassword(final String residentId, final String newPassword, Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("/userInfo/SetPassword.json", NO_CMD_ARGS, null);
		Arg.addArg("residentId", residentId);
		Arg.addArg("newPassword", newPassword);

		executeCommand(Arg, CB_SET_PASSWORD, false, true, Ctx, ResultHandler);
	}

	public void GetNoticeList(final int nSeqMember, final String strTimeCreatedMax, final int nCountNotice, Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("GetNoticeList", NO_CMD_ARGS, null);
		Arg.addArg("SeqMember", nSeqMember);
		Arg.addArg("TimeCreatedMax", strTimeCreatedMax);
		Arg.addArg("CountNotice", nCountNotice);

		executeCommand(Arg, CB_GET_NOTICE_LIST, false, true, Ctx, ResultHandler);
	}

	public void GetOveredKwh(final String appUserSeq, final String siteSeq, Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("/userInfo/GetOveredKwh.json", NO_CMD_ARGS, null);
		Arg.addArg("appUserSeq", appUserSeq);
		Arg.addArg("siteSeq", siteSeq);

		executeCommand(Arg, CB_GET_OVERED_KWH, false, true, Ctx, ResultHandler);
	}

	public void GetOveredWon(final String appUserSeq, final String siteSeq, Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("/userInfo/GetOveredWon.json", NO_CMD_ARGS, null);
		Arg.addArg("appUserSeq", appUserSeq);
		Arg.addArg("siteSeq", siteSeq);

		executeCommand(Arg, CB_GET_OVERED_WON, false, true, Ctx, ResultHandler);
	}

	public void Unsubscribe(final int nSeqMember, Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("activity_login/Unsubscribe.json", NO_CMD_ARGS, null);
		Arg.addArg("appUserSeq", nSeqMember);

		executeCommand(Arg, CB_UNSUBSCRIBE, false, true, Ctx, ResultHandler);
	}

	public void GetMemberCandidateInfo(final int nSeqHo, final String strName, final String strPhone, Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("GetMemberCandidateInfo", NO_CMD_ARGS, null);
		Arg.addArg("SeqHo", nSeqHo);
		Arg.addArg("Name", strName);
		Arg.addArg("Phone", strPhone);

		executeCommand(Arg, CB_GET_MEMBER_CANDIDATE_INFO, false, true, Ctx, ResultHandler);
	}

	public void CreateMemberMain(final int nSeqHo, final String strName, final String strPhone, final String strMemberId, final String strPassword,
			Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("CreateMemberMain", NO_CMD_ARGS, null);
		Arg.addArg("SeqHo", nSeqHo);
		Arg.addArg("Name", strName);
		Arg.addArg("Phone", strPhone);
		Arg.addArg("MemberId", strMemberId);
		Arg.addArg("Password", strPassword);
		Arg.addArg("DeviceId", CaApplication.m_Info.m_strPushId);
		Arg.addArg("Os", "ANDROID");

		executeCommand(Arg, CB_CREATE_MEMBER_MAIN, false, true, Ctx, ResultHandler);
	}

	public void CreateMemberSub(final int nSeqHo, final String strName, final String strPhone, final String strMemberId, final String strPassword,
								 Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("CreateMemberSub", NO_CMD_ARGS, null);
		Arg.addArg("SeqHo", nSeqHo);
		Arg.addArg("Name", strName);
		Arg.addArg("Phone", strPhone);
		Arg.addArg("MemberId", strMemberId);
		Arg.addArg("Password", strPassword);
		Arg.addArg("DeviceId", CaApplication.m_Info.m_strPushId);
		Arg.addArg("Os", "ANDROID");

		executeCommand(Arg, CB_CREATE_MEMBER_SUB, false, true, Ctx, ResultHandler);
	}

    public void RequestAckMember(final int nSeqMemberAckRequester, Context Ctx, IaResultHandler ResultHandler) {
        CaArg Arg = new CaArg("RequestAckMember", NO_CMD_ARGS, null);
        Arg.addArg("SeqMemberAckRequester", nSeqMemberAckRequester);

        executeCommand(Arg, CB_REQUEST_ACK_MEMBER, false, true, Ctx, ResultHandler);
    }

	public void ResponseAckMember(final int nSeqMemberSub, final int nAck, final boolean bShowWaitDialog, Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("ResponseAckMember", NO_CMD_ARGS, null);
		Arg.addArg("SeqMemberSub", nSeqMemberSub);
		Arg.addArg("Ack", nAck);

		executeCommand(Arg, CB_RESPONSE_ACK_MEMBER, false, bShowWaitDialog, Ctx, ResultHandler);
	}

	public void GetMemberIdSeq(final String strName, final String strPhone, Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("GetMemberIdSeq", NO_CMD_ARGS, null);
		Arg.addArg("Name", strName);
		Arg.addArg("Phone", strPhone);

		executeCommand(Arg, CB_GET_MEMBER_ID_SEQ, false, true, Ctx, ResultHandler);
	}

	public void ChangePassword(final int nSeqMember, final String strPasswordNew, Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("ChangePassword", NO_CMD_ARGS, null);
		Arg.addArg("SeqMember", nSeqMember);
		Arg.addArg("PasswordNew", strPasswordNew);

		executeCommand(Arg, CB_CHANGE_PASSWORD, false, true, Ctx, ResultHandler);
	}

	public void GetUsageOfOneMeter(final int nSeqSite, final int nSeqMeter, final String strFromYYYYMMdd, final String strToYYYYMMddhhmm, Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("GetUsageOfOneMeter", NO_CMD_ARGS, null);
		Arg.addArg("SeqSite", nSeqSite);
		Arg.addArg("SeqMeter", nSeqMeter);
		Arg.addArg("From", strFromYYYYMMdd);
		Arg.addArg("To", strToYYYYMMddhhmm);

		executeCommand(Arg, CB_GET_USAGE_OF_ONE_METER, false, true, Ctx, ResultHandler);
	}

	public void GetUsageOfOneDay(final int nSeqSite, final int nSeqMeter, final int nYear, final int nMonth, final int nDay, Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("GetUsageOfOneDay", NO_CMD_ARGS, null);
		Arg.addArg("SeqSite", nSeqSite);
		Arg.addArg("SeqMeter", nSeqMeter);
		Arg.addArg("Year", nYear);
		Arg.addArg("Month", nMonth);
		Arg.addArg("Day", nDay);

		executeCommand(Arg, CB_GET_USAGE_OF_ONE_DAY, false, true, Ctx, ResultHandler);
	}

	public void GetUsageOfOneMonth(final int nSeqSite, final int nSeqMeter, final int nYear, final int nMonth, Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("GetUsageOfOneMonth", NO_CMD_ARGS, null);
		Arg.addArg("SeqSite", nSeqSite);
		Arg.addArg("SeqMeter", nSeqMeter);
		Arg.addArg("Year", nYear);
		Arg.addArg("Month", nMonth);

		executeCommand(Arg, CB_GET_USAGE_OF_ONE_MONTH, false, true, Ctx, ResultHandler);
	}

	public void GetUsageOfOneYear(final int nSeqSite, final int nSeqMeter, final int nYear, Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("GetUsageOfOneYear", NO_CMD_ARGS, null);
		Arg.addArg("SeqSite", nSeqSite);
		Arg.addArg("SeqMeter", nSeqMeter);
		Arg.addArg("Year", nYear);

		executeCommand(Arg, CB_GET_USAGE_OF_ONE_YEAR, false, true, Ctx, ResultHandler);
	}

	public void GetUsageCurrentOfOneSite(final int nSeqSite, Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("GetUsageCurrentOfOneSite", NO_CMD_ARGS, null);
		Arg.addArg("SeqSite", nSeqSite);

		executeCommand(Arg, CB_GET_USAGE_CURRENT_OF_ONE_SITE, false, true, Ctx, ResultHandler);
	}

	public void ChangeMemberSettins(final int nSeqMember, final boolean bNotiAll, final boolean bNotiKwh, final boolean bNotiWon,
		final boolean bNotiPriceLevel, final boolean bNotiUsage, final int nDiscountFamily, final int nDiscountSocial,
									final double dThresholdKwh, final double dThresholdWon,
									final int nNextPriceLevelPercent, final int nUsageNotiType, final int nUsageNotiHour,
									Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("ChangeMemberSettings", NO_CMD_ARGS, null);
		Arg.addArg("SeqMember", nSeqMember);
		Arg.addArg("NotiAll", bNotiAll ? 1 : 0);
		Arg.addArg("NotiKwh", bNotiKwh ? 1 : 0);
		Arg.addArg("NotiWon", bNotiWon ? 1 : 0);
		Arg.addArg("NotiPriceLevel", bNotiPriceLevel ? 1 : 0);
		Arg.addArg("NotiUsage", bNotiUsage ? 1 : 0);
		Arg.addArg("DiscountFamily", nDiscountFamily);
		Arg.addArg("DiscountSocial", nDiscountSocial);
		Arg.addArg("ThresholdKwh", dThresholdKwh);
		Arg.addArg("ThresholdWon", dThresholdWon);
		Arg.addArg("NextPriceLevelPercent", nNextPriceLevelPercent);
		Arg.addArg("UsageNotiType", nUsageNotiType);
		Arg.addArg("UsageNotiHour", nUsageNotiHour);

		executeCommand(Arg, CB_CHANGE_MEMBER_SETTINS, false, true, Ctx, ResultHandler);
	}

	public void GetAlarmList(final int nSeqMember, final int nCountMax, Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("GetAlarmList", NO_CMD_ARGS, null);
		Arg.addArg("SeqMember", nSeqMember);
		Arg.addArg("CountMax", nCountMax);

		executeCommand(Arg, CB_GET_ALARM_LIST, false, true, Ctx, ResultHandler);
	}

	public void SetAlarmListAsRead(final int nSeqMember, final String strSeqAlarmList, Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("SetAlarmListAsRead", NO_CMD_ARGS, null);
		Arg.addArg("SeqMember", nSeqMember);
		Arg.addArg("SeqAlarmList", strSeqAlarmList);

		executeCommand(Arg, CB_SET_ALARM_LIST_AS_READ, false, true, Ctx, ResultHandler);
	}

	public void SetNoticeListAsRead(final int nSeqMember, final String strSeqNoticeList, Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("SetNoticeListAsRead", NO_CMD_ARGS, null);
		Arg.addArg("SeqMember", nSeqMember);
		Arg.addArg("SeqNoticeList", strSeqNoticeList);

		executeCommand(Arg, CB_SET_NOTICE_LIST_AS_READ, false, true, Ctx, ResultHandler);
	}

	public void GetFaqList(final int nSeqSite, Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("GetFaqList", NO_CMD_ARGS, null);
		Arg.addArg("SeqSite", nSeqSite);

		executeCommand(Arg, CB_GET_FAQ_LIST, false, true, Ctx, ResultHandler);
	}

	public void GetQnaList(final int nSeqMember, Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("GetQnaList", NO_CMD_ARGS, null);
		Arg.addArg("SeqMember", nSeqMember);

		executeCommand(Arg, CB_GET_QNA_LIST, false, true, Ctx, ResultHandler);
	}

	public void SetQnaListAsRead(final int nSeqMember, final String strSeqQnaList, Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("SetQnaListAsRead", NO_CMD_ARGS, null);
		Arg.addArg("SeqMember", nSeqMember);
		Arg.addArg("SeqQnaList", strSeqQnaList);

		executeCommand(Arg, CB_SET_QNA_LIST_AS_READ, false, true, Ctx, ResultHandler);
	}

	public void CreateQuestion(final int nSeqMember, final String strQuestion, Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("CreateQuestion", NO_CMD_ARGS, null);
		Arg.addArg("SeqMember", nSeqMember);
		Arg.addArg("Question", strQuestion);

		executeCommand(Arg, CB_CREATE_QUESTION, false, true, Ctx, ResultHandler);
	}

	public void GetMinuteSinceLastLp(Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("GetMinuteSinceLastLp", NO_CMD_ARGS, null);

		executeCommand(Arg, CB_GET_MINUTE_SINCE_LAST_LP, false, false, Ctx, ResultHandler);
	}

	public void GetSiteUsageFront(final int nSeqSite, final int Year, final int Month, final int Day, Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("GetSiteUsageFront", NO_CMD_ARGS, null);
		Arg.addArg("SeqSite", nSeqSite);
		Arg.addArg("Year", Year);
		Arg.addArg("Month", Month);
		Arg.addArg("Day", Day);

		executeCommand(Arg, CB_GET_SITE_USAGE_FRONT, false, true, Ctx, ResultHandler);
	}

	public void GetSiteDailyUsage(final int nSeqSite, final String DateFrom, final String DateTo, Context Ctx, IaResultHandler ResultHandler) {
		CaArg Arg = new CaArg("GetSiteDailyUsage", NO_CMD_ARGS, null);
		Arg.addArg("SeqSite", nSeqSite);
		Arg.addArg("DateFrom", DateFrom);
		Arg.addArg("DateTo", DateTo);

		executeCommand(Arg, CB_GET_SITE_DAILY_USAGE, false, true, Ctx, ResultHandler);
	}

	public static HashMap<String, String> parseBasicResult(final CaResult Result) {
		if (Result == null) return null;

		switch (Result.m_nCallback) {
			case CB_SET_MEMBER_INFO:
			case CB_CHECK_LOGIN:
			case CB_IS_EXISTING_MEMBER_ID:
			case CB_SET_ALARM_INFO:
			case CB_SET_PASSWORD:
			case CB_UNSUBSCRIBE:
			case CB_POST_FILE: {
				if (Result.object == null) return null;

				try {
					HashMap<String, String> mapRes = new HashMap<>();
					//JSONObject jo = Result.object.getJSONObject("resultMsg");
					JSONObject jo = Result.object;
					mapRes.put("resultCode", jo.getString("resultCode"));
					mapRes.put("resultMessage", jo.getString("resultMessage"));
					return mapRes;
				} catch (JSONException ex) {
					ex.printStackTrace();
				}
			}
			break;

			default:
				return null;
		}

		return null;

	}

	public static HashMap<String, String> parseGetOveredKwh(final CaResult Result) {

		if (Result == null) return null;
		if (CB_GET_OVERED_KWH != Result.m_nCallback) return null;
		if (Result.object == null) return null;

		try {
			HashMap<String, String> mapRes = new HashMap<>();
			JSONObject jo = Result.object.getJSONObject("usedInfo");
			mapRes.put("warningValue", jo.getString("warningValue"));
			return mapRes;
		} catch (JSONException ex) {
			ex.printStackTrace();
		}

		return null;

	}

	public static HashMap<String, String> parseGetOveredWon(final CaResult Result) {

		if (Result == null) return null;
		if (CB_GET_OVERED_WON != Result.m_nCallback) return null;
		if (Result.object == null) return null;

		try {
			HashMap<String, String> mapRes = new HashMap<>();
			JSONObject jo = Result.object.getJSONObject("usedInfo");
			mapRes.put("warningCharge", jo.getString("warningCharge"));
			return mapRes;
		} catch (JSONException ex) {
			ex.printStackTrace();
		}

		return null;

	}


}