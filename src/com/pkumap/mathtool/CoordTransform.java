package com.pkumap.mathtool;


public abstract class CoordTransform
{
	public static float KILOMETER = 1000.0f;

	static private class EllipseType
	{
		double a, b, c;
		double ee1, ee2;

		public EllipseType(double _a, double _b)
		{
			// 长半轴
			a = _a;
			// 短半轴
			b = _b;
			c = a * a / b;
			// 椭球第一离心率的平方
			ee1 = (a * a - b * b) / (a * a);
			// 椭球第二离心率的平方
			ee2 = (a * a - b * b) / (b * b);
		}
	}

	//static private EllipseType WGS84_ellipse = new EllipseType(6378.137 * KILOMETER, 6356.752 * KILOMETER);
	static private EllipseType WGS84_ellipse = new EllipseType(6378.137 * KILOMETER, 6378.137 * KILOMETER);

	// return value in vec
	public static void LongLat2GlobalCoord(double longitude, double latitude, double height, Vector3D vec)
	{
		double v, lon, lat;
		lon = Math.toRadians(longitude);
		lat = Math.toRadians(latitude);
		double sinlat = Math.sin(lat), coslat = Math.cos(lat);
		double sinlon = Math.sin(lon), coslon = Math.cos(lon);

		if (MathUtils.FEqual(WGS84_ellipse.a, WGS84_ellipse.b))
		{
			v = WGS84_ellipse.a + height;
			vec.x = v * coslat * coslon;
			vec.y = v * coslat * sinlon;
			vec.z = v * sinlat;
		}
		else
		{
			v = WGS84_ellipse.a / Math.sqrt(1 - WGS84_ellipse.ee1 * sinlat * sinlat);
			vec.x = (v + height) * coslat * coslon;
			vec.y = (v + height) * coslat * sinlon;
			vec.z = ((1 - WGS84_ellipse.ee1) * v + height) * sinlat;
		}

	}

	// return <lon(a), lat(b), height(c)>
	public static Vector3D GlobalCoord2LongLat(Vector3D globalcoord)
	{
		Vector3D lon_lat_h = new Vector3D();
		lon_lat_h.x = Math.atan2(globalcoord.y, globalcoord.x) * MathUtils.radiansToDegrees;
		lon_lat_h.y = Math.asin(globalcoord.z / getShortAxis()) * MathUtils.radiansToDegrees;
		lon_lat_h.z = globalcoord.len() - getShortAxis();
		return lon_lat_h;
	}

	public static double getLongAxis()
	{
		return WGS84_ellipse.a;
	}

	public static double getShortAxis()
	{
		return WGS84_ellipse.b;
	}
}
