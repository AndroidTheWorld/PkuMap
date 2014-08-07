package com.pkumap.eventlistener;

import com.pkumap.activity.MapActivity;
import com.pkumap.bean.Poi;
import com.pkumap.bean.Point;
import com.pkumap.util.PoiManager;
import com.zdx.pkumap.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;

public class SearchPoiOnClickListener implements OnClickListener {
	private PoiManager poiManager;
	private AutoCompleteTextView poi_txt=null;
	private Activity eventActivity;
	public final static int RESULT_CODE=1;
	public SearchPoiOnClickListener(Activity targetActivity,PoiManager poiManagerArg){
		this.eventActivity=targetActivity;
//		poiManager=new PoiManager(eventActivity.getApplicationContext());
		this.poiManager=poiManagerArg;
		poi_txt=(AutoCompleteTextView) eventActivity.findViewById(R.id.poi_search_txt);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.poi_search_btn:
			goToMapWithPoiName();
			break;
		}
	}

	/**
	 * 将搜索的Poi带回Map，进行展示
	 */
	private void goToMapWithPoiName(){
		String poiName=poi_txt.getText().toString();
		Poi poi=poiManager.getPoiByName(poiName);
		
		Bundle bundle=new Bundle();
		bundle.putString("poi_name", poiName);
		bundle.putParcelable("poi", poi);
		
/*		Bundle bundle=new Bundle();
		bundle.putString("key", "北京");*/
		
		Intent intent=new Intent();
		intent.setClass(eventActivity,MapActivity.class);
		intent.putExtras(bundle);
/*		Bundle test=bundle.getParcelable("poi");
		Point point=test.getParcelable("center");*/
		eventActivity.setResult(Activity.RESULT_OK, intent);
		eventActivity.finish();
	}
}
