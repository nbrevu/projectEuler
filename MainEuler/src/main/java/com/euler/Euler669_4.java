package com.euler;

import java.util.Arrays;
import java.util.BitSet;
import java.util.stream.IntStream;

public class Euler669_4 {
	private static interface Sequence	{
		public long length();
		public long getElement(long index);
	}
	
	private static class SequenceWithReflections	{
		private final Sequence seq;
		private final long limit1;
		private final long limit2;
		private final long limit3;
		private final long pos0;
		public SequenceWithReflections(Sequence seq,boolean is0Before)	{
			this.seq=seq;
			long len=seq.length();
			limit1=len;
			limit2=limit1+len;
			limit3=limit2+len;
			pos0=limit2+(is0Before?-1:0);
		}
		public long getElement(long index)	{
			if (index==pos0) return 0;
			else if (index<limit1) return seq.getElement(index);
			else if (index<limit2) return seq.getElement(limit2-1-index);
			else if (index<limit3) return seq.getElement(index-limit2);
			else throw new IndexOutOfBoundsException();
		}
	}
	
	private static class KnownSequence implements Sequence	{
		private final long[] array;
		public KnownSequence(long[] array)	{
			this.array=array;
		}
		@Override
		public long length() {
			return array.length;
		}
		@Override
		public long getElement(long index) {
			return array[(int)index];
		}
	}
	
	private static class RecursiveSequence implements Sequence	{
		private final SequenceWithReflections seq1;
		private final SequenceWithReflections seq2;
		private final long length;
		public RecursiveSequence(SequenceWithReflections seq1,SequenceWithReflections seq2,long length)	{
			this.seq1=seq1;
			this.seq2=seq2;
			this.length=length;
		}
		@Override
		public long length() {
			return length;
		}
		@Override
		public long getElement(long index) {
			return seq1.getElement(index)+seq2.getElement(index);
		}
	}
	
	private static class FibonacciArrangementFinder	{
		private final int maxValue;
		private final int[] fibNumbers;
		public FibonacciArrangementFinder(int maxValue)	{
			this.maxValue=maxValue;
			fibNumbers=getFibonaccis(2*maxValue);
		}
		private static int[] getFibonaccis(int maxValue)	{
			IntStream.Builder fibValues=IntStream.builder();
			fibValues.accept(1);
			fibValues.accept(2);
			int prev2=1;
			int prev=2;
			for (;;)	{
				int curr=prev+prev2;
				if (curr>maxValue) break;
				fibValues.accept(curr);
				prev2=prev;
				prev=curr;
			}
			return fibValues.build().toArray();
		}
		public int[] getArrangement()	{
			int[] validSums=Arrays.copyOfRange(fibNumbers,fibNumbers.length-3,fibNumbers.length);
			int[] result=new int[maxValue];
			BitSet available=new BitSet(maxValue);
			available.set(1,maxValue);
			result[0]=maxValue;
			result[1]=validSums[0];	// I.e. fib(n-1).
			int lastNum=result[1];
			int lastIndex=2;
			for (int i=2;i<result.length;++i) switch (lastIndex)	{
				case 0:
				case 2:	{// Yep, exact same code.
					int next=validSums[1]-lastNum;
					if (!available.get(next)) throw new IllegalStateException("Es funktioniert nicht.");
					result[i]=next;
					lastNum=next;
					lastIndex=1;
					break;
				}	case 1:	{
					int next=validSums[2]-lastNum;
					lastIndex=2;
					if (!available.get(next))	{
						next=validSums[0]-lastNum;
						if (!available.get(next)) throw new IllegalStateException("Es funktioniert nicht.");
						lastIndex=0;
					}
					result[i]=next;
					lastNum=next;
					break;
				}
			}
			return result;
		}
	}
	
	public static void main(String[] args)	{
		int[] fib3int=new FibonacciArrangementFinder(3).getArrangement();
		int[] fib5int=new FibonacciArrangementFinder(5).getArrangement();
		Sequence fib3=new KnownSequence(Arrays.stream(fib3int).asLongStream().toArray());
		Sequence fib5=new KnownSequence(Arrays.stream(fib5int).asLongStream().toArray());
		SequenceWithReflections prev2=new SequenceWithReflections(fib3,false);
		SequenceWithReflections prev=new SequenceWithReflections(fib5,true);
		boolean is0Before=false;
		int[] nextFibos=new int[] {8,13,21,34,55,89,144,233,377,610,987,1597};
		for (int i=0;i<nextFibos.length;++i)	{
			int fibo=nextFibos[i];
			int[] trueSequence=new FibonacciArrangementFinder(fibo).getArrangement();
			Sequence recursiveSequence=new RecursiveSequence(prev,prev2,fibo);
			System.out.println("A ver qué pasa con el "+fibo+".");
			for (int j=0;j<fibo;++j) if (recursiveSequence.getElement(j)!=trueSequence[j])	{
				System.out.println(String.format("f(%d)=%d (but I get %d :'( ).",j,trueSequence[j],recursiveSequence.getElement(j)));
				throw new IllegalStateException();
			}
			prev2=prev;
			prev=new SequenceWithReflections(recursiveSequence,is0Before);
			is0Before=!is0Before;
		}
	}
}
