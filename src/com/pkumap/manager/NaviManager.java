package com.pkumap.manager;

import android.content.Context;

import com.pkumap.bean.PointLonLat;
import com.pkumap.mathtool.CoordTransform;
import com.pkumap.mathtool.Vector3D;

public class NaviManager {
	//当前位置
	private PointLonLat curLonLat;
	//下一个位置
	private PointLonLat nextLonLat;
	//目标位置
	private PointLonLat destLonLat;
	//地球半径
	private final double EARTH_RADIUS=6378.137;
	private static NaviManager singleton;
	public static NaviManager getInstance(){
		if(singleton==null){
			singleton=new NaviManager();
		}
		return singleton;
	}
	/**
	 * 获取目标点相对于基准点的方位角
	 * @param srcLonLat  基准点
	 * @param destLonLat  目标点
	 * @return  方位角
	 */ 
	public double GetBearing(PointLonLat srcLonLat,PointLonLat destLonLat){
		double realBearing=0;
		
		double dx=destLonLat.getX()-srcLonLat.getX();  //经度差
		double dy=destLonLat.getY()-srcLonLat.getY();  //维度差
		double tempY=(destLonLat.getY()+srcLonLat.getY())/2;//平均维度
		tempY=Math.toRadians(tempY);//转化为弧度
		double tmpBearing=Math.atan(Math.abs(dx*Math.cos(tempY)/dy));
		tmpBearing=Math.toDegrees(tmpBearing);
		if(dy<0&&dx>=0){
			realBearing=180-tmpBearing;
		}else if(dy<0&&dx<0){
			realBearing=180+tmpBearing;
		}else if(dy>0&&dx>=0){
			realBearing=tmpBearing;
		}else if(dy>0&&dx<0){
			realBearing=360-tmpBearing;
		}else if(dy==0&&dx>0){
			realBearing=90;
		}else if(dy==0&&dx<0){
			realBearing=270;
		}
		return realBearing;
	}
	/**
	 * 获取目标点相对于基准点的距离
	 * @param srcLonLat  基准点
	 * @param destLonLat  目标点
	 * @return  方位角
	 */
	public double GetDistance(PointLonLat srcLonLat,PointLonLat destLonLat){
		Vector3D srcV3d=new Vector3D();
		CoordTransform.LongLat2GlobalCoord(srcLonLat.getX(), srcLonLat.getY(), 0, srcV3d);
		Vector3D destV3d=new Vector3D();
		CoordTransform.LongLat2GlobalCoord(destLonLat.getX(), destLonLat.getY(),0, destV3d);
		double dis=destV3d.dst(srcV3d);
		return dis;
	}
	
	/**
	 * 根据方位角获取具体的朝向
	 * @param bearing  方位角
	 * @return  朝向
	 */
	public String GetOrientBaseOnBearing(double bearing){
		String orient="";
		
		if(bearing>22.5&&bearing<=67.5){
			orient="东北";
		}else if(bearing>67.5&&bearing<=112.5){
			orient="东";
		}else if(bearing>112.5&&bearing<=157.5){
			orient="东南";
		}else if(bearing>157.5&&bearing<=202.5){
			orient="南";
		}else if(bearing>202.5&&bearing<=247.5){
			orient="西南";
		}else if(bearing>247.5&&bearing<=292.5){
			orient="西";
		}else if(bearing>292.5&&bearing<=337.5){
			orient="西北";
		}else if(bearing>337.5||bearing<=22.5){
			orient="北";
		}
		return orient;
	}
	/**
	 * 获取目标点相对于基准点的方位和距离
	 * @param srcLonLat
	 * @param destLonLat
	 * @return
	 */
	public String GetOrientAndDistance(PointLonLat srcLonLat,PointLonLat destLonLat){
		StringBuffer oriDis=new StringBuffer();
		double distance=GetDistance(srcLonLat, destLonLat);
		String orient=GetOrientBaseOnBearing(GetBearing(srcLonLat, destLonLat));
		oriDis.append("向"+orient+"方向");
		oriDis.append("行走大约"+(int)distance+"米");
		return oriDis.toString();
	}
	/**
	 * 获取目标点相对于基准点的方位和距离
	 * @param srcLonLat
	 * @param destLonLat
	 * @param threeLonLat
	 * @return
	 */
	public String GetOrientAndDistance(PointLonLat srcLonLat,PointLonLat destLonLat,PointLonLat threeLonLat){
		StringBuffer oriDis=new StringBuffer();
		double distance=GetDistance(srcLonLat, destLonLat);
		String orient=GetOrientBaseOnBearing(GetBearing(srcLonLat, destLonLat));
		oriDis.append("向"+orient+"方向");
		oriDis.append("行走大约"+(int)distance+"米");
		if(threeLonLat!=null){
			String nextOrient=GetOrientBaseOnBearing(GetBearing(destLonLat, threeLonLat));
			if(orient.equals(nextOrient)){
				oriDis.append("然后直走");
			}else{
				String newOrient=GetLeftOrRightByBearing(orient, nextOrient);
				if("".equals(newOrient)){
					newOrient=nextOrient;
				}
				oriDis.append("然后向"+newOrient+"转");
			}
			
		}else{
			oriDis.append("到达终点");
		}
		return oriDis.toString();
	}
	/**
	 * 根据当前的方位和下一个方位判断是左转还是右转
	 * @param orient
	 * @param nextOrient
	 * @return
	 */
	public String GetLeftOrRightByBearing(String orient,String nextOrient){
		String neworient="";
		if("南".equals(orient)&&"东".equals(nextOrient)){
			neworient="右";
		}else if("南".equals(orient)&&"西".equals(nextOrient)){
			neworient="左";
		}else if("北".equals(orient)&&"东".equals(nextOrient)){
			neworient="右";
		}else if("北".equals(orient)&&"西".equals(nextOrient)){
			neworient="左";
		}else if("西".equals(orient)&&"南".equals(nextOrient)){
			neworient="左";
		}else if("西".equals(orient)&&"北".equals(nextOrient)){
			neworient="右";
		}else if("东".equals(orient)&&"北".equals(nextOrient)){
			neworient="左";
		}else if("东".equals(orient)&&"南".equals(nextOrient)){
			neworient="右";
		}
		return neworient;
	}
}
