package com.euler;

import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collection;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler616 {
	private final static long LIMIT=LongMath.pow(10,12);
	
	private final static Collection<Long> FORBIDDEN_NUMBERS=Arrays.asList(16l);
	
	private static class PowerAdder	{
		public final int maxBase;
		private final boolean[] composites;
		private final double maxLog;
		
		public PowerAdder(long limit)	{
			maxBase=(int)LongMath.sqrt(limit,RoundingMode.DOWN);
			composites=Primes.sieve(maxBase);
			maxLog=Math.log(1+limit);	// Adding 1 to prevent floating point equality errors.
		}
		
		public void addAll(long base,Set<Long> target)	{
			double baseLog=Math.log(base);
			boolean isComposite=composites[(int)base];
			for (int i=2;;++i)	{
				double log=baseLog*i;
				if (log>=maxLog) break;
				if (isComposite||composites[i]) target.add(LongMath.pow(base,i));
			}
		}
	}
	
	private static NavigableSet<Long> getCreativeNumbers(long limit)	{
		NavigableSet<Long> result=new TreeSet<>();
		PowerAdder adder=new PowerAdder(limit);
		for (int i=2;i<=adder.maxBase;++i) adder.addAll(i,result);
		result.removeAll(FORBIDDEN_NUMBERS);
		return result;
	}
	
	private static long getSum(Collection<Long> collection)	{
		return collection.stream().mapToLong(i->i).sum();
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		NavigableSet<Long> creative=getCreativeNumbers(LIMIT);
		long result=getSum(creative);
		long tac=System.nanoTime();
		System.out.println(result);
		double seconds=(tac-tic)*1e-9;
		System.out.println("Time: "+seconds+" seconds.");
	}
}
