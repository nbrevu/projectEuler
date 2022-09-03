package com.euler;

import java.math.BigInteger;
import java.math.RoundingMode;

import com.google.common.math.IntMath;
import com.google.common.math.LongMath;

public class Euler535_2 {
	private final static long LIMIT=LongMath.pow(10l,18);
	
	private final static int INITIAL_LIMIT=sqrt(3*LIMIT);
	
	private static int[] generateFractalSequence(int size)	{
		int[] result=new int[size];
		if (size<=0) return result;
		result[0]=1;
		result[1]=1;
		int currentIndex=2;
		int nextCircledDigit=2;
		int currentReference=1;
		boolean canContinue=true;
		while (canContinue)	{
			int nextDigit=result[currentReference];
			++currentReference;
			int extraNumbers=IntMath.sqrt(nextDigit,RoundingMode.DOWN);
			for (int i=0;i<extraNumbers;++i)	{
				result[currentIndex]=nextCircledDigit;
				++nextCircledDigit;
				++currentIndex;
				if (currentIndex>=size)	{
					canContinue=false;
					break;
				}
			}
			if (!canContinue) break;
			result[currentIndex]=nextDigit;
			++currentIndex;
			canContinue=(currentIndex<size);
		}
		return result;
	}
	
	private static int sqrt(long sq)	{
		// Calling LongMath.sqrt is faster than a binary search on the squares cache.
		return (int)LongMath.sqrt(sq,RoundingMode.DOWN);
	}
	
	private static long sumConsecutive(long start,long end)	{
		return ((end+start)*(end+1-start))/2;
	}
	
	private static class SquareCache	{
		private final long[] squares;
		public SquareCache(int size)	{
			squares=new long[size];
			for (long i=0;i<size;++i) squares[(int)i]=i*i;
		}
		public long sumSquareRoots(long minIncluded,long maxIncluded)	{
			int sqrtMin=sqrt(minIncluded);
			int sqrtMax=sqrt(maxIncluded);
			if (sqrtMin==sqrtMax) return (1+maxIncluded-minIncluded)*sqrtMin;
			long leftExcess=squares[1+sqrtMin]-minIncluded;
			long rightExcess=1+maxIncluded-squares[sqrtMax];
			long result=leftExcess*sqrtMin+rightExcess*sqrtMax;
			for (int i=1+sqrtMin;i<sqrtMax;++i) result+=(2*i+1)*(long)i;
			return result;
		}
	}
	
	// Wrong result, but close! At least the order of magnitude is right. ZUTUN: debug a bit.
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int[] baseSeq=generateFractalSequence(INITIAL_LIMIT);
		SquareCache cache=new SquareCache((int)LongMath.sqrt(LIMIT,RoundingMode.UP));
		long firstLevelIndex=0;
		long secondLevelIndex=0;
		long totalTermsAdded=0;
		BigInteger result=BigInteger.ZERO;
		for (int i=0;;++i)	{
			int seqElement=baseSeq[i];
			int seqElementSqrt=sqrt(seqElement);
			long additionalFirstLevelTerms=seqElementSqrt;
			long minFirstLevelTerm=1+firstLevelIndex;
			long maxFirstLevelTerm=additionalFirstLevelTerms+firstLevelIndex;
			long additionalSecondLevelTerms=cache.sumSquareRoots(minFirstLevelTerm,maxFirstLevelTerm)+seqElementSqrt;
			long currentTerms=1+additionalFirstLevelTerms+additionalSecondLevelTerms;
			if (totalTermsAdded+currentTerms<=LIMIT)	{
				long minSecondLevelTerm=1+secondLevelIndex;
				long maxSecondLevelTerm=additionalSecondLevelTerms+secondLevelIndex;
				long toAdd=seqElement+sumConsecutive(minFirstLevelTerm,maxFirstLevelTerm)+sumConsecutive(minSecondLevelTerm,maxSecondLevelTerm);
				result=result.add(BigInteger.valueOf(toAdd));
				firstLevelIndex=maxFirstLevelTerm;
				secondLevelIndex=maxSecondLevelTerm;
				totalTermsAdded+=currentTerms;
				if (totalTermsAdded==LIMIT) break;
			}	else	{
				long remainingTerms=LIMIT-totalTermsAdded;
				long toAdd=0;
				for (long firstLevelTerm=minFirstLevelTerm;(remainingTerms>0)&&(firstLevelTerm<=maxFirstLevelTerm);++firstLevelTerm)	{
					long secondLevelPiece=Math.min(sqrt(firstLevelTerm),remainingTerms);
					long minSecondLevelTerm=1+secondLevelIndex;
					long maxSecondLevelTerm=secondLevelPiece+secondLevelIndex;
					toAdd+=sumConsecutive(minSecondLevelTerm,maxSecondLevelTerm);
					remainingTerms-=secondLevelPiece;
					if (remainingTerms>0)	{
						toAdd+=firstLevelTerm;
						--remainingTerms;
					}
					secondLevelIndex=maxSecondLevelTerm;
				}
				if (remainingTerms>0)	{
					if (remainingTerms>seqElementSqrt) throw new RuntimeException("Pero qué haces.");
					long minSecondLevelTerm=1+secondLevelIndex;
					long maxSecondLevelTerm=remainingTerms+secondLevelIndex;
					toAdd+=sumConsecutive(minSecondLevelTerm,maxSecondLevelTerm);
				}
				result=result.add(BigInteger.valueOf(toAdd));
				break;
			}
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
