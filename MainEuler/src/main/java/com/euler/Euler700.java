package com.euler;

import java.math.RoundingMode;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;

public class Euler700 {
	private final static long BASE=1504170715041707l;
	private final static long MOD=4503599627370517l;
	
	public static void main(String[] args)	{
		// So easy, it would have been a crime not to get it in the first try!
		if (EulerUtils.gcd(BASE,MOD)!=1) throw new IllegalArgumentException();
		long tic=System.nanoTime();
		// System.out.println(EulerUtils.modulusInverse(BASE,MOD));	// 3451657199285664. JAJA SI.
		long firstStageLimit=LongMath.sqrt(MOD,RoundingMode.DOWN);	// As good as any other number in that order of magnitude, really.
		long result=0;
		long current=0;
		long max=Long.MAX_VALUE;
		for(;;)	{
			current+=BASE;
			current%=MOD;
			if (current<max)	{
				result+=current;
				max=current;
				if (current<firstStageLimit) break;
			}
		}
		long inverseIndex=EulerUtils.modulusInverse(BASE,MOD);
		long minIndex=Long.MAX_VALUE;
		for (long i=1,currentIndex=inverseIndex;i<current;++i,currentIndex=(currentIndex+inverseIndex)%MOD) if (currentIndex<minIndex)	{
			minIndex=currentIndex;
			result+=i;
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
		System.out.println(result);
	}
}
