package com.euler;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.function.IntConsumer;

import com.euler.common.Primes;
import com.google.common.collect.Lists;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import com.koloboke.collect.IntCursor;

public class Euler705_3 {
	private final static int N=IntMath.pow(10,8);
	private final static long MOD=LongMath.pow(10l,9)+7;
	
	private final static int[][] FACTORS=generateFactorsMap();
	
	private static int[] getFactors(int i)	{
		List<Integer> result=new ArrayList<>();
		for (int j=1;j<=i;++j) if ((i%j)==0) result.add(j);
		return result.stream().mapToInt(Integer::intValue).toArray();
	}
	
	private static int[][] generateFactorsMap()	{
		int[][] result=new int[10][];
		for (int i=0;i<10;++i) result[i]=getFactors(i);
		return result;
	}
	
	private static class NumberDigitIterator implements IntCursor	{
		private final int[] digits;
		private int index;
		private NumberDigitIterator(int[] digits)	{
			this.digits=digits;
			index=-1;
		}
		public static NumberDigitIterator getFor(int n)	{
			Deque<Integer> list=new ArrayDeque<>();
			while (n>0)	{
				int digit=n%10;
				if (digit!=0) list.addFirst(digit);
				n/=10;
			}
			return new NumberDigitIterator(list.stream().mapToInt(Integer::intValue).toArray());
		}
		@Override
		public boolean moveNext() {
			++index;
			return index<digits.length;
		}
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		@Override
		public int elem() {
			return digits[index];
		}
		@Override
		public void forEachForward(IntConsumer arg0) {
			throw new UnsupportedOperationException();
		}
	}
	
	private static class PrimeListDigitIterator implements IntCursor	{
		private final Iterator<NumberDigitIterator> iterators;
		private NumberDigitIterator currentIterator;
		private PrimeListDigitIterator(Iterator<NumberDigitIterator> iterators)	{
			this.iterators=iterators;
			currentIterator=iterators.next();
		}
		public static PrimeListDigitIterator getFor(int primeLimit)	{
			List<Integer> primes=Primes.listIntPrimes(primeLimit);
			Iterator<NumberDigitIterator> iterators=Lists.transform(primes,NumberDigitIterator::getFor).iterator();
			if (!iterators.hasNext()) throw new IllegalArgumentException();
			return new PrimeListDigitIterator(iterators);
		}
		@Override
		public boolean moveNext() {
			if (currentIterator.moveNext()) return true;
			if (!iterators.hasNext()) return false;
			currentIterator=iterators.next();
			currentIterator.moveNext();
			return true;
		}
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		@Override
		public int elem() {
			return currentIterator.elem();
		}
		@Override
		public void forEachForward(IntConsumer arg0) {
			throw new UnsupportedOperationException();
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long[] counters=new long[10];
		long result=0;
		long combinations=1;
		for (IntCursor cursor=PrimeListDigitIterator.getFor(N);cursor.moveNext();)	{
			int digit=cursor.elem();
			int[] newDigits=FACTORS[digit];
			result*=newDigits.length;
			for (int f:newDigits) for (int i=f+1;i<10;++i) result+=counters[i];
			for (int i=1;i<10;++i) counters[i]*=newDigits.length;
			for (int f:newDigits) counters[f]+=combinations;
			combinations*=newDigits.length;
			result%=MOD;
			for (int i=1;i<10;++i) counters[i]%=MOD;
			combinations%=MOD;
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
		System.out.println(result);
	}
}
