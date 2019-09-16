package com.ruiger.toolbox.util;

import java.io.IOException;

/**
 * <p>Title: </p>
 * <p> </p>
 * <p>Company: www.dhcc.com.cn</p>
 * <p>Email: yeyi@dhcc.com.cn</p>
 * <p>Copyright: Copyright (c) 2019</p>
 *
 * @author yeyi
 * @version 1.0
 * @date 2019/9/16
 **/
public class HolidayUtil {
	private static String URL = "http://tool.bitefu.net/jiari/";

	public HolidayUtil() {
	}

	public static String dayType(String date) throws IOException {
		String result = HttpUtil.executeUrl(URL + "?d=" + date, 1);
		return result.replace("\"", "");
	}

	public static boolean isHoliday(String date) throws IOException {
		String result = dayType(date);
		return !"0".equals(result);
	}
}
