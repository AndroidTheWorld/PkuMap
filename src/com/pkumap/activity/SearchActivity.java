package com.pkumap.activity;

import java.util.ArrayList;

import com.pkumap.eventlistener.SearchPoiOnClickListener;
import com.pkumap.util.PoiManager;
import com.zdx.pkumap.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

public class SearchActivity extends Activity {
	private AutoCompleteTextView search_edit=null;
	private Button search_btn=null;
	private PoiManager poiManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_main);
		
		poiManager=new PoiManager(this.getApplicationContext());
		
		search_edit=(AutoCompleteTextView) findViewById(R.id.poi_search_txt);
		ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line,poiManager.getPoiNameFromDB());
		search_edit.setAdapter(arrayAdapter);
		search_edit.setThreshold(1);
		
		search_btn=(Button) findViewById(R.id.poi_search_btn);
		search_btn.setOnClickListener(new SearchPoiOnClickListener(SearchActivity.this,poiManager));
		Intent intent=getIntent();
		Bundle bundle=intent.getExtras();
		String str=bundle.getString("poi_name_search");
		search_edit.setText(str);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		poiManager.close();
	}
	
	
}
