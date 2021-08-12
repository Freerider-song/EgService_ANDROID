package com.enernet.eg.model;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CaNotice {
	public int m_nSeqNotice=0;
	public String m_strTitle="";
	public String m_strContent="";
	public boolean m_bTop=false;
	public int m_nWriterType=1; // 1=아파트관리자, 2=에너넷
	public boolean m_bNotice = false;

	public Date m_dtCreated=null;
	public Date m_dtRead=null;
	public boolean m_bRead=true;

	public boolean m_bReadStateChanged=false;

	public String getTimeCreated() {
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(m_dtCreated == null) return null;
		else{
			return df.format(m_dtCreated);
		}

	}

	public int m_nSeqSurvey=0;
	public String m_strExplain="";
	public Date m_dtStart;
	public Date m_dtEnd;
	public int m_nAnsweredQuestionCount=0;
	public ArrayList<CaQuestion> m_alQuestion = new ArrayList<>();

	public String getPeriod(){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

		return (df.format(m_dtStart) + " ~ " + df.format(m_dtEnd));
	}
}
