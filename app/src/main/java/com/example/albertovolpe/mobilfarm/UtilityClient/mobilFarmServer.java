package com.example.albertovolpe.mobilfarm.UtilityClient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by albertovolpe on 07/01/17.
 */

public final class mobilFarmServer {

    private static final String TAG = "mobilFarmServer";
    private static final String url = "http://ec2-184-73-59-26.compute-1.amazonaws.com/mobilFarm/";

    private static CookieManager msCookieManager = new CookieManager();
    private static ConnectivityManager conMgr;
    private static NetworkInfo activeNetwork;

    public mobilFarmServer(Context context){

        conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = conMgr.getActiveNetworkInfo();
    }

    /* Given a URL, establishes an HttpUrlConnection and retrieves
       the web page content as a InputStream, which it returns as
       a string. */
    public static JSONObject connect(String section, JSONObject data)
            throws IOException, ConnectionErrorException, JSONException {

        InputStream inputStream = null;
        String result = null;

        if (activeNetwork == null || !activeNetwork.isConnected()) throw new ConnectionErrorException();

        try {
            Log.i(TAG, mobilFarmServer.url+section+".php");
            URL url = new URL(mobilFarmServer.url+section+".php");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            if (msCookieManager.getCookieStore().getCookies().size() > 0) {
                //While joining the Cookies, use ',' or ';' as needed. Most of the server are using ';'
                conn.setRequestProperty("Cookie" , TextUtils.join(";", msCookieManager.getCookieStore().getCookies()));
            }

            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();

            Writer writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
            writer.write(String.valueOf(data));
            writer.flush();
            writer.close();

            List<String> cookiesHeader = conn.getHeaderFields().get("Set-Cookie");

            if (cookiesHeader != null) {
                for (String cookie : cookiesHeader) {
                    msCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                }
            }

            Log.i(TAG, "Sended: " +  String.valueOf(data));

            inputStream = conn.getInputStream();
            if (inputStream != null)
                result = mobilFarmServer.readIt(inputStream);
            else
                Log.i(TAG, "inputStream == null");

        } finally {

            /* Makes sure that the InputStream is closed after the app is
               finished using it. */
            if (inputStream != null) inputStream.close();
        }

        Log.i(TAG, "Received: " + result);
        return new JSONObject(result);
    }


    public static void deleteCookie(){
        msCookieManager.getCookieStore().removeAll();
    }

    // Reads an InputStream and converts it to a String.
    public static String readIt(InputStream stream) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        String line;
        String result = "";

        while((line = bufferedReader.readLine()) != null) result += line;

        stream.close();
        return result;
    }
}
