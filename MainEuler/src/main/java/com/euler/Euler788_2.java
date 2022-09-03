package com.euler;

import java.math.BigInteger;
import java.math.RoundingMode;

import com.euler.common.BigIntegerUtils.BigCombinatorialNumberCache;
import com.google.common.math.IntMath;

public class Euler788_2 {
	private final static int N=2022;
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		BigInteger NINE=BigInteger.valueOf(9);
		BigInteger exp9[]=new BigInteger[1+N];
		exp9[0]=BigInteger.ONE;
		for (int i=1;i<=N;++i) exp9[i]=exp9[i-1].multiply(NINE);
		BigCombinatorialNumberCache cache=new BigCombinatorialNumberCache(N);
		BigInteger result=BigInteger.ZERO;
		for (int i=1;i<=N;++i)	{
			int j0=IntMath.divide(i-1,2,RoundingMode.UP);
			BigInteger thisResult=BigInteger.ZERO;
			for (int j=j0+1;j<i;++j) thisResult=thisResult.add(cache.get(i-1,j).multiply(exp9[i-1-j]));
			thisResult=thisResult.multiply(BigInteger.TEN).add(cache.get(i-1,j0).multiply(exp9[i-1-j0]));
			result=result.add(thisResult);
		}
		result=result.multiply(NINE);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
