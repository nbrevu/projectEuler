package com.euler;

import java.math.RoundingMode;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;

public class Euler764_3 {
	private final static long LIMIT=LongMath.pow(10l,4);
	
	public static void main(String[] args)	{
		long limitY=LongMath.sqrt(LIMIT,RoundingMode.UP);
		long limitX=LongMath.divide(LIMIT,4,RoundingMode.UP);
		int counter=0;
		long sum=0;
		for (long x=1;x<=limitX;++x) for (long y=1;y<=limitY;++y) if (EulerUtils.areCoprime(x,y))	{
			long x16=16*x*x;
			long y2=y*y;
			long y4=y2*y2;
			long z2=x16+y4;
			long z=LongMath.sqrt(z2,RoundingMode.DOWN);
			if ((z<=LIMIT)&&(z*z==z2))	{
				System.out.println("Triple: "+x+", "+y+", "+z+".");
				++counter;
				sum+=x+y+z;
			}
		}
		System.out.println(counter);
		System.out.println(sum);
	}
}
