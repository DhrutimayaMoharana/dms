package com.watsoo.dms.util;

import java.text.DecimalFormat;

public class ConvertionUtility {

	public static Double convertKilonotsTokm(Double value) {

		double result = value * 1.852;
		DecimalFormat df = new DecimalFormat("#.##");
		return Double.valueOf(df.format(result));

	}

}
