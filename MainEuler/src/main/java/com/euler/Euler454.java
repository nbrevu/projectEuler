package com.euler;

import com.euler.common.EulerUtils;

public class Euler454 {
	private final static long LIMIT=1000;
	
	// OK. 1/(p+a^2) + 1/(p+b^2) = 1/p for p=a·b. Still not fast enough. Also, not all the numbers verify this pattern.
	public static void main(String[] args)	{
		long counter=0;
		for (int i=1;i<LIMIT;++i) for (int j=i+1;j<=LIMIT;++j)	{
			// 1/i + 1/j = (j+i)/ij
			long num=i+j;
			long den=i*j;
			long g=EulerUtils.gcd(num,den);
			if (num==g)	{
				++counter;
				den/=g;
				System.out.println("1/"+i+" + 1/"+j+" = 1/"+den);
			}
		}
		System.out.println("Da sind "+counter+" Zählen! Oder?");
	}
}
