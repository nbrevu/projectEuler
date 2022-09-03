package com.euler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.euler.common.Primes;

public class Euler251 {
	private final static long LIMIT=110000000;

	private final static List<Long> PRIME_LIST=Primes.listLongPrimes(LIMIT);
	
	private static class Decomposition	{
		public final long normalFactor;
		public final long squaredFactor;
		public Decomposition(long normalFactor,long squaredFactor)	{
			this.normalFactor=normalFactor;
			this.squaredFactor=squaredFactor;
		}
		@Override
		public String toString()	{
			return ""+normalFactor+"·"+squaredFactor+"^2";
		}
	}
	
	private static void addFactor(Map<Long,Integer> factors,long factor)	{
		Integer prev=factors.get(factor);
		int next=1+((prev==null)?0:prev.intValue());
		factors.put(factor,next);
	}
	
	private static Map<Long,Integer> getFactors(long n)	{
		Map<Long,Integer> result=new HashMap<>();
		for (long prime:PRIME_LIST)	{
			if (prime*prime>n) break;
			while ((n%prime)==0)	{
				addFactor(result,prime);
				n/=prime;
			}
		}
		if (n>1) addFactor(result,n);
		return result;
	}
	
	private static SortedSet<Long> getAllFactors(Map<Long,Integer> decomp)	{
		SortedSet<Long> result=new TreeSet<>();
		result.add(1l);
		for (Map.Entry<Long,Integer> entry:decomp.entrySet())	{
			long factor=entry.getKey();
			int power=entry.getValue();
			long[] powers=new long[power];
			powers[0]=factor;
			for (int i=1;i<power;++i) powers[i]=factor*powers[i-1];
			Set<Long> toAdd=new TreeSet<>();
			for (long prev:result) for (long pow:powers) toAdd.add(prev*pow);
			result.addAll(toAdd);
		}
		return result;
	}
	
	private static List<Decomposition> getAllDecompositions(long n)	{
		Map<Long,Integer> factors=getFactors(n);
		long singleFactor=1l;
		Map<Long,Integer> squareFactors=new HashMap<>();
		for (Map.Entry<Long,Integer> entry:factors.entrySet())	{
			long factor=entry.getKey();
			int power=entry.getValue();
			int halfPow=power/2;
			if (halfPow>0) squareFactors.put(factor,halfPow);
			if ((power%2)==1) singleFactor*=factor;
		}
		List<Decomposition> decomps=new ArrayList<>();
		SortedSet<Long> acceptableSquares=getAllFactors(squareFactors);
		long fullSquare=acceptableSquares.last();
		for (long square:acceptableSquares)	{
			long residue=fullSquare/square;
			decomps.add(new Decomposition(singleFactor*residue*residue,square));
		}
		return decomps;
	}
	
	public static void main(String[] args)	{
		long kLimit=LIMIT/3;
		long count=0;
		for (long k=0;k<=kLimit;++k)	{
			if ((k%100000)==0) System.out.println(""+k+" => "+count+"...");
			long a=3*k+2;
			long f=2*k+1;
			long b=a*a+(f*f*f);
			long diff=LIMIT-a;
			for (Decomposition decomp:getAllDecompositions(b)) if (decomp.normalFactor+decomp.squaredFactor<diff) ++count;
		}
		System.out.println(count);
	}
}
