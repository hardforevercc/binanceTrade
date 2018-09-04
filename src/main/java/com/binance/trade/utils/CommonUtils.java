package com.binance.trade.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtils {
	public static String getTime() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSSS").format(new Date());
	} 
}
