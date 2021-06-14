package com.enernet.eg;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;

public class CaTask extends AsyncTask<CaArg, Void, CaResult> {

   // private static final String m_strUrlApi  = "https://192.168.100.48:3187/api/";
    private static final String m_strUrlApi  = "https://egservice.co.kr:3187/api/";

    private IaResultHandler m_ResultHandler;
    private Context m_Context;
    private Dialog m_DialogWait = null;

    private int m_nCallMethod;
    private boolean m_bShowWaitDialog;

    public CaTask(final int nCallMethod, final boolean bShowWaitDialog, Context C, IaResultHandler ResultHandler) {
        m_bShowWaitDialog = bShowWaitDialog;
        m_nCallMethod = nCallMethod;
        m_Context = C;
        m_ResultHandler = ResultHandler;
    }

    @Override
    public void onPreExecute() {

        if (m_bShowWaitDialog) {

            try {
                if (m_Context != null) {
                    String holdOnWait = StringUtil.getString(m_Context, R.string.text_hold_on_wait);
                    m_DialogWait = ProgressDialog.show(m_Context, null, holdOnWait, true);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        super.onPreExecute();
    }

    @Override
    protected CaResult doInBackground(CaArg... args) {

        CaResult Result = null;
        CaHttp httpClient = null;
        CaArg arg = args[0];

        if (null != arg.fileData && arg.command.equals("userInfo/apkFileUpload.json")) {
				/*
				try {
					int fileSize = arg.fileData.length;

					httpClient = new HTTPMultipartPostEngine(MDL_HTTP_URL + arg.command, arg.fileData,
							fileSize, "", "fileUpload", currentContext);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				*/
        }
        else {
            Log.i("TASK", "URL = " + m_strUrlApi + arg.command);

            httpClient = new CaHttpPost(m_strUrlApi + arg.command);
        }

        if (null != httpClient) {

            if (null != arg.args) {

                for (Pair<String, String> A : arg.args) {
                    httpClient.addEntityPair(A);
                    Log.i("TASK","key="+A.first+", value="+A.second);
;                }
            }

            String strResponse = httpClient.execute();
            Log.i("TASK", "Command="+arg.command+", Response="+strResponse);

            try {
                JSONObject jo = new JSONObject(strResponse);

                Result = new CaResult();
                Result.object = jo;
            }
            catch (JSONException ex1) {

                try {
                    JSONArray ja = new JSONArray(strResponse);

                    Result = new CaResult();
                    Result.array = ja;
                }
                catch (JSONException ex2) {
                    Result = new CaResult();
                }

            }
        }


        Result.m_nCallback = m_nCallMethod;

        return Result;
    }

    @Override
    protected void onPostExecute(CaResult mdlResult) {

        if (null != m_DialogWait) {
            try {
                m_DialogWait.dismiss();
            } catch (Exception e){
                return;
            }
        }

        super.onPostExecute(mdlResult);

        if (null != m_ResultHandler) {

            if (m_ResultHandler instanceof IaResultHandler) {
                m_ResultHandler.onResult(mdlResult);
            }
        }
    }
}

