package com.euler;

import java.math.BigInteger;

public class Euler401 {
	private final static BigInteger TWO=BigInteger.valueOf(2);
	private final static BigInteger THREE=BigInteger.valueOf(3);
	private final static BigInteger SIX=BigInteger.valueOf(6);
	private static BigInteger sumOfSquaresUpTo(BigInteger in)	{
		BigInteger square=in.multiply(in);
		BigInteger cube=square.multiply(in);
		BigInteger numerator=TWO.multiply(cube).add(THREE.multiply(square)).add(in);
		return numerator.divide(SIX);
	}
	
	private static long sigma2(long in,long mod)	{
		long maxDivisor=(long)(Math.floor(Math.sqrt((double)in)));
		BigInteger bigIn=BigInteger.valueOf(in);
		BigInteger bigMod=BigInteger.valueOf(mod);
		BigInteger bigMaxDivisor=BigInteger.valueOf(maxDivisor);
		BigInteger commonDiff=sumOfSquaresUpTo(bigMaxDivisor);
		BigInteger res=BigInteger.ZERO;
		for (BigInteger i=BigInteger.ONE;i.compareTo(bigMaxDivisor)<=0;i=i.add(BigInteger.ONE))	{
			BigInteger iSq=i.multiply(i).mod(bigMod);
			BigInteger howMany=bigIn.divide(i);
			res=res.add(iSq.multiply(howMany)).mod(bigMod);
			BigInteger tmp1=sumOfSquaresUpTo(howMany);
			BigInteger tmp2=tmp1.subtract(commonDiff);
			res=res.add(tmp2).mod(bigMod);
		}
		return res.longValue();
	}
	
	public static void main(String[] args)	{
		System.out.println(sigma2(1000000000000000l,1000000000l));
	}
}
