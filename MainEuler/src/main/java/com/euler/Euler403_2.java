package com.euler;

import java.math.RoundingMode;
import java.util.SortedMap;
import java.util.TreeMap;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;

public class Euler403_2 {
	private final static long N=100;
	
	/*
	 * This depends only on K.
	 * "Cake numbers": https://oeis.org/A000125.
	 */
	public static void main(String[] args)	{
		SortedMap<Long,Long> ks=new TreeMap<>();
		long result=0;
		for (long a=-N;a<=N;++a) for (long b=-N;b<=N;++b)	{
			long k2=a*a+4*b;
			if (k2<0) continue;
			long k=LongMath.sqrt(k2,RoundingMode.DOWN);
			if (k*k!=k2) continue;
			EulerUtils.increaseCounter(ks,k,1l);
			double firstX=0.5*(a-k);
			double lastX=0.5*(a+k);
			long minX=(long)Math.ceil(firstX);
			long maxX=(long)Math.floor(lastX);
			long count=0;
			for (long x=minX;x<=maxX;++x)	{
				long minY=x*x;
				long maxY=a*x+b;
				if (minY<=maxY) count+=maxY+1-minY;
			}
			result+=count;
			System.out.println(String.format("L(%d,%d)=%d [k=%d].",a,b,count,k));
		}
		System.out.println(result);
		System.out.println(ks);
	}
}
