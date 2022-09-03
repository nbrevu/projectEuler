package com.euler;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;

public class Euler370 {
	// TODO: far too slow.
	private final static long LIMIT=LongMath.pow(10l,6);
	// private final static long LIMIT=25*LongMath.pow(10l,12);

	public static void main(String[] args)	{
		long sum=LIMIT/3;	// Equilateral triangles.
		for (long d=2;;++d)	{
			if ((d%1000)==0) System.out.println(""+d+": "+sum+"...");
			long start=d+1;
			long end=start+start;
			long smallest=3*d*(d+1)+1;
			if (smallest>LIMIT) break;
			for (long n=start;n<=end;++n)	{
				if (EulerUtils.gcd(n,d)!=1) continue;
				long twoSmaller=d*d+n*d;
				long bigger=n*n;
				if (bigger>=twoSmaller) break;
				long first=d*d+n*d+n*n;
				sum+=LIMIT/first;
			}
		}
		System.out.println(sum);
	}
}
