package com.joxxe.analyser.model;
/**
 * Utility class.
 *  @author joakim hagberg joakimhagberg87@gmail.com
 *
 */
public class Util {

	public static double round2Decimal(double i){
		return (double) Math.round(i * 100) / 100;
	}

	public static String bigNumber(double number) {
		return (int)(number/1000) + "k";
	}

	public static double round1Decimal(double val) {
		return (double) Math.round(val * 10) / 10;
	}
}
