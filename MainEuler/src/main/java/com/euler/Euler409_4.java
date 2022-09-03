package com.euler;

import com.euler.common.EulerUtils;
import com.euler.common.EulerUtils.CombinatorialNumberModCache;

public class Euler409_4 {
	private final static long MOD=1_000_000_007l;
	
	private static class InclExclCalculator	{
		private final long mod;
		private final CombinatorialNumberModCache combis;
		public InclExclCalculator(long mod)	{
			combis=new CombinatorialNumberModCache(10,mod);
			this.mod=mod;
		}
		public long rawTerm(long nTerms,long nBits)	{
			// Wait, really? There is room for composition here.
			long baseTerm=EulerUtils.expMod(2,nBits-1,mod);
			return EulerUtils.expMod(baseTerm,nTerms,mod);
		}
		// ZUTUN! Cases with 0, and then repetitions, and finally incl-excl.
		// Oops, incl. excl. is even worse than I imagined. This can be done for N=100, but no way for N=10^7.
	}
	
	public static void main(String[] args)	{
		// Factorials are calculated in about 4 seconds. The maximum power needed is 999_999_788, so yes, I basically need all of them.
		/*-
		long tic=System.nanoTime();
		int[] factorials=new int[(int)MOD];
		factorials[0]=1;
		for (int i=1;i<factorials.length;++i)	{
			long nextResult=i*(long)factorials[i-1];
			factorials[i]=(int)(nextResult%MOD);
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
		long max=0;
		int N=10_000_000;
		long p=1;
		for (int i=1;i<=N;++i)	{
			p=(p+p)%MOD;
			max=Math.max(p,max);
		}
		System.out.println("MAX POWER!!!!! "+max+".");
		*/
	}
}
