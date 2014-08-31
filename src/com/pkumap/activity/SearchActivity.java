package com.pkumap.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.widget.GridView;
import android.widget.SimpleAdapter;

public class SearchActivity extends Activity {
	private AutoCompleteTextView search_edit=null;
	private Button search_btn=null;
	private GridView gridView;
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
		
		gridView=(GridView) findViewById(R.id.poi_theme_search);
		
		fillGridByThemeLabel();
		
	}
	/**
	 * 填充GridView
	 */
	private void fillGridByThemeLabel(){
		List<Map<String, Object>> gridItems=new ArrayList<Map<String,Object>>();
		for(int i=0;i<10;i++){
			Map<String, Object> gridItem=new HashMap<String,Object>();
			gridItem.put("poi", "条目"+i);
			gridItems.add(gridItem);
		}
		SimpleAdapter simpleAdapter=new SimpleAdapter(this, gridItems, R.layout.poi_theme_search,
				new String[]{"poi"},new int[]{R.id.poi_grid_label});
		gridView.setAdapter(simpleAdapter);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		poiManager.close();
		buildingManager.close();
	}
	
	
}
