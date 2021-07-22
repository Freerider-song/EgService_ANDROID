package com.enernet.eg;

import android.content.Context;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.enernet.eg.model.CaAlarm;
import com.enernet.eg.model.CaDiscount;
import com.enernet.eg.model.CaFamily;
import com.enernet.eg.model.CaFaq;
import com.enernet.eg.model.CaNotice;
import com.enernet.eg.model.CaPrice;
import com.enernet.eg.model.CaQna;
import com.enernet.eg.model.CaUsage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CaInfo {

    public static final double KWH_TO_GAS = 0.46625;
    public static final int DEFAULT_REQUEST_NOTICE_COUNT = 10;

    public SimpleDateFormat m_dfStd=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public SimpleDateFormat m_dfyyyyMMddhhmmStd=new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public SimpleDateFormat m_dfyyyyMMddhhmm=new SimpleDateFormat("yyyyMMddHHmm");
    public SimpleDateFormat m_dfyyyyMMddhhmmss=new SimpleDateFormat("yyyyMMddHHmmss");
    public SimpleDateFormat m_dfyyyyMMdd=new SimpleDateFormat("yyyyMMdd");
    public SimpleDateFormat m_dfyyyyMMddhhmm_ampm=new SimpleDateFormat("yyyy-MM-dd hh:mm a");

    public DecimalFormat m_dfKwh = new DecimalFormat("0.#"); // 12345.7
    public DecimalFormat m_dfPercent = new DecimalFormat("0.##");
    public DecimalFormat m_dfWon = new DecimalFormat("#,##0"); // 87,654

    public int m_nSeqMember=0;
    public String m_strMemberId="";
    public String m_strPassword="";
    public String m_strMemberName="";
    public String m_strPhone="";
    public String m_strMail="";
    public boolean m_bMainMember=false;
    public boolean m_bSystemMember=false;
    public Date m_dtCreated=null;
    public Date m_dtModified=null;
    public Date m_dtLasLogin=null;
    public Date m_dtChangePassword=null;
    public Date m_dtAuthRequested=null;
    public Date m_dtAuthResponsed=null;

    public boolean m_bHoliday = true;

    public boolean m_bNotiAll=true;
    public boolean m_bNotiKwh=true;
    public boolean m_bNotiWon=true;
    public boolean m_bNotiPriceLevel=true;
    public boolean m_bNotiUsageAtTime=true;
    public double m_dThresholdKwh=0.0;
    public double m_dThresholdWon=0.0;
    public int m_nNextPriceLevelPercent=0;
    public int m_nUsageNotiType=3; // 0=매일, 1=매주일요일, 2=매주월요일, 3=매월1일, 4=매월검침일
    public int m_nUsageNotiHour=9;

    public int m_nSeqSite=0;
    public String m_strSiteAddress="";
    public String m_strSiteName="";
    public int m_nSiteBuiltYear=0;
    public int m_nSiteBuiltMonth=0;
    public String m_strSiteServerUrl="";
    public int m_nSiteReadDay=1;
    public double m_dxSite=0.0;
    public double m_dySite=0.0;
    public int m_nSeqAptDong=0;
    public String m_strAptDongName="";
    public int m_nSeqAptHo=0;
    public String m_strAptHoName="";
    public int m_nAptHoArea=0;
    public int m_nSeqMeter=0;
    public String m_strMeterMac="";
    public String m_strMid="";
    public String m_strMeterMaker="";
    public String m_strMeterModel="";
    public int m_nMeterState=0; // 0=unknown, 1=운영중, 2=보관중, 3=수리중
    public int m_nMeterType=0; // 0=unknown, 1=p-type, 2=e-type, 3=g-type, 4=ae-type
    public int m_nMeterValidInstr=1;
    public Date m_dtMeterInstalled=null;
    public Date m_dtPriceModified=null;
    public String m_strPriceComment="";
    public String m_strPushId="";

    //main
    public double m_wonCurr=0.0;
    public double m_kwhCurr =0.0;
    public String m_dtUpdate = "";
    public double m_wonExpected =0.0;
    public double m_wonPrevMonth = 0.0;
    public double m_wonPrevYear = 0.0;

    //site state

    public String m_strSiteUpdate = "";
    public double m_dSiteKwhFromMonth = 0.0;
    public double m_dSiteKwhFromMonthPrevYear = 0.0;

    public int m_nAuthType=CaEngine.AUTH_TYPE_UNKNOWN;

    public boolean m_bSubscribingAsMainMember=false;
    public int m_nSeqAptHoSubscribing=0;
    public String m_strMemberNameSubscribing="";
    public String m_strPhoneSubscribing="";

    public ArrayList<CaPrice> m_alPrice=new ArrayList<>();
    public ArrayList<CaUsage> m_alUsage=new ArrayList<>();

    public ArrayList<CaDiscount> m_alDiscountFamily=new ArrayList<>();
    public ArrayList<CaDiscount> m_alDiscountSocial=new ArrayList<>();

    public ArrayList<CaFamily> m_alFamily=new ArrayList<>();
    public ArrayList<CaAlarm> m_alAlarm=new ArrayList<>();
    public ArrayList<CaNotice> m_alNotice=new ArrayList<>();
    public ArrayList<CaFaq> m_alFaq=new ArrayList<>();
    public ArrayList<CaQna> m_alQna=new ArrayList<>();

    public int m_nDiscountFamily=0;
    public int m_nDiscountSocial=0;
    public int m_nMonthlyWonMethod=0;

    public int m_nTransState=-1;

    public Date m_dtNoticeCreatedMaxForNextRequest=null;

    public CaPrice findPrice(double dKwh) {
        double dHeightCum=0.0;

        for (int i=0; i<m_alPrice.size(); i++) {
            CaPrice Price=m_alPrice.get(i);
            dHeightCum += Price.m_dHeight;

            if (dKwh <= dHeightCum) return Price;
        }

        return m_alPrice.get(m_alPrice.size()-1);
    }


    public String getPhoneNumber() {
        TelephonyManager telephony = (TelephonyManager) CaApplication.m_Context.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber ="";

        try {
            if (telephony.getLine1Number() != null) {
                phoneNumber = telephony.getLine1Number();
            }
            else {
                if (telephony.getSimSerialNumber() != null) {
                    phoneNumber = telephony.getSimSerialNumber();
                }
            }
        }
        catch (SecurityException e) {
            e.printStackTrace();
            Log.e("CaInfo", "getPhoneNumber failed due to SecurityException = "+e.getCause());
        }

        if (phoneNumber.startsWith("+82")) {
            phoneNumber = phoneNumber.replace("+82", "0"); // +8210xxxxyyyy 로 시작되는 번호
        }

        phoneNumber = PhoneNumberUtils.formatNumber(phoneNumber);

        Log.e("CaInfo", "PhoneNumber = "+phoneNumber);

        return phoneNumber;
    }

    public boolean removeFamilyMember(int nSeqMember) {

        for (CaFamily family : m_alFamily) {
            if (family.m_nSeqMember==nSeqMember) {
                m_alFamily.remove(family);
                return true;
            }
        }

        return false;
    }

    public String getAtAlarmDesc(int nUsageNotiType, int nUsageNotiHour) {
        String str="";

        switch (nUsageNotiType) {
            case 0:
                str+="매일 ";
                break;

            case 1:
                str+="매주 일요일 ";
                break;

            case 2:
                str+="매주 월요일 ";
                break;

            case 3:
                str+="매달 1일 ";
                break;

            case 4:
                str+="매달 검침일 ";
                break;


            default:
                str+="unknown ";
                break;
        }

        if (nUsageNotiHour<12) str+=(nUsageNotiHour + " AM");
        else if (nUsageNotiHour == 12) str+=(nUsageNotiHour + " PM");
        else str+=((nUsageNotiHour-12) + " PM");

        return str;
    }

    public String getAtAlarmDesc() {
        return getAtAlarmDesc(m_nUsageNotiType, m_nUsageNotiHour);
    }

    public String getNotiDesc() {
        String str="";
        str+=("사용량 ["+m_dfKwh.format(m_dThresholdKwh)+" kWh] 도달시");
        if (m_bNotiKwh) str+=(" (○)");
        else str+=(" (Ⅹ)");

        str+=("\n사용요금 ["+m_dfWon.format(m_dThresholdWon)+" 원] 도달시");
        if (m_bNotiWon) str+=(" (○)");
        else str+=(" (Ⅹ)");

        str+=("\n다음누진등급 ["+m_nNextPriceLevelPercent+" %] 도달시");
        if (m_bNotiPriceLevel) str+=(" (○)");
        else str+=(" (Ⅹ)");

        str+=("\n사용량 보고 ["+getAtAlarmDesc()+"]");
        if (m_bNotiUsageAtTime) str+=(" (○)");
        else str+=(" (Ⅹ)");

        return str;
    }

    public CaDiscount findDiscountFamily(int nDiscountId) {
        for (CaDiscount discount : m_alDiscountFamily) {
            if (discount.m_nDiscountId==nDiscountId) return discount;
        }

        return null;
    }

    public CaDiscount findDiscountSocial(int nDiscountId) {
        for (CaDiscount discount : m_alDiscountSocial) {
            if (discount.m_nDiscountId==nDiscountId) return discount;
        }

        return null;
    }

    public int getUnreadAlarmCount() {
        int nCount=0;

        for (CaAlarm alarm : m_alAlarm) {
            if (!alarm.m_bRead) nCount++;
        }

        return nCount;
    }

    public int getUnreadNoticeCount() {
        int nCount=0;

        for (CaNotice notice : m_alNotice) {
            if (!notice.m_bRead) nCount++;
        }

        return nCount;
    }

    public int getUnreadQnaCount() {
        int nCount=0;

        for (CaQna qna : m_alQna) {
            if (qna.isAnswered() && !qna.isAnswerRead()) nCount++;
        }

        return nCount;
    }

    public void setResponseCodeForMemberSub(int nSeqMemberSub, int nAck) {
        for (CaAlarm alarm : m_alAlarm) {
            if (alarm.m_nSeqMemberAckRequester==nSeqMemberSub) {
                alarm.m_nResponse=nAck;
            }
        }
    }

    public String getAlarmReadListString() {
        String strResult="";

        for (CaAlarm alarm : m_alAlarm) {
            if (alarm.m_bReadStateChanged) {
                strResult = (strResult + Integer.toString(alarm.m_nSeqAlarm)+",");
            }
        }

        if (strResult.isEmpty()) return strResult;

        strResult = strResult.substring(0, strResult.length() - 1);
        return strResult;
    }

    public String getNoticeReadListString() {
        String strResult="";

        for (CaNotice notice : m_alNotice) {
            if (notice.m_bReadStateChanged) {
                strResult = (strResult + Integer.toString(notice.m_nSeqNotice)+",");
            }
        }

        if (strResult.isEmpty()) return strResult;

        strResult = strResult.substring(0, strResult.length() - 1);
        return strResult;
    }

    public Date parseDate(String str) {
        Calendar cal=new GregorianCalendar(1970, 1, 1, 0, 0, 0);

        Date dt;

        try {
            dt = CaApplication.m_Info.m_dfStd.parse(str);
        }
        catch (ParseException e) {
            // e.printStackTrace();
            dt=cal.getTime();
        }

        return dt;
    }

    public void setAlarmList(JSONArray ja) {

        m_alAlarm.clear();

        try {
            for (int i=0; i<ja.length(); i++) {
                JSONObject joAlarm=ja.getJSONObject(i);

                CaAlarm alarm=new CaAlarm();
                alarm.m_nSeqAlarm=joAlarm.getInt("seq_alarm");
                alarm.m_nAlarmType=joAlarm.getInt("alarm_type");
                alarm.m_nResponse=joAlarm.getInt("ack_response");
                alarm.m_strTitle=joAlarm.getString("title");
                alarm.m_strContent=joAlarm.getString("content");
                alarm.m_bRead=(joAlarm.getInt("is_read")==1);
                alarm.m_dtCreated=parseDate(joAlarm.getString("time_created"));
                alarm.m_nSeqMemberAckRequester=joAlarm.getInt("seq_member_ack_requester");

                if (alarm.m_bRead) {
                    alarm.m_dtRead=parseDate(joAlarm.getString("time_read"));
                }

                m_alAlarm.add(alarm);
            }

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public CaNotice findNotice(int nSeqNotice) {

        for (CaNotice notice : m_alNotice) {
            if (notice.m_nSeqNotice==nSeqNotice) return notice;
        }

        return null;
    }

    public CaNotice registerNotice(int nSeqNotice) {
        CaNotice notice=findNotice(nSeqNotice);
        if (notice==null) {
            notice=new CaNotice();
            notice.m_nSeqNotice=nSeqNotice;
            m_alNotice.add(notice);
        }

        return notice;
    }

    public void setNoticeList(JSONArray jaTop, JSONArray jaNormal) {

        try {
            ArrayList<CaNotice> alTop=new ArrayList<>();

            for (int i=0; i<jaTop.length(); i++) {
                JSONObject joNotice=jaTop.getJSONObject(i);

                CaNotice notice=new CaNotice();
                notice.m_nSeqNotice=joNotice.getInt("seq_notice");
                notice.m_nWriterType=joNotice.getInt("writer_type");
                notice.m_strTitle=joNotice.getString("title");
                notice.m_strContent=joNotice.getString("content");
                notice.m_bTop=true;
                notice.m_dtCreated=parseDate(joNotice.getString("time_created"));

                String strTimeRead=joNotice.getString("time_read");
                Log.i("NoticeTopList", "strTimeRead="+strTimeRead);

                if (strTimeRead.equals("null")) {
                    notice.m_bRead=false;
                    notice.m_dtRead=null;
                }
                else {
                    notice.m_bRead=true;
                    notice.m_dtRead=parseDate(strTimeRead);
                }

                alTop.add(notice);
            }

            ArrayList<CaNotice> alNormal=new ArrayList<>();

            for (CaNotice notice : m_alNotice) {
                if (notice.m_bTop) continue;
                alNormal.add(notice);
            }

            m_alNotice.clear();

            for (CaNotice notice : alTop) {
                m_alNotice.add(notice);
            }

            for (CaNotice notice : alNormal) {
                m_alNotice.add(notice);
            }

            for (int i=0; i<jaNormal.length(); i++) {
                JSONObject joNotice=jaNormal.getJSONObject(i);

                int nSeqNotice=joNotice.getInt("seq_notice");
                CaNotice notice=registerNotice(nSeqNotice);

                notice.m_nWriterType=joNotice.getInt("writer_type");
                notice.m_strTitle=joNotice.getString("title");
                notice.m_strContent=joNotice.getString("content");
                notice.m_bTop=false;
                notice.m_dtCreated=parseDate(joNotice.getString("time_created"));

                String strTimeRead=joNotice.getString("time_read");
                Log.i("NoticeList", "strTimeRead="+strTimeRead);

                if (strTimeRead.equals("null")) {
                    notice.m_bRead=false;
                    notice.m_dtRead=null;
                }
                else {
                    notice.m_bRead=true;
                    notice.m_dtRead=parseDate(strTimeRead);
                }

                m_dtNoticeCreatedMaxForNextRequest=notice.m_dtCreated;
            }

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("CaInfo", "NoticeCreatedTimeMaxForNextRequest="+m_dfStd.format(m_dtNoticeCreatedMaxForNextRequest));
    }

    public void setQnaList(JSONArray ja) {

        m_alQna.clear();

        try {
            for (int i=0; i<ja.length(); i++) {
                JSONObject joQna=ja.getJSONObject(i);

                CaQna qna=new CaQna();
                qna.m_nSeqQna=joQna.getInt("seq_qna");
                qna.m_strQuestion=joQna.getString("question");
                qna.m_strAnswer=joQna.getString("answer");
                qna.m_dtQuestion=parseDate(joQna.getString("time_question"));

                String strTimeAnswer=joQna.getString("time_answer");

                if (strTimeAnswer.equals("null")) {
                    qna.m_dtAnswer=null;
                }
                else {
                    qna.m_dtAnswer=parseDate(strTimeAnswer);
                }

                String strTimeAnswerRead=joQna.getString("time_answer_read");

                if (strTimeAnswerRead.equals("null")) {
                    qna.m_dtAnswerRead=null;
                }
                else {
                    qna.m_dtAnswerRead=parseDate(strTimeAnswerRead);
                }

                m_alQna.add(qna);
            }

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getQnaReadListString() {
        String strResult="";

        for (CaQna qna : m_alQna) {
            if (qna.m_bReadStateChanged) {
                strResult = (strResult + Integer.toString(qna.m_nSeqQna)+",");
            }
        }

        if (strResult.isEmpty()) return strResult;

        strResult = strResult.substring(0, strResult.length() - 1);
        return strResult;
    }

    public static String getDecoPhoneNumber(String src) {
        if (src == null) {
            return "";
        }
        if (src.length() == 8) {
            return src.replaceFirst("^([0-9]{4})([0-9]{4})$", "$1-$2");
        } else if (src.length() == 12) {
            return src.replaceFirst("(^[0-9]{4})([0-9]{4})([0-9]{4})$", "$1-$2-$3");
        }
        return src.replaceFirst("(^02|[0-9]{3})([0-9]{3,4})([0-9]{4})$", "$1-$2-$3");
    }
}
