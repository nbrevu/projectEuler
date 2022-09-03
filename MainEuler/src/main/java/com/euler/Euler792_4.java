package com.euler;

import com.euler.common.EulerUtils.CombinatorialNumberModCache;

public class Euler792_4 {
	/*-
	private static class DirectCalculator	{
		private final BigCombinatorialNumberCache cache;
		public DirectCalculator(int maxN)	{
			cache=new BigCombinatorialNumberCache(2*maxN+1);
		}
		public int calculateFor(int n)	{
			return n+2+calculateRecursive(BigInteger.ZERO,2*n+1,n+1);
		}
		private int calculateRecursive(BigInteger addend,int upper,int lower)	{
			BigInteger total=addend.add(cache.get(upper,lower));
			if (total.testBit(0)) return 0;
			BigInteger reduced=total.shiftRight(1).negate();
			return 1+calculateRecursive(reduced,upper,1+lower);
		}
	}
	*/
	private static class DirectModdedCalculator	{
		private final CombinatorialNumberModCache cache;
		public DirectModdedCalculator(int maxN,int maxDigits)	{
			cache=new CombinatorialNumberModCache(2*maxN+1,1l<<maxDigits);
		}
		public int calculateFor(int n)	{
			return n+2+calculateRecursive(0l,2*n+1,n+1);
		}
		private int calculateRecursive(long accumulated,int upper,int lower)	{
			long total=accumulated+cache.get(upper,lower);
			if ((total&1l)!=0) return 0;
			long reduced=-(total>>1);
			return 1+calculateRecursive(reduced,upper,1+lower);
		}
	}
	
	public static void main(String[] args)	{
		DirectModdedCalculator calculator=new DirectModdedCalculator(31251,32);
		int result=0;
		for (int i=1;i<=25;++i)	{
			int cube=i*i*i;
			int u=calculator.calculateFor(cube);
			result+=u;
			System.out.println(String.format("u(%d^3)=%d=%d+%d.",i,u,cube,u-cube));
		}
		System.out.println(result);
	}
}
