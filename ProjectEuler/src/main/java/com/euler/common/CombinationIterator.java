package com.euler.common;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;

import com.google.common.base.Functions;

public class CombinationIterator implements Iterable<int[]>,Iterator<int[]>	{
	private static final Function<int[],int[]> COPY_STRATEGY=(int[] array)->Arrays.copyOf(array,array.length);
	
	private final int size;
	private final int maxValue;
	private final boolean allowRepetition;
	private final int[] currentStatus;
	private final Function<int[],int[]> returnStrategy;
	private boolean isNextCalculated;
	public CombinationIterator(int size,int maxValue,boolean allowRepetition)	{
		this(size,maxValue,allowRepetition,COPY_STRATEGY);
	}
	public CombinationIterator(int size,int maxValue,boolean allowRepetition,boolean copyEachTime)	{
		this(size,maxValue,allowRepetition,copyEachTime?COPY_STRATEGY:Functions.identity());
	}
	private CombinationIterator(int size,int maxValue,boolean allowRepetition,Function<int[],int[]> returnStrategy)	{
		this.size=size;
		this.maxValue=maxValue;
		this.allowRepetition=allowRepetition;
		this.returnStrategy=returnStrategy;
		currentStatus=new int[size];
		if (!allowRepetition) setupInitial();
		isNextCalculated=true;
	}
	private void setupInitial()	{
		assert maxValue>=size;
		for (int i=1;i<size;++i) currentStatus[i]=i;
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
			int maxAvailable=(allowRepetition)?(maxValue-1):(maxValue-size+i);
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
		for (++i;i<size;++i) currentStatus[i]=(allowRepetition?0:1)+currentStatus[i-1];
		isNextCalculated=true;
	}
	private void finish()	{
		for (int i=0;i<size;++i) currentStatus[i]=-1;
		isNextCalculated=true;
	}
}