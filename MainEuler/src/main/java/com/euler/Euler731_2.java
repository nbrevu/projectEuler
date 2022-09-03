package com.euler;

import java.util.ArrayList;
import java.util.List;

import com.google.common.math.IntMath;
import com.google.common.math.LongMath;

public class Euler731_2 {
	private final static long START=LongMath.pow(10l,8);
	private final static int HOW_MANY=10;
	
	private static class PeriodSource	{
		private final int[] periodDigits;
		private final long periodOffset;
		public static long getPeriodOffsetFor(int n)	{
			return LongMath.pow(3l,n);
		}
		private PeriodSource(int[] periodDigits,long periodOffset)	{
			this.periodDigits=periodDigits;
			this.periodOffset=periodOffset;
		}
		public static PeriodSource getFor(int n)	{
			long number=LongMath.pow(3,n);
			int howManyDigits=(n==1)?1:(IntMath.pow(3,n-2));
			int[] periodDigits=new int[howManyDigits];
			long q=1;
			for (int i=0;i<howManyDigits;++i)	{
				q*=10;
				periodDigits[i]=(int)(q/number);
				q%=number;
			}
			long periodOffset=getPeriodOffsetFor(n);
			return new PeriodSource(periodDigits,periodOffset);
		}
		public int[] getDigits(long initialOffset,int howMany)	{
			int[] result=new int[howMany];
			long trueOffset=initialOffset-periodOffset;
			int currentOffset=(int)(trueOffset%periodDigits.length);
			for (int i=0;i<howMany;++i)	{
				result[i]=periodDigits[currentOffset];
				currentOffset=(currentOffset+1)%periodDigits.length;
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
		long initialOffset=START-1;
		int howMany=HOW_MANY+2;
		List<int[]> toSum=new ArrayList<>();
		for (int i=1;;++i)	{
			if (PeriodSource.getPeriodOffsetFor(i)>initialOffset) break;
			PeriodSource source=PeriodSource.getFor(i);
			toSum.add(source.getDigits(initialOffset,howMany));
		}
		int[] sum=sum(toSum);
		String result=toString(sum,HOW_MANY);
		System.out.println(result);
	}
}
