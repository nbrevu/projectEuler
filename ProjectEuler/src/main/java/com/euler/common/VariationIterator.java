package com.euler.common;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;

import com.google.common.base.Functions;

public class VariationIterator implements Iterable<int[]>,Iterator<int[]>	{
	private final static Function<int[],int[]> COPY_STRATEGY=(int[] array)->Arrays.copyOf(array,array.length);
	
	private final int size;
	private final int maxValue;
	private final int[] currentStatus;
	private final Function<int[],int[]> returnStrategy;
	private boolean isNextCalculated;
	public VariationIterator(int size,int maxValue)	{
		this(size,maxValue,COPY_STRATEGY);
	}
	public VariationIterator(int size,int maxValue,boolean copyEachTime)	{
		this(size,maxValue,copyEachTime?COPY_STRATEGY:Functions.identity());
	}
	private VariationIterator(int size,int maxValue,Function<int[],int[]> returnStrategy)	{
		this.size=size;
		this.maxValue=maxValue;
		this.returnStrategy=returnStrategy;
		currentStatus=new int[size];
		isNextCalculated=true;
	}
	@Override
	public boolean hasNext() {
		if (!isNextCalculated) calculateNext();
		return currentStatus[0]>=0;
	}
	@Override
	public int[] next() {
		if (!isNextCalculated) calculateNext();
		isNextCalculated=false;
		return returnStrategy.apply(currentStatus);
	}
	@Override
	public Iterator<int[]> iterator() {
		return this;
	}
	private void calculateNext()	{
		int i=size-1;
		for (;;)	{
			int maxAvailable=maxValue-1;
			if (currentStatus[i]>=maxAvailable)	{
				--i;
				if (i<0)	{
					finish();
					return;
				}
			}	else	{
				++currentStatus[i];
				break;
			}
		}
		for (++i;i<size;++i) currentStatus[i]=0;
		isNextCalculated=true;
	}
	private void finish()	{
		for (int i=0;i<size;++i) currentStatus[i]=-1;
		isNextCalculated=true;
	}
}