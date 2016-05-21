package com.example.cityweather.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.cityweather.model.City;
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
					Log.d("cityWeatherInfo", "cityname="+cityName+" cityId = "+id);
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
}
