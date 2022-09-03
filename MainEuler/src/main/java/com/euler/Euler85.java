package com.euler;

import java.math.RoundingMode;

import com.google.common.math.IntMath;

public class Euler85 {
	// I've already done this problem, but I want to try this alternate method.
	private final static int LIMIT=2000000;
	
	private static int[] calculateTriangulars(int limit)	{
		int[] result=new int[limit];
		result[0]=0;
		for (int i=1;i<limit;++i) result[i]=result[i-1]+(int)i;
		return result;
	}
	
	private static int searchForBestComplement(int in,int goal)	{
		return IntMath.sqrt((2*goal)/in,RoundingMode.FLOOR);
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int actualLimit=IntMath.sqrt(2*LIMIT,RoundingMode.FLOOR);
		int[] triangs=calculateTriangulars(actualLimit);
		int currentBest=0;
		int currentBestProduct=0;
		for (int i=2;i<triangs.length;++i)	{
			int approximateResult=searchForBestComplement(triangs[i],LIMIT);
			if (approximateResult<i) break;
			int low=Math.max(0,approximateResult-1);
			int high=Math.min(triangs.length,approximateResult+2);
			for (int j=low;j<high;++j)	{
				int area=triangs[i]*triangs[j];
				if (Math.abs(LIMIT-area)<Math.abs(LIMIT-currentBest))	{
					currentBest=area;
					currentBestProduct=i*j;
				}
			}
		}
		System.out.println(currentBestProduct);
		long tac=System.nanoTime();
		double seconds=((double)(tac-tic))/1e9;
		System.out.println("Elapsed: "+seconds+" secs.");
	}
}
