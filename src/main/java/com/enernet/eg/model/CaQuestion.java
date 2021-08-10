package com.enernet.eg.model;

import java.util.ArrayList;

public class CaQuestion {
    public int m_nSeqQuestion=0;
    public String m_strQuestionTitle="";
    public int m_nQuestionType=0;
    public boolean m_bEssential=false;
    public ArrayList<CaItem> m_alItem = new ArrayList<>();
}
