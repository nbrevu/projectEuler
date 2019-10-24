package com.euler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.euler.common.Primes;
import com.euler.common.Primes.RabinMiller;
import com.euler.common.Timing;

public class Euler146 {
	private final static int LIMIT=150000000;
	
	private final static int[] COMBINATIONS=new int[] {10,80,130,200};
	private final static int MODULUS=210;
	private final static int[] EXCESSES=new int[] {1,3,7,9,13,27};
	private final static int MEDDLING_EXCESS=21;
	private final static int[] RABIN_MILLER_WITNESSES=new int[] {2,3,5,7,11,13,17,19,23};
	private final static int[] SMALL_PRIMES=Primes.listIntPrimes(1000).stream().skip(4).mapToInt(Integer::intValue).toArray();

	private static boolean isValid(long n,RabinMiller rabinMiller)	{
		long n2=n*n;
		for (int i:EXCESSES)	{
			long trial=n2+i;
			boolean definitelyPrime=false;
			for (int p:SMALL_PRIMES) if ((trial%p)==0) return false;
			else if (p>n)	{
				definitelyPrime=true;
				break;
			}
			if (!definitelyPrime) if (!rabinMiller.isPrime(BigInteger.valueOf(trial), RABIN_MILLER_WITNESSES)) return false;
		}
		return !rabinMiller.isPrime(BigInteger.valueOf(n2+MEDDLING_EXCESS),RABIN_MILLER_WITNESSES);
	}
	
	private static Callable<Long> getTask(long initial)	{
		return ()->	{
			RabinMiller rabinMiller=new RabinMiller();
			long result=0;
			for (long n=initial;n<LIMIT;n+=MODULUS) if (isValid(n,rabinMiller)) result+=n;
			return result;
		};
	}
	private static long solve()	{
		try	{
			ExecutorService executor=Executors.newFixedThreadPool(COMBINATIONS.length);
			List<Future<Long>> futures=new ArrayList<>(COMBINATIONS.length);
			for (int c:COMBINATIONS) futures.add(executor.submit(getTask(c)));
			long result=0;
			for (Future<Long> f:futures) result+=f.get();
			executor.shutdown();
			return result;
		}	catch (ExecutionException|InterruptedException exc)	{
			return -1l;
		}
	}

	public static void main(String[] args)	{
		Timing.time(Euler146::solve);
	}
}
