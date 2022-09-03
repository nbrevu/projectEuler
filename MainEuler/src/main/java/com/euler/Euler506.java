package com.euler;

import com.google.common.math.LongMath;

public class Euler506 {
	private final static long LIMIT=LongMath.pow(10l,14);
	private final static long MOD=123454321l;
	
	// This can be sped up by a factor of 6 by adding control for full "123432" cycles. I don't even care, this might be slow but it's still fast enough.
	private static class ClockDigitIterator	{
		private final static int[] DIGITS=new int[]{1,2,3,4,3,2};
		private final static int N=DIGITS.length;
		private final static int[] MOD_INDICES=new int[]{0,0,1,2,3,4,0,3,5,3,0,4,3,2,1};
		private final static int[] DIGITS_IN_NUMBER=new int[]{0,1,1,1,1,2,3,2,4,3,4,5,5,5,5};
		private int currentIndex;
		public ClockDigitIterator(int startingIndex)	{
			currentIndex=startingIndex%N;
		}
		public int getNextDigit()	{
			int result=DIGITS[currentIndex];
			currentIndex=(1+currentIndex)%N;
			return result;
		}
		public static int getStartingDigitForNumber(long in)	{
			int mod=(int)(in%15);
			return MOD_INDICES[mod];
		}
		public static long getDigitsInNumber(long in)	{
			long cycles=in/15;
			int mod=(int)(in%15);
			return 6*cycles+DIGITS_IN_NUMBER[mod];
		}
	}
	
	private static long getClockDigitNumberMod(long number,long mod)	{
		int startingNumber=ClockDigitIterator.getStartingDigitForNumber(number);
		long iterations=ClockDigitIterator.getDigitsInNumber(number);
		long result=0;
		ClockDigitIterator iterator=new ClockDigitIterator(startingNumber);
		for (long i=0;i<iterations;++i) result=(10*result+iterator.getNextDigit())%mod;
		return result;
	}
	
	/*
	private static long findNumber()	{
		Map<Long,Integer> factors=ImmutableMap.of(2l,4,3l,3,5l,2,41l,1,271l,1);
		long[] allDivisors=Euler622_4.getAllDivisors(factors);
		Arrays.sort(allDivisors);
		for (long div:allDivisors)	{
			long result=getClockDigitNumberMod(div,MOD);
			if ((result==0)&&((div%15)==0)) return div;
		}
		throw new RuntimeException("WAS?");
	}
	*/
	
	public static void main(String[] args)	{
		// The magic number is 833325, found by analysis (see findNumber() method, commented).
		// For every integers N and K, getClockDigitNumberMod(K,MOD) == getClockDigitNumberMod(K+(N*833325),MOD).
		long magicNumber=833325l;
		long cycles=LIMIT/magicNumber;	// "cycles" ends up being 120001200, slightly lower than the mod.
		long remainder=LIMIT%magicNumber;
		long sum=0;
		for (long i=1;i<=magicNumber;++i)	{
			if ((i%10000)==0) System.out.println(i);
			long result=getClockDigitNumberMod(i,MOD);
			long multipliedResult=result*cycles;
			if (i<=remainder) multipliedResult+=result;
			sum=(sum+multipliedResult)%MOD;
		}
		System.out.println(sum);
	}
}
