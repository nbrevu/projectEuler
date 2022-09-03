package com.euler;

import com.koloboke.collect.map.IntDoubleMap;
import com.koloboke.collect.map.hash.HashIntDoubleMaps;

public class Euler697 {
	private static class LogFactCache	{
		private final IntDoubleMap cache;
		public LogFactCache()	{
			cache=HashIntDoubleMaps.newMutableMap();
			cache.put(0,0.0);
		}
		public double getFact(int in)	{
			if (in<0) throw new IllegalArgumentException();
			return cache.computeIfAbsent(in,(int operand)->Math.log(operand)+getFact(operand-1));
		}
	}
	
	public static void main(String[] args)	{
		//double log10C=46.27;
		double log10C=46.27455152522842;
		double x=log10C*Math.log(10);
		double logX=Math.log(x);
		double sum=0.0;
		LogFactCache factorials=new LogFactCache();
		for (int i=0;i<100;++i) sum+=Math.exp(i*logX-factorials.getFact(i));
		double finalValue=Math.exp(-x)*sum;
		System.out.println(finalValue);
	}
}
