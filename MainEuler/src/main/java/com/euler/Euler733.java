package com.euler;

import java.util.NavigableMap;
import java.util.TreeMap;

import com.google.common.math.IntMath;
import com.google.common.math.LongMath;

public class Euler733 {
	private final static long BASE=153;
	private final static int SIZE=IntMath.pow(10,6);
	private final static long SEQ_MOD=LongMath.pow(10l,7)+19;
	private final static int SEQUENCE_LENGTH=4;
	private final static long MOD=LongMath.pow(10l,9)+7;
	
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
	}
	
	public static void main(String[] args)	{
		// Elapsed 46624.063086361006 seconds. JAJA SI.
		long tic=System.nanoTime();
		NavigableMap<Long,MultipleSequenceCounter> tmpResults=new TreeMap<>();
		long[] seqValues=generateSequence(BASE,SIZE,SEQ_MOD);
		long result=0l;
		for (int i=1;i<=SIZE;++i)	{
			long value=seqValues[i];
			MultipleSequenceCounter counter=new MultipleSequenceCounter(seqValues[i]);
			for (MultipleSequenceCounter other:tmpResults.headMap(value,false).values()) counter.updateWith(other);
			result=(result+counter.getSum())%MOD;
			tmpResults.put(value,counter);
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
