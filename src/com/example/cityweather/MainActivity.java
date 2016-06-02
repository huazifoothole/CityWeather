package com.example.cityweather;



import android.R.animator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Window;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

import com.example.cityweather.db.CityWeatherDB;
import com.example.cityweather.service.AutoUpdateService;
import com.example.cityweather.util.HttpCallbackListener;
import com.example.cityweather.util.HttpUtil;
import com.example.cityweather.util.Utility;
 
 
 

public class MainActivity extends Activity  {

	private SearchView seachView;
 
	private TextView tempView;
	private TextView cityNameView;
	private TextView cloundView;
	private TextView windView;
	private TextView todayView;
	private TextView tomorowView;
	private CityWeatherDB cityWeatherDB;
	private GestureDetector mGestureDetector;
	private SwipeRefreshLayout swipeRefreshLayout;
 
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
		
		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
		swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
//		swipeRefreshLayout.setBackgroundColor(R.color.red);
		swipeRefreshLayout.setDistanceToTriggerSync(10);
		swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
		
		String mIsIdExist = cityWeatherDB.queryID(this, "北京");
		if(mIsIdExist.compareTo("CN101280101") == 0){
			saveCitiesId();
			Log.d("listenString", "save=");
		}
			
		 
		 
		seachView.setOnQueryTextListener(new OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
				if(!TextUtils.isEmpty(query)){
					SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
					editor.putString("cityName", query);
					editor.commit();
				}
			
				seachView.setIconified(true);//key_down和key_down会执行两次搜索 加上此句clear搜索框避免
				String cityId = cityWeatherDB.queryID(MainActivity.this,query);
				queryCityWeather(cityId);
				return true;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		
		swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						swipeRefreshLayout.setRefreshing(false);
						showWeather();
					}
				}, 2000);
				
			}
		});
	}
	
 
	private void queryCityWeather(String cityId) {
		String address = "http://api.heweather.com/x3/weather?cityid="+cityId+"&key=b24a8807e904419ea31f3125fab0ca16";
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				boolean result = Utility.parseWeather(MainActivity.this, response);
				if(result){
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							showWeather();
						}
					});
				}
					
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
	
	private void showWeather() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		cloundView.setText(pref.getString("tdClound", ""));
		cityNameView.setText(pref.getString("cityName", ""));
		tempView.setText(String.valueOf(pref.getInt("tdAver", 20)));
		windView.setText("风："+pref.getString("tdWind", ""));
		String todayForcaString = "今天  "+ pref.getString("tdMax", "") +"/"+pref.getString("tdMin", "") +" C\n"+pref.getString("tdWind", "");
		todayView.setText(todayForcaString);
		
		String tomorowForcastString ="明天  "+ pref.getString("tmMax","") +"/"+pref.getString("tmMin", "") +" C\n"+pref.getString("tmWind", "");
		tomorowView.setText(tomorowForcastString);
		Intent intent = new Intent(this,AutoUpdateService.class);
		startService(intent);
	}
	
	 

}
