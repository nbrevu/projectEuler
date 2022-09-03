package com.euler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;

import com.euler.common.Primes;
import com.google.common.collect.ImmutableList;

public class Euler646_6 {
	private final static int FACTORIAL_OPERAND=70;
	private final static double LOG_10=Math.log(10d);
	private final static double LOWER_BOUND=20*LOG_10;
	private final static double UPPER_BOUND=60*LOG_10;
	private final static long MOD=1_000_000_007l;
	
	private static class PrimeAndExp	{
		public final long prime;
		public final int exponent;
		public PrimeAndExp(long prime,int exponent)	{
			this.prime=prime;
			this.exponent=exponent;
		}
	}
	
	private static class WorkDistribution	{
		public final List<PrimeAndExp> firstHalf;
		public final List<PrimeAndExp> secondHalf;
		private WorkDistribution(List<PrimeAndExp> firstHalf,List<PrimeAndExp> secondHalf)	{
			this.firstHalf=firstHalf;
			this.secondHalf=secondHalf;
		}
		public static WorkDistribution getFromFactorialOperand(int op)	{
			int[] primes=Primes.listIntPrimesAsArray(op);
			List<PrimeAndExp> fullList=new ArrayList<>();
			long allDivs=1;
			for (int p:primes)	{
				PrimeAndExp element=new PrimeAndExp(p,getAppearancesInFactorial(p,op));
				fullList.add(element);
				allDivs*=element.exponent+1;
			}
			long firstDivs=1l;
			long secondDivs=allDivs;
			for (int i=0;i<fullList.size();++i)	{
				int factor=fullList.get(i).exponent+1;
				firstDivs*=factor;
				secondDivs/=factor;
				if (firstDivs>secondDivs)	{
					List<PrimeAndExp> firstHalf=ImmutableList.copyOf(fullList.subList(0,i+1));
					List<PrimeAndExp> secondHalf=ImmutableList.copyOf(fullList.subList(i+1,fullList.size()));
					return new WorkDistribution(firstHalf,secondHalf);
				}
			}
			throw new IllegalStateException("Lo que me habéis endiñao pa papear me roe las tripas.");
		}
		private static int getAppearancesInFactorial(int prime,int factorialOp) {
			int result=0;
			do	{
				factorialOp/=prime;
				result+=factorialOp;
			}	while (factorialOp>=prime);
			return result;
		}
	}
	
	private static SortedMap<Double,Long> getFactorsMap(List<PrimeAndExp> elements,long mod)	{
		SortedMap<Double,Long> result=new TreeMap<>();
		result.put(0d,1l);
		for (PrimeAndExp pe:elements)	{
			long p=pe.prime;
			double logP=Math.log(p);
			Map<Double,Long> toAdd=new HashMap<>();
			for (Map.Entry<Double,Long> entry:result.entrySet())	{
				double log=entry.getKey();
				long number=entry.getValue();
				for (int i=1;i<=pe.exponent;++i)	{
					log+=logP;
					number=mod-((number*p)%mod);
					toAdd.put(log,number);
				}
			}
			result.putAll(toAdd);
		}
		return result;
	}
	
	private static class PartialSummationRepository	{
		private final NavigableMap<Double,Long> summations;
		private final double lowerBound;
		private final double upperBound;
		private final long mod;
		public PartialSummationRepository(List<PrimeAndExp> elements,double lowerBound,double upperBound,long mod)	{
			SortedMap<Double,Long> sortedFactors=getFactorsMap(elements,mod);
			long currentSum=0;
			summations=new TreeMap<>();
			for (Map.Entry<Double,Long> entry:sortedFactors.entrySet())	{
				double log=entry.getKey();
				long number=entry.getValue();
				currentSum=(currentSum+number)%mod;
				summations.put(log,currentSum);
			}
			this.lowerBound=lowerBound;
			this.upperBound=upperBound;
			this.mod=mod;
		}
		private long getSumForFactor(double log)	{
			Map.Entry<Double,Long> lastEntry=summations.floorEntry(upperBound-log);
			if (lastEntry==null) return 0;
			Map.Entry<Double,Long> previousEntry=summations.lowerEntry(lowerBound-log);
			long minuend=lastEntry.getValue().longValue();
			if (previousEntry==null) return minuend;
			else return minuend-previousEntry.getValue().longValue();
		}
		public long combine(Map<Double,Long> otherPart)	{
			long result=0;
			for (Map.Entry<Double,Long> entry:otherPart.entrySet())	{
				double log=entry.getKey();
				long value=entry.getValue();
				long toAdd=(getSumForFactor(log)*value)%mod;
				result=(result+toAdd)%mod;
			}
			return result;
		}
	}
	
	/*
	 * Stupid me. The bounds are about 10^62, not 2^62 :|. I need BigInteger for everything.
	 * Aside from very stupid issues related to type bounds, I nailed the algorithm at the first try, and it's very fast.
	 */
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		WorkDistribution distr=WorkDistribution.getFromFactorialOperand(FACTORIAL_OPERAND);
		PartialSummationRepository partialSummations=new PartialSummationRepository(distr.firstHalf,LOWER_BOUND,UPPER_BOUND,MOD);
		Map<Double,Long> secondPart=getFactorsMap(distr.secondHalf,MOD);
		long result=partialSummations.combine(secondPart);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
