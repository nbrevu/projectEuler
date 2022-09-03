package com.euler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.euler.common.Primes.RabinMiller;

public class Euler516 {
	private final static long LIMIT=1000000000000l;
	private final static BigInteger BI_LIMIT=BigInteger.valueOf(LIMIT);
	private final static Collection<Integer> witnesses=Arrays.asList(2,3,5,7,11);
	private final static RabinMiller RM=new RabinMiller();
	private final static List<Long> HAMMING_NUMBERS=Arrays.asList(2l,3l,5l);
	private final static long MOD=1l<<32;
	
	private static void getHammingPrimes(long seed,List<Long> factors,SortedSet<Long> out)	{
		List<Long> newList=new ArrayList<>();
		Iterator<Long> factorsIterator=factors.iterator();
		long myFactor=factorsIterator.next();
		while (factorsIterator.hasNext()) newList.add(factorsIterator.next());
		for (;;)	{
			if (!newList.isEmpty()) getHammingPrimes(seed,newList,out);
			seed*=myFactor;
			if (seed>=LIMIT) return;
			if (seed>5)	{
				BigInteger bi=BigInteger.valueOf(seed+1);
				if (RM.isPrime(bi, witnesses)) out.add(seed+1);
			}
		}
	}
	
	private static SortedSet<Long> getHammingPrimes()	{
		SortedSet<Long> res=new TreeSet<>();
		getHammingPrimes(1l,HAMMING_NUMBERS,res);
		return res;
	}
	
	private static void combineOnlyOnce(long seed,List<Long> factors,int pos,SortedSet<Long> out)	{
		for (;pos<factors.size();++pos)	{
			// We have to do this to prevent overflows.
			BigInteger bi=BigInteger.valueOf(seed);
			BigInteger factor=BigInteger.valueOf(factors.get(pos));
			if (bi.multiply(factor).compareTo(BI_LIMIT)>=0)	return;
			long newNumber=seed*factors.get(pos);
			out.add(newNumber);
			combineOnlyOnce(newNumber,factors,pos+1,out);
		}
	}
	private static long combineAll(long seed,List<Long> factors)	{
		long res=0;
		List<Long> newList=new ArrayList<>();
		Iterator<Long> factorsIterator=factors.iterator();
		long myFactor=factorsIterator.next();
		while (factorsIterator.hasNext()) newList.add(factorsIterator.next());
		boolean isFirst=true;
		for (;;)	{
			if (!newList.isEmpty()) res=(res+combineAll(seed,newList))%MOD;
			if (isFirst) isFirst=false;
			else res=(res+seed)%MOD;
			seed*=myFactor;
			if (seed>LIMIT) return res;
		}
	}

	public static void main(String args[])	{
		SortedSet<Long> hammingPrimes=getHammingPrimes();
		System.out.println("Found "+hammingPrimes.size()+" primes...");
		SortedSet<Long> hammingFactors=new TreeSet<>();
		combineOnlyOnce(1l,new ArrayList<>(hammingPrimes),0,hammingFactors);
		hammingFactors.add(1l);
		System.out.println("Found "+hammingFactors.size()+" base factors...");
		long res=0; 
		for (Long hamming:hammingFactors) res=(res+hamming+combineAll(hamming,HAMMING_NUMBERS))%MOD;
		System.out.println(res);
	}
}
