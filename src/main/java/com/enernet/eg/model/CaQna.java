package com.enernet.eg.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CaQna {
    public int m_nSeqQna=0;
    public String m_strQuestion="";
    public String m_strAnswer="";
    public Date m_dtQuestion=null;
    public Date m_dtAnswer=null;
    public Date m_dtAnswerRead=null;
    public boolean m_bReadStateChanged=false;

    public boolean isAnswered() {
        return (m_dtAnswer!=null);
    }

    public boolean isAnswerRead() {
        return (m_dtAnswerRead!=null);
    }

    public String getQnaState() {
        if (isAnswered()) return "[답변완료]";
        else return "[답변대기]";
    }

    public String getTimeQna() {
        SimpleDateFormat dfQuestion = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String strQuestion = dfQuestion.format(m_dtQuestion);

        if (isAnswered()) {
            SimpleDateFormat dfAnswer = new SimpleDateFormat("MM-dd HH:mm");
            String strAnswer = dfAnswer.format(m_dtAnswer);

            return ("[문의] " + strQuestion + "    [답변] " + strAnswer);
        } else {
            return ("[문의] " + strQuestion);
        }
    }

    public String getTimeQuestion() {
            SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strTime=df.format(m_dtQuestion);

            return ("[문의] "+strTime);
    }

    public String getTimeAnswer() {
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strTime=df.format(m_dtAnswer);

        return ("[답변] "+strTime);
    }
}
