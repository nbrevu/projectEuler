package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.euler.common.EulerUtils;
import com.euler.common.EulerUtils.Pair;
import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler418_2 {
	// Esto sigue sin funcionar :(.
	
	private final static long FACT_OPERATOR=43l;
	
	private final static double LEEWAY=0.1;
	
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
	
	private static Pair<NavigableSet<Long>,NavigableSet<Long>> getAllNumbers(Map<Long,Integer> factors,long[] limits)	{
		NavigableSet<Long> allNumbers=new TreeSet<>();
		allNumbers.add(1l);
		long ignoreIfHigherThan=limits[2];
		for (Map.Entry<Long,Integer> entry:factors.entrySet())	{
			long prime=entry.getKey();
			System.out.println(""+prime+"... ("+allNumbers.size()+")");	// Gets stuck after 29, so... no good.
			int exponent=entry.getValue();
			Set<Long> toAdd=new HashSet<>();
			long[] powers=new long[exponent];
			powers[0]=prime;
			for (int i=1;i<exponent;++i) powers[i]=powers[i-1]*prime;
			for (Long n:allNumbers) for (int i=0;i<exponent;++i)	{
				if (powers[i]>=ignoreIfHigherThan/n) break;
				long number=n*powers[i];
				toAdd.add(number);
			}
			allNumbers.addAll(toAdd);
		}
		NavigableSet<Long> lowerSet=allNumbers.subSet(limits[0],true,limits[1],true);
		NavigableSet<Long> higherSet=allNumbers.subSet(limits[1],true,limits[2],true);
		// Freeing memory semi-manually...
		Pair<NavigableSet<Long>,NavigableSet<Long>> result=new Pair<>(new TreeSet<>(lowerSet),new TreeSet<>(higherSet));
		allNumbers.clear();
		return result;
	}
	
	private static long[] getLimits(long factOperator,double leeway)	{
		double fact=1.0;
		for (long i=2;i<=factOperator;++i) fact*=i;
		double cubeRoot=Math.pow(fact,1.0/3.0);
		long minLimit=(long)Math.floor(cubeRoot*(1-leeway));
		long cubeRootL=(long)Math.round(cubeRoot);
		long maxLimit=(long)Math.ceil(cubeRootL*(1+leeway));
		return new long[]{minLimit,cubeRootL,maxLimit};
	}
	
	private static class FactoredNumber implements Comparable<FactoredNumber>	{
		private final static List<Long> PRIMES=Primes.listLongPrimes(FACT_OPERATOR);
		private final long number;
		private final Map<Long,Integer> factors;
		public FactoredNumber(long number)	{
			this.number=number;
			factors=factor(number);
		}
		private static Map<Long,Integer> factor(long in)	{
			Map<Long,Integer> result=new HashMap<>();
			addFactors(result,in,PRIMES);
			return result;
		}
		public long getNumber()	{
			return number;
		}
		public Map<Long,Integer> getFactors()	{
			return factors;
		}
		@Override
		public boolean equals(Object other)	{
			FactoredNumber fOther=(FactoredNumber)other;
			return number==fOther.number;
		}
		@Override
		public int hashCode()	{
			return Long.hashCode(number);
		}
		@Override
		public int compareTo(FactoredNumber other)	{
			return Long.compare(number,other.number);
		}
	}
	
	private static List<FactoredNumber> factorAll(Collection<Long> numbers)	{
		List<FactoredNumber> result=new ArrayList<>();
		for (long l:numbers) result.add(new FactoredNumber(l));
		return result;
	}
	
	private static void remove(Map<Long,Integer> minuend,Map<Long,Integer> substraend)	{
		for (Map.Entry<Long,Integer> entry:substraend.entrySet()) EulerUtils.increaseCounter(minuend,entry.getKey(),-entry.getValue());
	}
	
	private static Map<Long,Integer> getDifference(Map<Long,Integer> base,Map<Long,Integer> n1)	{
		Map<Long,Integer> result=new HashMap<>(base);
		remove(result,n1);
		return result;
	}
	
	private static long build(Map<Long,Integer> factors)	{
		long result=1;
		for (Map.Entry<Long,Integer> factor:factors.entrySet())	{
			int exponent=factor.getValue();
			if (exponent<0) return -1;	// Nicht möglich!!!!!
			else if (exponent>0) result*=LongMath.pow(factor.getKey(),exponent);
		}
		return result;
	}
	
	private static double getQuotient(long a,long b,long c)	{
		long min=Math.min(a,Math.min(b,c));
		long max=Math.max(a,Math.max(b,c));
		return ((double)max)/((double)min);
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		Map<Long,Integer> factors=getFactorsOfFactorial(FACT_OPERATOR);
		long[] limits=getLimits(FACT_OPERATOR,LEEWAY);
		System.out.println(Arrays.toString(limits));
		Pair<NavigableSet<Long>,NavigableSet<Long>> allFactors=getAllNumbers(factors,limits);
		List<FactoredNumber> l1=factorAll(allFactors.first);
		SortedSet<FactoredNumber> l2=new TreeSet<>(factorAll(allFactors.second));
		System.out.println(""+l1.size()+" x "+l2.size());
		long sum=0;
		double quotient=Double.MAX_VALUE;
		for (FactoredNumber n1:l1)	{
			Map<Long,Integer> diff1=getDifference(factors,n1.getFactors());
			for (FactoredNumber n2:l2)	{
				Map<Long,Integer> diff2=getDifference(diff1,n2.getFactors());
				long thirdNumber=build(diff2);
				if (thirdNumber==-1) continue;
				double newQuotient=getQuotient(n1.getNumber(),n2.getNumber(),thirdNumber);
				if (newQuotient<quotient)	{
					quotient=newQuotient;
					sum=n1.getNumber()+n2.getNumber()+thirdNumber;
				}
				break;	// If we got this far, no need to keep iterating over l2 because the next number will be higher.
			}
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)/1e9;
		System.out.println(sum);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
