package com.euler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;

import com.euler.common.Primes;
import com.google.common.collect.ImmutableList;

public class Euler646_5 {
	private final static int FACTORIAL_OPERAND=70;
	private final static BigInteger LOWER_BOUND=BigInteger.TEN.pow(20);
	private final static BigInteger UPPER_BOUND=BigInteger.TEN.pow(60);
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
			while (factorialOp>0)	{
				factorialOp/=prime;
				result+=factorialOp;
			}
			return result;
		}
	}
	
	// Values set to FALSE should be negated.
	private static SortedMap<BigInteger,Boolean> getSortedFactorsMap(List<PrimeAndExp> elements)	{
		SortedMap<BigInteger,Boolean> result=new TreeMap<>();
		result.put(BigInteger.ONE,Boolean.TRUE);
		for (PrimeAndExp pe:elements)	{
			BigInteger p=BigInteger.valueOf(pe.prime);
			Map<BigInteger,Boolean> toAdd=new HashMap<>();
			for (Map.Entry<BigInteger,Boolean> entry:result.entrySet())	{
				BigInteger number=entry.getKey();
				Boolean sign=entry.getValue();
				for (int i=1;i<=pe.exponent;++i)	{
					number=number.multiply(p);
					sign=!sign;
					toAdd.put(number,sign);
				}
			}
			result.putAll(toAdd);
		}
		return result;
	}
	
	private static class PartialSummationRepository	{
		private final BigInteger lowerBound;
		private final BigInteger upperBound;
		private final NavigableMap<BigInteger,BigInteger> summations;
		public PartialSummationRepository(List<PrimeAndExp> elements,BigInteger lowerBound,BigInteger upperBound)	{
			SortedMap<BigInteger,Boolean> sortedFactors=getSortedFactorsMap(elements);
			BigInteger currentSum=BigInteger.ZERO;
			summations=new TreeMap<>();
			for (Map.Entry<BigInteger,Boolean> entry:sortedFactors.entrySet())	{
				BigInteger num=entry.getKey();
				if (entry.getValue().booleanValue()) currentSum=currentSum.add(num);
				else currentSum=currentSum.subtract(num);
				summations.put(num,currentSum);
			}
			this.lowerBound=lowerBound;
			this.upperBound=upperBound;
		}
		public BigInteger getSumForFactor(BigInteger n)	{
			BigInteger[] lowDivision=lowerBound.divideAndRemainder(n);
			Map.Entry<BigInteger,BigInteger> previousEntry=(lowDivision[1].signum()==0)?summations.lowerEntry(lowDivision[0]):summations.floorEntry(lowDivision[0]);
			BigInteger highDivision=upperBound.divide(n);
			Map.Entry<BigInteger,BigInteger> lastEntry=summations.floorEntry(highDivision);
			BigInteger subtrahend=(previousEntry==null)?BigInteger.ZERO:previousEntry.getValue();
			BigInteger minuend=(lastEntry==null)?BigInteger.ZERO:lastEntry.getValue();
			return minuend.subtract(subtrahend);
		}
		public BigInteger combine(Map<BigInteger,Boolean> otherPart)	{
			BigInteger result=BigInteger.ZERO;
			for (Map.Entry<BigInteger,Boolean> entry:otherPart.entrySet())	{
				BigInteger n=entry.getKey();
				boolean isPositive=entry.getValue().booleanValue();
				BigInteger toAdd=getSumForFactor(n).multiply(n);
				if (isPositive) result=result.add(toAdd);
				else result=result.subtract(toAdd);
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
		PartialSummationRepository partialSummations=new PartialSummationRepository(distr.firstHalf,LOWER_BOUND,UPPER_BOUND);
		Map<BigInteger,Boolean> secondPart=getSortedFactorsMap(distr.secondHalf);
		BigInteger bigResult=partialSummations.combine(secondPart);
		long result=bigResult.mod(BigInteger.valueOf(MOD)).longValueExact();
		if (result<0) result=MOD+result;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
