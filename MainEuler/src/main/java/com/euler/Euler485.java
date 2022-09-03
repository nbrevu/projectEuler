package com.euler;

import static com.euler.common.EulerUtils.decreaseCounter;
import static com.euler.common.EulerUtils.increaseCounter;

import java.util.Iterator;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.euler.common.Primes;

public class Euler485 {
	//*
	private final static int LIMIT=100000000;
	private final static int WINDOW=100000;
	/*/
	private final static int LIMIT=1000;
	private final static int WINDOW=10;
	//*/
	private static int[] amountOfDivisors(int limit)	{
		int[] firstPrimes=Primes.firstPrimeSieve(1+limit);
		int[] result=new int[1+limit];
		result[1]=1;
		for (int i=2;i<=limit;++i)	{
			int p=firstPrimes[i];
			if (p==0)	{
				result[i]=2;
				continue;
			}
			int counter=2;	// This is the amount of times the power appears, plus one.
			int N=i/p;
			while ((N%p)==0)	{
				N/=p;
				++counter;
			}
			result[i]=result[N]*counter;
		}
		return result;
	}
	
	private static class SlidingWindow	{
		private NavigableMap<Integer,Integer> counterHolder;
		public SlidingWindow()	{
			counterHolder=new TreeMap<>();
		}
		public void addNumber(int num)	{
			increaseCounter(counterHolder,num);
		}
		public void removeNumber(int num)	{
			decreaseCounter(counterHolder,num);
		}
		public int getHighestNumber()	{
			return counterHolder.lastKey();
		}
	}
	
	private static class HighestNumberGenerator implements Iterator<Integer>	{
		private final int[] baseSequence;
		private int removeIndex;
		private int addIndex;
		private SlidingWindow generator;
		public HighestNumberGenerator(int[] baseSequence,int windowSize) {
			this.baseSequence=baseSequence;
			generator=new SlidingWindow();
			for (int i=1;i<=windowSize;++i) generator.addNumber(baseSequence[i]);
			removeIndex=1;
			addIndex=1+windowSize;
		}
		@Override
		public boolean hasNext() {
			return addIndex<=baseSequence.length;
		}
		@Override
		public Integer next() {
			int result=generator.getHighestNumber();
			if (addIndex<baseSequence.length)	{
				int N1=baseSequence[removeIndex];
				int N2=baseSequence[addIndex];
				if (N1!=N2)	{
					generator.removeNumber(baseSequence[removeIndex]);
					generator.addNumber(baseSequence[addIndex]);
				}
			}
			++removeIndex;
			++addIndex;
			return result;
		}
	}
	
	public static void main(String[] args)	{
		long tic0=System.nanoTime();
		int[] divisors=amountOfDivisors(LIMIT);
		long tic=System.nanoTime();
		HighestNumberGenerator gen=new HighestNumberGenerator(divisors,WINDOW);
		long sum=0;
		while (gen.hasNext()) sum+=gen.next();
		long tac=System.nanoTime();
		System.out.println(sum);
		double time1=((double)(tic-tic0))/1e9;
		System.out.println("Divisors calculated in "+time1+" seconds.");
		double time2=((double)(tac-tic))/1e9;
		System.out.println("Result calculated in "+time2+" seconds.");
	}
}
