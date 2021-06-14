package com.enernet.eg.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CaFamily {
    public int m_nSeqMember=0;
    public boolean m_bMain=false;
    public String m_strName="";
    public String m_strPhone="";
    public Date m_dtLastLogin=null;

    public String getLastLoginTime() {
        if (m_dtLastLogin==null) return "";

        SimpleDateFormat df=new SimpleDateFormat("MM-dd HH:mm:ss");
        return df.format(m_dtLastLogin);

    }
}
