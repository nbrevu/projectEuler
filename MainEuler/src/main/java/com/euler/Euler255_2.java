package com.euler;

import java.math.RoundingMode;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import com.google.common.math.LongMath;

public class Euler255_2 {
	private static int getSteps(long in,int d)	{
		int firstDigit,tenPower;
		if ((d%2)==0)	{
			firstDigit=7;
			tenPower=(d-2)/2;
		}	else	{
			firstDigit=2;
			tenPower=(d-1)/2;
		}
		long x=firstDigit*LongMath.pow(10l,tenPower);
		for (int counter=1;;++counter)	{
			long xNext=LongMath.divide(x+LongMath.divide(in,x,RoundingMode.UP),2l,RoundingMode.DOWN);
			if (xNext==x) return counter;
			x=xNext;
		}
	}
	
	private static class RangeAccumulator	{
		private final RangeMap<Long,Integer> ranges;
		private long currentStart;
		private long lastPosition;
		private int currentValue;
		public RangeAccumulator()	{
			ranges=TreeRangeMap.create();
			currentStart=Long.MIN_VALUE;
			lastPosition=Long.MIN_VALUE;
			currentValue=-1;
		}
		public void addValue(long position,int value)	{
			if ((position!=lastPosition+1)||(value!=currentValue))	{
				finish();
				currentStart=position;
				currentValue=value;
			}
			lastPosition=position;
		}
		public void finish()	{
			if (lastPosition>0)	{
				Range<Long> range=Range.closedOpen(currentStart,1+lastPosition);
				ranges.put(range,currentValue);
			}
		}
	}
	
	public static void main(String[] args)	{
		for (int i=1;i<=10;++i)	{
			long start=LongMath.pow(10l,i-1);
			long end=LongMath.pow(10l,i);
			long sum=0l;
			RangeAccumulator accumulator=new RangeAccumulator();
			for (long j=start;j<end;++j)	{
				int steps=getSteps(j,i);
				accumulator.addValue(j,steps);
				sum+=steps;
			}
			accumulator.finish();
			double result=((double)sum)/((double)(end-start));
			System.out.println(String.format("%d digits: sum=%d, result=%.10f.",i,sum,result));
		}
	}
}
