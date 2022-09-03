package com.euler;

import com.euler.common.EulerUtils;

public class Euler795 {
	public static void main(String[] args)	{
		long result=0l;
		long k=1234;
		for (int n=1;n<=k;++n)	{
			long g=0;
			for (int i=1;i<=n;++i)	{
				long gcd=EulerUtils.gcd(n,i*i);
				if ((i%2)==0) g+=gcd;
				else g-=gcd;
			}
			System.out.println(String.format("g(%d)=%d.",n,g));
			result+=g;
		}
		System.out.println(result);
	}
}
