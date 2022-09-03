package com.euler;

import java.math.RoundingMode;

import com.google.common.math.LongMath;

public class Euler804_4 {
	private final static long LIMIT=LongMath.pow(10l,16);
	private final static long L4=LIMIT*4;
	
	private static long countCases(long y)	{
		long delta=L4-163*y*y;
		if (delta<0) return 0;
		long sqDelta=LongMath.sqrt(delta,RoundingMode.DOWN);
		long maxX=(sqDelta-y)/2;
		long minX=(-sqDelta-y)/2;
		return 1+(maxX-minX);
	}
	
	// 4921620542595932 is not valid.
	// Nor it's 4920872388317003 :O. What am I doing wrong? It seems completely direct...
	// 4921370551066914, also wrong.
	public static void main(String[] args)	{
		long result=2*LongMath.sqrt(LIMIT,RoundingMode.DOWN);	// Cases for y=0.
		long jitter=1000;
		long lastWithSolutions=0;
		for (long y=1;;++y)	{
			long cases=countCases(y);
			if (cases==0)	{
				long diff=Math.abs(y-lastWithSolutions);
				if (diff>jitter) break;
				else continue;
			}
			lastWithSolutions=y;
			result+=cases;
		}
		for (long y=-1;;--y)	{
			long cases=countCases(y);
			if (cases==0)	{
				long diff=Math.abs(y-lastWithSolutions);
				if (diff>jitter) break;
				else continue;
			}
			lastWithSolutions=y;
			result+=cases;
		}
		System.out.println(result);
	}
}
