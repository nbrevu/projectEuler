package com.euler;

import com.euler.common.EulerUtils;

public class Euler570_6 {
	private static long getProperMod(long n,long p)	{
		n=n%p;	// This is in [-(p-1)..p-1].
		return (p+n)%p;	// This is in [0..p-1].
	}
	
	public static void main(String[] args)	{
		long p=17;
		long a0=2;
		long p4=1;
		long p3=1;
		long f3_4=getProperMod(-17,p);
		long f2_3=17%p;
		for (;;)	{
			p4=(4*p4)%p;
			p3=(3*p3)%p;
			f3_4=(f3_4+3)%p;
			f2_3=(f2_3+2)%p;
			++a0;
			if (a0>=5000) break;
			if (((f3_4*p4+f2_3*p3)%p)==0) System.out.println("a_5("+a0+")=0.");
		}
		System.out.println(EulerUtils.expMod(4,41,19));
		System.out.println(EulerUtils.expMod(3,41,19));
	}
}
