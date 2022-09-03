package com.euler;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.math.IntMath;

public class Euler565_2 {
	private static long MAX=100000000000l;
	private static long PRIME_SIEVE_LIMIT=10000000l;
	private static class Seed	{
		public final long number;
		public final long factor;
		public String toString()	{
			return "["+number+" <= "+factor+']';
		}
		public Seed(long number,long factor)	{
			this.number=number;
			this.factor=factor;
		}
		public Seed getNegativeSeed()	{
			return new Seed(number*factor,factor);
		}
		public static long getSumUpToLimit(long number)	{
			long instances=MAX/number;
			long triangularSum=instances*(instances+1)/2;
			return triangularSum*number;
		}
		public boolean isInsideBounds()	{
			return number<=MAX;
		}
		public long getSumUpToLimit()	{
			return getSumUpToLimit(number);
		}
		public static Multimap<Long,Seed> combineSeeds(Seed s1,Seed s2)	{
			Multimap<Long,Seed> result=LinkedListMultimap.create();
			if (s1.number>MAX/s2.number) return result;
			long combined=s1.number*s2.number;
			if (s1.number*s2.number>MAX) return result;
			if (notCoprime(s1.factor,s2.factor)&&!areDescendants(s1,s2))	{
				throw new RuntimeException("D'oh! This requires further attention: "+s1+", "+s2+".");
			}
			result.put(-1L, new Seed(combined,s1.factor));
			result.put(-1L, new Seed(combined,s2.factor));
			result.put(1L, new Seed(combined,s1.factor*s2.factor));
			return result;
		}
		private static boolean notCoprime(long l1,long l2)	{
			for (;;)	{
				long r=l1%l2;
				if (r==0) return (l2!=1l);
				l1=l2;
				l2=r;
			}
		}
		private static boolean areDescendants(Seed s1,Seed s2)	{
			if (s1.factor!=s2.factor) return false;
			long l1=s1.number;
			long l2=s2.number;
			if (l1>l2) return l1==l2*s1.factor;
			else return l2==l1*s1.factor;
		}
	}
	private static class PrimeHolder	{
		private final long[] primes;
		private int countPrimes(boolean[] composites)	{
			int total=0;
			for (int i=2;i<composites.length;++i) if (!composites[i]) ++total;
			return total;
		}
		public PrimeHolder(boolean[] composites)	{
			primes=new long[countPrimes(composites)];
			int index=0;
			for (int i=2;i<composites.length;++i) if (!composites[i])	{
				primes[index]=(long)i;
				++index;
			}
		}
		public boolean isPrime(long candidate)	{
			for (long p:primes)	{
				if ((candidate%p)==0) return false;
				else if (p*p>candidate) return true;
			}
			return true;
		}
	}
	private static long PRIME=2017l;
	private static boolean[] primeSieve(int limit)	{
		boolean[] composites=new boolean[limit+1];
		for (int i=4;i<=limit;i+=2) composites[i]=true;
		int sq=IntMath.sqrt(limit,RoundingMode.FLOOR);
		for (int i=3;i<=sq;i+=2) if (!composites[i]) for (int j=i*i;j<=limit;j+=i) composites[j]=true;
		return composites;
	}
	private static Collection<Seed> checkPrime(long prime)	{
		Collection<Seed> result=new ArrayList<>();
		long sum=1+prime;
		long current=prime;
		do	{
			if ((sum%PRIME)==0l) result.add(new Seed(current,prime));
			current*=prime;
			sum+=current;
		}	while (current<=MAX);
		return result;
	}
	private static Multimap<Long,Seed> combineAll(Multimap<Long,Seed> base)	{
		Multimap<Long,Seed> result=LinkedListMultimap.create();
		for (Map.Entry<Long,Seed> entry1:base.entries()) for (Map.Entry<Long,Seed> entry2:base.entries())	{
			if (entry1.getValue().number<=entry2.getValue().number) continue;
			for (Map.Entry<Long,Seed> combinedEntry:Seed.combineSeeds(entry1.getValue(), entry2.getValue()).entries())	{
				result.put(entry1.getKey()*entry2.getKey()*combinedEntry.getKey(), combinedEntry.getValue());
			}
		}
		return result;
	}
	private static long firstCandidateAfterSieve()	{
		long doub=PRIME+PRIME;
		long mod=PRIME_SIEVE_LIMIT%doub;
		return PRIME_SIEVE_LIMIT+doub-mod-1;
	}
	public static void main(String[] args)	{
		Multimap<Long,Seed> firstMap=LinkedListMultimap.create();
		firstMap.putAll(1l,checkPrime(2l));
		boolean[] composites=primeSieve((int)PRIME_SIEVE_LIMIT);
		for (int i=3;i<composites.length;i+=2) if (!composites[i]) firstMap.putAll(1l,checkPrime((long)i));
		for (Seed s:firstMap.get(1l))	{
			Seed ns=s.getNegativeSeed();
			if (ns.isInsideBounds()) firstMap.put(-1l,ns);
		}
		long total=0;
		firstMap.putAll(combineAll(firstMap));
		for (Map.Entry<Long,Seed> entry:firstMap.entries())	{
			total+=entry.getKey()*entry.getValue().getSumUpToLimit();
		}
		PrimeHolder primeHolder=new PrimeHolder(composites);
		for (long l=firstCandidateAfterSieve();l<=MAX;l+=PRIME+PRIME) if (primeHolder.isPrime(l))	{
			total+=Seed.getSumUpToLimit(l);
		}
		System.out.println(total);
	}
}
