package com.euler;

import java.math.BigInteger;
import java.math.RoundingMode;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;

public class Euler447_8 {
	private final static long N=LongMath.pow(10l,14);
	private final static long MOD=1_000_000_007l;
	
	private static BigInteger getConsecutiveSum(long a,long b)	{
		BigInteger sum=BigInteger.valueOf(a+b);
		BigInteger howMany=BigInteger.valueOf(b+1-a);
		BigInteger prod=sum.multiply(howMany).shiftRight(1);
		return prod;
	}
	
	private static BigInteger getHarmonicSum(long q)	{
		BigInteger result=BigInteger.ZERO;
		long sq=LongMath.sqrt(q,RoundingMode.DOWN);
		for (long i=1;i<=sq;++i)	{
			long r=q/i;
			result=result.add(BigInteger.valueOf(i*r));
		}
		long x=1+sq;
		while (x<=q)	{
			long r=q/x;
			long maxI=q/r;
			BigInteger factor=getConsecutiveSum(x,maxI);
			result=result.add(factor.multiply(BigInteger.valueOf(r)));
			x=1+maxI;
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long sn=LongMath.sqrt(N,RoundingMode.DOWN);
		int[] möbius=EulerUtils.möbiusSieve((int)sn);
		BigInteger fullResult=BigInteger.ZERO;
		for (long i=1;i<=sn;++i)	{
			long m=möbius[(int)i];
			if (m==0) continue;
			BigInteger harmonicTerm=getHarmonicSum(N/(i*i));
			fullResult=fullResult.add(harmonicTerm.multiply(BigInteger.valueOf(i*m)));
		}
		BigInteger bigN=BigInteger.valueOf(N);
		BigInteger tri=bigN.multiply(bigN.add(BigInteger.ONE)).shiftRight(1);
		fullResult=fullResult.subtract(tri);
		BigInteger result=fullResult.mod(BigInteger.valueOf(MOD));
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(fullResult);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
