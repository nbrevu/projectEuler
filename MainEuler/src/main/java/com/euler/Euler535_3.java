package com.euler;

import java.math.BigInteger;
import java.math.RoundingMode;

import com.google.common.math.IntMath;
import com.google.common.math.LongMath;

public class Euler535_3 {
	private final static long LIMIT=LongMath.pow(10l,18);
	private final static long MOD=LongMath.pow(10l,9);
	
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
	
	private static BigInteger sumConsecutive(long start,long end)	{
		BigInteger term1=BigInteger.valueOf(end+start);
		BigInteger term2=BigInteger.valueOf(end+1-start);
		return term1.multiply(term2).shiftRight(1);
	}
	
	private static class SquareCache	{
		private final long[] squares;
		public SquareCache(int size)	{
			squares=new long[size];
			for (long i=0;i<size;++i) squares[(int)i]=i*i;
		}
		public BigInteger sumSquareRoots(long minIncluded,long maxIncluded)	{
			int sqrtMin=sqrt(minIncluded);
			int sqrtMax=sqrt(maxIncluded);
			if (sqrtMin==sqrtMax)	{
				BigInteger term1=BigInteger.valueOf(1+maxIncluded-minIncluded);
				return term1.multiply(BigInteger.valueOf(sqrtMin));
			}
			BigInteger leftExcess=BigInteger.valueOf(squares[1+sqrtMin]-minIncluded);
			BigInteger rightExcess=BigInteger.valueOf(1+maxIncluded-squares[sqrtMax]);
			BigInteger result=leftExcess.multiply(BigInteger.valueOf(sqrtMin)).add(rightExcess.multiply(BigInteger.valueOf(sqrtMax)));
			for (int i=1+sqrtMin;i<sqrtMax;++i)	{
				BigInteger howMany=BigInteger.valueOf(2*i+1);
				result=result.add(howMany.multiply(BigInteger.valueOf(i)));
			}
			return result;
		}
	}
	
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
			long additionalSecondLevelTerms=cache.sumSquareRoots(minFirstLevelTerm,maxFirstLevelTerm).longValueExact()+seqElementSqrt;
			long currentTerms=1+additionalFirstLevelTerms+additionalSecondLevelTerms;
			if (totalTermsAdded+currentTerms<=LIMIT)	{
				long minSecondLevelTerm=1+secondLevelIndex;
				long maxSecondLevelTerm=additionalSecondLevelTerms+secondLevelIndex;
				BigInteger toAdd=BigInteger.valueOf(seqElement).add(sumConsecutive(minFirstLevelTerm,maxFirstLevelTerm)).add(sumConsecutive(minSecondLevelTerm,maxSecondLevelTerm));
				result=result.add(toAdd);
				firstLevelIndex=maxFirstLevelTerm;
				secondLevelIndex=maxSecondLevelTerm;
				totalTermsAdded+=currentTerms;
				if (totalTermsAdded==LIMIT) break;
			}	else	{
				long remainingTerms=LIMIT-totalTermsAdded;
				BigInteger toAdd=BigInteger.ZERO;
				for (long firstLevelTerm=minFirstLevelTerm;(remainingTerms>0)&&(firstLevelTerm<=maxFirstLevelTerm);++firstLevelTerm)	{
					long secondLevelPiece=Math.min(sqrt(firstLevelTerm),remainingTerms);
					long minSecondLevelTerm=1+secondLevelIndex;
					long maxSecondLevelTerm=secondLevelPiece+secondLevelIndex;
					toAdd=toAdd.add(sumConsecutive(minSecondLevelTerm,maxSecondLevelTerm));
					remainingTerms-=secondLevelPiece;
					if (remainingTerms>0)	{
						toAdd=toAdd.add(BigInteger.valueOf(firstLevelTerm));
						--remainingTerms;
					}
					secondLevelIndex=maxSecondLevelTerm;
				}
				if (remainingTerms>0)	{
					if (remainingTerms>seqElementSqrt) throw new RuntimeException("Pero qué haces.");
					long minSecondLevelTerm=1+secondLevelIndex;
					long maxSecondLevelTerm=remainingTerms+secondLevelIndex;
					toAdd=toAdd.add(sumConsecutive(minSecondLevelTerm,maxSecondLevelTerm));
				}
				result=result.add(toAdd);
				break;
			}
		}
		result=result.mod(BigInteger.valueOf(MOD));
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
