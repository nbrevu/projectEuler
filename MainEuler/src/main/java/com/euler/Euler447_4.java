package com.euler;

import java.math.RoundingMode;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;

public class Euler447_4 {
	private final static long N=LongMath.pow(10l,7);
	private final static long MOD=1_000_000_007l;
	
	// ES FUNKTIONIERT :O. Ahora a pasarlo a BigInteger.
	public static void main(String[] args)	{
		int[] möbius=EulerUtils.möbiusSieve((int)N);
		long result=0;
		long sn=LongMath.sqrt(N,RoundingMode.DOWN);
		for (long j=1;j<=sn;++j)	{
			long m=möbius[(int)j];
			if (m==0) continue;
			long factor1=j*m;
			long factor2=0;
			long q=N/(j*j);
			for (long i=1;i<=q;++i)	{
				long inFactor=N/(j*j*i);
				factor2+=i*inFactor;
			}
			result+=factor1*factor2;
		}
		long triangular=((N+1)*N)/2;
		result-=triangular;
		System.out.println(result);
		System.out.println(result%MOD);
	}
}
