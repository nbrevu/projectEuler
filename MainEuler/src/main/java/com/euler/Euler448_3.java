package com.euler;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes.PrimeDecomposer;
import com.euler.common.Primes.StandardPrimeDecomposer;

public class Euler448_3 {
	private final static long N=9999_9999_019l;
	private final static long MOD=999_999_017l;
	
	private final static int T=17;
	
	/*
	 * 106467648
	 * Elapsed 36920.7119302 seconds.
	 * With 17 threads :|.
	 */
	private static class Euler448ThreadedCalculator	{
		private final long limit;
		private final int numThreads;
		private final PrimeDecomposer decomposer;
		private class InternalThread implements Runnable	{
			private final int id;
			private long result;
			public InternalThread(int id)	{
				this.id=id;
				result=0;
			}
			@Override
			public void run()	{
				long nextToShow=100_000_000l;
				long initialValue=1+id;
				long primaryIncr=2*id+1;
				long secondaryIncr=2*numThreads-primaryIncr;
				boolean usePrimary=false;
				for (long a=initialValue;a<=limit;a+=(usePrimary?primaryIncr:secondaryIncr),usePrimary=!usePrimary)	{
					if (a>=nextToShow)	{
						System.out.println("Thread "+id+": "+a+"...");
						nextToShow+=100_000_000l;
					}
					DivisorHolder decomp=decomposer.decompose(a);
					long totient=decomp.getTotient()%MOD;
					long factor=((a%MOD)*totient)%MOD;
					long howMany=(N/a)%MOD;
					long toAdd=(howMany*factor)%MOD;
					result+=toAdd;
					result%=MOD;
				}
			}
		}
		public Euler448ThreadedCalculator(long limit,int numThreads,int primeLimit)	{
			this.limit=limit;
			this.numThreads=numThreads;
			decomposer=new StandardPrimeDecomposer(primeLimit);
		}
		public long calculate() throws InterruptedException	{
			long result=limit;
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
			result%=MOD;
			result*=((MOD+1)/2);
			result%=MOD;
			return result;
		}
	}
	
	public static void main(String[] args) throws InterruptedException	{
		long tic=System.nanoTime();
		Euler448ThreadedCalculator calculator=new Euler448ThreadedCalculator(N,T,1_000_000_000);
		long result=calculator.calculate();
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
