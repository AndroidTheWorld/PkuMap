package com.pkumap.activity;

import com.pkumap.eventlistener.PathPlanOnClickListener;
import com.pkumap.util.PoiManager;
import com.zdx.pkumap.R;

import android.app.Activity;
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
	public final static int RESULT_PATHPLAN=3;
	private PoiManager poiManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pathplan_main);
		
		poiManager=new PoiManager(this.getApplicationContext());
		
		btn_return=(Button) findViewById(R.id.path_return);
		btn_return.setOnClickListener(new PathPlanOnClickListener(PathPlanActivity.this));
		
		btn_search=(Button) findViewById(R.id.path_search);
		btn_search.setOnClickListener(new PathPlanOnClickListener(PathPlanActivity.this));
		
		path_start_txt=(AutoCompleteTextView) findViewById(R.id.path_start);
		ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line,poiManager.getPoiNameFromDB());
		path_start_txt.setAdapter(arrayAdapter);
		path_start_txt.setThreshold(1);
		
		path_end_txt=(AutoCompleteTextView) findViewById(R.id.path_end);
		path_end_txt.setAdapter(arrayAdapter);
		path_end_txt.setThreshold(1);
		
		
		btn_import=(Button) findViewById(R.id.btn_import);
		btn_import.setOnClickListener(new PathPlanOnClickListener(PathPlanActivity.this));
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		poiManager.close();
	}
	
}
