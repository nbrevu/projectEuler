package com.euler;

import java.math.RoundingMode;
import java.util.Arrays;
import java.util.stream.LongStream;

import com.euler.common.Primes;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongLongMap;
import com.koloboke.collect.map.hash.HashLongLongMaps;

public class Euler715_7 {
	private final static int LOG10_N=12;
	
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
				primePos=(prime-1)/2;
				primeSquaredPos=(primeSquared-1)/2;
			}
		}
		private final long[] goals;
		private final long[] holder;
		private final long[] firstCopy;
		private final LongLongMap neededSums;
		private final PrimeInfo[] primesData;
		private final int repetitions;
		private int goalIndex;
		private long currentPos;
		private long currentSum;
		public ModifiedMöbiusSumCalculator(int blockSize,int blocks,long[] goals,int prefixToCopy)	{
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
			firstCopy=new long[prefixToCopy];
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
					if (p.primeSquaredPos+p.primeSquared<0)	{
						throw new IllegalStateException();
					}
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
		private void fillFirstCopy()	{
			for (int i=0;i<firstCopy.length;i+=2)	{
				firstCopy[i]=evaluateMöbius(2*i+1,holder[i]);
				firstCopy[i+1]=-evaluateMöbius(2*i+3,holder[i+1]);
			}
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
			fillBlock();
			fillFirstCopy();
			updateSums();
			for (int i=1;i<repetitions;++i)	{
				fillBlock();
				updateSums();
			}
		}
		public long getCumulativeSum(long x)	{
			return neededSums.computeIfAbsent(x,(long unused)->{throw new IllegalArgumentException("Invalid number.");});
		}
		public long getDirectValue(int x)	{
			if ((x%2)==0) return 0l;
			else return firstCopy[(x-1)/2];
		}
	}
	
	private static long sumCubesUpTo(long limit,long mod)	{
		limit%=mod;
		long sum=(limit*(limit+1))/2;
		sum%=mod;
		return (sum*sum)%mod;
	}
	
	// "Elapsed 8642.831830000001 seconds". But the answer is right.
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int halfUp=IntMath.divide(LOG10_N,2,RoundingMode.UP);
		int blockSizeLog=IntMath.divide(3*LOG10_N,4,RoundingMode.UP);
		int blockSize=IntMath.pow(10,blockSizeLog);
		int repetitions=IntMath.pow(10,LOG10_N-blockSizeLog)/2;
		long N=LongMath.pow(10l,LOG10_N);
		long sqrtNUpper=LongMath.pow(10l,halfUp);
		long sqrtNLower=N/sqrtNUpper;
		LongStream.Builder longs=LongStream.builder();
		longs.accept(sqrtNUpper);
		for (long i=1;i<=sqrtNLower;++i) longs.accept(N/i);
		long[] needed=longs.build().sorted().distinct().toArray();
		ModifiedMöbiusSumCalculator calculator=new ModifiedMöbiusSumCalculator(blockSize,repetitions,needed,(int)(sqrtNUpper/2));
		calculator.calculate();
		long result=0;
		// First part.
		for (long d=1;d<=sqrtNUpper;++d)	{
			long addend=sumCubesUpTo(N/d,MOD);
			result+=addend*calculator.getDirectValue((int)d);
			result%=MOD;
		}
		// Second part.
		long bottom=calculator.getCumulativeSum(sqrtNUpper);
		for (long i=1;i<=sqrtNLower;++i)	{
			long sq=(i*i)%MOD;
			long cb=(sq*i)%MOD;
			long top=calculator.getCumulativeSum(N/i);
			result+=cb*(top-bottom);
			result%=MOD;
		}
		if (result<0) result=result+MOD;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
