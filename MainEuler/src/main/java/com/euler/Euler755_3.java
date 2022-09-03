package com.euler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.LongConsumer;

import com.euler.common.LongFenwickTree;
import com.google.common.math.LongMath;

public class Euler755_3 {
	// This is *slower* than the previous version :O.
	private final static long LIMIT=LongMath.pow(10l,13);
	
	private static long[] listFibosUnder(long limit)	{
		List<Long> result=new ArrayList<>();
		long fPrev=0l;
		long fCurr=1l;
		for (;;)	{
			long fNext=fPrev+fCurr;
			if (fNext>limit) break;
			result.add(fNext);
			fPrev=fCurr;
			fCurr=fNext;
		}
		return result.stream().mapToLong(Long::longValue).toArray();
	}
	
	private static class BitsetStyleIterator	{
		private final long[] fibos;
		private final int start;
		private final int end;
		private final LongConsumer action;
		public BitsetStyleIterator(long[] fibos,int start,int end,LongConsumer action)	{
			this.fibos=fibos;
			this.start=start;
			this.end=end;
			this.action=action;
		}
		public void consume()	{
			if (start<=end) consumeRecursive(0,start);
		}
		private void consumeRecursive(long currentSum,int currentIndex)	{
			if (currentIndex==end)	{
				action.accept(currentSum);
				action.accept(currentSum+fibos[end]);
			}	else	{
				int nextIndex=1+currentIndex;
				consumeRecursive(currentSum,nextIndex);
				consumeRecursive(currentSum+fibos[currentIndex],nextIndex);
			}
		}
	}
	
	private static LongFenwickTree initFenwickTree(long[] fibos,int howMany,int fenwickSize)	{
		LongFenwickTree result=new LongFenwickTree(fenwickSize);
		BitsetStyleIterator iterator=new BitsetStyleIterator(fibos,0,howMany-1,(long sum)->result.putData(1+(int)sum,1l));
		iterator.consume();
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long[] fibos=listFibosUnder(LIMIT);
		int howManyFibos=fibos.length;
		int firstPartFibos=(howManyFibos+1)/2;
		/*
		 * The sum of the first N fibos equals the N+2'th fibo minus 2.
		 * Also, since fibos grow exponentially, the max fibo involved in this kind of sum is expected to be of the order of 10^6.5, so the
		 * Fenwick tree shouldn't grow much.
		 */
		int firstPartMaxSum=(int)fibos[firstPartFibos+2];
		LongFenwickTree fenwickTree=initFenwickTree(fibos,firstPartFibos,firstPartMaxSum);
		long[] result=new long[1];
		BitsetStyleIterator iterator=new BitsetStyleIterator(fibos,firstPartFibos,howManyFibos-1,(long sum)->	{
			long remaining=LIMIT-sum;
			if (remaining<0) return;
			else if (remaining>firstPartMaxSum) result[0]+=fenwickTree.getTotal();
			else result[0]+=fenwickTree.readData((int)remaining+1);
		});
		iterator.consume();
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result[0]);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
