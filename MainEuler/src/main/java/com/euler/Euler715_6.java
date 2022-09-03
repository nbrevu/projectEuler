package com.euler;

import java.math.RoundingMode;
import java.util.Arrays;
import java.util.TreeMap;

import com.euler.common.Primes;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongLongCursor;
import com.koloboke.collect.map.LongLongMap;
import com.koloboke.collect.map.hash.HashLongLongMaps;

public class Euler715_6 {
	private final static long MOD=1_000_000_007l;
	
	// I'm not proud of this. But it WORKS.
	private static class ModifiedMöbiusSumCalculator	{
		private static class PrimeInfo	{
			public final long prime;
			public final long minus;
			public final long primeSquared;
			private long primePos;
			private long primeSquaredPos;
			public PrimeInfo(long prime)	{
				this.prime=prime;
				this.minus=-prime;
				primeSquared=prime*prime;
				primePos=(int)((prime-1)/2);
				primeSquaredPos=(int)((primeSquared-1)/2);
			}
		}
		private final long[] goals;
		private final long[] holder;
		private final LongLongMap neededSums;
		private final PrimeInfo[] primesData;
		private final int repetitions;
		private int goalIndex;
		private long currentPos;
		private long currentSum;
		public ModifiedMöbiusSumCalculator(int blockSize,int blocks,long[] goals)	{
			long n=2l*(long)blockSize*(long)blocks;
			primesData=Primes.listLongPrimes(LongMath.sqrt(n,RoundingMode.DOWN)).stream().skip(1l).map(PrimeInfo::new).toArray(PrimeInfo[]::new);
			this.goals=goals;
			for (int i=0;i<goals.length;++i) if ((goals[i]%2)==0) --goals[i];
			holder=new long[blockSize];
			repetitions=blocks;
			neededSums=HashLongLongMaps.newMutableMap();
			goalIndex=0;
			currentPos=1l;
			currentSum=0l;
		}
		private void fillBlock()	{
			Arrays.fill(holder,1l);
			for (PrimeInfo p:primesData)	{
				while (p.primePos<holder.length)	{
					holder[(int)p.primePos]*=p.minus;
					p.primePos+=p.prime;
				}
				while (p.primeSquaredPos<holder.length)	{
					holder[(int)p.primeSquaredPos]=0;
					p.primeSquaredPos+=p.primeSquared;
				}
				p.primePos-=holder.length;
				p.primeSquaredPos-=holder.length;
			}
		}
		private static int evaluateMöbius(long index,long value)	{
			if (value==0l) return 0;
			else if (index==value) return 1;
			else if (index==-value) return -1;
			else return (value<0)?1:-1;
		}
		private void updateSums()	{
			for (int i=0;i<holder.length;i+=2)	{
				currentSum+=evaluateMöbius(currentPos,holder[i]);
				if (currentPos==goals[goalIndex])	{
					neededSums.put(currentPos,currentSum);
					neededSums.put(1+currentPos,currentSum);
					do ++goalIndex; while ((goalIndex<goals.length)&&(goals[goalIndex]==currentPos));
				}
				currentPos+=2;
				currentSum-=evaluateMöbius(currentPos,holder[i+1]);
				if (currentPos==goals[goalIndex])	{
					neededSums.put(currentPos,currentSum);
					neededSums.put(1+currentPos,currentSum);
					do ++goalIndex; while ((goalIndex<goals.length)&&(goals[goalIndex]==currentPos));
				}
				currentPos+=2;
			}
		}
		public void calculate()	{
			for (int i=0;i<repetitions;++i)	{
				fillBlock();
				updateSums();
			}
		}
		public long getCumulativeSum(long x)	{
			return neededSums.computeIfAbsent(x,(long unused)->{throw new IllegalArgumentException("Invalid number.");});
		}
		public void debug()	{
			System.out.println(new TreeMap<>(neededSums));
		}
	}
	
	public static void main(String[] args)	{
		long N=LongMath.pow(10l,5);
		LongLongMap arguments=HashLongLongMaps.newMutableMap();
		for (long i=1;i<=N;++i)	{
			long cube=i*i*i;
			arguments.put(cube%MOD,N/i);
		}
		long[] needed=arguments.values().stream().mapToLong(Long::longValue).distinct().sorted().toArray();
		ModifiedMöbiusSumCalculator calculator=new ModifiedMöbiusSumCalculator(1000,50,needed);
		calculator.calculate();
		long result=0;
		for (LongLongCursor cursor=arguments.cursor();cursor.moveNext();) result+=cursor.key()*calculator.getCumulativeSum(cursor.value());
		result%=MOD;
		System.out.println(result);
		calculator.debug();
	}
}
