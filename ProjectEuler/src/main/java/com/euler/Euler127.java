package com.euler;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.euler.common.Timing;

public class Euler127 {
	private final static int LIMIT=120000;

	private static long solve()	{
		int[] rads=new int[LIMIT];
		int[] firstPrimes=Primes.firstPrimeSieve(LIMIT-1);
		for (int i=0;i<4;++i) rads[i]=i;
		long result=0;
		for (int c=4;c<LIMIT;++c)	{
			int p=firstPrimes[c];
			if (p==0) rads[c]=c;
			else	{
				int prevRad=rads[c/p];
				rads[c]=((prevRad%p)==0)?prevRad:(prevRad*p);
				if (rads[c]==c) continue;
				int radLim=c/rads[c];
				int lim=(c-1)/2;
				for (int a=1;a<=lim;++a) if (rads[a]>=radLim) continue;
				else if (rads[a]*rads[c-a]>=radLim) continue;
				else if (EulerUtils.areCoprime(c,a)) result+=c;
			}
		}
		return result;
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler127::solve);
	}
}
