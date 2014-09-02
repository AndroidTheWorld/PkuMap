package com.pkumap.handler;

import com.pkumap.activity.MapView;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
//当图片加载完成时，在指定位置进行绘制
public class BitmapHandler extends Handler {

	private MapView mapView;
	//块索引
	private int block_x;
	private int block_y;
	private Bitmap bitmap;
	//绘制的目标区域
	private RectF dstRectF;
	//起始绘的位置
	private float startX;
	private float startY;
	//图片的大小（BitMapWidth/BitMapHeight）
	private float BitmapWidth;
	private float BitmapHeight;
	//画布上绘制的块的索引范围
	private int left,right,top,bottom;
	//画布类
	private Canvas canvas;
	public BitmapHandler(MapView mapView){
		this.mapView=mapView;
		this.startX=mapView.startX;
		this.startY=mapView.startY;
		this.left=mapView.left;
		this.top=mapView.top;
		this.canvas=mapView.canvas;
		this.dstRectF=new RectF();
	}
	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
		case 0x456:
			Bundle bundle=msg.getData();
			block_x=bundle.getInt("block_x");
			block_y=bundle.getInt("block_y");
			bitmap=bundle.getParcelable("Bitmap");
//			drawBitmapInCanvas();
			mapView.drawTile(block_x, block_y, bitmap);
			break;
		}
		super.handleMessage(msg);
	}
	/**
	 * 在指定位置上绘制Bitmap
	 */
	private void drawBitmapInCanvas(){
		makeDstRect(block_x, block_y);
		if(bitmap!=null){
			canvas.drawBitmap(bitmap, null, dstRectF, null);
		}
	}
	/**
	 * 绘制时目标矩形区域的坐标
	 * @param x
	 * @param y
	 */
	public void makeDstRect(int x,int y){
		dstRectF.left=startX+(x-left)*BitmapWidth;
		dstRectF.top=startY+(y-top)*BitmapHeight;
		dstRectF.right=dstRectF.left+BitmapWidth;
		dstRectF.bottom=dstRectF.top+BitmapHeight;
		Log.i("DstRect","dstLeft:"+dstRectF.left+",dstRight:"+dstRectF.right+",dstTop:"+dstRectF.top+",dstBottom:"+dstRectF.bottom);
	}
}
