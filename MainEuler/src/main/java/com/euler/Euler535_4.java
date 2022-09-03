package com.euler;

import java.math.RoundingMode;

import com.google.common.math.IntMath;
import com.google.common.math.LongMath;

public class Euler535_4 {
	private final static long LIMIT=LongMath.pow(10l,18);
	private final static long MOD=LongMath.pow(10l,9);
	
	private final static int INITIAL_LIMIT=2*IntMath.pow(10,8);
	
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
	
	private static class TwoLevelIndexCalculator	{
		private final int[] baseSeq;
		private final SquareCache cache;
		public TwoLevelIndexCalculator(long limit,int initialLimit)	{
			baseSeq=generateFractalSequence(initialLimit);
			cache=new SquareCache(sqrt(limit));
		}
		public long getFinalResult(long baseSum,long lastFirstLevel,long lastSecondLevel,long mod)	{
			lastFirstLevel%=2*mod;
			lastSecondLevel%=2*mod;
			long result=baseSum+((lastFirstLevel*(lastFirstLevel+1))+(lastSecondLevel*(lastSecondLevel+1)))/2;
			return result%mod;
		}
		public long solve(long mod)	{
			long firstLevelIndex=0;
			long secondLevelIndex=0;
			long totalTermsAdded=0;
			long baseSum=0;
			for (int i=0;;++i)	{
				int seqElement=baseSeq[i];
				int seqElementSqrt=sqrt(seqElement);
				long additionalFirstLevelTerms=seqElementSqrt;
				long minFirstLevelTerm=1+firstLevelIndex;
				long maxFirstLevelTerm=additionalFirstLevelTerms+firstLevelIndex;
				long additionalSecondLevelTerms=cache.sumSquareRoots(minFirstLevelTerm,maxFirstLevelTerm)+seqElementSqrt;
				long currentTerms=1+additionalFirstLevelTerms+additionalSecondLevelTerms;
				if (totalTermsAdded+currentTerms<=LIMIT)	{
					firstLevelIndex=maxFirstLevelTerm;
					secondLevelIndex+=additionalSecondLevelTerms;
					totalTermsAdded+=currentTerms;
					baseSum+=seqElement;
					if (totalTermsAdded==LIMIT) return getFinalResult(baseSum,firstLevelIndex,secondLevelIndex,mod);
				}	else	{
					long remainingTerms=LIMIT-totalTermsAdded;
					for (long firstLevelTerm=minFirstLevelTerm;(remainingTerms>0)&&(firstLevelTerm<=maxFirstLevelTerm);++firstLevelTerm)	{
						long secondLevelPiece=Math.min(sqrt(firstLevelTerm),remainingTerms);
						long maxSecondLevelTerm=secondLevelPiece+secondLevelIndex;
						remainingTerms-=secondLevelPiece;
						if (remainingTerms>0)	{
							firstLevelIndex=firstLevelTerm;
							--remainingTerms;
						}	else firstLevelIndex=firstLevelTerm-1;
						secondLevelIndex=maxSecondLevelTerm;
					}
					if (remainingTerms>0)	{
						if (remainingTerms>seqElementSqrt) throw new RuntimeException("Pero qué haces.");
						secondLevelIndex+=remainingTerms;
					}
					return getFinalResult(baseSum,firstLevelIndex,secondLevelIndex,mod);
				}
			}
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		TwoLevelIndexCalculator calculator=new TwoLevelIndexCalculator(LIMIT,INITIAL_LIMIT);
		long result=calculator.solve(MOD);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
