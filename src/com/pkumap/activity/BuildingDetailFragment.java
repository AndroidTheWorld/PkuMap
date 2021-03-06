package com.pkumap.activity;

import com.pkumap.bean.Building;
import com.pkumap.bean.Poi;
import com.zdx.pkumap.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BuildingDetailFragment extends Fragment {
	private TextView poi_name_txt=null;
	private TextView poi_addr_txt=null;
	private TextView poi_desc_txt=null;
	private Building building;
	public BuildingDetailFragment(Building building){
		this.building=building;
	}
	 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		 
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.poi_detail_fragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		poi_name_txt=(TextView)getView().findViewById(R.id.poi_detail_name);
		 poi_name_txt.setText(building.getName());
		    
		 poi_addr_txt=(TextView)getView().findViewById(R.id.poi_detail_addr);
		 poi_addr_txt.setText(building.getCategory());
		    
		 poi_desc_txt=(TextView)getView().findViewById(R.id.poi_detail_desc);
		 poi_desc_txt.setText(building.getIntroduction());
	}
	
}
