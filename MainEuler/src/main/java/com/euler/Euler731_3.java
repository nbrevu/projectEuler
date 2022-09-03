package com.euler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;

public class Euler731_3 {
	private final static long START=LongMath.pow(10l,16);
	private final static int HOW_MANY=10;
	
	private final static int ADDITIONAL_DIGITS=5;
	
	private static class PeriodSource	{
		private final long number;
		private final long periodOffset;
		public static long getNumberFor(int n)	{
			return LongMath.pow(3l,n);
		}
		private PeriodSource(long number,long periodOffset)	{
			this.number=number;
			this.periodOffset=periodOffset;
		}
		public static PeriodSource getFor(int n)	{
			long number=getNumberFor(n);
			long periodOffset=number;
			return new PeriodSource(number,periodOffset);
		}
		public int[] getDigits(long initialOffset,int howMany)	{
			int[] result=new int[howMany];
			long realOffset=initialOffset-periodOffset-1;
			long lastRemainder=EulerUtils.expMod(BigInteger.TEN,realOffset,BigInteger.valueOf(number)).longValue();
			for (int i=0;i<howMany;++i)	{
				lastRemainder*=10l;
				result[i]=(int)(lastRemainder/number);
				lastRemainder%=number;
			}
			return result;
		}
	}
	
	private static int[] sum(List<int[]> toSum)	{
		int digits=toSum.get(0).length;
		int[] result=new int[digits];
		int carry=0;
		for (int i=digits-1;i>=0;--i)	{
			result[i]=carry;
			for (int[] array:toSum) result[i]+=array[i];
			carry=result[i]/10;
			result[i]%=10;
		}
		return result;
	}
	
	private static String toString(int[] digits,int howMany)	{
		StringBuilder result=new StringBuilder();
		for (int i=0;i<howMany;++i) result.append(digits[i]);
		return result.toString();
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long initialOffset=START;
		int howManyEffective=HOW_MANY+ADDITIONAL_DIGITS;
		List<int[]> toSum=new ArrayList<>();
		for (int i=1;;++i)	{
			if (PeriodSource.getNumberFor(i)>initialOffset) break;
			PeriodSource source=PeriodSource.getFor(i);
			toSum.add(source.getDigits(initialOffset,howManyEffective));
		}
		int[] sum=sum(toSum);
		String result=toString(sum,HOW_MANY);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
