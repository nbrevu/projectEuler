package com.euler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;

import com.euler.common.Primes;
import com.google.common.collect.ImmutableList;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongObjCursor;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;

public class Euler646_4 {
	private final static int FACTORIAL_OPERAND=70;
	private final static long LOWER_BOUND=LongMath.pow(10l,20);
	private final static long UPPER_BOUND=LongMath.pow(10l,60);
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
	private static LongObjMap<Boolean> getFactorsMap(List<PrimeAndExp> elements)	{
		int howMany=1;
		for (PrimeAndExp pe:elements) howMany*=pe.exponent+1;
		LongObjMap<Boolean> result=HashLongObjMaps.newMutableMap(howMany);
		result.put(1l,Boolean.TRUE);
		for (PrimeAndExp pe:elements)	{
			LongObjMap<Boolean> toAdd=HashLongObjMaps.newMutableMap();
			for (LongObjCursor<Boolean> cursor=result.cursor();cursor.moveNext();)	{
				long number=cursor.key();
				Boolean sign=cursor.value();
				for (int i=1;i<=pe.exponent;++i)	{
					number*=pe.prime;
					sign=!sign;
					toAdd.put(number,sign);
				}
			}
			result.putAll(toAdd);
		}
		return result;
	}
	
	private static SortedMap<Long,Boolean> getSortedFactorsMap(List<PrimeAndExp> elements)	{
		SortedMap<Long,Boolean> result=new TreeMap<>();
		result.put(1l,Boolean.TRUE);
		for (PrimeAndExp pe:elements)	{
			LongObjMap<Boolean> toAdd=HashLongObjMaps.newMutableMap();
			for (Map.Entry<Long,Boolean> entry:result.entrySet())	{
				long number=entry.getKey();
				Boolean sign=entry.getValue();
				for (int i=1;i<=pe.exponent;++i)	{
					number*=pe.prime;
					sign=!sign;
					toAdd.put(number,sign);
				}
			}
			result.putAll(toAdd);
		}
		return result;
	}
	
	private static class PartialSummationRepository	{
		private final long lowerBound;
		private final long upperBound;
		private final NavigableMap<Long,Long> summations;
		private final long mod;
		public PartialSummationRepository(List<PrimeAndExp> elements,long lowerBound,long upperBound,long mod)	{
			SortedMap<Long,Boolean> sortedFactors=getSortedFactorsMap(elements);
			long currentSum=0;
			summations=new TreeMap<>();
			for (Map.Entry<Long,Boolean> entry:sortedFactors.entrySet())	{
				long num=entry.getKey().longValue();
				if (entry.getValue().booleanValue()) currentSum+=num;
				else currentSum+=mod-num;
				currentSum%=mod;
				summations.put(num,currentSum);
			}
			this.lowerBound=lowerBound;
			this.upperBound=upperBound;
			this.mod=mod;
		}
		public long getSumForFactor(long n)	{
			Long low=lowerBound/n;
			Map.Entry<Long,Long> previousEntry=((lowerBound%n)==0)?summations.lowerEntry(low):summations.floorEntry(low);
			Long high=upperBound/n;
			if ((lowerBound%n)==0) System.out.println("Uy, cuidadín con el lower.");
			if ((upperBound%n)==0) System.out.println("Uy, cuidadín con el higher.");
			Map.Entry<Long,Long> lastEntry=summations.floorEntry(high);
			long subtrahend=(previousEntry==null)?0:previousEntry.getValue().longValue();
			long minuend=(lastEntry==null)?0:lastEntry.getValue().longValue();
			return (minuend+mod-subtrahend)%mod;
		}
		public long combine(LongObjMap<Boolean> otherPart)	{
			long result=0;
			for (LongObjCursor<Boolean> cursor=otherPart.cursor();cursor.moveNext();)	{
				long n=cursor.key();
				boolean isPositive=cursor.value().booleanValue();
				long toAdd=((n%mod)*getSumForFactor(n))%mod;
				result+=isPositive?toAdd:mod-toAdd;
				result%=mod;
			}
			return result;
		}
	}
	
	// The sample result for 30! is right, but there must be an overflow or something and the result is wrong :(.
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		WorkDistribution distr=WorkDistribution.getFromFactorialOperand(FACTORIAL_OPERAND);
		PartialSummationRepository partialSummations=new PartialSummationRepository(distr.firstHalf,LOWER_BOUND,UPPER_BOUND,MOD);
		LongObjMap<Boolean> secondPart=getFactorsMap(distr.secondHalf);
		long result=partialSummations.combine(secondPart);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
