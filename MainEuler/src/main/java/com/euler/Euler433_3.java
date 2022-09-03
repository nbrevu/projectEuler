package com.euler;

import java.util.BitSet;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.set.LongSet;

public class Euler433_3 {
	private final static int LIMIT=5_000_000;
	
	private final static int N=17;
	
	private static int countSteps(int a,int b)	{
		int result=0;
		do	{
			int r=a%b;
			a=b;
			b=r;
			++result;
		}	while (b!=0);
		return result;
	}
	
	private static class Euler433ThreadedCalculator	{
		private final long limit;
		private final int numThreads;
		private final int[] lastPrimes;
		private class InternalThread implements Runnable	{
			private final int id;
			private long result;
			public InternalThread(int id)	{
				this.id=id;
				result=0;
			}
			@Override
			public void run()	{
				int initialValue=2+id;
				int primaryIncr=2*id+1;
				int secondaryIncr=2*numThreads-primaryIncr;
				boolean usePrimary=false;
				BitSet validNums=new BitSet((int)limit);
				for (int a=initialValue;a<=limit;a+=(usePrimary?primaryIncr:secondaryIncr),usePrimary=!usePrimary)	{
					long tmpResult=0;
					validNums.clear();
					validNums.set(1,a);
					if (lastPrimes[a]!=0)	{
						LongSet primes=DivisorHolder.getFromFirstPrimes(a,lastPrimes).getFactorMap().keySet();
						for (LongCursor cursor=primes.cursor();cursor.moveNext();)	{
							int p=(int)cursor.elem();
							for (int j=p;j<a;j+=p) validNums.clear(j);
						}
					}
					long multiplier=limit/a;
					for (int b=validNums.nextSetBit(0);b>=0;b=validNums.nextSetBit(b+1)) tmpResult+=countSteps(a,b);
					result+=2*multiplier*tmpResult;
				}
			}
		}
		public Euler433ThreadedCalculator(long limit,int numThreads)	{
			this.limit=limit;
			this.numThreads=numThreads;
			lastPrimes=Primes.lastPrimeSieve((int)limit);
		}
		public long calculate() throws InterruptedException	{
			long result=(limit*(limit+1))/2;
			Thread[] threads=new Thread[numThreads];
			InternalThread[] data=new InternalThread[numThreads];
			for (int i=0;i<numThreads;++i)	{
				data[i]=new InternalThread(i);
				threads[i]=new Thread(data[i]);
				threads[i].start();
			}
			for (int i=0;i<numThreads;++i)	{
				threads[i].join();
				result+=data[i].result;
			}
			return result;
		}
	}
	
	/*
	 * 326624372659664
	 * Elapsed 18126.305997800002 seconds.
	 * And that's with 17 threads :|.
	 */
	public static void main(String[] args) throws InterruptedException	{
		long tic=System.nanoTime();
		Euler433ThreadedCalculator calculator=new Euler433ThreadedCalculator(LIMIT,N);
		long result=calculator.calculate();
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
