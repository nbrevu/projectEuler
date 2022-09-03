package com.euler;

import java.util.Arrays;
import java.util.BitSet;
import java.util.stream.LongStream;

import com.koloboke.collect.map.LongLongMap;
import com.koloboke.collect.map.hash.HashLongLongMaps;

public class Euler669_5 {
	private final static long N=99_194_853_094_755_497l;
	private final static long P=10_000_000_000_000_000l;
	
	private final static int FIXED_SEQ=19;
	
	private static interface Sequence	{
		public long length();
		public long getElement(long index);
	}
	
	private static class SequenceWithReflections	{
		private final Sequence seq;
		private final long limit1;
		private final long limit2;
		private final long limit3;
		private final LongLongMap cache;
		public SequenceWithReflections(Sequence seq,boolean is0Before)	{
			this.seq=seq;
			long len=seq.length();
			limit1=len;
			limit2=limit1+len;
			limit3=limit2+len;
			long pos0=limit2+(is0Before?-1:0);
			cache=HashLongLongMaps.newMutableMap();
			cache.put(pos0,0l);
		}
		public long getElement(long idx)	{
			return cache.computeIfAbsent(idx,(long index)->	{
				if (index<limit1) return seq.getElement(index);
				else if (index<limit2) return seq.getElement(limit2-1-index);
				else if (index<limit3) return seq.getElement(index-limit2);
				else throw new IndexOutOfBoundsException();
			});
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
		private final int index;
		private final long[] fibNumbers;
		public FibonacciArrangementFinder(int index,long[] fibNumbers)	{
			this.index=index;
			this.fibNumbers=fibNumbers;
		}
		public int[] getArrangement()	{
			int maxValue=(int)fibNumbers[index];
			int[] validSums=new int[3];
			for (int i=0;i<3;++i) validSums[i]=(int)fibNumbers[index-1+i];
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
					lastIndex=1;
					available.clear(next);
					result[i]=next;
					lastNum=next;
					break;
				}	case 1:	{
					int next=validSums[2]-lastNum;
					lastIndex=2;
					if (!available.get(next))	{
						next=validSums[0]-lastNum;
						lastIndex=0;
					}
					available.clear(next);
					result[i]=next;
					lastNum=next;
					break;
				}
			}
			return result;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		LongStream.Builder fiboBuilder=LongStream.builder();
		long prev2Fibo=1l;
		long prevFibo=2l;
		fiboBuilder.accept(prev2Fibo);
		fiboBuilder.accept(prevFibo);
		for (;;)	{
			long curr=prev2Fibo+prevFibo;
			fiboBuilder.accept(curr);
			if (curr==N) break;
			else if (curr>N) throw new IllegalArgumentException(N+" is not a Fibonacci number.");
			prev2Fibo=prevFibo;
			prevFibo=curr;
		}
		long[] fibos=fiboBuilder.build().toArray();
		int[] prev2Int=new FibonacciArrangementFinder(FIXED_SEQ-1,fibos).getArrangement();
		int[] prevInt=new FibonacciArrangementFinder(FIXED_SEQ,fibos).getArrangement();
		Sequence prev2Seq=new KnownSequence(Arrays.stream(prev2Int).asLongStream().toArray());
		Sequence prevSeq=new KnownSequence(Arrays.stream(prevInt).asLongStream().toArray());
		SequenceWithReflections prev2=new SequenceWithReflections(prev2Seq,(FIXED_SEQ%2)==0);
		SequenceWithReflections prev=new SequenceWithReflections(prevSeq,(FIXED_SEQ%2)!=0);
		for (int i=1+FIXED_SEQ;i<fibos.length;++i)	{
			Sequence recursiveSequence=new RecursiveSequence(prev,prev2,fibos[i]);
			prev2=prev;
			prev=new SequenceWithReflections(recursiveSequence,(i%2)==0);
		}
		long result=prev.getElement(N-P);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
