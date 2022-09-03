package com.euler;

import com.google.common.math.LongMath;

public class Euler672_2 {
	private final static int K=1_000_000_000;
	private final static long MOD=1_117_117_717l;
	
	private final static int BASE=7;
	private final static int TRIANG_BASE=(BASE*(BASE-1))/2;
	private final static long INITIAL_NUMBER=(LongMath.pow(BASE,10)-1)/11;
	private final static int EXTRA_ITERATIONS=(K/10)-1;
	private final static int[] TRIANG_DIFFS=getTriangDiffs(BASE);
	
	private static int[] getTriangDiffs(int base)	{
		int[] result=new int[base];
		result[0]=0;
		for (int i=1;i<result.length;++i) result[i]=result[i-1]+BASE-i;
		return result;
	}
	
	private static int[] extractDigits(long in)	{
		int numDigits=(int)Math.ceil(Math.log(in+1)/Math.log(BASE));
		int[] result=new int[numDigits];
		for (int i=result.length-1;i>=0;--i)	{
			result[i]=(int)(in%7);
			in/=7;
		}
		return result;
	}
	
	private static class DataHolder	{
		public long lastValue;
		public long gLastValuePlus1;
		public long sumG;
		public DataHolder(int initialDigit)	{
			lastValue=initialDigit;
			gLastValuePlus1=BASE-1-initialDigit;	// Correctly sets the value 0 for the special case initialDigit=BASE-1.
			sumG=0;
			for (int i=2;i<=initialDigit;++i) sumG+=BASE-i;
		}
		public void evolve(int digit)	{
			long newLastValue=(BASE*lastValue)+digit;
			long newGLastValuePlus1=gLastValuePlus1+BASE-1-digit;
			long newSumG=TRIANG_BASE*lastValue+digit*gLastValuePlus1+BASE*sumG+TRIANG_DIFFS[digit]-(BASE-1);
			lastValue=newLastValue%MOD;
			gLastValuePlus1=newGLastValuePlus1%MOD;
			sumG=newSumG%MOD;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int[] digits=extractDigits(INITIAL_NUMBER);
		DataHolder result=new DataHolder(digits[0]);
		for (int i=1;i<digits.length;++i) result.evolve(digits[i]);
		for (int i=0;i<EXTRA_ITERATIONS;++i)	{
			result.evolve(0);
			for (int d:digits) result.evolve(d);
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result.sumG);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
