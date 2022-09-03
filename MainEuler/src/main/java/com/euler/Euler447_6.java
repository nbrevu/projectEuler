package com.euler;

import java.math.RoundingMode;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;

public class Euler447_6 {
	private final static long N=LongMath.pow(10l,7);
	private final static long MOD=1_000_000_007l;
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long sn=LongMath.sqrt(N,RoundingMode.DOWN);
		int[] möbius=EulerUtils.möbiusSieve((int)sn);
		long result=0;
		for (long j=1;j<=sn;++j)	{
			long m=möbius[(int)j];
			if (m==0) continue;
			long factor1=j*m;
			long factor2=0;
			long q=N/(j*j);
			for (long i=1;i<=q;++i)	{	// D'OH. THIS LOOP IS HUGE WHEN J IS SMALL :(. This should be reduced with the "square root" trick.
				long inFactor=q/i;
				factor2+=i*inFactor;
				factor2%=MOD;
			}
			result+=factor1*factor2;
			result%=MOD;
		}
		long nMod=N%MOD;
		long tri=(nMod*(nMod+1))%MOD;
		tri*=EulerUtils.modulusInverse(2,MOD);
		tri%=MOD;
		result-=tri;
		if (result<0) result+=MOD;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
