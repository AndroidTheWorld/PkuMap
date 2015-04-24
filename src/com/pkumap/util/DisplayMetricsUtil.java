package com.pkumap.util;

import android.content.Context;

public class DisplayMetricsUtil {
	private DisplayMetricsUtil() {}
	private static DisplayMetricsUtil displayMetricsUtil = new DisplayMetricsUtil();
	private static float scale = 0.0f;
	private static float scaledDensity = 0.0f; 
	private static final int CHINESE = 10;
	private static final int NUMBER_OR_CHARACTER = 11;
	public static DisplayMetricsUtil getInstance(Context context) {
		if(scale == 0.0f) {
			scale = context.getResources().getDisplayMetrics().density;
		}
		if(scaledDensity == 0.0f) {
			scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		}
		return displayMetricsUtil;
	}
	/**
	 * dp转成px
	 * @param dipValue
	 * @return
	 */
	public int dip2px(float dipValue) {
		return (int) (dipValue * scale + 0.5f);
	}
	/**
	 * px转成dp
	 * @param pxValue
	 * @return
	 */
	public int px2dip(float pxValue) {
		return (int) (pxValue / scale + 0.5f);
	}
	public float sp2px(float spValue, int type) {
		switch(type) {
		case CHINESE:
			return spValue * scaledDensity;
		case NUMBER_OR_CHARACTER:
			return spValue * scaledDensity * 10.0f /18.0f;
			default:
				return spValue * scaledDensity;
		}
	}
}
