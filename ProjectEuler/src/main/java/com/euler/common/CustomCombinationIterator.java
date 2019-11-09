package com.euler.common;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;

import com.google.common.base.Functions;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;

public class CustomCombinationIterator implements Iterable<int[]>,Iterator<int[]>	{
	private final static Function<int[],int[]> COPY_STRATEGY=(int[] array)->Arrays.copyOf(array,array.length);
	
	private final int size;
	private final int[] values;
	private final IntIntMap reverseValues;
	private final boolean allowRepetition;
	private final int[] currentStatus;
	private final Function<int[],int[]> returnStrategy;
	private boolean isNextCalculated;
	public CustomCombinationIterator(int size,int[] values,boolean allowRepetition)	{
		this(size,values,allowRepetition,COPY_STRATEGY);
	}
	public CustomCombinationIterator(int size,int[] values,boolean allowRepetition,boolean copyEachTime)	{
		this(size,values,allowRepetition,copyEachTime?COPY_STRATEGY:Functions.identity());
	}
	private CustomCombinationIterator(int size,int[] values,boolean allowRepetition,Function<int[],int[]> returnStrategy)	{
		this.size=size;
		this.values=values;
		reverseValues=reverse(values);
		this.allowRepetition=allowRepetition;
		this.returnStrategy=returnStrategy;
		currentStatus=new int[size];
		setupInitial();
		isNextCalculated=true;
	}
	private static IntIntMap reverse(int[] array) {
		IntIntMap result=HashIntIntMaps.newMutableMap();
		for (int i=0;i<array.length;++i)	{
			if ((i>1)&&(array[i]<=array[i-1])) throw new IllegalArgumentException();
			result.put(array[i],i);
		}
		return result;
	}
	private void setupInitial()	{
		if (allowRepetition) for (int i=0;i<size;++i) currentStatus[i]=values[0];
		else	{
			assert values.length>=size;
			for (int i=0;i<size;++i) currentStatus[i]=values[i];
		}
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
			int maxIndexAvailable=(allowRepetition)?(values.length-1):(values.length-size+i);
			if (currentStatus[i]>=values[maxIndexAvailable])	{
				--i;
				if (i<0)	{
					finish();
					return;
				}
			}	else	{
				int currentIndex=reverseValues.get(currentStatus[i]);
				++currentIndex;
				currentStatus[i]=values[currentIndex];
				break;
			}
		}
		if (allowRepetition) for (++i;i<size;++i) currentStatus[i]=currentStatus[i-1];
		else	{
			int currentIndex=reverseValues.get(currentStatus[i]);
			for (++i;i<size;++i)	{
				++currentIndex;
				currentStatus[i]=values[currentIndex];
			}
		}
		isNextCalculated=true;
	}
	private void finish()	{
		for (int i=0;i<size;++i) currentStatus[i]=-1;
		isNextCalculated=true;
	}
}