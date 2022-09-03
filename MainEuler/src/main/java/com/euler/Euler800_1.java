package com.euler;

import com.euler.common.Primes;

public class Euler800_1 {
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
		private boolean isAboveLimit(int i1,int i2)	{
			double log=primes[i1]*primeLogs[i2]+primes[i2]*primeLogs[i1];
			return log>limitLog;
		}
		private int maxValidIndex(int baseIndex,int lastIndex)	{
			while (isAboveLimit(baseIndex,lastIndex)) --lastIndex;
			return lastIndex;
		}
		private long count()	{
			long result=0;
			int lastIndex=primes.length-1;
			for (int i=0;;++i)	{
				lastIndex=maxValidIndex(i,lastIndex);
				// System.out.println("For prime "+primes[i]+", the largest compatible prime is "+primes[allValidEntries]+".");
				if (lastIndex<=i) return result;
				else result+=lastIndex-i;
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
