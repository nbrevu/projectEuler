package com.euler;

import java.math.RoundingMode;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;

public class Euler404_2 {
	private final static long N=30000;
	
	public static void main(String[] args)	{
		long result=0;
		long maxY=LongMath.sqrt(N,RoundingMode.DOWN);
		// for (long x=1;x<=N;x+=2) for (long y=1;y<=N;y+=2) if (EulerUtils.areCoprime(x,y))	{
		for (long y=1;y<=maxY;y+=2) for (long x=y+2;x<4*y;x+=2) if (EulerUtils.areCoprime(x,y))	{
			long z2=x*x+4*y*y;
			if ((z2%5)!=0) continue;
			long z=LongMath.sqrt(z2/5,RoundingMode.DOWN);
			if (z*z*5==z2)	{
				long a=x*y;
				long b=x*z;
				long c=2*y*z;
				if (b>c)	{
					long d=b;
					b=c;
					c=d;
				}
				if ((c<2*a)&&(a<N))	{
					long m,n;
					if (((x-y)%10)==0)	{
						m=2*((x-y)/5);
						n=(4*y+x)/5;
					}	else	{
						m=2*((x+y)/5);
						n=(4*y-x)/5;
					}
					System.out.println(String.format("x=%d, y=%d, z=%d; a=%d, b=%d, c=%d; m=%d, n=%d.",x,y,z,a,b,c,m,n));
					++result;
				}
			}
		}
		System.out.println(result);
	}
}
