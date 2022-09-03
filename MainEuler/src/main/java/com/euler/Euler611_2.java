package com.euler;

import java.math.RoundingMode;

import com.google.common.math.LongMath;

public class Euler611_2 {
	private final static long LIMIT=100l;
	private final static long MAX_SQRT=LongMath.sqrt(LIMIT,RoundingMode.DOWN);
	
	public static void main(String[] args)	{
		int[] doors=new int[(int)(1+LIMIT)];
		for (long i=1;i<MAX_SQRT;++i)	{
			long ii=i*i;
			for (long j=1+i;j<=MAX_SQRT;++j)	{
				long jj=j*j;
				long idx=ii+jj;
				if (idx>LIMIT) break;
				int iIdx=(int)idx;
				doors[iIdx]=1+doors[iIdx];
			}
		}
		for (int i=0;i<=(int)LIMIT;++i) System.out.print(""+doors[i]+" ");
		System.out.println();
	}
}
