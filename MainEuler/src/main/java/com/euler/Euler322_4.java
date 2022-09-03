package com.euler;

import com.google.common.math.LongMath;

public class Euler322_4 {
	private final static long UPPER=LongMath.pow(10l,18);
	private final static long LOWER=LongMath.pow(10l,12)-10;

	private final static int OTHER_BASE=5;	// I.e. the base that is not 2.
	
	/*
	 * YES! This is the beauty of Lucas' theorem. Given a number N, the combinatorial number C(N,L) is a multiple of 2 if and only if N&L!=L.
	 * Explanation:
	 * - A bit can be either 0 or 1. As a consequence of Lucas' theorem, C(N,L) is a multiple of 2 if and only if there exists a bit such
	 * that L's bit is 1 and N's is 0.
	 * - This means that positions where L's bit is 0 don't matter, since they don't affect the condition.
	 * - So, we need the set of position where L's bit is 1. Which is... exactly L!
	 * - If N&L == L, then N has 1 in every meaningful bit, and C(N,L) is NOT a multiple of 2.
	 * - And if N&L != L, then there exists at least one bit where N has a 0 and L has an 1, and C(N,L) IS a multiple of 2.
	 * ENDUT!! HOCH HECH!!
	 */
	private static class LucasEnumerator	{
		private final int[] lower;
		private final int[] upper;
		private final long[] powers;
		private final int base;
		private final long mask;
		public LucasEnumerator(int[] lower,int[] upper,int base,long mask)	{
			this.lower=lower;
			this.upper=upper;
			powers=new long[upper.length];
			powers[0]=1l;
			for (int i=1;i<powers.length;++i) powers[i]=base*powers[i-1];
			this.base=base;
			this.mask=mask;
		}
		public long countNonDivisibleBoth()	{
			return countNonDivisibleBothLimited(0l,powers.length-1);
		}
		private long countNonDivisibleBothLimited(long currentSum,int currentIndex)	{
			int highDigit=upper[currentIndex];
			int lowDigit=(currentIndex>=lower.length)?0:lower[currentIndex];
			if (highDigit<lowDigit) return 0l;
			long power=powers[currentIndex];
			currentSum+=power*lowDigit;
			long result=0l;
			int nextIndex=currentIndex-1;
			for (int i=lowDigit;i<highDigit;++i)	{
				result+=countNonDivisibleBothNonLimited(currentSum,nextIndex);
				currentSum+=power;
			}
			result+=countNonDivisibleBothLimited(currentSum,nextIndex);
			return result;
		}
		private long countNonDivisibleBothNonLimited(long currentSum,int currentIndex)	{
			int digit=(currentIndex>=lower.length)?0:lower[currentIndex];
			long power=powers[currentIndex];
			currentSum+=power*digit;
			long result=0l;
			int nextIndex=currentIndex-1;
			for (int i=digit;i<base;++i)	{
				if (nextIndex>=0) result+=countNonDivisibleBothNonLimited(currentSum,nextIndex);
				else if ((currentSum&mask)==mask) ++result;
				currentSum+=power;
			}
			return result;
		}
	}
	
	private static class LucasCounter	{
		private static int[] translateToBase(long number,int base)	{
			int digits=(int)(1+Math.floor(Math.log(number)/Math.log(base)));
			int[] result=new int[digits];
			for (int i=0;i<digits;++i)	{
				result[i]=(int)(number%base);
				number/=base;
			}
			return result;
		}
		private final int[] upper;
		private final int[] lower;
		private final int base;
		public LucasCounter(long upper,long lower,int base)	{
			this.upper=translateToBase(upper,base);
			this.lower=translateToBase(lower,base);
			this.base=base;
		}
		public long countNonDivisibleNumbers()	{
			return countNonDivisibleRecursiveLimited(upper.length-1);
		}
		private long countNonDivisibleRecursiveLimited(int currentIndex)	{
			int highDigit=upper[currentIndex];
			int lowDigit=(currentIndex>=lower.length)?0:lower[currentIndex];
			if (highDigit<lowDigit) return 0l;
			long result=countNonDivisibleRecursiveLimited(currentIndex-1);
			if (highDigit>lowDigit) result+=(highDigit-lowDigit)*countNonDivisibleRecursiveNonLimited(currentIndex-1);
			return result;
		}
		private long countNonDivisibleRecursiveNonLimited(int currentIndex)	{
			int digit=(currentIndex>=lower.length)?0:lower[currentIndex];
			long result=base-digit;
			if (currentIndex>0) result*=countNonDivisibleRecursiveNonLimited(currentIndex-1);
			return result;
		}
		private long countNonDivisibleNumbers(long mask)	{
			return new LucasEnumerator(lower,upper,base,mask).countNonDivisibleBoth();
		}
	}
	
	private static long countDivisible(long upper,long lower,int extraBase)	{
		LucasCounter counter1=new LucasCounter(upper,lower,2);
		LucasCounter counter2=new LucasCounter(upper,lower,extraBase);
		long nonDivisible1=counter1.countNonDivisibleNumbers();
		long nonDivisible2=counter2.countNonDivisibleNumbers();
		long nonDivisibleBoth=counter2.countNonDivisibleNumbers(lower);
		return upper-lower-nonDivisible1-nonDivisible2+nonDivisibleBoth;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=countDivisible(UPPER,LOWER,OTHER_BASE);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
