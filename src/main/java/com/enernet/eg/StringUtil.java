package com.enernet.eg;

import java.io.BufferedReader;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class StringUtil {
	private final static String EMPTY_STRING = "";
	private final static String HTML_TAG_PATTERN = "(.+)? ";
	// 널 처리
	public static String nvl(String origin, String replaceString) {
		return (origin == null || EMPTY_STRING.equals(origin)) ? replaceString : origin.trim();
	}

	// 널 처리
	public static String nvl(Object origin, String replaceString) {
		return (origin == null || EMPTY_STRING.equals(origin.toString())) ? replaceString : origin.toString().trim();
	}

	//최초 공백전까지의 모든 문자를 리턴
	public static String getHtmlBeginTag(final String str) {
		if (str == null || EMPTY_STRING.equals(str))
			return null;

		Matcher m = Pattern.compile(HTML_TAG_PATTERN).matcher(str);
		if (m.find()) {
			return m.group(0);
		}

		return EMPTY_STRING;
	}

	//HTML태그의 닫힘 태그를 가져온다.
	public static String getHtmlEndTag(String str) {
		String beginTag = StringUtil.nvl(getHtmlBeginTag(str), EMPTY_STRING);
		if(!EMPTY_STRING.equals(beginTag)) {
			return "</" + beginTag.substring(1).trim() + ">"; 
		}
		return EMPTY_STRING;
	}

	// 문자열 변경
	public static String replace(String s, String old, String replacement) {
		if (s == null || old == null)
			return EMPTY_STRING;
		int i = s.indexOf(old);
		StringBuffer r = new StringBuffer();

		if (i == -1)
			return s;
		r.append(s.substring(0, i)).append(replacement);
		if (i + old.length() < s.length())
			r.append(replace(s.substring(i + old.length(), s.length()), old, replacement));

		return r.toString();
	}

	public static String getPatternString(String origin) {
		return "(\\_{2}" + origin.replaceAll("\\_", "") + "\\_{2})";
	}

	// 캐리지리턴을 BR로 바꾼다.
	public static String convertFromCRLFToBR(String x) {
		StringBuffer sb = null;

		try{
			StringReader sr = new StringReader(x.concat("\n"));
			BufferedReader br = new BufferedReader(sr);
			String s = null;
			sb = new StringBuffer();

			while ((s = br.readLine()) != null) {
				sb.append(s);
				sb.append("<BR>\n");
			}

			br.close();
			sr.close();
		}
		catch (Exception e) {
			return null;
		}

		return sb.toString();
	
	}

	// 지정한 글자만큼 잘라서 가져온다.
	public static String getCutString(String str, String newStr, int i) {
		if (str==null) return EMPTY_STRING;
		return StringUtil.getLengthBString(str, newStr, i-(newStr.length()));
	}

	// 문자의 바이트 수 리턴
	private static int getByte(char c) {
		Character ch = Character.valueOf(c);
		if (ch.hashCode() < 0 || ch.hashCode() > 255) {
			return 2;
		} else {
			return 1;
		}
	}

	// 바이트 수 만큼 가져온다.
	private static String getLengthBString(String origin, String newStr, int b) {
		if (origin == null || EMPTY_STRING.equals(origin)) return EMPTY_STRING;
		int lengthByte = 0;
		StringBuffer sf = new StringBuffer();

		for (int i = 0; i < origin.length(); i++) {
			lengthByte += getByte(origin.charAt(i));

			if(lengthByte < b) {
				sf.append(origin.charAt(i));
			} else if (lengthByte == b) {
				sf.append(origin.charAt(i));
				sf.append(newStr);
				break;
			} else {
				sf.append(newStr);
				break;
			}
		}

		return sf.toString();
	}
	
	// 숫자에 콤마 찍어서 반환.(소수점)
	public static String toNumFormat(Double num) {
		DecimalFormat df = new DecimalFormat("#,###");
		return df.format(num);
	}
	
	// 숫자에 콤마 찍어서 반환.(10단위 절사)
	public static String toNumFormat_2(Double num) {
		double num2 = Math.floor(num/10) * 10;
		DecimalFormat df = new DecimalFormat("#,###");
		return df.format(num2);
	}
	
	public static String getString()
	{
		return null;
	}

	public static String getString(Context currentContext, int id) {
		// TODO Auto-generated method stub
		return currentContext.getResources().getString(id);
	}
	
	public static Drawable getDrawable(Context currentContext, int id) {
		// TODO Auto-generated method stub
		return currentContext.getResources().getDrawable(id);
	}

	public static String removeNonDigitChars(String strIn) {
		String strRes=strIn.replaceAll("[^\\d]", "");
		return strRes;
	}
	
}

