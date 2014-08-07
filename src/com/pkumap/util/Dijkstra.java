package com.pku.path;

import java.util.ArrayList;


public class Dijkstra {
	/**
	 * 总共多少个路口点。
	 */
	static final int pCount = 1000;
	/**
	 * 无穷大，建立地图时，用来表示不可达
	 */
	static final int INF = 999999;
	boolean visit[] = new boolean[pCount];
	/**
	 * 数组prev[i]记录的是从源到顶点i最短路径上i的前一个顶点
	 */
	int prev[] = new int[pCount];
	double dis[] = new double[pCount];
	double [][]map = new double[pCount][pCount];

	/**
	 * 构造函数，注意点的ID是从0还是1开始
	 * @param t 地图信息，即邻接矩阵
	 */
	public Dijkstra(double [][]t){
		for(int i=0;i<pCount;i++)
			for(int j=0;j<pCount;j++)
				map[i][j] = t[i][j];
	}
	/**
	 * 计算两个点之间最短的距离
	 * @param startPoint 起点
	 * @param endPoint 终点
	 * @return 从起点到终点的路径，点索引的集合
	 */
	public ArrayList<Integer> getShortDistance(int startPoint,int endPoint){
		int i,j,k;
		double _min;
		ArrayList<Integer> resultlist = new ArrayList<Integer>();
		for(i=0;i<pCount;i++){
			visit[i] = false;
			dis[i] = map[startPoint][i];
			if(dis[i] == INF) 
				prev[i] = -1;
			else {
				prev[i] = startPoint;
			}
		}
		dis[startPoint] = 0;
		visit[startPoint] = true;
		
		for (i = 1; i < pCount; i++) {
			_min = INF;
			k = startPoint;
			for (j = 0; j < pCount; j++) {
				if(!visit[j] && _min>dis[j])           
				{               
				   _min = dis[j];                
				   k = j;           
				} 				
			}
			visit[k] = true;
			for( j = 0 ; j < pCount; j++)       
		  	{            
		   		if(!visit[j] && dis[j]>dis[k] + map[k][j]){             
		    		dis[j] = dis[k] + map[k][j];   
		    		prev[j] =  k;       
		   		}    
		  	}
		}


		//String path = PrintRoad(startPoint,endPoint);
		resultlist = PrintRoad(startPoint,endPoint);
		//System.out.println("dis:"+dis[endPoint]);
		//return path;
		return resultlist;
	}
	
	/**
	 * 记录从起点到终点的路径
	 * @param startPoint 起点
	 * @param endPoint 终点
	 * @return 路径所经过的点ID
	 */
	public ArrayList<Integer> PrintRoad(int startPoint, int endPoint) {
		//roadPath是用来显示输出路径的
		ArrayList<Integer> resultlist = new ArrayList<Integer>();
		//临时存放路径
		int []path = new int[pCount];
		int tmp = endPoint;
		int i =0;
		for (i = 0; i < pCount; i++) {
			path[i] = -1;
		}
		//记录到达点下标
		path[0] = endPoint  ;
		i = 1;
		for(int j=1;j<pCount;j++) {
			if(prev[tmp] != -1&&tmp != 0)
	        {
	            path[i] = prev[tmp] ;
	            tmp = prev[tmp];
	            i++;
	        }
	        else break;
		}
		String roadPath = "";
		//输出路径，数组逆向输出 
		for(i=pCount-1;i>=0;i--)
	    {
	        if(path[i] !=-1)
	        { //閿熻剼绛规嫹0鍏冮敓鏂ゆ嫹 
	        	roadPath = roadPath + path[i];	
	        	resultlist.add(path[i]);
	            if(i != 0)  //不是最后一个输出符号 
	                roadPath = roadPath + "-->";
	        }
	    }

	    //System.out.println(roadPath);
	   // return roadPath;
	    return resultlist;
	}
}
