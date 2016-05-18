package com.example.cityweather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.util.JsonReader;
import android.util.Log;

public class HttpUtil {
	public static void sendHttpRequest(final String address,final HttpCallbackListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpURLConnection connection = null;
				try {
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder responsebuBuilder = new StringBuilder();
					String lineString;
					while((lineString = reader.readLine()) != null){
						responsebuBuilder.append(lineString);
					}
					 //回调onFinish方法
					if(listener != null){
						listener.onFinish(responsebuBuilder.toString());
					}
					reader.close();
					in.close();
				} catch (Exception e) {
					if(listener != null){
						listener.onError(e);
					}
					e.printStackTrace();
					 
				}finally{
					if(connection != null){
						connection.disconnect();
					}
				}
				
			}
		}).start();
	}
	
	
	
}
