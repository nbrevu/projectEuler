package com.euler;

import java.math.RoundingMode;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import com.google.common.math.LongMath;

public class Euler255 {
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
		public RangeMap<Long,Integer> getRanges()	{
			return ranges;
		}
	}
	
	public static void main(String[] args)	{
		int digits=5;
		long start=LongMath.pow(10l,digits-1);
		long end=LongMath.pow(10l,digits);
		long sum=0l;
		RangeAccumulator accumulator=new RangeAccumulator();
		for (long i=start;i<end;++i)	{
			int steps=getSteps(i,digits);
			accumulator.addValue(i,steps);
			sum+=steps;
		}
		accumulator.finish();
		System.out.println(accumulator.getRanges());
		double result=((double)sum)/((double)(end-start));
		System.out.println(result);
	}
}
