package com.pkumap.activity;

import java.util.ArrayList;

import com.pkumap.eventlistener.SearchPoiOnClickListener;
import com.pkumap.manager.BuildingManager;
import com.pkumap.manager.PoiManager;
import com.zdx.pkumap.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

public class SearchActivity extends Activity {
	private AutoCompleteTextView search_edit=null;
	private Button search_btn=null;
	public PoiManager poiManager;
	public BuildingManager buildingManager;
	public String search_map_type="2dmap";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_main);
		
		poiManager=new PoiManager(this.getApplicationContext());
		buildingManager=new BuildingManager(this.getApplicationContext());
		
		Intent intent=getIntent();
		Bundle bundle=intent.getExtras();
		String str=bundle.getString("poi_name_search");
		this.search_map_type=bundle.getString("map_type");
		ArrayList<String> name=new ArrayList<String>();
		if("3dmap".equals(search_map_type)){
			name=buildingManager.getAllBuildingNames();
		}else{
			name=poiManager.getPoiNameFromDB();
		}
		
		search_edit=(AutoCompleteTextView) findViewById(R.id.poi_search_txt);
		ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line,name);
		search_edit.setAdapter(arrayAdapter);
		search_edit.setThreshold(1);
		
		SearchPoiOnClickListener searchPoiOnClickListener=new SearchPoiOnClickListener(this);
		
		search_btn=(Button) findViewById(R.id.poi_search_btn);
		search_btn.setOnClickListener(searchPoiOnClickListener);
		search_edit.setText(str);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		poiManager.close();
		buildingManager.close();
	}
	
	
}
