package com.euler;

import com.euler.common.EulerUtils;

public class Euler769_6 {
	private final static long N=1000000;
	
	private final static double P_BOUND_DENOM=5*Math.sqrt(3)-6;
	
	// OK!! This gets the right solution :D. Not workable for 10^14, so I need to sum instead of counting. But VERY promising.
	public static void main(String[] args)	{
		long result=0;
		long minP1=(long)Math.sqrt(N/P_BOUND_DENOM);
		for (long p=1;p<=minP1;++p)	{
			long delta=Math.max(0l,13*p*p-4*N);
			long q1=(long)Math.ceil(Math.sqrt(3)*p);
			long q2=(long)Math.ceil((5*p-Math.sqrt(delta))/2);
			for (long q=q1;q<q2;++q) if (EulerUtils.areCoprime(p,q)&&(((9*p-q)%13)!=0)) ++result;
		}
		long minP2=(long)Math.sqrt(13*N/P_BOUND_DENOM);
		for (long p=1;p<=minP2;++p)	{
			long delta=Math.max(0l,13*p*p-52*N);
			long q1=(long)Math.ceil(Math.sqrt(3)*p);
			long q2=(long)Math.ceil((5*p-Math.sqrt(delta))/2);
			for (long q=q1;q<q2;++q) if (EulerUtils.areCoprime(p,q)&&(((9*p-q)%13)==0)) ++result;
		}
		System.out.println(result);
	}
}
