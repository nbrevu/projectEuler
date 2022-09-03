package com.euler;

import java.util.NoSuchElementException;

import com.euler.common.EulerUtils;

public class Euler571_2 {
	private final static int BASE=12;
	private final static int NUMBERS=500;
	
	private static boolean isPandigital(long number,int base)	{
		boolean[] present=new boolean[base];
		int sum=0;
		while (number>0)	{
			int mod=(int)(number%base);
			if (!present[mod])	{
				++sum;
				if (sum==base) return true;
				present[mod]=true;
			}
			number/=base;
		}
		return false;
	}
	
	private static boolean isAllPandigital(long number,int upToBase)	{
		for (int i=upToBase;i>=2;--i) if (!isPandigital(number,i)) return false;
		return true;
	}
	
	private static long generateNumber(long[] digits)	{
		long res=0;
		final int base=digits.length;
		for (int i=0;i<base;++i) res=res*base+digits[i];
		return res;
	}
	
	private static class PandigitalGenerator	{
		private final long[] digits;
		public PandigitalGenerator(int base)	{
			digits=new long[base];
			for (int i=0;i<base;++i) digits[i]=(long)i;
		}
		public long getNextPandigital()	{
			while (EulerUtils.nextPermutation(digits)) if (digits[0]!=0) return generateNumber(digits);
			throw new NoSuchElementException();
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		PandigitalGenerator gen=new PandigitalGenerator(BASE);
		int count=0;
		long sum=0;
		for (;;) try	{
			long candidate=gen.getNextPandigital();
			if (isAllPandigital(candidate,BASE-1))	{
				++count;
				sum+=candidate;
				if (count==NUMBERS)	{
					long toc=System.nanoTime();
					System.out.println(sum);
					double time=(double)(toc-tic)/1e9;
					System.out.println("Time: "+time+"s.");
					return;
				}
			}
		}	catch (NoSuchElementException exc)	{
			break;
		}
		System.out.println("ACHTUNG! ICH HABE NUR "+count+" ZÄHLEN GEFUNDEN!!!!! => "+sum);
	}
}
