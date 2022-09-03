package com.euler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import com.euler.common.EulerUtils;
import com.euler.common.Primes.RabinMiller;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.math.LongMath;
import com.google.common.primitives.Longs;

public class Euler248 {
	private final static int FACTORIAL=13;
	private final static int ORDINAL=150000;
	
	private static List<Integer> WITNESSES=Arrays.asList(2,13,23,1662803);
	
	private static long calculateNumber(Map<Long,Integer> primePowers)	{
		long result=1;
		for (Map.Entry<Long,Integer> entry:primePowers.entrySet()) result*=LongMath.pow(entry.getKey(),entry.getValue());
		return result;
	}

	private static class FactoredNumber implements Comparable<FactoredNumber>	{
		private static NavigableMap<Long,Integer> totalFactors;
		public static void setFactorMap(NavigableMap<Long,Integer> theFactors)	{
			totalFactors=theFactors;
		}
		private final NavigableMap<Long,Integer> primePowers;
		public final long number;
		public FactoredNumber(NavigableMap<Long,Integer> primePowers)	{
			this.primePowers=primePowers;
			number=calculateNumber(primePowers);
		}
		public FactoredNumber()	{
			this(Collections.emptyNavigableMap());
		}
		public List<FactoredNumber> getChildren()	{
			List<FactoredNumber> result=new ArrayList<>();
			Map.Entry<Long,Integer> lastEntry=primePowers.lastEntry();
			if (lastEntry==null) for (long key:totalFactors.keySet()) result.add(getChild(key));
			else	{
				long lastKey=lastEntry.getKey();
				if (lastEntry.getValue()<totalFactors.get(lastKey)) result.add(getChild(lastKey));
				for (Long factor:totalFactors.tailMap(lastKey,false).keySet()) result.add(getChild(factor));
			}
			return result;
		}
		private FactoredNumber getChild(Long additionalFactor)	{
			NavigableMap<Long,Integer> newPrimePowers=new TreeMap<>(primePowers);
			EulerUtils.increaseCounter(newPrimePowers,additionalFactor);
			return new FactoredNumber(newPrimePowers);
		}
		@Override
		public int hashCode()	{
			return Longs.hashCode(number);
		}
		@Override
		public boolean equals(Object o)	{
			FactoredNumber other=(FactoredNumber)o;
			return other.number==number;
		}
		@Override
		public int compareTo(FactoredNumber o) {
			return Long.signum(number-o.number);
		}
		@Override
		public String toString()	{
			return Long.toString(number);
		}
	}
	
	private static class PossibleFactor	{
		public final long number;
		private final long primeFactor;
		private final Map<Long,Integer> factors;
		public final long totientValue;
		public PossibleFactor(long primeFactor,int power)	{
			number=LongMath.pow(primeFactor,power);
			this.primeFactor=primeFactor;
			factors=new HashMap<>();
			factors.put(primeFactor-1,1);
			if (power>1) factors.put(primeFactor,power-1);
			totientValue=calculateNumber(factors);
		}
		public PossibleFactor(long primeFactor)	{
			this(primeFactor,1);
		}
		public boolean isCompatibleWith(Collection<PossibleFactor> currentFactors)	{
			for (PossibleFactor existing:currentFactors) if (primeFactor==existing.primeFactor) return false;
			return true;
		}
		@Override
		public String toString()	{
			return ""+number+" (phi="+totientValue+")";
		}
	}
	
	private static SortedMap<Long,FactoredNumber> generateDivisors(NavigableMap<Long,Integer> factors)	{
		FactoredNumber.setFactorMap(factors);
		SortedMap<Long,FactoredNumber> result=new TreeMap<>();
		Queue<FactoredNumber> pending=new LinkedList<>();
		pending.add(new FactoredNumber());
		while (!pending.isEmpty())	{
			FactoredNumber toAdd=pending.poll();
			result.put(toAdd.number,toAdd);
			pending.addAll(toAdd.getChildren());
		}
		return result;
	}
	
	private static boolean isPrime(long number,RabinMiller tester)	{
		if (number==2) return true;
		else if ((number%2)==0) return false;
		else return tester.isPrime(BigInteger.valueOf(number),WITNESSES);
	}
	
	private static Multimap<Long,PossibleFactor> getAllPossibleFactors(Collection<Long> divisors,NavigableMap<Long,Integer> factorialFactors,RabinMiller tester)	{
		List<PossibleFactor> factors=new ArrayList<>();
		for (long divisor:divisors)	{
			long candidate=1+divisor;
			if (isPrime(candidate,tester))	{
				factors.add(new PossibleFactor(candidate));
				Integer highestPower=factorialFactors.get(candidate);
				if (highestPower!=null) for (int i=2;i<=1+highestPower;++i) factors.add(new PossibleFactor(candidate,i));
			}
		}
		Multimap<Long,PossibleFactor> result=ArrayListMultimap.create();
		for (PossibleFactor factor:factors) for (long divisor:divisors) if ((divisor%factor.totientValue)==0) result.put(divisor,factor);
		return result;
	}
	
	private static NavigableSet<Long> collectValidValues(Multimap<Long,PossibleFactor> categorizedFactors,long goal,List<PossibleFactor> currentFactors)	{
		NavigableSet<Long> result=new TreeSet<>();
		if (goal==1)	{
			result.add(1l);
			for (PossibleFactor candidate:categorizedFactors.get(goal)) if (candidate.isCompatibleWith(currentFactors)) result.add(candidate.number);
			return result;
		}
		for (PossibleFactor candidate:categorizedFactors.get(goal)) if (candidate.isCompatibleWith(currentFactors))	{
			currentFactors.add(candidate);
			Set<Long> recursiveResult=collectValidValues(categorizedFactors,goal/candidate.totientValue,currentFactors);
			for (long nonMultiplied:recursiveResult) result.add(candidate.number*nonMultiplied);
			currentFactors.remove(currentFactors.size()-1);
		}
		return result;
	}
	
	private static NavigableSet<Long> collectValidValues(Multimap<Long,PossibleFactor> categorizedFactors,long goal)	{
		return collectValidValues(categorizedFactors,goal,new ArrayList<>());
	}
	
	public static void main(String[] args)	{
		NavigableMap<Long,Integer> factorialFactors=EulerUtils.getFactorialFactors(FACTORIAL);
		SortedMap<Long,FactoredNumber> allDivisors=generateDivisors(factorialFactors);
		long factorialValue=allDivisors.lastKey();
		Multimap<Long,PossibleFactor> categorizedFactors=getAllPossibleFactors(allDivisors.keySet(),factorialFactors,new RabinMiller());
		long tic=System.nanoTime();
		NavigableSet<Long> allResults=collectValidValues(categorizedFactors,factorialValue);
		long tac=System.nanoTime();
		System.out.println(allResults.size());
		double seconds=((tac-tic)/1e9);
		System.out.println("He tardado "+seconds+" segundos. ¡Atiende qué gañanazo!");
		long result=Iterables.get(allResults,ORDINAL-1);
		System.out.println(result);
		/*
		 * 182752
		 * He tardado 502.513600058 segundos. ¡Atiende qué gañanazo!
		 * 23507044290
		 */
	}
}
