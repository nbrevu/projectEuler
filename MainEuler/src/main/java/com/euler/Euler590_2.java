package com.euler;

import java.util.ArrayList;
import java.util.List;

import com.euler.common.EulerUtils;

public class Euler590_2 {
	private final static long LIMIT=50000l;
	private final static long MOD=1000000000l;
	
	private static long getMaxAvailablePower(long base,long limit)	{
		return (long)(Math.floor(Math.log((double)limit)/Math.log((double)base)));
	}
	
	private static class Combination	{
		private final long mainNumbers;
		private final long secondaryNumbers;
		private Combination(long mainNumbers,long secondaryNumbers)	{
			this.mainNumbers=mainNumbers;
			this.secondaryNumbers=secondaryNumbers;
		}
		public static Combination getCombination(long prime,long limit)	{
			long secondaryNumbers=getMaxAvailablePower(prime,limit);
			return new Combination(1l,secondaryNumbers);
		}
		public Combination combineWith(Combination other,long mod)	{
			long s1=EulerUtils.expMod(2l,secondaryNumbers,mod)-1;
			long s2=EulerUtils.expMod(2l,other.secondaryNumbers,mod)-1;
			long s12=(s1*other.mainNumbers)%mod;
			long s21=(s2*mainNumbers)%mod;
			long newMain=(2*(s12*s21)+s12+s21+1)%mod;
			long newSecondary=(secondaryNumbers*other.secondaryNumbers)%mod;
			return new Combination(newMain,newSecondary);
		}
		public long getAllCombinations(long mod)	{
			return (mainNumbers*EulerUtils.expMod(2l,secondaryNumbers,mod))%mod;
		}
		public static Combination combineAll(List<Combination> allCombinations,long mod)	{
			Combination current=allCombinations.get(0);
			List<Combination> remaining=allCombinations.subList(1,allCombinations.size());
			for (Combination comb:remaining) current=current.combineWith(comb,mod);
			return current;
		}
		// For debugging purposes...
		public static Combination getFromPrimeFactors(long[] factors,long mod)	{
			List<Combination> combs=new ArrayList<>();
			for (long f:factors)	{
				combs.add(new Combination(1l,f));
			}
			return combineAll(combs,mod);
		}
	}
	
	public static void main(String[] args)	{
		/*
		List<Long> allPrimes=Primes.listLongPrimes(LIMIT);
		List<Combination> combinations=Lists.transform(allPrimes,new Function<Long,Combination>()	{
			@Override
			public Combination apply(Long num)	{
				return Combination.getCombination(num,LIMIT);
			}
		});
		Combination finalCombination=Combination.combineAll(combinations,MOD);
		System.out.println(finalCombination.getAllCombinations(MOD));
		*/
		long[] two=new long[]{1l};
		long[] six=new long[]{1l,1l};
		long[] twelve=new long[]{2l,1l};
		long[] thirty=new long[]{1l,1l,1l};
		long[] seventyTwo=new long[]{3l,2l};
		System.out.println("2: "+(Combination.getFromPrimeFactors(two,MOD)).getAllCombinations(MOD));
		System.out.println("6: "+(Combination.getFromPrimeFactors(six,MOD)).getAllCombinations(MOD));
		System.out.println("12: "+(Combination.getFromPrimeFactors(twelve,MOD)).getAllCombinations(MOD));
		System.out.println("30: "+(Combination.getFromPrimeFactors(thirty,MOD)).getAllCombinations(MOD));
		System.out.println("72: "+(Combination.getFromPrimeFactors(seventyTwo,MOD)).getAllCombinations(MOD));
	}
}
