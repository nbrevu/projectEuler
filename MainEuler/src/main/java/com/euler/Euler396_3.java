package com.euler;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;

public class Euler396_3 {
	private final static long MOD=LongMath.pow(10l,9);
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long sum=0;
		EulerUtils.FastGrowingHierarchyCalculator calculator=new EulerUtils.FastGrowingHierarchyCalculator(MOD);
		for (int i=1;i<16;++i)	{
			long length=calculator.getGoldsteinLength(i);
			sum=(sum+length)%MOD;
		}
		long tac=System.nanoTime();
		System.out.println(sum);
		double seconds=(tac-tic)/1e9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}