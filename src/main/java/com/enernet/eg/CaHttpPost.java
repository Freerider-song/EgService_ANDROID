package com.enernet.eg;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.MalformedParametersException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class CaHttpPost extends CaHttp {

    public class TrivialTrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            // do some checks on the chain here
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    public class TrivialHostVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String host, SSLSession session) {
            // check host and return true if verified
            return true;
        }

    }

    public CaHttpPost() {

    }

    public CaHttpPost(final String U) {
        setURI(U);
    }

    public String execute() {
//////////////////////
        if (BuildConfig.DEBUG) {
            try {
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new TrustManager[]{new TrivialTrustManager()}, null);
                HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(new TrivialHostVerifier());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        String str="";

        try {
            JSONObject jo = new JSONObject();
            for (Pair<String, String> A : entities) {
                jo.put(A.first, A.second);
            }

            URL url=new URL(m_strUri);

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

            Log.i("CaHttpPost ", "conn="+conn.toString());

            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os=conn.getOutputStream();

            BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            String strOutput = getPostDataString(jo);
            writer.write(strOutput);

            writer.flush();
            writer.close();
            os.close();

            int nResponseCode=conn.getResponseCode();

            Log.i("CaHttpPost ", "reponse_code="+nResponseCode);

            if (nResponseCode==HttpsURLConnection.HTTP_OK) {
                BufferedReader reader=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb=new StringBuilder("");
                String line="";

                while ((line=reader.readLine())!=null) {
                    sb.append(line);
                }

                reader.close();
                return sb.toString();
            }
            else {
                Log.i("CaHttpPost ", "reponse_code is not HTTP_OK");
            }

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {

        }
        catch (SocketTimeoutException e) {
            Log.i("CaHttpPost ", "SocketTimeoutException is caught...");
        }
        catch (IOException e) {

        }

        return str;
    }
}
