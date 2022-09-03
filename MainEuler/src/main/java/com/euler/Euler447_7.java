package com.euler;

import java.math.RoundingMode;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;

public class Euler447_7 {
	private final static long N=LongMath.pow(10l,14);
	private final static long MOD=1_000_000_007l;
	
	private final static long MOD_INV_2=(MOD+1)/2;
	
	private static long getConsecutiveSum(long a,long b)	{
		long sum=(a+b)%MOD;
		long howMany=(b+1-a)%MOD;
		long prod=(sum*howMany)%MOD;
		prod*=MOD_INV_2;
		return prod%MOD;
	}
	
	private static long getHarmonicSum(long q)	{
		long result=0;
		long sq=LongMath.sqrt(q,RoundingMode.DOWN);
		for (long i=1;i<=sq;++i)	{
			long r=q/i;
			result+=i*r;
			result%=MOD;
		}
		long x=1+sq;
		while (x<=q)	{
			long r=q/x;
			long maxI=q/r;
			long factor=getConsecutiveSum(x,maxI);
			result+=factor*(r%MOD);
			result%=MOD;
			x=1+maxI;
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long sn=LongMath.sqrt(N,RoundingMode.DOWN);
		int[] möbius=EulerUtils.möbiusSieve((int)sn);
		long result=0;
		for (long i=1;i<=sn;++i)	{
			long m=möbius[(int)i];
			if (m==0) continue;
			long factor1=(i%MOD)*m;
			long factor2=getHarmonicSum(N/(i*i));
			result+=factor1*factor2;
			result%=MOD;
			if (result<0) result+=MOD;
		}
		long nMod=N%MOD;
		long tri=(nMod*(nMod+1))%MOD;
		tri*=MOD_INV_2;
		tri%=MOD;
		result-=tri;
		if (result<0) result+=MOD;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
