package com.enernet.eg.model;

import com.enernet.eg.CaEngine;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CaAlarm {
    public int m_nSeqAlarm=0;
    public int m_nAlarmType= CaEngine.ALARM_TYPE_UNKNOWN;
    public int m_nSeqMemberAckRequester=0;
    public int m_nResponse=-1;
    public String m_strTitle="";
    public String m_strContent="";

    public Date m_dtCreated=null;
    public Date m_dtRead=null;
    public boolean m_bRead=true;

    public boolean m_bReadStateChanged=false;

    public boolean isRequestAck() {
        return (m_nAlarmType==CaEngine.ALARM_TYPE_REQUEST_ACK_MEMBER);
    }

    public String getTimeCreated() {
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(m_dtCreated);
    }
}
