package com.euler;

import com.euler.common.Primes;

public class Euler800 {
	private final static double LIMIT_LOG=800800*Math.log(800800);
	
	private static class HybridNumberBoundCalculator	{
		private final double limitLog;
		private final long[] primes;
		private final double[] primeLogs;
		public HybridNumberBoundCalculator(double limitLog,long primeLimit)	{
			this.limitLog=limitLog;
			primes=Primes.listLongPrimesAsArray(primeLimit);
			primeLogs=new double[primes.length];
			for (int i=0;i<primes.length;++i) primeLogs[i]=Math.log(primes[i]);
		}
		private boolean isBelowLimit(int i1,int i2)	{
			double log=primes[i1]*primeLogs[i2]+primes[i2]*primeLogs[i1];
			return log<=limitLog;
		}
		private int maxValidIndex(int baseIndex)	{
			int low=0;
			if (!isBelowLimit(baseIndex,low)) throw new IllegalArgumentException("The initial prime is too big!");
			int high=primes.length-1;
			if (isBelowLimit(baseIndex,high)) throw new IllegalArgumentException("Need more primes!");
			while (high-low>1)	{
				int mid=low+(high-low)/2;
				if (isBelowLimit(baseIndex,mid)) low=mid;
				else high=mid;
			}
			return low;
		}
		private long count()	{
			long result=0;
			for (int i=0;;++i)	{
				int allValidEntries=maxValidIndex(i);
				// System.out.println("For prime "+primes[i]+", the largest compatible prime is "+primes[allValidEntries]+".");
				if (allValidEntries<=i) return result;
				else result+=allValidEntries-i;
			}
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		HybridNumberBoundCalculator calcu=new HybridNumberBoundCalculator(LIMIT_LOG,16_000_000);
		long result=calcu.count();
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
