package com.enernet.eg;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class CaHttp {

	public String m_strUri = null;
	public List<Pair<String, String>> entities = new ArrayList<Pair<String,String>>();

	public abstract String execute();

	public void setURI(final String U) {
		this.m_strUri = U;
	}

	public String getURI() {
		return m_strUri;
	}

	public int addEntityPair(final Pair<String, String> E) {
		entities.add(E);
		
		return entities.size();
	}

	public int getNumberOfEntities() {
		return entities.size();
	}

	public int removeEntity(final int Index) {
		entities.remove(Index);
		
		return entities.size();
	}

	public void clearEntities()	{
		entities.clear();
	}

	public List<Pair<String, String>> getEntities() {
		return entities;
	}

	public String getPostDataString(JSONObject jo) throws JSONException, UnsupportedEncodingException {
		StringBuilder result=new StringBuilder();
		boolean bFirst=true;

		Iterator<String> itr=jo.keys();

		while (itr.hasNext()) {
			String key=itr.next();
			Object value=jo.get(key);

			if (bFirst) bFirst=false;
			else result.append("&");

			result.append(URLEncoder.encode(key, "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(value.toString(), "UTF-8"));
		}

		return result.toString();
	}

}
