package com.example.cityweather.db;


import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.DownloadManager.Query;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.http.SslCertificate.DName;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.cityweather.model.City;

public class CityWeatherDB {
	public static final String DB_NAME = "city_weather";
	public static final int VERSION = 1;
	private static CityWeatherDB cityWeatherDB;
	private static SQLiteDatabase db;
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
	
 
	
public String  queryID(Context context,String cityName) {
		String cityId = "CN101280101";//默认广州
		db = cityWeatherOpenHelper.getReadableDatabase();
		Cursor cursor = db.query(true, "City",new String[]{"city_code"}, "city_name=?", new String[]{cityName}, null, null, null, null, null);
		if(cursor.moveToFirst()){
			cityId = cursor.getString(cursor.getColumnIndex("city_code"));
			saveCityCode(context, cityId);
		}
		db.close();
		return cityId;
	}

private void saveCityCode(Context context,String cityId) {
	SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
	SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
	editor.putString("city_code", cityId);
	editor.commit();
}

public boolean isDataBaseExist(String tableName) {
	
	boolean isTableExist = false;
	
	if(tableName == null){
		return false;
	}
	 
	Cursor cursor = null;
	try {
		db = cityWeatherOpenHelper.getReadableDatabase();
//		String sql = "select count(*) from sqlite_master where type ='table' and name ='"+tableName.trim()+"' "; 
		String sql = "select * from sqlite_master where type = 'table' and name = 'city_code'";
		cursor = db.rawQuery(sql, null);
		if(cursor != null && cursor.moveToNext()){
			if(cursor.getInt(0) > 10){
				isTableExist = true;
			}
			Log.d("cityWeatherInfo", "count = "+cursor.getInt(0));
		}
	} catch (Exception e) {
		// TODO: handle exception
	}
	db.close();
	return isTableExist;
}
	 
}
