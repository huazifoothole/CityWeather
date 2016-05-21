package com.example.cityweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CityWeatherOpenHelper extends SQLiteOpenHelper {
	
	public static final String CREATE_CITY ="create table City ("
			+"id integer primary key autoincrement,"
			+" city_name text,"
			+" city_code text)";
	
 
 

	public CityWeatherOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_CITY);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + "City" ); // drop table if exists

	    onCreate(db);

	}

}
