package com.pkumap.activity;

import android.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;

public class testClass extends Activity{
	private MyView myView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		myView=new MyView(testClass.this);
		
		setContentView(myView);
	}
	public class MyView extends View{

		public MyView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onDraw(Canvas canvas) {
			// TODO Auto-generated method stub
			Paint paint=new Paint();
			paint.setColor(Color.RED);
			canvas.drawLine(0, 0, 400, 600, paint);
			RectF rect=new RectF(0,0,300,300);
			canvas.saveLayerAlpha(rect, 0x88, 11);
			canvas.drawCircle(200, 200, 100, paint);
			canvas.restore();
		}
		
	}
}
