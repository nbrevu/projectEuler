package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.google.common.collect.Lists;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;

public class Euler733_2 {
	private final static long BASE=153;
	private final static int SIZE=IntMath.pow(10,2);
	private final static long SEQ_MOD=LongMath.pow(10l,7)+19;
	private final static int SEQUENCE_LENGTH=4;
	private final static long MOD=LongMath.pow(10l,18)+7;
	
	private static long[] generateSequence(long base,int size,long mod)	{
		long[] result=new long[1+size];
		long value=1l;
		for (int i=1;i<=size;++i)	{
			value*=base;
			value%=mod;
			result[i]=value;
		}
		return result;
	}
	
	private static class SequenceCounter	{
		public final long count;
		public final long sum;
		public SequenceCounter(long count,long sum)	{
			this.count=count;
			this.sum=sum;
		}
		public SequenceCounter addSequence(SequenceCounter smallerSequence,long value)	{
			long newCount=smallerSequence.count;
			long newSum=smallerSequence.sum+value*newCount;
			newCount%=MOD;
			newSum%=MOD;
			return new SequenceCounter((count+newCount)%MOD,(sum+newSum)%MOD);
		}
		public SequenceCounter addRaw(List<SequenceCounter> others)	{
			long accCount=count;
			long accSum=sum;
			for (SequenceCounter counter:others)	{
				accCount+=counter.count;
				accSum+=counter.sum;
			}
			return new SequenceCounter(accCount%MOD,accSum%MOD);
		}
	}
	
	private final static SequenceCounter EMPTY_SEQUENCE=new SequenceCounter(0l,0l);
	
	private static class MultipleSequenceCounter	{
		private final long value;
		private final SequenceCounter[] elements;
		public MultipleSequenceCounter(long value)	{
			this.value=value;
			elements=new SequenceCounter[SEQUENCE_LENGTH];
			elements[0]=new SequenceCounter(1l,value);
			for (int i=1;i<SEQUENCE_LENGTH;++i) elements[i]=EMPTY_SEQUENCE;
		}
		public void updateWith(MultipleSequenceCounter other)	{
			for (int i=1;i<SEQUENCE_LENGTH;++i) elements[i]=elements[i].addSequence(other.elements[i-1],value);
		}
		public long getSum()	{
			return elements[elements.length-1].sum;
		}
		public void addRaw(List<MultipleSequenceCounter> others)	{
			for (int i=0;i<SEQUENCE_LENGTH;++i)	{
				final int j=i;
				List<SequenceCounter> toAdd=Lists.transform(others,(MultipleSequenceCounter counter)->counter.elements[j]);
				elements[i]=elements[i].addRaw(toAdd);
			}
		}
	}
	
	private static class FullElementData	{
		private final MultipleSequenceCounter thisElementCounter;
		private final MultipleSequenceCounter accumulated;
		private int lastIndex;
		public FullElementData(MultipleSequenceCounter thisElementCounter,MultipleSequenceCounter accumulated,int lastIndex)	{
			this.thisElementCounter=thisElementCounter;
			this.accumulated=accumulated;
			this.lastIndex=lastIndex;
		}
		public MultipleSequenceCounter getThisElementCounter() {
			return thisElementCounter;
		}
		public MultipleSequenceCounter getAccumulated() {
			return accumulated;
		}
		public int getLastIndex() {
			return lastIndex;
		}
		public void update(List<MultipleSequenceCounter> toUpdate,int newIndex)	{
			if (!toUpdate.isEmpty()) accumulated.addRaw(toUpdate);
			lastIndex=newIndex;
		}
	}
	
	public static void main(String[] args)	{
		/*
		 * Está mal pero no sé muy bien por qué. Aún así, casi seguramente sigue siendo demasiado lento.
		 */
		long tic=System.nanoTime();
		NavigableMap<Long,FullElementData> tmpResults=new TreeMap<>();
		long[] seqValues=generateSequence(BASE,SIZE,SEQ_MOD);
		FullElementData[] storage=new FullElementData[1+SIZE];
		long result=0l;
		for (int i=1;i<=SIZE;++i)	{
			long value=seqValues[i];
			MultipleSequenceCounter counter=new MultipleSequenceCounter(seqValues[i]);
			FullElementData newData;
			NavigableMap<Long,FullElementData> otherCounters=tmpResults.headMap(value,false);
			if (otherCounters.isEmpty()) newData=new FullElementData(counter,counter,i);
			else	{
				Map.Entry<Long,FullElementData> lastEntry=otherCounters.lastEntry();
				long limit=lastEntry.getKey();
				FullElementData lastData=lastEntry.getValue();
				List<MultipleSequenceCounter> toAccumulate=new ArrayList<>();
				for (int j=lastData.getLastIndex()+1;j<i;++j) if (seqValues[j]<limit) toAccumulate.add(storage[j].getThisElementCounter());
				lastData.update(toAccumulate,i);
				counter.updateWith(lastData.getAccumulated());
				MultipleSequenceCounter accumulated=new MultipleSequenceCounter(value);
				accumulated.addRaw(Arrays.asList(lastData.getAccumulated(),counter));
				newData=new FullElementData(counter,accumulated,i);
			}
			result=(result+counter.getSum())%MOD;
			tmpResults.put(value,newData);
			storage[i]=newData;
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
