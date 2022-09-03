package com.euler;

import com.euler.common.EulerUtils;

public class Euler795_2 {
	public static void main(String[] args)	{
		long result=0l;
		long k=1234;
		for (int i=1;i<=k;++i)	{
			long g=0;
			for (int n=i;n<=k;++n) g+=EulerUtils.gcd(i*i,n);
			System.out.println(String.format("g'(%d)=%d.",i,g));
			if ((i%2)==0) result+=g;
			else result-=g;
		}
		System.out.println(result);
	}
}
