package com.euler;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.LongStream;

import com.euler.common.AlternatePrimeCounter;
import com.euler.common.AlternatePrimeCounter.AlternatePrimeCounts;
import com.euler.common.BaseSquareDecomposition;
import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils.LongPair;
import com.euler.common.Primes;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler611_5 {
	private final static long LIMIT=LongMath.pow(10l,12);
	private final static int SMALL_LIMIT=IntMath.pow(10,8);
	
	private final static long[] SMALL_PRIMES=new long[] {5,13,17,29,37,41,53,61};

	private static class SquarePatternExaminator	{
		private final long[] primes;
		private final long[] limits;
		private final List<int[]> validPatterns;
		private final SumSquareDecomposer decomposer;
		public SquarePatternExaminator(long limit,long... primes)	{
			this.primes=primes;
			this.limits=new long[primes.length];
			for (int i=0;i<limits.length;++i) limits[i]=limit/primes[i];
			validPatterns=new ArrayList<>();
			decomposer=new SumSquareDecomposer();
		}
		public void examine()	{
			int[] exps=new int[primes.length];
			exps[0]=1;
			long number=primes[0];
			DivisorHolder divs=new DivisorHolder();
			divs.addFactor(primes[0],1);
			examineRecursive(exps,number,divs,0);
		}
		private void examineRecursive(int[] exps,long number,DivisorHolder divs,int lastIndex)	{
			BaseSquareDecomposition decomps=decomposer.getFor(divs);
			int valid=0;
			for (LongPair pair:decomps.getBaseCombinations()) if ((pair.x>0)&&(pair.x!=pair.y)) ++valid;
			if ((valid&1)==1) validPatterns.add(Arrays.copyOf(exps,1+lastIndex));
			int nextIndex=1+lastIndex;
			if ((nextIndex<exps.length)&&(number<limits[nextIndex]))	{
				long p=primes[nextIndex];
				++exps[nextIndex];
				divs.addFactor(p,1);
				examineRecursive(exps,number*p,divs,nextIndex);
				divs.removeFactor(p,1);
				--exps[nextIndex];
			}
			if ((number<limits[lastIndex])&&((lastIndex==0)||(exps[lastIndex]<exps[lastIndex-1])))	{
				long p=primes[lastIndex];
				++exps[lastIndex];
				divs.addFactor(p,1);
				examineRecursive(exps,number*p,divs,lastIndex);
				divs.removeFactor(p,1);
				--exps[lastIndex];
			}
		}
		public List<int[]> getPatterns()	{
			return validPatterns;
		}
	}
	
	private static class RootAndPowerCalculator	{
		private final long[][] powers;
		public RootAndPowerCalculator(long maxValue,int maxPower)	{
			powers=new long[1+maxPower][];
			{
				int thisMax=(int)Math.ceil(Math.cbrt(maxValue));
				long[] thisPowers=new long[1+thisMax];
				thisPowers[0]=0l;
				thisPowers[1]=1l;
				for (long x=2;x<=thisMax;++x) thisPowers[(int)x]=x*x*x;
				powers[3]=thisPowers;
			}
			for (int i=4;i<=maxPower;++i)	{
				int thisMax=(int)Math.ceil(Math.pow(maxValue,1d/i));
				long[] prevPowers=powers[i-1];
				long[] thisPowers=new long[1+thisMax];
				thisPowers[0]=0l;
				thisPowers[1]=1l;
				for (int x=2;x<=thisMax;++x) thisPowers[x]=prevPowers[x]*x;
				powers[i]=thisPowers;
			}
		}
		public long getRoot(long x,int index)	{
			switch (index)	{
				case 1:return x;
				case 2:return LongMath.sqrt(x,RoundingMode.DOWN);
				default:	{
					int pos=Arrays.binarySearch(powers[index],x);
					if (pos>=0) return pos;
					else return -2-pos;
				}
			}
		}
		public long getPower(long x,int power)	{
			if (power==1) return x;
			else if (power==2) return x*x;
			else return powers[power][(int)x];
		}
	}
	
	private static class PrimeDecompositionCounter	{
		private static long[] combine(LongSet factors,long limit)	{
			LongSet resultSet=HashLongSets.newMutableSet();
			resultSet.add(1l);
			for (LongCursor factorsCursor=factors.cursor();factorsCursor.moveNext();)	{
				LongSet toAdd=HashLongSets.newMutableSet();
				long factor=factorsCursor.elem();
				long max=limit/factor;
				for (LongCursor oldElemsCursor=resultSet.cursor();oldElemsCursor.moveNext();)	{
					long val=oldElemsCursor.elem();
					while (val<=max)	{
						val*=factor;
						toAdd.add(val);
					}
				}
				resultSet.addAll(toAdd);
			}
			long[] result=resultSet.toLongArray();
			Arrays.sort(result);	// Necessary if we want to be able to do a break instead of a continue in a loop down below.
			return result;
		}
		private static long[] getExtraFactors(long limit,boolean[] composites)	{
			LongSet singleFactors=HashLongSets.newMutableSet();
			singleFactors.add(2l);
			for (int i=3;i<composites.length;i+=4) if (!composites[i])	{
				long p2=i*(long)i;
				if (p2>limit) break;
				singleFactors.add(p2);
			}
			return combine(singleFactors,limit);
		}
		private final long smallLimit;
		private final long[] primes;
		private final long[] extraFactors;
		private final RootAndPowerCalculator calculator;
		private AlternatePrimeCounter lucyHedgehogCounter;
		public PrimeDecompositionCounter(long limit,int smallLimit)	{
			this.smallLimit=smallLimit;
			boolean[] composites=Primes.sieve(smallLimit);
			LongStream.Builder builder=LongStream.builder();
			for (int i=5;i<smallLimit;i+=4) if (!composites[i]) builder.accept(i);
			primes=builder.build().toArray();
			extraFactors=getExtraFactors(limit,composites);
			int maxPower=(int)Math.floor(Math.log(limit)/Math.log(5d));
			calculator=new RootAndPowerCalculator(limit,maxPower);
		}
		private long countPrimesUpTo(long max)	{
			if (max<=smallLimit)	{
				int index=Arrays.binarySearch(primes,max);
				if (index>=0) return index+1;
				else return -1-index;
			}	else	{
				if (lucyHedgehogCounter==null) lucyHedgehogCounter=new AlternatePrimeCounter(max);
				AlternatePrimeCounts counts=lucyHedgehogCounter.sumF(max);
				if (counts==null)	{
					lucyHedgehogCounter=new AlternatePrimeCounter(max);
					counts=lucyHedgehogCounter.sumF(max);
				}
				return counts.getSum4k1();
			}
		}
		public long getCounts(int[] exps,long limit)	{
			LongSet usedPrimes=HashLongSets.newMutableSet();
			long result=0l;
			int expSum=0;
			for (int e:exps) expSum+=e;
			long minValue=calculator.getPower(5l,expSum);
			for (long f:extraFactors)	{
				long smallLimit=limit/f;
				if (smallLimit<minValue) break;
				result+=getCountsRecursive(exps,0,0,smallLimit,usedPrimes);
			}
			return result;
		}
		private long estimateMaxPrime(int[] exps,int index,long max)	{
			int thisExp=exps[index];
			int expTimes=1;
			int otherExps=0;
			for (int i=1+index;i<exps.length;++i) if (exps[i]==thisExp) ++expTimes;
			else otherExps+=exps[i];
			long factor=(otherExps==0)?1l:calculator.getPower(5,otherExps);
			long realMax=max/factor;
			return calculator.getRoot(realMax,thisExp*expTimes);
		}
		private long getCountsRecursive(int[] exps,int expIndex,int primeIndex,long max,LongSet usedPrimes)	{
			int thisExp=exps[expIndex];
			if (expIndex==exps.length-1)	{
				long root=calculator.getRoot(max,thisExp);
				long result=countPrimesUpTo(root)-primeIndex;
				long firstValidPrime=primes[primeIndex];
				for (LongCursor cursor=usedPrimes.cursor();cursor.moveNext();)	{
					long usedPrime=cursor.elem();
					if ((usedPrime>=firstValidPrime)&&(usedPrime<=root)) --result;
				}
				return result;
			}
			long maxPrime=estimateMaxPrime(exps,expIndex,max);
			boolean nextExpIsSame=(exps[expIndex+1]==thisExp);
			long result=0;
			for (int i=primeIndex;i<primes.length;++i)	{
				long p=primes[i];
				if (p>maxPrime) break;
				else if (usedPrimes.contains(p)) continue;
				long pow=calculator.getPower(p,thisExp);
				long nextMax=max/pow;
				int nextPrimeIndex=(nextExpIsSame?(i+1):0);
				usedPrimes.add(p);
				result+=getCountsRecursive(exps,1+expIndex,nextPrimeIndex,nextMax,usedPrimes);
				usedPrimes.removeLong(p);
			}
			return result;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=0l;
		SquarePatternExaminator examinator=new SquarePatternExaminator(LIMIT,SMALL_PRIMES);
		examinator.examine();
		PrimeDecompositionCounter counter=new PrimeDecompositionCounter(LIMIT,SMALL_LIMIT);
		for (int[] exps:examinator.getPatterns()) result+=counter.getCounts(exps,LongMath.pow(10l,12));
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
