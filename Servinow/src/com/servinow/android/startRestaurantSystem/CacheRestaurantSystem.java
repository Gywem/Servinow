package com.servinow.android.startRestaurantSystem;

import java.io.IOException;
import java.util.Collection;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.servinow.android.QRSystem.QRResultCallback;
import com.servinow.android.dao.RestaurantCache;
import com.servinow.android.domain.Place;
import com.servinow.android.domain.Restaurant;
import com.servinow.android.synchronization.ServinowApi_GetRestaurant;

public class CacheRestaurantSystem {

	private final int restaurantID;
	private final int placeID;
	private final QRResultCallback callbacks;
	private final Context context;
	private CallForRestaurantToCache callForRestaurantToCache;

	public CacheRestaurantSystem(Context context, int restaurantID, int placeID, QRResultCallback callbacks) {
		this.restaurantID = restaurantID;
		this.placeID = placeID;
		this.callbacks = callbacks;
		this.context = context;
	}
	
	public void start(){
		callForRestaurantToCache = new CallForRestaurantToCache();
		callForRestaurantToCache.execute();
	}
	
	public void stop(){
		callForRestaurantToCache.cancel(true);
	}
	
	private class CallForRestaurantToCache extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			ServinowApi_GetRestaurant callToInternet = new ServinowApi_GetRestaurant(restaurantID, placeID);
			//try {
				//String restaurantJson = callToInternet.call();
				String restaurantJson = "{onlineID: 12,lastUpdate: 12345,name: \"My Restaurant\",places: [{onlineID: 1,lastUpdate: 12345}]}";
				
				Restaurant restaurant = new Gson().fromJson(restaurantJson, Restaurant.class);
				RestaurantCache restaurantCache = new RestaurantCache(context);
				restaurantCache.setRestaurantCache(restaurant);
				restaurantCache.close();
			//} catch (IOException e) {
			//	return false;
			//}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean goodCall) {
			super.onPostExecute(goodCall);
			
			if(goodCall)
				callbacks.onAnswer(restaurantID, placeID);
			else
				callbacks.onBadCode();
		}
	}
}
