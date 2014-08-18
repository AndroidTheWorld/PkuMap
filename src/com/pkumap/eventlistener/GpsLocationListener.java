package com.pkumap.eventlistener;


import com.pkumap.activity.MapActivity;
import com.pkumap.bean.PointLonLat;
import com.pkumap.util.KalmanLatLong;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.widget.Toast;

public class GpsLocationListener implements LocationListener {

	private KalmanLatLong kalman;
	private LocationManager locManager;
	private MapActivity mapActivity;
	public GpsLocationListener(MapActivity mapActivity){
		kalman=new KalmanLatLong(3);
		this.mapActivity=mapActivity;
		this.locManager=mapActivity.locManager;
	}
	
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if(location!=null){
			MapActivity.gpsLonLat=kalman.Process(location.getLatitude(), location.getLongitude(), 
					location.getAccuracy(), location.getTime());
//			Toast.makeText(mapActivity,"当前GPS坐标："+MapActivity.gpsLonLat.getX()+","+MapActivity.gpsLonLat.getY(), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
/*		switch(status){
		case LocationProvider.AVAILABLE:
			Toast.makeText(mapActivity, "当前GPS为可用状态",
					Toast.LENGTH_SHORT).show();
			break;
		case LocationProvider.OUT_OF_SERVICE:
			Toast.makeText(mapActivity, "当前GPS为服务区外状态",
					Toast.LENGTH_SHORT).show();
			break;
		case LocationProvider.TEMPORARILY_UNAVAILABLE:
			Toast.makeText(mapActivity, "当前GPS为暂停服务状态",
					Toast.LENGTH_SHORT).show();
			break;
		}*/
//		MapActivity.gpsLonLat=null;
//		Toast.makeText(mapActivity, "onStatusChanged",Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		// 当GPS LocationProvider可用时，更新位置
		Location tlocation = locManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		Toast.makeText(mapActivity, "正在请求GPS定位，请稍等",
				Toast.LENGTH_SHORT).show();
		if (tlocation != null) {
			MapActivity.gpsLonLat = kalman.Process(tlocation.getLatitude(),
					tlocation.getLongitude(), tlocation.getAccuracy(),
					tlocation.getTime());
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		MapActivity.gpsLonLat=null;
		Toast.makeText(mapActivity, "onProviderDisabled",
				Toast.LENGTH_SHORT).show();
	}

}
