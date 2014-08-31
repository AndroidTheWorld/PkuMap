package com.pkumap.activity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.pkumap.bean.Building;
import com.pkumap.bean.Poi;
import com.pkumap.bean.Point;
import com.pkumap.bean.RoadNode;
import com.pkumap.handler.BitmapHandler;
import com.pkumap.handler.InertiaHandler;
import com.pkumap.manager.BuildingManager;
import com.pkumap.manager.InertiaTimerManager;
import com.pkumap.manager.PathPlanManager;
import com.pkumap.manager.PoiManager;
import com.pkumap.task.BitMapAsyncTask;
import com.pkumap.util.ConvertCoordinate;
import com.pkumap.util.ImageLoader;
import com.zdx.pkumap.R;

import android.R.integer;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class MapView extends View {
	
	private final static String TAG="MapView";
	/**
	 * 地图左上角的坐标（mapPosX,mapPosY）
	 */
	private float mapPosX=0;
	private float mapPosY=0;
	/**
	 * 地图的总体宽度和高度（与Level有关）
	 */
	private float mapWidth;
	private float mapHeight;
	/**
	 * 地图在缩放之前的mapDX,mapDY,方便计算下层中心点对应
	 */
	private float preZoomMapDX;
	private float preZoomMapDY;
	/**
	 * 画布上起始绘的位置
	 */
	public float startX=0;
	public float startY=0;
	private ImageLoader imageLoader;
	/**
	 * 手机屏幕中心点到整个地图的左端的距离---mapDX
	 * 手机屏幕中心点到整个地图的顶端的距离---mapDY
	 * 需要一直维护这样的距离
	 */
	public float mapDX;
	public float mapDY;
	/**
	 * 在放缩过程中不断变化的图片的大小(宽度，高度),需要维护
	 */
	private float BitmapWidth;
	private float BitmapHeight;
	/**
	 * 手指按下时的坐标（屏幕坐标）
	 */
	private float preX,preY;
	/**
	 * 手指滑动时的当前坐标（屏幕坐标）
	 */
	private float curX,curY;
	/**
	 * 地图的大小（宽和高）
	 */
	public float ScreenWidth,ScreenHeight;
	/**
	 * 瓦片初始的大小
	 */
	private final static float PIC_WIDTH=256f;
	private final static float PIC_HEIGHT=256f;
	/**
	 * 图片要绘制的区域和绘制的位置（一般要绘区域为整个瓦片）
	 */
	private RectF srcRect;
	private RectF dstRect;
	/**
	 * 放缩的次数（临时加）
	 */
	private int flag=0;
	/**
	 * 当前地图的层级
	 */
	public int level=1;
	/**
	 * 地图横向和纵向的总的块数
	 */
	private int x_num;
	private int y_num;
	/**
	 * 画布上要绘制的块的索引范围
	 */
	public int left=0,top=0,right=0,bottom=0;
	/**
	 * 单指在屏幕滑动时，x方向和y方向上的偏移量
	 */
	public float moveDX;
	public float moveDY;
	/**
	 * 记录上次两指之间的距离
	 */
	private double lastFingerDis;
	/**
	 * 记录当前两指之间的距离
	 */
	private double curFingerDis;
	/**
	 * 记录两指同时放在屏幕上时，中心点的横坐标值
	 */
	private float centerPointX;

	/**
	 * 记录两指同时放在屏幕上时，中心点的纵坐标值
	 */
	private float centerPointY;
	/**
	 * 初始化状态常量
	 */
	public static final int STATUS_INIT = 1;

	/**
	 * 图片缩放状态常量
	 */
	public static final int STATUS_ZOOM = 2;
	/**
	 * 图片拖动状态常量
	 */
	public static final int STATUS_MOVE = 3;
	/**
	 * 记录当前操作的状态，可选值为STATUS_INIT、STATUS_ZOOM_OUT、STATUS_ZOOM_IN和STATUS_MOVE
	 */
	public int currentStatus;
	/**
	 * 记录总得放缩率
	 */
	public float scaleLevel=1f;
	/**
	 * 记录单次滑动，lastFingerDis/curFingerDis
	 */
	public float singleScaleLevel=1f;
	/**
	 * 判断是否开始缩放，记下初始地图的mapDX，mapDY
	 */
	private boolean isStartZoom;
	/**
	 * 处理POI业务的管理类
	 */
	public PoiManager poiManager;
	/**
	 * 处理Building业务的管理类
	 */
	public BuildingManager buildingManager;
	/**
	 * 处理路径规划的管理类
	 */
	public PathPlanManager pathPlanManager;
	/**
	 * 坐标转换类
	 */
	private ConvertCoordinate convertCoordinate;
	/**
	 * 一个全局的Canvas
	 */
	public Canvas canvas;
	/**
	 * 当前在地图上有标注的Poi
	 */
	public Poi poi;
	/**
	 * 当前3D地图上有标注的Building
	 */
	public Building building;
	/**
	 * 当前地图上有路径
	 */
	public ArrayList<RoadNode> roadPoints;
	/**
	 * 获取传入的Activity实例
	 */
	public Context context;
	private FragmentManager fmView;
	/**
	 * 地图的当前类型（二维或者三维）
	 */
	public String map_type="2dmap";
	/**
	 * 将MapActivity传过来
	 */
	private MapActivity mapActivity;
	/**
	 * 记录当手指触屏时的位置
	 */
	private float downX,downY;
	/**
	 * 手指按下的时间
	 */
	private long downtime;
	/**
	 * 手指抬起的时间
	 */
	private long uptime;
	/**
	 * 当前在地图上的位置
	 */
	public Point curLocation;
	/**
	 * 最后一次Move的过程
	 */
	private long preMoveTime;
	/**
	 * 最后一次Move的位置
	 */
	private float preMoveX;
	private float preMoveY;
	//为惯性起的线程
	private InertiaHandler inertiaHandler;
	//为惯性设置的定时器类
	private InertiaTimerManager inertiaTimerManager;
	//为绘制指定位置瓦片起的线程
	private BitmapHandler bitmapHandler;
	private BitMapAsyncTask bitMapAsyncTask;
	public MapView(Context context,AttributeSet set) {
		super(context,set);
		this.context=context;
	    imageLoader=ImageLoader.getInstance();
	 // 获取屏幕宽高
	    DisplayMetrics dm= getResources().getDisplayMetrics();
	    ScreenWidth=dm.widthPixels;
	    ScreenHeight=dm.heightPixels;
	    mapDX=ScreenWidth/2;
	    mapDY=ScreenHeight/2;
/*	    x_num=ScreenWidth/PIC_WIDTH+1;
	    y_num=ScreenHeight/PIC_HEIGHT+1;
	    right=left+x_num;
	    bottom=top+y_num;*/
	    BitmapWidth=PIC_WIDTH;
	    BitmapHeight=PIC_HEIGHT;
	    srcRect=new RectF();
	    dstRect=new RectF();
	    currentStatus = STATUS_INIT;
	    isStartZoom=false;
	    poiManager=new PoiManager(context);
	    buildingManager=new BuildingManager(context);
	    pathPlanManager=new PathPlanManager(context);
	    mapActivity=(MapActivity) context;
	    inertiaHandler=new InertiaHandler(this);
	    inertiaTimerManager=new InertiaTimerManager(inertiaHandler);
	    bitmapHandler=new BitmapHandler(this);
	 
	    if(null!=mapActivity.fm){
	    	fmView=mapActivity.fm;
	    }else{
	    	fmView=mapActivity.getFragmentManager();
	    }
	    convertCoordinate=new ConvertCoordinate();
	    roadPoints=new ArrayList<RoadNode>();
	    Log.i("ScreenWH", "width:"+ScreenWidth+",height:"+ScreenHeight);
	}
	/**
	 * 计算当前层的地图的宽和高
	 */
	public void makeMapWH(){
		mapWidth=BitmapWidth*(2<<(level+1));
		mapHeight=BitmapHeight*(2<<(level+1));
	}
	/**
	 * 计算在画布上要绘的块的范围和起始点的坐标
	 */
	public void makeCurBound(){
		//屏幕左侧到地图左侧的距离disTmpX,屏幕顶端到地图顶端的距离disTmpY
		float disTmpX=ScreenWidth/2-mapDX;
		float disTmpY=ScreenHeight/2-mapDY;
	    //左侧和上侧的块  
		int leftBlock=(int) (Math.abs(disTmpX)/BitmapWidth);
		int topBlock=(int) (Math.abs(disTmpY)/BitmapHeight);
		//起始点的坐标
		startX=disTmpX%BitmapWidth;
		startY=disTmpY%BitmapHeight;
		//跨越的块数
		x_num=(int) ((ScreenWidth-startX)/BitmapWidth+1);
		y_num=(int) ((ScreenHeight-startY)/BitmapHeight+1);
		//索引范围
		left=leftBlock;
		top=topBlock;
		right=left+x_num;
		bottom=top+y_num;
		Log.i("LRTB","left:"+left+",right:"+right+",top:"+top+",bottom:"+bottom);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		this.canvas=canvas;
		Log.i("zdx", ""+currentStatus);
		switch(currentStatus){
		case STATUS_MOVE:
			move();
			break;
		case STATUS_INIT:
			initMap(canvas);
			break;
		case STATUS_ZOOM:
			zoom_summer();
			break;
		}
		
	}
	/**
	 * 判断地图的边界值
	 */
	private void judgeBounds(){
		//获取当前层和图片大小情况下的地图大小
		makeMapWH();
		if(mapDX<ScreenWidth/2){
			mapDX=ScreenWidth/2;
		}
		if(mapDY<ScreenHeight/2){
			mapDY=ScreenHeight/2;
		}
		if(mapDX>mapWidth-ScreenWidth/2){
			mapDX=mapWidth-ScreenWidth/2;
		}
		if(mapDY>mapHeight-ScreenHeight/2){
			mapDY=mapHeight-ScreenHeight/2;
		}
	}
	/**
	 * 地图移动
	 * @param canvas
	 */
	public void move(){
		Log.i("Move","pre:"+preX+",preY:"+preY+",curX:"+curX+",curY:"+curY);
		mapDX=mapDX-moveDX;
		mapDY=mapDY-moveDY;
		//判断边界值
		judgeBounds();
		//计算要绘图的起始点和块索引范围
		makeCurBound();
		//在Canvas上绘图
		DrawVisibleMap();
		if(poi!=null&&"2dmap".equals(this.map_type)){
			DrawPoiMarker(poi);
			
		}
		if(building!=null&&"3dmap".equals(this.map_type)){
			DrawBuildingMarker(building);
			
		}
		if(roadPoints.size()>0){
			DrawPathInMap(roadPoints);
		}
		if(curLocation!=null){
			DrawCurLocationInMap(curLocation);
		}
	}
	/**
	 * 地图缩放bySummer
	 * @param canvas
	 */
	private void zoom_summer() {
	/*	if(!isStartZoom){
			preZoomMapDX=mapDX;
			preZoomMapDY=mapDY;
			isStartZoom=true;
		}*/
		if(scaleLevel > 1 && scaleLevel < 2) {
			mapDX *= singleScaleLevel;
			mapDY *= singleScaleLevel;
			BitmapWidth=BitmapWidth*singleScaleLevel;
			BitmapHeight=BitmapHeight*singleScaleLevel;
		
			makeCurBound();
		
			DrawVisibleMap(canvas);
		}
		else if(scaleLevel >= 2) {
			if(level == 3) {
				scaleLevel /= singleScaleLevel;
				
				makeCurBound();
				
				DrawVisibleMap(canvas);
				return;
			}
			scaleLevel -= 1;
			level += 1;
			float preMapDX=mapDX;
			float preMapDY=mapDY;
			
			mapDX *= singleScaleLevel;
			mapDY *= singleScaleLevel;
			
			float dis_x=mapDX-preMapDX;
			float dis_y=mapDY-preMapDY;
			Log.i("zdx_mapDXYDIS", "Out:mapDX-preMapDX:"+dis_x+",mapDY-preMapDY:"+dis_y);
			
			BitmapWidth = PIC_WIDTH*scaleLevel;
			BitmapHeight = PIC_HEIGHT*scaleLevel;
			Log.i("zdx_summer","scaleLevel_out:"+scaleLevel+",singleScaleLevel:"+singleScaleLevel);
			
/*			mapDX=preZoomMapDX*2;
			mapDY=preZoomMapDY*2;
			
			BitmapWidth=PIC_WIDTH;
			BitmapHeight=PIC_HEIGHT;
			
			isStartZoom=false;*/
			makeCurBound();
			
			DrawVisibleMap(canvas);
		}
		else if(scaleLevel <= 1) {
			if(level == 1) {
				scaleLevel /= singleScaleLevel;
				
				makeCurBound();
				
				DrawVisibleMap(canvas);
				
				return;
			}
			
			scaleLevel += 1;
			level -= 1;
			
			float preMapDX=mapDX;
			float preMapDY=mapDY;
			
			mapDX *= singleScaleLevel;
			mapDY *= singleScaleLevel;
			
			float dis_x=mapDX-preMapDX;
			float dis_y=mapDY-preMapDY;
			Log.i("zdx_mapDXYDIS", "In:mapDX-preMapDX:"+dis_x+",mapDY-preMapDY:"+dis_y);
			
			BitmapWidth = PIC_WIDTH * scaleLevel;
			BitmapHeight = PIC_HEIGHT * scaleLevel;
			Log.i("zdx_summer","scaleLevel_in:"+scaleLevel+",singleScaleLevel:"+singleScaleLevel);
			
/*			mapDX=preZoomMapDX*2;
			mapDY=preZoomMapDY*2;
			
			BitmapWidth=PIC_WIDTH;
			BitmapHeight=PIC_HEIGHT;
			isStartZoom=false;*/
			
			makeCurBound();
			
			DrawVisibleMap(canvas);	
		}
		if(poi!=null&&"2dmap".equals(this.map_type)){
			DrawPoiMarker(poi);
		}
		if(building!=null&&"3dmap".equals(this.map_type)){
			DrawBuildingMarker(building);
		}
		if(roadPoints.size()>0){
			DrawPathInMap(roadPoints);
		}
		if(curLocation!=null){
			DrawCurLocationInMap(curLocation);
		}
	}
	/**
	 * 地图缩放
	 * @param canvas 
	 */
	private void zoom(Canvas canvas){
		//记录下缩放初始状态mapDX,mapDY,为下一层中心点对应做准备
		if(flag==1||flag==-1){
			preZoomMapDX=mapDX;
			preZoomMapDY=mapDY;
		}
		//缩放倍数的控制（暂时先这样，之后再调整）
		if(scaleLevel>1.5f){
			level++;
			if(level<4){
				BitmapWidth=PIC_WIDTH;
				BitmapHeight=PIC_HEIGHT;
				
				mapDX=preZoomMapDX*2;
				mapDY=preZoomMapDY*2;
				
				flag=0;
				scaleLevel=1f;
				
			}else{
				level=3;
				flag=8;
				scaleLevel=scaleLevel/singleScaleLevel;
				
			}	
		}else if(scaleLevel<0.8f){
			level--;
			if(level>0){
				BitmapWidth=PIC_WIDTH;
				BitmapHeight=PIC_HEIGHT;
				
				mapDX=preZoomMapDX/2;
				mapDY=preZoomMapDY/2;
					
				flag=0;
				scaleLevel=1f;
			}else{
				level=1;
				flag=-4;
				scaleLevel=scaleLevel/singleScaleLevel;
			}
			
		}else{
			mapDX=mapDX*singleScaleLevel;
			mapDY=mapDY*singleScaleLevel;
			
			BitmapWidth=BitmapWidth*singleScaleLevel;
			BitmapHeight=BitmapHeight*singleScaleLevel;
			
		}
		
		makeCurBound();
		
		DrawVisibleMap(canvas);
		
	}
	/**
	 * 根据距离mapDX,mapDY,来计算整个地图当前左上角的坐标mapPosX,mapPosY
	 */
	private void makeMapPosXYFromMapDXY(){
		mapPosX=ScreenWidth/2-mapDX;
		mapPosY=ScreenHeight/2-mapDY;
	}
	/**
	 * 图片放缩
	 * @param bgimage
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	public Bitmap scaleImage(Bitmap bgimage, float newWidth, float newHeight) {  
		// 获取这个图片的宽和高  
		int width = bgimage.getWidth();  
		int height = bgimage.getHeight();  
		// 创建操作图片用的matrix对象  
		Matrix matrix = new Matrix();  
		// 计算缩放率，新尺寸除原始尺寸  
		float scaleWidth = newWidth/ width;  
		float scaleHeight = newHeight/ height;  
		// 缩放图片动作  
		matrix.postScale(scaleWidth, scaleHeight);  
		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, width, height,  
		matrix, true);  
		return bitmap;  
	}   
	/**
	 * 初始化地图(mapPosX=0,mapPosY=0,mapDX=ScreenWidth/2,mapDY=ScreenHeight/2)
	 * @param canvas
	 */
	private void initMap(Canvas canvas){
		makeCurBound();
		Log.i("InitMap", "left:"+left+",right:"+right+",top:"+top+",bottom:"+bottom);
		DrawVisibleMap(canvas);
		if(poi!=null&&"2dmap".equals(this.map_type)){
			DrawPoiMarker(poi);
		}
		if(building!=null&&"3dmap".equals(this.map_type)){
			DrawBuildingMarker(building);
		}
		if(roadPoints.size()>0){
			DrawPathInMap(roadPoints);
		}
		if(curLocation!=null){
			DrawCurLocationInMap(curLocation);
		}
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//改变View透明度
		if(mapActivity.flag==1){
			mapActivity.flag=0;
			mapActivity.windowDimOut();
			mapActivity.layers_window.dismiss();
		}
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_POINTER_DOWN:
			 Log.i(TAG,"ACTION_POINTER_DOWN");
			 if(event.getPointerCount()==2){
				 lastFingerDis=distanceBetweenFingers(event);
			 }
			 break;
		 case MotionEvent.ACTION_DOWN:
			 downtime=System.currentTimeMillis();
			 Log.i(TAG,"ACTION_DOWN");
			 preX=event.getX();
			 preY=event.getY();
			 downX=event.getX();
			 downY=event.getY();
       	
			 Log.i("preXY","preX="+preX+",preY="+preY);
            break;
		 case MotionEvent.ACTION_MOVE:
	       	 Log.i(TAG,"ACTION_MOVE");
	       	 preMoveTime=System.currentTimeMillis();
	       	 if(event.getPointerCount()==1){
	       		 curX=event.getX();
	        	 curY=event.getY();
	        	 preMoveX=preX;
	        	 preMoveY=preY;
	        	
	          	 Log.i("curXY", "curX:"+curX+",curY:"+curY);
	         	 moveDX=curX-preX;
	         	 moveDY=curY-preY;
	         	 Log.i("dXYMove","moveDX:"+moveDX+",moveDY:"+moveDY);
	         	 currentStatus = STATUS_MOVE;
	         	 preX=curX;
	         	 preY=curY;
	         	 invalidate();
	       	 }else if(event.getPointerCount()==2){
	       		 // 有两个手指按在屏幕上移动时，为缩放状态
			//		centerPointBetweenFingers(event);
					curFingerDis = distanceBetweenFingers(event);
					singleScaleLevel=(float) (curFingerDis/lastFingerDis);
					Log.i("SingleScaleLevel","singleScale:"+singleScaleLevel);
					scaleLevel=scaleLevel*singleScaleLevel;
					currentStatus=STATUS_ZOOM;
					Log.i("ScaledLevel", "scalelevel:"+scaleLevel);
					lastFingerDis=curFingerDis;
					invalidate();
	       	 } 
	         break;
        case MotionEvent.ACTION_UP:
        	uptime=System.currentTimeMillis();
        	Log.i("DTime", "uptime-downtime:"+(uptime-downtime));
        	curX=event.getX();
       	 	curY=event.getY();
       	 	Log.i("DXYUPDOWN", "curX-preX:"+(curX-preX)+",curY-preY:"+(curY-preY));
        	if((uptime-downtime)<150){
        		 if("3dmap".equals(this.map_type)){
        	       		Building curBuilding=getBuildingFromLocation(downX, downY);
        	       		if(null!=curBuilding){
        	       //			Toast.makeText(this.getContext(),curBuilding.getName(),Toast.LENGTH_SHORT).show();
        	       			ShowBuildingDetailInMap(curBuilding);
        	       		}
        	       	 }else{
        	       		 Poi curPoi=getPoiFromLocation(downX,downY);
        	       		 if(null!=curPoi){
        	       			 ShowPoiDetailInMap(curPoi);
        	       		 }
        	       	 }
        		
        	}
    		Log.i("dTime", "dTime:"+(uptime-preMoveTime));
    		float speedX=0f;
    		float speedY=0f;
    		if((uptime-preMoveTime)!=0f){
    			speedX=(curX-preMoveX)/(uptime-preMoveTime);
           	 	speedY=(curY-preMoveY)/(uptime-preMoveTime);
           	 	speedX=Math.abs(speedX)>5f?(speedX>0?5f:-5f):speedX;
           	 	speedY=Math.abs(speedY)>5f?(speedY>0?5f:-5f):speedY;
           	 	inertiaTimerManager.startTimer(speedX, speedY);
    		}
       	 	Log.i("ACTION_UP","speedX:"+speedX+",speedY:"+speedY);
       	 	
       	 	Log.i(TAG,"ACTION_UP");
        	
       	 	break;
           
        case MotionEvent.ACTION_POINTER_UP:
       	 Log.i(TAG,"ACTION_POINTER_UP");
       	 	//lastFingerDis=-1;
            break;
       
        }
		return true;
	}
	/**
	 * 隐藏Poi的标注和详情Fragment
	 */
	public void HidePoiMarkerAndDetail(){
		 poi=null;
		 building=null;
		 roadPoints.clear();
		 mapActivity.timerManager.stopTimer();
   		 this.currentStatus=STATUS_INIT;
   		 invalidate();
   		 
   		Fragment curFragment=fmView.findFragmentByTag("PoiDetailFragment");
   		if(curFragment!=null&&curFragment.isVisible()){
   			FragmentTransaction ft=fmView.beginTransaction();
   			ft.hide(curFragment);
   			ft.commit();
   		}
   		
	}
	/**
	 *在地图对应的位置显示一个poi的标注Marker
	 * @param poiPoint
	 */
	public void DrawPoiMarker(Poi poi){
		Point curLonLat=poi.getCenter();
		Point screenPoint=convertCoordinate.getScreenPointFromLonLat(curLonLat, this);
		
		Paint paint=new Paint();
		Bitmap marker=BitmapFactory.decodeResource(getResources(), R.drawable.marker);
	//	RectF rect=new RectF(0, 0, 24, 36);
	//	canvas.saveLayerAlpha(rect, 0x88, 101);
		canvas.drawBitmap(marker, screenPoint.getX()-8,screenPoint.getY()-27, paint);
	//	canvas.restore();
	}
	/**
	 * 在地图上对应位置添加一个Building的标注,同时要高亮显示该建筑
	 * @param building
	 */
	public void DrawBuildingMarker(Building building){
		Point curLonLat=building.getCenter();
		Point screenPoint=convertCoordinate.getScreenPointFromLonLat(curLonLat, this);
		
		Bitmap marker=BitmapFactory.decodeResource(getResources(), R.drawable.marker);
		canvas.drawBitmap(marker, screenPoint.getX()-8,screenPoint.getY()-27,null);
		MakeBuildingHighLight(building);
	}
	/**
	 * 高亮显示相应的建筑
	 * @param building
	 */
	public void MakeBuildingHighLight(Building building){
		ArrayList<Point> coordinates=building.getCoordinates();
		
		ArrayList<Point> newCoordinates=new ArrayList<Point>();
		for(int i=0;i<coordinates.size();i++){
			Point point=new Point();
			float x=coordinates.get(i).getX();
			float y=coordinates.get(i).getY();
			
			point.setX(x);
			point.setY(y);
			Point newPoint=new Point();
			newPoint=convertCoordinate.getScreenPointFromLonLat(point, this);
			newCoordinates.add(newPoint);
		}
		
		Paint paint=new Paint();
		paint.setAntiAlias(true);   
		paint.setColor(Color.YELLOW);
		paint.setAlpha(80);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setStrokeWidth(2);
		
		Path path=new Path();
		Point start=newCoordinates.get(0);
		path.moveTo(start.getX(), start.getY());
		for(int i=1;i<newCoordinates.size();i++){
			Point midPoint=newCoordinates.get(i);
			path.lineTo(midPoint.getX(), midPoint.getY());
		}
		path.close();
		canvas.drawPath(path, paint);
		
	}
	/**
	 * 在3D地图上显示相应的Marker，同时在fragment上显示详细的Building信息，同时将该Building高亮显示
	 * @param curBuilding
	 */
	private void ShowBuildingDetailInMap(Building curBuilding){
		building=curBuilding;
		this.currentStatus=STATUS_INIT;
		invalidate();
		
		Fragment curFragment=fmView.findFragmentByTag("PoiDetailFragment");
		FragmentTransaction ft=fmView.beginTransaction();
		BuildingDetailFragment bdf=new BuildingDetailFragment(building);
		if(curFragment!=null){
   			ft.replace(R.id.poi_detail_layout, bdf, "PoiDetailFragment");
   			if(!curFragment.isVisible()){
   				ft.show(curFragment);
   			}
   		}else{
   			ft.add(R.id.poi_detail_layout, bdf, "PoiDetailFragment");
   		}
   		ft.commit();
	}
	/**
	 * 在地图上显示相应的的Marker，同时在fragment上显示详细的Poi信息
	 * @param poi
	 */
	private void ShowPoiDetailInMap(Poi curPoi){
		poi=curPoi;
		this.currentStatus=STATUS_INIT;
  		invalidate();
		
		Fragment curFragment=fmView.findFragmentByTag("PoiDetailFragment");
   		FragmentTransaction ft=fmView.beginTransaction();
   		PoiDetailFragment pdf=new PoiDetailFragment(poi);
   		if(curFragment!=null){
   			ft.replace(R.id.poi_detail_layout, pdf, "PoiDetailFragment");
   			if(!curFragment.isVisible()){
   				ft.show(curFragment);
   			}
   		}else{
   			ft.add(R.id.poi_detail_layout, pdf, "PoiDetailFragment");
   		}
   		ft.commit();
   		
	}
	/**
	 * 在地图上画出相应的路径
	 * @param pathPoints
	 */
	public void DrawPathInMap(ArrayList<RoadNode> roadNodes){
		
		ArrayList<Point> pathPoints=new ArrayList<Point>();
		for(int i=0;i<roadNodes.size();i++){
			Point point=new Point();
			float x=roadNodes.get(i).getX();
			float y=roadNodes.get(i).getY();
			
			point.setX(x);
			point.setY(y);
			Point newPoint=new Point();
			newPoint=convertCoordinate.getScreenPointFromLonLat(point, this);
			pathPoints.add(newPoint);
		}
		
		Paint paint=new Paint();
		paint.setAntiAlias(true);   
		paint.setColor(Color.BLUE);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3);
		
		Path path=new Path();
		Point start=pathPoints.get(0);
		path.moveTo(start.getX(), start.getY());
		for(int i=1;i<pathPoints.size();i++){
			Point midPoint=pathPoints.get(i);
//			Bitmap marker=BitmapFactory.decodeResource(getResources(), R.drawable.marker);
//			canvas.drawBitmap(marker, midPoint.getX()-8,midPoint.getY()-27,null);
			path.lineTo(midPoint.getX(), midPoint.getY());
			
		}
		canvas.drawPath(path, paint);
	}
	/**
	 * 在当前地图上绘制出当前Gps获取到的位置
	 * @param curLocPoint
	 */
	private void DrawCurLocationInMap(Point curLocPoint){
		Point screenPoint=convertCoordinate.getScreenPointFromLonLat(curLocPoint, this);
		Bitmap marker=BitmapFactory.decodeResource(getResources(), R.drawable.cur_loc);
		canvas.drawBitmap(marker, screenPoint.getX()-8,screenPoint.getY()-27, null);
	}
	/**
	 * 在三维地图上获取当前位置是否在Building范围内
	 * @param x 手机屏幕上当前点的X坐标
	 * @param y 手机屏幕上当前点的y坐标
	 * @return
	 */
	private Building getBuildingFromLocation(float x,float y){
		int flagScale=getScaleFlag(level);
		Point curScreenPoint=new Point(x,y);
		Point lonLatPoint=convertCoordinate.getLonLatFromScreen(curScreenPoint, this);
		Log.i("LonLatPoint","FlagScale:"+flagScale+",ScaleLevel:"+scaleLevel+",XY"+x+","+y+",lonlat:"+lonLatPoint.getX()+","+lonLatPoint.getY());
		return buildingManager.getBuildingByBound(lonLatPoint);
	}
	/**
	 * 当前手点下的位置是否在poi的显示范围
	 * @param x 手机屏幕上当前点的X坐标
	 * @param y 手机屏幕上当前点的y坐标
	 * @return
	 */
	private Poi getPoiFromLocation(float x,float y){
		
		int flagScale=getScaleFlag(level);
		Point curScreenPoint=new Point(x,y);
		Point lonLatPoint=convertCoordinate.getLonLatFromScreen(curScreenPoint, this);
		Log.i("LonLatPoint","FlagScale:"+flagScale+",ScaleLevel:"+scaleLevel+",XY"+x+","+y+",lonlat:"+lonLatPoint.getX()+","+lonLatPoint.getY());
		return poiManager.getPoiFromBound(lonLatPoint);
	}
	/**
	 * 从层级Level中获取分裂的倍数
	 * @param level
	 * @return
	 */
	public int getScaleFlag(int level){
		//当前层级level的条件下，地图放缩的比例level（1,2,3）--->scale(1,2,4)
		int flagScale=level;
		if(flagScale!=1){
			flagScale=2<<(flagScale-2);
		}
		return flagScale;
	}
	
	/**
	 * 弹框显示
	 * @param v
	 */
	private void showPopUp(View v,float x,float y) {
		LinearLayout layout = new LinearLayout(this.getContext());
		layout.setBackgroundColor(Color.GRAY);
		TextView tv = new TextView(this.getContext());
		tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		tv.setText("I'm a pop ------!");
		tv.setTextColor(Color.WHITE);
		layout.addView(tv);

		PopupWindow popupWindow = new PopupWindow(layout,120,120);
		
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		
		int[] location = new int[2];
		v.getLocationOnScreen(location);
		
		popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, (int)x, (int)y-popupWindow.getHeight());
	}
	/**
	 * 计算两个手指之间的距离。
	 * 
	 * @param event
	 * @return 两个手指之间的距离
	 */
	private double distanceBetweenFingers(MotionEvent event) {
		float disX = Math.abs(event.getX(0) - event.getX(1));
		float disY = Math.abs(event.getY(0) - event.getY(1));
		return Math.sqrt(disX * disX + disY * disY);
	}
	/**
	 * 计算两个手指之间中心点的坐标。
	 * 
	 * @param event
	 */
	private void centerPointBetweenFingers(MotionEvent event) {
		float xPoint0 = event.getX(0);
		float yPoint0 = event.getY(0);
		float xPoint1 = event.getX(1);
		float yPoint1 = event.getY(1);
		centerPointX = (xPoint0 + xPoint1) / 2;
		centerPointY = (yPoint0 + yPoint1) / 2;
	}
	
	
	/**
	 * 绘制当前可视区域(索引范围)
	 * @param left
	 * @param right
	 * @param top
	 * @param bottom
	 */
	public void DrawVisibleMap(Canvas canvas){
		Paint paint=new Paint();
		makeSrcRect();
		for(int x=left;x<right;x++){
			for(int y=top;y<bottom;y++){
				Bitmap bitmap=null;
				String url="http://192.168.0.5:8083/pkumap/map?level="+level+"&x="+x+"&y="+y+"&type=2dmap";
				Log.i("URL",url);
				bitmap=readBitmapFromAsset(level,x,y);
				if(bitmap==null)bitmap=readBitmapFromAsset();
				Log.i("startXY","startX:"+startX+",startY:"+startY+"x:"+x+",y:"+y);
				if(bitmap!=null){
					makeDstRect(x,y);
					canvas.drawBitmap(bitmap, null, dstRect, null);
		/*			paint.setColor(Color.RED);
					paint.setStyle(Style.STROKE);
					canvas.drawRect(dstRect, paint);*/
				}
			}		
		}
	}
	public void DrawVisibleMap(){
		Paint paint=new Paint();
		makeSrcRect();
		for(int x=left;x<right;x++){
			for(int y=top;y<bottom;y++){
				Bitmap bitmap=null;
				String url="http://192.168.0.5:8083/pkumap/map?level="+level+"&x="+x+"&y="+y+"&type=2dmap";
				Log.i("URL",url);
				Integer[] params=new Integer[]{level,x,y};
//				bitMapAsyncTask=new BitMapAsyncTask(this, bitmapHandler);
//				bitMapAsyncTask.execute(params);
				bitmap=readBitmapFromAsset(level,x,y);
				if(bitmap==null)bitmap=readBitmapFromAsset();
				Log.i("startXY","startX:"+startX+",startY:"+startY+"x:"+x+",y:"+y);
				if(bitmap!=null){
					makeDstRect(x,y);
					canvas.drawBitmap(bitmap, null, dstRect, null);
			/*		paint.setColor(Color.RED);
					paint.setStyle(Style.STROKE);
					canvas.drawRect(dstRect, paint);*/
				}
			}		
		}
	}
	/**
	 * 绘制单独一个Tile块
	 * @param block_x
	 * @param block_y
	 */
	public void drawTile(int block_x,int block_y,Bitmap bitmap){
		makeDstRect(block_x, block_y);
		canvas.drawBitmap(bitmap, null, dstRect, null);
	}
	/**
	 * 要绘制的图片的区域
	 */
	public void makeSrcRect(){
		srcRect.left=0;
		srcRect.top=0;
		srcRect.right=(int) (BitmapWidth-1);
		srcRect.bottom=(int) (BitmapHeight-1);
	}
	/**
	 * 绘制时目标矩形区域的坐标
	 * @param x
	 * @param y
	 */
	public void makeDstRect(int x,int y){
		dstRect.left=startX+(x-left)*BitmapWidth;
		dstRect.top=startY+(y-top)*BitmapHeight;
		dstRect.right=dstRect.left+BitmapWidth;
		dstRect.bottom=dstRect.top+BitmapHeight;
		Log.i("DstRect","dstLeft:"+dstRect.left+",dstRight:"+dstRect.right+",dstTop:"+dstRect.top+",dstBottom:"+dstRect.bottom);
	}
	/**
	 * 按指定的路径读取位图资源
	 * @param path
	 * @return
	 * @throws IOException 
	 */
	public Bitmap readBitmapFromAsset(){
		String path="map3d/blank.jpg";
		Bitmap bitmap=null;
		bitmap=imageLoader.getBitmapFromMemoryCache(path);
		InputStream inputStream=null;
		if(bitmap!=null){
			Log.i("MemoryCache",path);
			return bitmap;
		}else{
			try{
				inputStream=getResources().getAssets().open(path);
				if(inputStream!=null){
					bitmap=BitmapFactory.decodeStream(inputStream);
					imageLoader.addBitmapToMemoryCache(path, bitmap);
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
		}
		return bitmap;
	}
	/**
	 * 从本地读取文件
	 * @param level
	 * @param x
	 * @param y
	 * @return
	 * @throws IOException 
	 */
	public Bitmap readBitmapFromAsset(int level,int x,int y) {
		Bitmap bitmap=null;
		String bitmapUrl="";
		if("3dmap".equals(map_type)){
			bitmapUrl="map3d/"+level+"/"+x+","+y+".jpg";
		}
		if("2dmap".equals(map_type)){
			bitmapUrl="map2d/"+level+"/"+y+"_"+x+".png";
		}
		bitmap=imageLoader.getBitmapFromMemoryCache(bitmapUrl);
		InputStream inputStream=null;
		try{	
			if(bitmap!=null){
				Log.i("MemoryCache",bitmapUrl);
				return bitmap;
			}else{
				inputStream=getResources().getAssets().open(bitmapUrl);
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
