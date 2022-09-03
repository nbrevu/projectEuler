package com.euler;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.function.IntConsumer;

import com.euler.common.Primes;
import com.google.common.collect.Lists;
import com.koloboke.collect.IntCursor;

public class Euler705_2 {
	private final static int N=20;
	
	private final static int[][] FACTORS=generateFactorsMap();
	
	private static List<Integer> getFactors(int i)	{
		if (i==0) return Collections.emptyList();
		List<Integer> result=new ArrayList<>();
		for (int j=1;j<=i;++j) if ((i%j)==0) result.add(j);
		return result;
	}
	
	private static int generateFactor(int x,List<Integer> previousDivs)	{
		int result=0;
		for (int div:previousDivs) if (div>x) ++result;
		return result;
	}
	
	private static int[][] generateFactorsMap()	{
		int[][] result=new int[10][10];
		List<List<Integer>> factors=new ArrayList<>(10);
		for (int i=0;i<10;++i) factors.add(getFactors(i));
		for (int i=1;i<10;++i) for (int j=1;j<10;++j) for (int f:factors.get(i)) result[i][j]+=generateFactor(f,factors.get(j));
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
		for (int i=1;i<10;++i)	{
			for (int j=1;j<10;++j) System.out.print(FACTORS[i][j]);
			System.out.println();
		}
		
		int[] counters=new int[10];
		int result=0;
		for (IntCursor cursor=PrimeListDigitIterator.getFor(N);cursor.moveNext();)	{
			int digit=cursor.elem();
			for (int i=1;i<10;++i)	{	// I could use 2 as starting index, right?
				result+=counters[i]*FACTORS[i][digit];
			}
			++counters[digit];
		}
		System.out.println(result);
	}
}
