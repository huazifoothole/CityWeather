package com.example.cityweather.db;


import android.app.DownloadManager.Query;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.cityweather.model.City;

public class CityWeatherDB {
	public static final String DB_NAME = "city_weather";
	public static final int VERSION = 1;
	private static CityWeatherDB cityWeatherDB;
	private SQLiteDatabase db;
	CityWeatherOpenHelper cityWeatherOpenHelper;
	//将构造方法私有化
	private  CityWeatherDB(Context context) {
		cityWeatherOpenHelper = new CityWeatherOpenHelper(context,DB_NAME,null, VERSION);
		db = cityWeatherOpenHelper.getWritableDatabase();
	
	}  
	
	public synchronized static CityWeatherDB getInstance(Context context) {
		if(cityWeatherDB == null){
			cityWeatherDB = new CityWeatherDB(context);
		}
		return cityWeatherDB;
	}
	
	public void  saveCity(City city) {
		if(city != null){
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getId());
			db.insert("City", null, values);
//			db.close();
		}
	}
	
	public void  queryID() {
		
		Cursor cursor = db.query("City", null, null, null, null, null, null);
		Log.d("cityWeatherInfo", "cursor count="+cursor.getCount());
		 while(cursor != null && cursor.moveToNext()){
				String name = cursor.getString(cursor.getColumnIndex("city_name"));
				String id = cursor.getString(cursor.getColumnIndex("city_code"));
				Log.d("cityWeatherInfo", "name = "+name+" id ="+id);
			 
		}
		db.close();
	}
	
public String  queryID(String cityName) {
		String cityId = "CN101280101";//默认广州
		db = cityWeatherOpenHelper.getReadableDatabase();
		Cursor cursor = db.query(true, "City",new String[]{"city_code"}, "city_name=?", new String[]{cityName}, null, null, null, null, null);
		if(cursor.moveToFirst()){
			cityId = cursor.getString(cursor.getColumnIndex("city_code"));
			
		}
		Log.d("cityWeatherInfo", "cityName = "+cityName+"cityId = "+cityId);
		db.close();
		return cityId;
	}
	 
}
