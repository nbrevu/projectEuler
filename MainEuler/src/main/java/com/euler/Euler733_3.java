package com.euler;

import java.util.Arrays;
import java.util.List;

import com.euler.common.FenwickTree;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;

public class Euler733_3 {
	private final static long BASE=153;
	private final static int SIZE=IntMath.pow(10,6);
	private final static long SEQ_MOD=LongMath.pow(10l,7)+19;
	private final static int SEQUENCE_LENGTH=4;
	private final static long MOD=LongMath.pow(10l,9)+7;
	
	private static int[] generateSequence(long base,int size,long mod)	{
		int[] result=new int[1+size];
		long value=1l;
		for (int i=1;i<=size;++i)	{
			value*=base;
			value%=mod;
			result[i]=(int)value;
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
		@Override
		public String toString()	{
			return "{"+count+","+sum+"}";
		}
	}
	
	private final static SequenceCounter EMPTY_SEQUENCE=new SequenceCounter(0l,0l);
	private final static MultipleSequenceCounter EMPTY_SEQUENCES=MultipleSequenceCounter.getEmpty();
	
	private static class MultipleSequenceCounter	{
		private final SequenceCounter[] elements;
		private MultipleSequenceCounter(SequenceCounter[] elements)	{
			this.elements=elements;
		}
		public static MultipleSequenceCounter getEmpty()	{
			SequenceCounter[] elements=new SequenceCounter[SEQUENCE_LENGTH];
			for (int i=0;i<SEQUENCE_LENGTH;++i) elements[i]=EMPTY_SEQUENCE;
			return new MultipleSequenceCounter(elements);
		}
		public static MultipleSequenceCounter singleValue(long value)	{
			SequenceCounter[] elements=new SequenceCounter[SEQUENCE_LENGTH];
			elements[0]=new SequenceCounter(1l,value);
			for (int i=1;i<SEQUENCE_LENGTH;++i) elements[i]=EMPTY_SEQUENCE;
			return new MultipleSequenceCounter(elements);
		}
		public MultipleSequenceCounter updateWith(MultipleSequenceCounter other,long value)	{
			SequenceCounter[] result=new SequenceCounter[SEQUENCE_LENGTH];
			result[0]=elements[0];
			for (int i=1;i<SEQUENCE_LENGTH;++i) result[i]=elements[i].addSequence(other.elements[i-1],value);
			return new MultipleSequenceCounter(result);
		}
		public long getSum()	{
			return elements[elements.length-1].sum;
		}
		public static MultipleSequenceCounter getSum(List<MultipleSequenceCounter> seqs)	{
			SequenceCounter[] elements=new SequenceCounter[SEQUENCE_LENGTH];
			for (int i=0;i<SEQUENCE_LENGTH;++i)	{
				long count=0;
				long sum=0;
				for (MultipleSequenceCounter otherSeq:seqs)	{
					count+=otherSeq.elements[i].count;
					sum+=otherSeq.elements[i].sum;
				}
				count%=MOD;
				sum%=MOD;
				elements[i]=new SequenceCounter(count,sum);
			}
			return new MultipleSequenceCounter(elements);
		}
		public static MultipleSequenceCounter sum(MultipleSequenceCounter a,MultipleSequenceCounter b)	{
			SequenceCounter[] elements=new SequenceCounter[SEQUENCE_LENGTH];
			for (int i=0;i<SEQUENCE_LENGTH;++i)	{
				long count=(a.elements[i].count+b.elements[i].count)%MOD;
				long sum=(a.elements[i].sum+b.elements[i].sum)%MOD;
				elements[i]=new SequenceCounter(count,sum);
			}
			return new MultipleSequenceCounter(elements);
		}
		@Override
		public String toString()	{
			return Arrays.toString(elements);
		}
	}
	
	private static class FenwickImplementation extends FenwickTree<MultipleSequenceCounter>	{
		public FenwickImplementation(int maxValue) {
			super(maxValue);
		}
		@Override
		protected MultipleSequenceCounter[] createArray(int length) {
			return new MultipleSequenceCounter[length];
		}
		@Override
		protected MultipleSequenceCounter sum(List<MultipleSequenceCounter> values) {
			return MultipleSequenceCounter.getSum(values);
		}
		@Override
		protected MultipleSequenceCounter getZeroElement(int index) {
			return EMPTY_SEQUENCES;
		}
		@Override
		protected MultipleSequenceCounter sumTwo(MultipleSequenceCounter a,MultipleSequenceCounter b)	{
			return MultipleSequenceCounter.sum(a,b);
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int[] seqValues=generateSequence(BASE,SIZE,SEQ_MOD);
		long result=0l;
		FenwickImplementation dataHolder=new FenwickImplementation((int)SEQ_MOD);
		for (int i=1;i<=SIZE;++i)	{
			int value=seqValues[i];
			MultipleSequenceCounter counter=MultipleSequenceCounter.singleValue(value);
			counter=counter.updateWith(dataHolder.readData(value-1),value);
			result=(result+counter.getSum())%MOD;
			dataHolder.putData(value,counter);
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
