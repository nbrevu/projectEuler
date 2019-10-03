package com.euler;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.euler.common.Primes;
import com.euler.common.Timing;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Euler88 {
	private final static int LIMIT=12000;
	
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
	
	private static int getCandidate(Multiset<Integer> decomp,int total)	{
		int result=decomp.size()+total;
		for (int n:decomp) result-=n;
		/*
		 * Theoretically we should consider the case that total<sum. This actually never happens since we have at least two factors and
		 * they are greater than 1.
		 */
		return result;
	}
	
	private static long solve()	{
		boolean[] found=new boolean[LIMIT+1];
		found[0]=true;
		found[1]=true;
		int pending=LIMIT-1;
		IntObjMap<Set<Multiset<Integer>>> divisors=HashIntObjMaps.newMutableMap();
		divisors.put(2,Collections.singleton(HashMultiset.create(Arrays.asList(2))));
		divisors.put(3,Collections.singleton(HashMultiset.create(Arrays.asList(3))));
		int[] firstPrimes=Primes.firstPrimeSieve(2*LIMIT);
		IntSet result=HashIntSets.newMutableSet();
		for (int i=4;pending>0;++i)	{
			int prime=firstPrimes[i];
			if (prime==0) divisors.put(i,Collections.singleton(HashMultiset.create(Arrays.asList(i))));
			else	{
				Set<Multiset<Integer>> thisDivisors=getChildren(divisors.get(i/prime),prime);
				divisors.put(i,thisDivisors);
				for (Multiset<Integer> multiset:thisDivisors) if (multiset.size()>1)	{
					int candidate=getCandidate(multiset,i);
					if ((candidate<2)||(candidate>LIMIT)||found[candidate]) continue;
					found[candidate]=true;
					--pending;
					result.add(i);
				}
			}
		}
		long sum=0;
		for (int val:result) sum+=val;
		return sum;
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler88::solve);
	}
}
