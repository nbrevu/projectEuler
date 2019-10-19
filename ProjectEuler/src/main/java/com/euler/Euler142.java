package com.euler;

import java.math.RoundingMode;

import com.euler.common.Timing;
import com.google.common.math.LongMath;

public class Euler142 {
	private static boolean isSquare(long n)	{
		long sq=LongMath.sqrt(n,RoundingMode.DOWN);
		return sq*sq==n;
	}
	
	private static long solve()	{
		for (long i=4;;++i)	{
			long a=i*i;
			for (long j=3;j<i;++j)	{
				long c=j*j;
				long f=a-c;
				if (!isSquare(f)) continue;
				long minK=LongMath.sqrt(a-c,RoundingMode.UP);
				minK+=2-((minK-j)%2);
				for (long k=minK;k<j;k+=2)	{
					long d=k*k;
					long e=a-d;
					if (isSquare(e)&&isSquare(c-e)) return (e+f)/2+c;
				}
			}
		}
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler142::solve);
	}
}
