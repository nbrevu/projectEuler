package com.euler;

import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler521 {
	private static class SegmentedSieve	{
		private static class PrimeInfo	{
			public final long prime;
			private long currentPos;
			public PrimeInfo(long prime)	{
				this.prime=prime;
				currentPos=0;
			}
		}
		private final long[] holder;
		private final PrimeInfo[] primesData;
		private final int repetitions;
		private final long mod;
		private final int modInterval;
		private long currentSum;
		public SegmentedSieve(int blockSize,int blocks,long mod,int modInterval)	{
			long n=(long)blockSize*(long)blocks;
			List<Long> basePrimeList=Primes.listLongPrimes(LongMath.sqrt(n,RoundingMode.DOWN));
			Collections.reverse(basePrimeList);
			primesData=basePrimeList.stream().map(PrimeInfo::new).toArray(PrimeInfo[]::new);
			holder=new long[blockSize];
			repetitions=blocks;
			this.mod=mod;
			this.modInterval=modInterval;
			currentSum=0l;
		}
		private void fillBlock()	{
			Arrays.fill(holder,0l);
			for (PrimeInfo p:primesData)	{
				while (p.currentPos<holder.length)	{
					holder[(int)p.currentPos]=p.prime;
					p.currentPos+=p.prime;
				}
				p.currentPos-=holder.length;
			}
		}
		private void updateSum(long iteration)	{
			long offset=iteration*holder.length;
			int nextToMod=modInterval;
			for (int i=0;i<holder.length;++i)	{
				long value=holder[i];
				currentSum+=(value==0)?(offset+i):value;
				if (i>=nextToMod)	{
					currentSum%=mod;
					nextToMod+=modInterval;
				}
			}
		}
		public void calculate()	{
			for (int i=0;i<repetitions;++i)	{
				fillBlock();
				updateSum(i);
				System.out.println(i+"...");
			}
		}
		public long getSum()	{
			return currentSum;
		}
	}
	
	// Elapsed 13416.4329376 seconds. JAJA SI.
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		SegmentedSieve calculator=new SegmentedSieve(1_000_000_000,1000,1_000_000_000l,1_000_000);
		calculator.calculate();
		long result=calculator.getSum()-1;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		result%=1_000_000_000l;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
