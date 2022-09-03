package com.euler;

import java.math.BigInteger;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.IntObjCursor;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;

public class Euler676_3 {
	private final static long N=LongMath.pow(10l,16);
	
	private static class CountAndSum	{
		public final static CountAndSum ZERO=new CountAndSum(BigInteger.ZERO,BigInteger.ZERO);
		private final BigInteger count;
		private final BigInteger sum;
		public CountAndSum(BigInteger count,BigInteger sum)	{
			this.count=count;
			this.sum=sum;
		}
		public CountAndSum add(CountAndSum other)	{
			return new CountAndSum(count.add(other.count),sum.add(other.sum));
		}
		public BigInteger getShiftedSum(long value)	{
			return sum.add(count.multiply(BigInteger.valueOf(value)));
		}
	}
	
	private static class CountAndSumDistribution	{
		private final IntObjMap<CountAndSum> counters;
		private final static CountAndSumDistribution EMPTY=new CountAndSumDistribution(HashIntObjMaps.newMutableMap());
		private CountAndSumDistribution(IntObjMap<CountAndSum> counters)	{
			this.counters=counters;
		}
		public CountAndSumDistribution newObjectWith(long additionalValue,int digitDiff)	{
			IntObjMap<CountAndSum> newCounters=HashIntObjMaps.newMutableMap(counters);
			newCounters.compute(digitDiff,(int unused,CountAndSum existing)->	{
				BigInteger count;
				BigInteger sum;
				if (existing==null)	{
					count=BigInteger.ONE;
					sum=BigInteger.valueOf(additionalValue);
				}	else	{
					count=existing.count.add(BigInteger.ONE);
					sum=existing.sum.add(BigInteger.valueOf(additionalValue));
				}
				return new CountAndSum(count,sum);
			});
			return new CountAndSumDistribution(newCounters);
		}
		public static CountAndSumDistribution combine(CountAndSumDistribution smaller,CountAndSumDistribution bigger,BigInteger factor)	{
			IntObjMap<CountAndSum> result=HashIntObjMaps.newMutableMap();
			for (IntObjCursor<CountAndSum> sCursor=smaller.counters.cursor();sCursor.moveNext();)	{
				int d1=sCursor.key();
				CountAndSum c1=sCursor.value();
				for (IntObjCursor<CountAndSum> bCursor=bigger.counters.cursor();bCursor.moveNext();)	{
					int d2=bCursor.key();
					CountAndSum c2=bCursor.value();
					BigInteger count=c1.count.multiply(c2.count);
					BigInteger sum=c1.sum.multiply(c2.count).add(c2.sum.multiply(factor).multiply(c1.count));
					result.merge(d1+d2,new CountAndSum(count,sum),CountAndSum::add);
				}
			}
			return new CountAndSumDistribution(result);
		}
		public CountAndSum getCounters(int diff)	{
			return counters.getOrDefault(diff,CountAndSum.ZERO);
		}
	}
	
	private static class SumDigitCache	{
		private static int[] calculateArray(int bigP,int smallP)	{
			int bigBase=1<<bigP;
			int smallBase=1<<smallP;
			int q=bigBase/smallBase;
			int[] result=new int[bigBase];
			result[0]=0;
			for (int i=0;i<q;++i)	{
				int firstTerm=result[i];
				int firstIndex=smallBase*i;
				for (int j=0;j<smallBase;++j) result[firstIndex+j]=firstTerm+j;
			}
			return result;
		}
		/*
		 * The first index is the "big P" and the second one is the "small P".
		 * For example, when we are calculating for k=4 and l=1, we will need big P=4 and small P=4 and 1. In both cases the array will have
		 * 16 elements.
		 */
		private final IntObjMap<IntObjMap<int[]>> counters;
		public SumDigitCache()	{
			counters=HashIntObjMaps.newMutableMap();
		}
		public int[] getFor(int bigP,int smallP)	{
			return counters.computeIfAbsent(bigP,(int unused)->HashIntObjMaps.newMutableMap()).computeIfAbsent(smallP,(int unused)->calculateArray(bigP,smallP));
		}
	}
	
	private static class BaseCombinationData	{
		private final static SumDigitCache SUM_DIGITS_CACHE=new SumDigitCache();
		public final int bigBase;
		private final int[] diffs;
		private final CountAndSumDistribution[] distributions;
		private BaseCombinationData(int bigBase,int[] diffs,CountAndSumDistribution[] distributions)	{
			this.bigBase=bigBase;
			this.diffs=diffs;
			this.distributions=distributions;
		}
		public static BaseCombinationData getForPow2Bases(int p1,int p2)	{
			int bigP=p1*p2/EulerUtils.gcd(p1,p2);
			int bigBase=1<<bigP;
			int[] diffs=new int[bigBase];
			CountAndSumDistribution[] distributions=new CountAndSumDistribution[bigBase];
			distributions[0]=CountAndSumDistribution.EMPTY.newObjectWith(0,0);
			int[] sums1=SUM_DIGITS_CACHE.getFor(bigP,p1);
			int[] sums2=SUM_DIGITS_CACHE.getFor(bigP,p2);
			for (int i=1;i<bigBase;++i)	{
				diffs[i]=sums1[i]-sums2[i];
				distributions[i]=distributions[i-1].newObjectWith(i,diffs[i]);
			}
			return new BaseCombinationData(bigBase,diffs,distributions);
		}
		public int getDiff(int digit)	{
			return diffs[digit];
		}
		public CountAndSumDistribution getDistributionUpTo(int digit)	{
			return distributions[digit];
		}
		public CountAndSumDistribution getFullDistribution()	{
			return distributions[distributions.length-1];
		}
	}
	
	private static int[] getDigits(long n,int base)	{
		int numDigits=(int)Math.ceil(Math.log(n+1)/Math.log(base));
		int[] result=new int[numDigits];
		for (int i=0;i<result.length;++i)	{
			result[i]=(int)(n%base);
			n/=base;
		}
		return result;
	}
	
	private static BigInteger getM(long n,int k,int l)	{
		BaseCombinationData data=BaseCombinationData.getForPow2Bases(k,l);
		int[] digits=getDigits(n,data.bigBase);
		BigInteger shift=BigInteger.valueOf(data.bigBase);
		CountAndSumDistribution[] completeDistributions=new CountAndSumDistribution[digits.length-1];
		long[] shifts=new long[digits.length-1];
		shifts[0]=data.bigBase;
		for (int i=1;i<shifts.length;++i) shifts[i]=shifts[i-1]*data.bigBase;
		completeDistributions[0]=data.getFullDistribution();
		for (int i=1;i<completeDistributions.length;++i) completeDistributions[i]=CountAndSumDistribution.combine(completeDistributions[0],completeDistributions[i-1],shift);
		int diff=0;
		long prefix=0;
		BigInteger result;
		// First step: no prefix considered. The digit is assumed to be >0.
		{
			int thisDigit=digits[digits.length-1];
			CountAndSumDistribution lowerDistr=completeDistributions[digits.length-2];
			CountAndSumDistribution upperDistr=data.getDistributionUpTo(thisDigit-1);
			CountAndSumDistribution totalDistr=CountAndSumDistribution.combine(lowerDistr,upperDistr,BigInteger.valueOf(shifts[digits.length-2]));
			result=totalDistr.getCounters(0).sum;
			diff=data.getDiff(thisDigit);
			prefix=digits[digits.length-1];
		}
		// Other steps (save the first).
		for (int i=digits.length-2;i>=1;--i)	{
			int thisDigit=digits[i];
			if (thisDigit!=0)	{
				CountAndSumDistribution lowerDistr=completeDistributions[i-1];
				CountAndSumDistribution upperDistr=data.getDistributionUpTo(thisDigit-1);
				CountAndSumDistribution totalDistr=CountAndSumDistribution.combine(lowerDistr,upperDistr,BigInteger.valueOf(shifts[i-1]));
				result=result.add(totalDistr.getCounters(-diff).getShiftedSum(prefix*shifts[i]));
				diff+=data.getDiff(thisDigit);
			}
			prefix=data.bigBase*prefix+thisDigit;
		}
		// Last step.
		{
			int thisDigit=digits[0];
			CountAndSumDistribution distr=data.getDistributionUpTo(thisDigit);
			result=result.add(distr.getCounters(-diff).getShiftedSum(prefix*shifts[0]));
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		BigInteger result=BigInteger.ZERO;
		for (int k=3;k<=6;++k) for (int l=1;l<=k-2;++l) result=result.add(getM(N,k,l));
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println(result.mod(BigInteger.valueOf(N)));
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
