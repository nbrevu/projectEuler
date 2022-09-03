package com.euler;

import java.math.BigInteger;

public class Euler704_3 {
	private final static BigInteger N=BigInteger.TEN.pow(100);
	
	private static BigInteger getFullChainSum(int n)	{
		BigInteger n_2=BigInteger.valueOf(n-2);
		BigInteger n4=BigInteger.valueOf(n+4);
		return BigInteger.TWO.pow(n+1).multiply(n_2).add(n4);
	}
	
	private static BigInteger getPartialChainSum(long n,BigInteger remaining)	{
		BigInteger result=BigInteger.ZERO;
		for (long k=n;k>=1;--k)	{
			if (BigInteger.ZERO.equals(remaining)) break;
			if (k==1)	{
				if (BigInteger.ONE.equals(remaining))	{
					result=result.add(BigInteger.ONE);
					break;
				}	else throw new IllegalArgumentException("Chain exceeded.");
			}
			BigInteger[] division=remaining.divideAndRemainder(BigInteger.TWO);
			BigInteger bigK=BigInteger.valueOf(k);
			result=result.add(bigK.multiply(division[0]));
			if (BigInteger.ONE.equals(division[1])) result=result.add(bigK);
			remaining=division[0];
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int maxChain=N.bitLength()-1;
		BigInteger base=getFullChainSum(maxChain-1);
		BigInteger inPartialSum=N.add(BigInteger.ONE).subtract(BigInteger.TWO.pow(maxChain));
		BigInteger result=base.add(getPartialChainSum(maxChain,inPartialSum));
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		String strResult=result.toString();
		int len=strResult.length();
		System.out.println(strResult.substring(0,10)+"..."+strResult.substring(len-10,len)+" ("+len+" digits).");
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
