package com.example.cityweather.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.cityweather.model.City;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.cityweather.db.CityWeatherDB;

public class Utility {
	public synchronized static boolean handleCitiesResponse(CityWeatherDB cityWeatherDB,String response) {
		if(!TextUtils.isEmpty(response)){
			Log.d("cityWeatherInfo", " handleCitiesResponse");
			try {
				City city = new City();
				JSONObject jsonObject = new JSONObject(response);
				JSONArray jsonArray = jsonObject.getJSONArray("city_info");
				//Log.d("cityWeatherInfo", "city number"+jsonArray.length());
				for(int i =0;i<jsonArray.length();i++){
					JSONObject jsObject =jsonArray.getJSONObject(i);
					String cityName = jsObject.getString("city");
					String id = jsObject.getString("id");
					city.setCityName(cityName);
					city.setId(id);
					Log.d("cityWeatherInfo", "cityname==="+cityName+" cityId = "+id);
 					cityWeatherDB.saveCity(city);
 					
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
	
	public static boolean parseWeather(Context context,String weatherData){
		 try {
				JSONObject jsonObject1 = new JSONObject(weatherData);
				JSONArray jsonArray = jsonObject1.getJSONArray("HeWeather data service 3.0");
				JSONObject weatherJsonObject = (JSONObject) jsonArray.get(0);
				JSONObject cityJsonObject = weatherJsonObject.getJSONObject("basic");
				final String cityString = cityJsonObject.getString("city");
				Log.d("listenString", "city="+cityString);
				//不能在非UI线程直接更新UI线程的信息
				//cityNameView.setText(cityString);
				 
				JSONArray dailyForecastJsonArray = weatherJsonObject.getJSONArray("daily_forecast");
				JSONObject todayJsonObject = dailyForecastJsonArray.getJSONObject(0);
				JSONObject secondDayJsonObject = dailyForecastJsonArray.getJSONObject(1); 
				
				JSONObject condJsonObject1 = todayJsonObject.getJSONObject("cond");
				JSONObject tempJsonObject = todayJsonObject.getJSONObject("tmp");
				JSONObject windJsonObject = todayJsonObject.getJSONObject("wind");
				final String todayClould = condJsonObject1.getString("txt_d");
				final String todayMaxTemp = tempJsonObject.getString("max");
				final String todayMinTemp = tempJsonObject.getString("min"); 
				final String wind = windJsonObject.getString("sc");
				final int averTemp = (Integer.parseInt(todayMaxTemp) + Integer.parseInt(todayMinTemp))/2;
				
				JSONObject tomorowTempJsonObject = secondDayJsonObject.getJSONObject("tmp");
				JSONObject tomorowWindJsonObject = secondDayJsonObject.getJSONObject("wind");
				final String tomorowMaxTemp = tomorowTempJsonObject.getString("max");
				final String tomorowMinTemp = tomorowTempJsonObject.getString("min");
				final String tomorowWind = tomorowWindJsonObject.getString("sc");
				final int tomorowAverTemp = (Integer.parseInt(tomorowMaxTemp)+ Integer.parseInt(tomorowMinTemp))/2; 
				
				saveWeather(context,todayClould,todayMaxTemp,todayMinTemp,wind,tomorowWind,tomorowMaxTemp,tomorowMinTemp);
			
				
				Log.d("listenString", "cloudString "+todayClould +"\ttodayMaxTemp "+todayMaxTemp+"\ttodayMinTemp "+todayMinTemp+"\twind "+wind);
				
				return true;
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return false;
	}
	
	private static void saveWeather(Context context, String todayClould,String todayMaxTemp,String todayMinTemp,String todayWind,String tomorowWind,String tomorowMaxTemp,String tomorowMinTemp){
		
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putString("tdClound", todayClould);
		editor.putString("tdWind", todayWind);
		editor.putString("tdMax", todayMaxTemp);
		editor.putString("tdMin", todayMinTemp);
		final int averTemp = (Integer.parseInt(todayMaxTemp) + Integer.parseInt(todayMinTemp))/2;
		editor.putInt("tdAver", averTemp);
		
		editor.putString("tmWind", tomorowWind);
		editor.putString("tmMax", tomorowMaxTemp);
		editor.putString("tmMin", tomorowMinTemp);
		final int tmAverTemp = (Integer.parseInt(tomorowMaxTemp)+ Integer.parseInt(tomorowMinTemp))/2; 
		editor.putInt("tmAver", tmAverTemp);
		editor.commit();
	}
}
