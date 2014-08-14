package com.pkumap.activity;

import com.zdx.pkumap.R;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


public class PathActivity extends FragmentActivity {
	private FragmentManager frm;
	@Override
	protected void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		setContentView(R.layout.path_main);
		
		frm=getFragmentManager();
		
	}
	
}
