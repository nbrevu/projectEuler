package com.euler;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler642_2 {
	private final static long LIMIT=201820182018l;
	
	/*-
	private static class SmoothNumberGenerator	{
		private final List<Long> primes;
		public SmoothNumberGenerator(List<Long> primes)	{
			this.primes=primes;
		}
		// There is an absurd amount of boxing/unboxing going on here :(.
		public NavigableSet<Long> getSmoothNumbers(long limit)	{
			NavigableSet<Long> result=new TreeSet<>();
			result.add(1l);
			for (long p:primes)	advanceSmoothNumbers(result,p,limit);
			return result;
		}
	}
	*/
	
	private static void advanceSmoothNumbers(NavigableSet<Long> smooth,long newPrime,long limit)	{
		List<Long> toAdd=new ArrayList<>();
		for (Long v:smooth)	{
			long in=v.longValue();
			for (;;)	{
				in*=newPrime;
				if (in>limit) break;
				else toAdd.add(in);
			}
		}
		smooth.addAll(toAdd);
	}

	/*
	 * First phase result (TBH there's no need to calculate it more than once:
	 * 65387904274
	 * Elapsed 4609.786664743 seconds.
	 */
	public static long getFirstPhase(long limit)	{
		long sq=LongMath.sqrt(limit,RoundingMode.DOWN);
		NavigableSet<Long> smooth=new TreeSet<>();
		smooth.add(1l);
		List<Long> primes=Primes.listLongPrimes(sq);
		long result=LongMath.log2(limit,RoundingMode.DOWN);
		for (int i=1;i<primes.size();++i)	{
			long p=primes.get(i);
			System.out.println(p+"...");
			long prev=primes.get(i-1);
			long newLimit=limit/p;
			smooth=smooth.headSet(newLimit,true);
			if ((i%10)==0) smooth=new TreeSet<>(smooth);
			advanceSmoothNumbers(smooth,prev,newLimit);
			for (long pp=p;pp<=limit;pp*=p) result+=smooth.headSet(limit/pp,true).size();
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		System.out.println(getFirstPhase(LIMIT));
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
