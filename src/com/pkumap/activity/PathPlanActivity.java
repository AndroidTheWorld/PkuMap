package com.pkumap.activity;

import java.util.ArrayList;

import com.pkumap.eventlistener.PathPlanOnClickListener;
import com.pkumap.manager.BuildingManager;
import com.pkumap.manager.PathPlanManager;
import com.pkumap.manager.PoiManager;
import com.zdx.pkumap.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

public class PathPlanActivity extends Activity {
	private Button btn_return=null;
	private Button btn_search=null;
	private Button btn_import=null;
	private AutoCompleteTextView path_start_txt=null;
	private AutoCompleteTextView path_end_txt=null;
	public PathPlanManager pathPlanManager;
	public PoiManager poiManager;
	public BuildingManager buildingManager;
	public String map_type="2dmap";
	public int typeCode=-1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pathplan_main);
	
		Intent intent=getIntent();
		map_type=intent.getStringExtra("map_type");
		typeCode=intent.getIntExtra("type", MapActivity.RESULT_PATHPATH);
		
		pathPlanManager=new PathPlanManager(this.getApplicationContext());
		poiManager=new PoiManager(this.getApplicationContext());
		buildingManager=new BuildingManager(this.getApplicationContext());
		PathPlanOnClickListener pathPlanOnClickListener=new PathPlanOnClickListener(this);
		
		
		ArrayList<String> name=new ArrayList<String>();
		if("3dmap".equals(map_type)){
			name=buildingManager.getAllBuildingNames();
		}else{
			name=poiManager.getPoiNameFromDB();
		}
		path_start_txt=(AutoCompleteTextView) findViewById(R.id.path_start);
		ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line,name);
		if(MapActivity.RESULT_NAVI==typeCode){
			path_start_txt.setEnabled(false);
//			path_start_txt.setAdapter(arrayAdapter);
//			path_start_txt.setThreshold(1);
		}else{
			path_start_txt.setAdapter(arrayAdapter);
			path_start_txt.setThreshold(1);
		}
		
		path_end_txt=(AutoCompleteTextView) findViewById(R.id.path_end);
		path_end_txt.setAdapter(arrayAdapter);
		path_end_txt.setThreshold(1);
		
		
		
		btn_return=(Button) findViewById(R.id.path_return);
		btn_return.setOnClickListener(pathPlanOnClickListener);
		
		btn_search=(Button) findViewById(R.id.path_search);
		btn_search.setOnClickListener(pathPlanOnClickListener);
		
		btn_import=(Button) findViewById(R.id.btn_import);
		btn_import.setOnClickListener(pathPlanOnClickListener);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		poiManager.close();
		buildingManager.close();
		pathPlanManager.close();
	}
	
}
