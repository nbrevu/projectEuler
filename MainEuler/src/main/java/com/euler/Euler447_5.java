package com.euler;

import java.math.BigInteger;
import java.math.RoundingMode;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;

public class Euler447_5 {
	private final static long N=LongMath.pow(10l,14);
	private final static long MOD=1_000_000_007l;
	
	// ES FUNKTIONIERT :O. Ahora a pasarlo a BigInteger. ES IST ÜBER-LANGSAM.
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long sn=LongMath.sqrt(N,RoundingMode.DOWN);
		int[] möbius=EulerUtils.möbiusSieve((int)sn);
		BigInteger fullResult=BigInteger.ZERO;
		for (long j=1;j<=sn;++j)	{
			long m=möbius[(int)j];
			if (m==0) continue;
			BigInteger factor1=BigInteger.valueOf(j*m);
			BigInteger factor2=BigInteger.ZERO;
			long q=N/(j*j);
			for (long i=1;i<=q;++i)	{	// 10^14 iterations when j=1, OOPS.
				long inFactor=N/(j*j*i);
				factor2=factor2.add(BigInteger.valueOf(i*inFactor));
			}
			fullResult=fullResult.add(factor1.multiply(factor2));
		}
		BigInteger bigN=BigInteger.valueOf(N);
		BigInteger triangular=bigN.multiply(bigN.add(BigInteger.ONE)).shiftRight(1);
		fullResult=fullResult.subtract(triangular);
		BigInteger result=fullResult.mod(BigInteger.valueOf(MOD));
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(fullResult);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
