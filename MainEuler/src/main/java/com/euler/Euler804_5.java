package com.euler;

import java.math.RoundingMode;

import com.google.common.math.LongMath;

public class Euler804_5 {
	private final static long LIMIT=LongMath.pow(10l,16);
	private final static long L4=LIMIT*4;
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=LongMath.sqrt(LIMIT,RoundingMode.DOWN);	// Cases for y=0.
		long maxY=LongMath.sqrt(L4/163,RoundingMode.DOWN);
		for (long y=1;y<=maxY;++y)	{
			long delta=L4-163*y*y;
			double sqDelta=Math.sqrt(delta);
			long maxX=(long)Math.floor((sqDelta-y)*0.5);
			long minX=(long)Math.ceil((-sqDelta-y)*0.5);
			long cases=1+(maxX-minX);
			result+=cases;
		}
		result+=result;	// Multiply times 2 to account for the sign changes in x and y.
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
