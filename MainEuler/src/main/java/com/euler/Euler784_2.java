package com.euler;

import java.math.RoundingMode;

import com.euler.common.EulerUtils;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;

public class Euler784_2 {
	private final static int LIMIT=2*IntMath.pow(10,6);
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long sum=0;
		for (long p=3;p<=LIMIT;++p) for (long r=1+LongMath.divide(p,2,RoundingMode.DOWN);r<p;++r) if (EulerUtils.areCoprime(p,r))	{
			long k1=p-r;
			long pr=p*r;
			if ((r==(p-1))||((pr%k1)==1))	{
				long q=(pr-1)/k1;
				sum+=p+q;
			}
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(sum);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
