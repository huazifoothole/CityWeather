package com.example.cityweather;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.cityweather.db.CityWeatherDB;
import com.example.cityweather.util.HttpCallbackListener;
import com.example.cityweather.util.HttpUtil;
import com.example.cityweather.util.Utility;

import android.R.integer;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.SearchView.OnQueryTextListener;
 
 
 

public class MainActivity extends Activity  {

	private SearchView seachView;
 
	private TextView tempView;
	private TextView cityNameView;
	private TextView cloundView;
	private TextView windView;
	private TextView todayView;
	private TextView tomorowView;
	private CityWeatherDB cityWeatherDB;
 
	private String weatherAddress = "http://api.heweather.com/x3/weather?cityid=CN101280101&key=b24a8807e904419ea31f3125fab0ca16";
	private String allCityIdAddress = "https://api.heweather.com/x3/citylist?search=allchina&key=b24a8807e904419ea31f3125fab0ca16" ;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		seachView = (SearchView) findViewById(R.id.searchView1);
		seachView.setBackgroundColor(Color.WHITE);
		seachView.setIconifiedByDefault(false);
		seachView.setSubmitButtonEnabled(true);
		 
		tempView = (TextView) findViewById(R.id.temp_view);
		cityNameView = (TextView) findViewById(R.id.city_name);
		windView = (TextView) findViewById(R.id.wind_view);
		cloundView = (TextView) findViewById(R.id.cond_view);
		todayView = (TextView) findViewById(R.id.today_forcast);
		tomorowView = (TextView) findViewById(R.id.tomorrow_forcast);
		
		cityWeatherDB = CityWeatherDB.getInstance(this);
		
		//保存城市ID
		//saveCitiesId();
		 
		seachView.setOnQueryTextListener(new OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
//				Toast.makeText(MainActivity.this, "search", Toast.LENGTH_SHORT).show();
				seachView.setIconified(true);//key_down和key_down会执行两次搜索 加上此句clear搜索框避免
				String cityId = cityWeatherDB.queryID(query);
				queryCityWeather(cityId);
				return true;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				return true;
			}
		});
	}
	
	private void queryCityWeather(String cityId) {
		String address = "http://api.heweather.com/x3/weather?cityid="+cityId+"&key=b24a8807e904419ea31f3125fab0ca16";
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				parseWeatherData(response);
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private void saveCitiesId() {
		HttpUtil.sendHttpRequest(allCityIdAddress, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				
				  boolean result = Utility.handleCitiesResponse(cityWeatherDB, response);
				
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				e.printStackTrace();
				
			}
		});
	}
	
	
	 
	private void parseWeatherData(String weatherData){
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
			final String cloudString1 = condJsonObject1.getString("txt_d");
			final String todayMaxTemp = tempJsonObject.getString("max");
			final String todayMinTemp = tempJsonObject.getString("min"); 
			final String wind = windJsonObject.getString("sc");
			final int averTemp = (Integer.parseInt(todayMaxTemp) + Integer.parseInt(todayMinTemp))/2;
			
			if(!TextUtils.isEmpty(weatherData)){
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						cloundView.setText(cloudString1);
						cityNameView.setText(cityString);
						tempView.setText(String.valueOf(averTemp));
						windView.setText("风："+wind);
						String todayForcaString = "今天  "+ todayMaxTemp +"/"+todayMinTemp +"C\n"+wind;
						todayView.setText(todayForcaString); 
					}
				});
			}
			Log.d("listenString", "cloudString "+cloudString1 +"\ttodayMaxTemp "+todayMaxTemp+"\ttodayMinTemp "+todayMinTemp+"\twind "+wind);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}
	 

}
