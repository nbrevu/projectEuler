package com.euler;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;

public class Euler418 {
	// This approach doesn't work :(. Too much memory consumption.
	private final static long FACT_OPERATOR=43l;
	
	private static Map<Long,Integer> getFactorsOfFactorial(long in)	{
		List<Long> primes=Primes.listLongPrimes(in);
		Map<Long,Integer> result=new TreeMap<>();
		for (long i=2;i<=in;++i) addFactors(result,i,primes);
		return result;
	}
	
	private static void addFactors(Map<Long,Integer> result,long in,List<Long> primes)	{
		for (long p:primes)	{
			if (in==1) return;
			else if (p*p>in)	{
				EulerUtils.increaseCounter(result,in);
				return;
			}	else while ((in%p)==0)	{
				EulerUtils.increaseCounter(result,p);
				in/=p;
			}
		}
	}
	
	private static class FactoredNumber implements Comparable<FactoredNumber>	{
		private final double numberLog;
		private final Map<Long,Integer> factors;
		public FactoredNumber(Map<Long,Integer> factors)	{
			this.factors=factors;
			numberLog=generate(factors);
		}
		private static double generate(Map<Long,Integer> factors)	{
			// This assumes that no individual factor is greater than Long.MAX_VALUE. This is, at least, true for this problem.
			double result=0.0;
			for (Map.Entry<Long,Integer> entry:factors.entrySet())	{
				double log=Math.log(entry.getKey());
				result+=entry.getValue()*log;
			}
			return result;
		}
		public Map<Long,Integer> getFactors()	{
			// Returns a mutable object, to be freely modified by caller.
			return new HashMap<>(factors);
		}
		@Override
		public boolean equals(Object other)	{
			FactoredNumber fOther=(FactoredNumber)other;
			return numberLog==fOther.numberLog;
		}
		@Override
		public int hashCode()	{
			return Double.hashCode(numberLog);
		}
		@Override
		public int compareTo(FactoredNumber other)	{
			return Double.compare(numberLog,other.numberLog);
		}
	}
	
	private static NavigableSet<FactoredNumber> getAllNumbers(Map<Long,Integer> factors)	{
		NavigableSet<FactoredNumber> result=new TreeSet<>();
		result.add(new FactoredNumber(Collections.emptyMap()));
		for (Map.Entry<Long,Integer> entry:factors.entrySet())	{
			Long prime=entry.getKey();	// No need to unbox, no operation will be done to this number here.
			System.out.println(""+prime+"... ("+result.size()+")");	// Gets stuck after 29, so... no good.
			int exponent=entry.getValue();
			Set<FactoredNumber> toAdd=new HashSet<>();
			for (FactoredNumber n:result) for (int i=1;i<=exponent;++i)	{
				Map<Long,Integer> myFactors=n.getFactors();
				myFactors.put(prime,i);
				toAdd.add(new FactoredNumber(myFactors));
			}
			result.addAll(toAdd);
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		Map<Long,Integer> factors=getFactorsOfFactorial(FACT_OPERATOR);
		NavigableSet<FactoredNumber> allFactors=getAllNumbers(factors);
		long tac=System.nanoTime();
		System.out.println(allFactors.size());	// 516096000.
		double seconds=(tac-tic)/1e9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
