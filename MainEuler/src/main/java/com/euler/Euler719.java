package com.euler;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import com.google.common.math.LongMath;
import com.koloboke.collect.map.IntLongMap;
import com.koloboke.collect.map.hash.HashIntLongMaps;

public class Euler719 {
	private final static long LIMIT=LongMath.pow(10l,12);
	private final static long SQ_LIMIT=LongMath.sqrt(LIMIT,RoundingMode.DOWN);
	
	private static class NumberSplitter	{
		private final long[] powers;
		public NumberSplitter(long[] powers)	{
			this.powers=powers;
		}
		public long splitAndSum(long n)	{
			long result=0;
			for (long p:powers)	{
				result+=n%p;
				n/=p;
			}
			result+=n;
			return result;
		}
	}
	
	private static class NumberSplitterFactory	{
		private static IntLongMap POWERS_OF_TEN=HashIntLongMaps.newMutableMap();
		static	{
			POWERS_OF_TEN.put(1,10l);
		}
		private static long getPowerOfTen(int i)	{
			return POWERS_OF_TEN.computeIfAbsent(i,(int p)->10*getPowerOfTen(p-1));
		}
		private final int amountOfDigits;
		private final int minBinSize;
		private final int maxBinSize;
		public NumberSplitterFactory(int amountOfDigits,int minBinSize,int maxBinSize)	{
			this.amountOfDigits=amountOfDigits;
			this.minBinSize=minBinSize;
			this.maxBinSize=maxBinSize;
		}
		public List<NumberSplitter> generateValidSplitters()	{
			/*
			 * We take all the possible bit combinations, from 000...000 to 111...111, of size (amountOfDigits-1). Set bits indicate that a new
			 * bin starts at the given position, therefore 000...000 indicates a single bin and 111..111 indicates all bins separated, with only
			 * one digit at each bin.
			 */
			int limit=(1<<(amountOfDigits-1));
			List<NumberSplitter> result=new ArrayList<>();
			for (int i=0;i<limit;++i)	{
				BitSet bits=BitSet.valueOf(new long[] {i});
				int[] diffs=diff(getRawPositions(bits));
				if (checkSizes(diffs))	{
					int N=diffs.length-1;
					long[] powers=new long[N];
					for (int j=0;j<N;++j) powers[j]=getPowerOfTen(diffs[j]);
					result.add(new NumberSplitter(powers));
				}
			}
			System.out.println("amountOfDigits="+amountOfDigits+", minBinSize="+minBinSize+", maxBinSize="+maxBinSize+": "+result.size()+" splitters.");
			return result;
		}
		private int[] getRawPositions(BitSet newBinPositions)	{
			int[] result=new int[2+newBinPositions.cardinality()];
			result[0]=0;
			int index=1;
			for (int i=newBinPositions.nextSetBit(0);i>=0;i=newBinPositions.nextSetBit(i+1))	{
				result[index]=1+i;
				++index;
			}
			assert index==result.length-1;
			result[index]=amountOfDigits;
			return result;
		}
		private int[] diff(int[] rawPositions)	{
			int N=rawPositions.length-1;
			int[] result=new int[N];
			for (int i=0;i<N;++i) result[i]=rawPositions[i+1]-rawPositions[i];
			return result;
		}
		private boolean checkSizes(int[] diffs)	{
			int maxSize=0;
			for (int d:diffs) maxSize=Math.max(maxSize,d);
			return (maxSize>=minBinSize)&&(maxSize<=maxBinSize);
		}
	}
	
	private static class FullNumberSplitter	{
		private int currentMaxNumberSize;
		private int currentMaxSumSize;
		private long currentNumberLimit;
		private long currentSumLimit;
		private List<NumberSplitter> currentNumberSplitters;
		public FullNumberSplitter()	{
			currentMaxNumberSize=2;
			currentMaxSumSize=1;
			currentNumberLimit=100;
			currentSumLimit=10;
			currentNumberSplitters=new NumberSplitterFactory(2,1,1).generateValidSplitters();
		}
		public boolean isSplittable(long number,long sum)	{
			// First of all we check whether we need to renew the splitter objects...
			boolean anyChanged=false;
			if (number>=currentNumberLimit)	{
				++currentMaxNumberSize;
				currentNumberLimit*=10;
				anyChanged=true;
			}
			if (sum>=currentSumLimit)	{
				++currentMaxSumSize;
				currentSumLimit*=10;
				anyChanged=true;
			}
			if (anyChanged) currentNumberSplitters=new NumberSplitterFactory(currentMaxNumberSize,currentMaxSumSize-1,currentMaxSumSize).generateValidSplitters();
			for (NumberSplitter splitter:currentNumberSplitters) if (splitter.splitAndSum(number)==sum) return true;
			return false;
		}
	}
	
	/*-
	 * Salida para 10^16:
	amountOfDigits=2, minBinSize=1, maxBinSize=1: 1 splitters.
	amountOfDigits=3, minBinSize=1, maxBinSize=2: 3 splitters.
	amountOfDigits=4, minBinSize=1, maxBinSize=2: 5 splitters.
	amountOfDigits=5, minBinSize=2, maxBinSize=3: 12 splitters.
	amountOfDigits=6, minBinSize=2, maxBinSize=3: 23 splitters.
	amountOfDigits=7, minBinSize=3, maxBinSize=4: 35 splitters.
	amountOfDigits=8, minBinSize=3, maxBinSize=4: 74 splitters.
	amountOfDigits=9, minBinSize=4, maxBinSize=5: 87 splitters.
	amountOfDigits=10, minBinSize=4, maxBinSize=5: 190 splitters.
	amountOfDigits=11, minBinSize=5, maxBinSize=6: 203 splitters.
	amountOfDigits=12, minBinSize=5, maxBinSize=6: 446 splitters.
	amountOfDigits=13, minBinSize=6, maxBinSize=7: 459 splitters.
	amountOfDigits=14, minBinSize=6, maxBinSize=7: 1006 splitters.
	amountOfDigits=15, minBinSize=7, maxBinSize=8: 1019 splitters.
	amountOfDigits=16, minBinSize=7, maxBinSize=8: 2222 splitters.
	amountOfDigits=17, minBinSize=8, maxBinSize=9: 2235 splitters.
	1666
	4220167395329224477
	Elapsed 1966.4236994480002 seconds.
	 */
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=0;
		boolean leap8=false;
		FullNumberSplitter splitter=new FullNumberSplitter();
		int howMany=0;
		for (long i=9;i<=SQ_LIMIT;i+=(leap8?8:1),leap8=!leap8)	{
			long square=i*i;
			if (splitter.isSplittable(square,i))	{
				++howMany;
				result+=square;
			}
		}
		System.out.println(howMany);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
