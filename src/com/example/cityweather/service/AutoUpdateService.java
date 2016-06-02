package com.example.cityweather.service;

import com.example.cityweather.receiver.AutoUpdateReceiver;
import com.example.cityweather.util.HttpCallbackListener;
import com.example.cityweather.util.HttpUtil;
import com.example.cityweather.util.Utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class AutoUpdateService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				updateWeather();
			}
		}).start();
		
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int anHour = 1 * 60 * 60 * 1000;//1小时毫秒数
		long triggerAtTime = SystemClock.elapsedRealtime() +anHour;
		Intent i = new Intent(this,AutoUpdateReceiver.class);
		PendingIntent pi =  PendingIntent.getBroadcast(this, 0, i, 0);
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void updateWeather(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String cityId = prefs.getString("city_code", "");
		String address = "http://api.heweather.com/x3/weather?cityid="+cityId+"&key=b24a8807e904419ea31f3125fab0ca16";
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				 Utility.parseWeather(AutoUpdateService.this, response);
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}
