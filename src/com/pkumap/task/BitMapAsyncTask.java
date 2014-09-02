package com.pkumap.task;

import java.io.IOException;
import java.io.InputStream;

import com.pkumap.activity.MapView;
import com.pkumap.util.ImageLoader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BitMapAsyncTask extends AsyncTask<Integer, Integer, Bitmap> {

	private int level;
	private int block_x;
	private int block_y;
	private String map_type;
	private ImageLoader imageLoader;
	private MapView mapView;
	private Handler handler;
	public BitMapAsyncTask(MapView mapView,Handler handler){
		this.mapView=mapView;
		this.handler=handler;
		this.imageLoader=ImageLoader.getInstance();
		this.map_type=mapView.map_type;
	}
	@Override
	protected Bitmap doInBackground(Integer... params) {
		level=params[0];
		block_x=params[1];
		block_y=params[2];
		return getBitmapFromAsset();
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		Message msg=new Message();
		msg.what=0x456;
		
		Bundle bundle=new Bundle();
		bundle.putInt("block_x", block_x);
		bundle.putInt("block_y", block_y);
		bundle.putParcelable("Bitmap", result);
		
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
	/**
	 * 从本地读取文件
	 * @param level
	 * @param x
	 * @param y
	 * @return
	 * @throws IOException 
	 */
	private Bitmap getBitmapFromAsset() {
		Bitmap bitmap=null;
		String bitmapUrl="";
		if("3dmap".equals(map_type)){
			bitmapUrl="map3d/"+level+"/"+block_x+","+block_y+".jpg";
		}
		if("2dmap".equals(map_type)){
			bitmapUrl="map2d/"+level+"/"+block_y+"_"+block_x+".png";
		}
		bitmap=imageLoader.getBitmapFromMemoryCache(bitmapUrl);
		InputStream inputStream=null;
		try{	
			if(bitmap!=null){
				Log.i("MemoryCache",bitmapUrl);
				return bitmap;
			}else{
				inputStream=mapView.getResources().getAssets().open(bitmapUrl);
				if(inputStream!=null){
					bitmap=BitmapFactory.decodeStream(inputStream);	
					imageLoader.addBitmapToMemoryCache(bitmapUrl, bitmap);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(inputStream!=null){
					inputStream.close();
				}			
			}catch(IOException ex){
				ex.printStackTrace();
			}	
		}
		return bitmap;
	}
}
