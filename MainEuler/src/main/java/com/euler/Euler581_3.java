package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

public class Euler581_3 {
	// I'm pretty sure that this doesn't give me the proper solution. If I could determine a limit...
	private final static long LIMIT=10000000000000000l;
	private final static List<Long> PRIMES=Arrays.asList(2l,3l,5l,7l,11l,13l,17l,19l,23l,29l,31l,37l,41l,47l);
	
	private static class PrimeCombination	{
		private final List<Long> primes;
		public final int index;
		private final Set<Long> productCombinations;
		private static Set<Long> ONE=Collections.singleton(1l);
		private PrimeCombination(List<Long> primes,int index,long limit)	{
			this.primes=primes;
			this.index=index;
			productCombinations=calculateProductCombinations(primes,limit);
		}
		private static Set<Long> addPrime(Set<Long> previous,long newPrime,long limit)	{
			Set<Long> result=new HashSet<>();
			for (long l:previous)	{
				do	{
					result.add(l);
					l*=newPrime;
				}	while (l<=limit);
			}
			return result;
		}
		private static Set<Long> calculateProductCombinations(List<Long> primes,long limit)	{
			Set<Long> current=ONE;
			for (long p:primes) current=addPrime(current,p,limit);
			return current;
		}
		public boolean isCompatible(PrimeCombination other)	{
			return (index&other.index)==0;
		}
		public Set<Long> getSuitableNumbers(PrimeCombination other)	{
			if (productCombinations.size()>other.productCombinations.size()) return other.getSuitableNumbers(this);
			Set<Long> result=new HashSet<>();
			for (long l:productCombinations)	{
				if (other.productCombinations.contains(l-1)) result.add(l-1);
				if (other.productCombinations.contains(l+1)) result.add(l);
			}
			return result;
		}
		private static PrimeCombination getPrimeCombination(List<Long> primes,int index,long limit)	{
			System.out.println("Estoy con el "+index+".");
			List<Long> effectiveList=new ArrayList<>();
			for (int i=0;i<primes.size();++i)	{
				int mask=1<<i;
				if ((index&mask)!=0) effectiveList.add(primes.get(i));
				else if (index<mask) break;
			}
			return new PrimeCombination(effectiveList,index,limit);
		}
		public static PrimeCombination[] getAllPrimeCombinations(List<Long> primes,long limit)	{
			int N=primes.size();
			int total=1<<N;
			PrimeCombination[] result=new PrimeCombination[total];
			for (int i=0;i<total;++i) result[i]=getPrimeCombination(primes,i,limit);
			return result;
		}
	}
	
	public static void main(String[] args)	{
		System.out.println("CO MEN ZAMOS");
		PrimeCombination[] allCombinations=PrimeCombination.getAllPrimeCombinations(PRIMES,LIMIT);
		System.out.println("Ya está listo tó lo gordo.");
		int N=allCombinations.length;
		NavigableSet<Long> suitableNumbers=new TreeSet<>();
		for (int i=0;i<N-1;++i) for (int j=i+1;j<N;++j)	{
			System.out.println("Combinando: "+i+"+"+j+".");
			PrimeCombination c1=allCombinations[i];
			PrimeCombination c2=allCombinations[j];
			if (c1.isCompatible(c2)) suitableNumbers.addAll(c1.getSuitableNumbers(c2));
		}
		System.out.println("Combinaciones listas.");
		System.out.println("Lo más gordaco que me he encontrado es "+suitableNumbers.last());
		long sum=0l;
		for (long l:suitableNumbers) sum+=l;
		System.out.println(sum);
	}
}
