package com.euler.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;

public class DecompositionFinder	{
	private final int[] firstPrimes;
	private final IntObjMap<Set<Multiset<Integer>>> decompositions;
	public DecompositionFinder(int maxSieve)	{
		firstPrimes=Primes.firstPrimeSieve(maxSieve);
		decompositions=HashIntObjMaps.newMutableMap();
	}
	private Set<Multiset<Integer>> getForPrime(Integer p)	{
		return Collections.singleton(HashMultiset.create(Arrays.asList(p)));
	}
	private Set<Multiset<Integer>> calculateDecomposition(int n)	{
		int p=firstPrimes[n];
		if (p==0) return getForPrime(n);
		else return getChildren(getDecomposition(n/p),p);
	}
	public Set<Multiset<Integer>> getDecomposition(int n)	{
		return decompositions.computeIfAbsent(n,this::calculateDecomposition);
	}
	private static void getChildren(Multiset<Integer> in,int newFactor,Set<Multiset<Integer>> result)	{
		int last=-1;
		for (int n:in)	{
			if (n==last) continue;
			Multiset<Integer> copy=HashMultiset.create(in);
			copy.remove(n,1);
			copy.add(n*newFactor);
			last=n;
			result.add(copy);
		}
		Multiset<Integer> copy=HashMultiset.create(in);
		copy.add(newFactor);
		result.add(copy);
	}
	
	private static Set<Multiset<Integer>> getChildren(Set<Multiset<Integer>> in,int newFactor)	{
		Set<Multiset<Integer>> result=new HashSet<>();
		for (Multiset<Integer> multiset:in) getChildren(multiset,newFactor,result);
		return result;
	}
}