package com.enernet.eg.model;

public class CaUsage {

	// for monthly usage
	public int m_nYear=0;
	public int m_nMonth=0; // also for weekly usage

	public double m_dKwh=0.0;
	public double m_dWon=0.0;
	public double m_dKwhPrev=0.0;
	public double m_dWonPrev=0.0;

	// for weekly usage
	public int m_nDay=0;
	public double m_dKwhAvg=0.0;
	public double m_dWonAvg=0.0;

	public int m_nHour=0;

	public int m_nUnit=0;

	private String flag;

	private String dcTime;

	private String actd;
	private String price;
	
	

	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getDcTime() {
		return dcTime;
	}
	public void setDcTime(String dcTime) {
		this.dcTime = dcTime;
	}

	public String getActd() {
		return actd;
	}
	public void setActd(String actd) {
		this.actd = actd;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	
	
}
