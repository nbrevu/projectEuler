package com.euler;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

public class Euler590 {
	private final static long LIMIT=50000l;
	private final static long MOD=1000000000l;
	
	private static class BruteForceCalculation	{
		private final int[] primePowers;
		public BruteForceCalculation(int[] primePowers)	{
			this.primePowers=primePowers;
		}
		private static long getNumber(int[] primePowers)	{
			List<Long> primes=Primes.listLongPrimes(LIMIT);
			long num=1;
			for (int i=0;i<primePowers.length;++i)	{
				long prime=primes.get(i);
				for (int j=0;j<primePowers[i];++j) num*=prime;
			}
			return num;
		}
		private Set<Long> getDivisors(long number)	{
			// Horrible brute force.
			Set<Long> res=new HashSet<>();
			for (long n=1;n<=number;++n) if ((number%n)==0) res.add(n);
			return res;
		}
		private static long getGcd(Set<Long> in)	{
			long result=1;
			for (long n:in) result=(result*n)/EulerUtils.gcd(result,n);
			return result;
		}
		private static Collection<Set<Long>> filter(List<Set<Long>> allSets,final long goal)	{
			return Collections2.filter(allSets,new Predicate<Set<Long>>()	{
				@Override
				public boolean apply(Set<Long> in) {
					return getGcd(in)==goal;
				}
			});
		}
		public long calculate()	{
			long num=getNumber(primePowers);
			Set<Long> div=getDivisors(num);
			List<Set<Long>> allSets=EulerUtils.getExponentialSet(div,false);
			Collection<Set<Long>> acceptableLists=filter(allSets,num);
			return acceptableLists.size();
		}
	}
	
	private static class BaseCombination	{
		// This represents one prime factor.
		private final long pariahs;
		public BaseCombination(long pariahs)	{
			this.pariahs=pariahs;
		}
		public long getPariahs()	{
			return pariahs;
		}
	}
	
	private static class ComplexCombination	{
		// This represents the product of several prime factors.
		private final long pariahs;
		private final long[] highCombs;
		public ComplexCombination(BaseCombination base)	{
			pariahs=base.getPariahs();
			highCombs=new long[2];
			highCombs[1]=1l;
		}
		private ComplexCombination(long pariahs,long[] highCombs)	{
			this.pariahs=pariahs;
			this.highCombs=highCombs;
		}
		public ComplexCombination combineWith(BaseCombination additional)	{
			long newPariahs=(pariahs*additional.getPariahs())%MOD;
			long[] newHighCombs=addOne(highCombs);
			return new ComplexCombination(newPariahs,newHighCombs);
		}
		private static long[] addOne(long[] base)	{
			// TODO! This is complex. But it can be done. Maybe not in a reasonable amount of time, though.
			return null;
		}
		public long getTotal()	{
			long sum=0l;
			for (long num:highCombs) sum=(sum+num)%MOD;
			long exp=EulerUtils.expMod(2l,pariahs,MOD);
			return (sum*exp)%MOD;
		}
	}
	
	private static long getMaxAvailablePower(long base)	{
		return (long)(Math.floor(Math.log((double)LIMIT)/Math.log((double)base)));
	}
	
	private static ComplexCombination combineAll(List<BaseCombination> allCombinations)	{
		BaseCombination initial=allCombinations.get(0);
		List<BaseCombination> remaining=allCombinations.subList(1,allCombinations.size());
		ComplexCombination current=new ComplexCombination(initial);
		for (BaseCombination base:remaining) current=current.combineWith(base);
		return current;
	}
	
	public static void main(String[] args)	{
		/*
		List<Long> allPrimes=Primes.listLongPrimes(LIMIT);
		List<BaseCombination> combinations=Lists.transform(allPrimes,new Function<Long,BaseCombination>()	{
			@Override
			public BaseCombination apply(Long num)	{
				return new BaseCombination(getMaxAvailablePower(num)-1);
			}
		});
		ComplexCombination finalCombination=combineAll(combinations);
		System.out.println(finalCombination.getTotal());
		*/
		// Some examples...
		int[] six=new int[]{1,1};
		int[] twelve=new int[]{2,1};
		int[] thirty=new int[]{1,1,1};
		int[] sixtyFour=new int[]{6};
		int[] seventyTwo=new int[]{3,2};	// Mieeeerda, este caso me muestra que lo que quiero hacer no vale :(.
		int[] nineHundred=new int[]{2,2,2};
		System.out.println("6: "+(new BruteForceCalculation(six)).calculate());
		System.out.println("12: "+(new BruteForceCalculation(twelve)).calculate());
		System.out.println("30: "+(new BruteForceCalculation(thirty)).calculate());
		System.out.println("64: "+(new BruteForceCalculation(sixtyFour)).calculate());
		System.out.println("72: "+(new BruteForceCalculation(seventyTwo)).calculate());
		System.out.println("900: "+(new BruteForceCalculation(nineHundred)).calculate());
		for (int i=1;i<10;++i)	{
			int[] cosa=new int[]{i};
			System.out.println("2^"+i+": "+(new BruteForceCalculation(cosa)).calculate());
		}
	}
}
