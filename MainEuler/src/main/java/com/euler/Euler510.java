package com.euler;

import static com.euler.common.EulerUtils.areCoprime;

import java.math.BigInteger;
import java.math.RoundingMode;

import com.google.common.math.LongMath;

public class Euler510 {
	private final static long LIMIT=1000000000;
	
	private final static long SQRT_LIMIT=LongMath.sqrt(LIMIT,RoundingMode.HALF_UP);
	
	private static long sumAll(long a,long b,long limit)	{
		// Assuming b>a.
		// ZUTUN!
		long a2=a*a;
		long b2=b*b;
		long ab=a+b;
		long ab2=ab*ab;
		long r1=a2*ab2;
		long r2=b2*ab2;
		long r3=a2*b2;
		if (r2>limit) return 0;
		long maxM=limit/r2;	// r2 is the biggest radius, thus the limiting factor.
		long triang=(maxM*(maxM+1))/2;
		return triang*(r1+r2+r3);
	}
	
	public static void main(String[] args)	{
		BigInteger result=BigInteger.valueOf(sumAll(1l,1l,LIMIT));
		for (long a=1;a<=SQRT_LIMIT;++a) for (long b=a+1;b<=SQRT_LIMIT;++b) if (areCoprime(a,b))	{
			long sum=sumAll(a,b,LIMIT);
			if (sum==0) break;
			result=result.add(BigInteger.valueOf(sum));
		}
		System.out.println(result.toString());
	}
}
