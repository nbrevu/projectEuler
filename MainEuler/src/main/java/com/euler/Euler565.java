package com.euler;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.math.IntMath;

public class Euler565 {
	private static long MAX=100000000l;
	private static long PRIME_SIEVE_LIMIT=1000000l;
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
//	private static class RabinMillerTest	{
//		private static List<BigInteger> witnesses=Arrays.asList(BigInteger.valueOf(2l),BigInteger.valueOf(13l),BigInteger.valueOf(23l),BigInteger.valueOf(1662803l));
//		private final BigInteger n;
//		private final long n_1;
//		private final int r;
//		private final long d;
//		public RabinMillerTest(long l)	{
//			n=BigInteger.valueOf(l);
//			--l;
//			n_1=l;
//			int rr=0;
//			do	{
//				++rr;
//				l/=2;
//			}	while ((l%2)==0);
//			r=rr;
//			d=l;
//		}
//		public boolean fullTest()	{
//			for (BigInteger a:witnesses)	{
//				long x=expMod(a,d,n);
//				if ((x==1)||(x==n_1)) continue;
//				boolean keepGoing=false;
//				for (int i=1;i<r;++i)	{
//					x=expMod(BigInteger.valueOf(x),2l,n);
//					if (x==1) return false;
//					if (x==n_1)	{
//						keepGoing=true;
//						break;
//					}
//				}
//				if (!keepGoing) return false;
//			}
//			return true;
//		}
//		private static long expMod(BigInteger base,long exponent,BigInteger mod)	{
//			BigInteger current=base;
//			BigInteger prod=BigInteger.ONE;
//			while (exponent>0)	{
//				if ((exponent%2l)==0l) prod=prod.multiply(current).mod(mod);
//				current=current.multiply(current);
//				exponent/=2;
//			}
//			return prod.longValue();
//		}
//	}
	public static class Primes	{
		private final long[] primes;
		private int countPrimes(boolean[] composites)	{
			int count=0;
			for (int i=2;i<composites.length;++i) if (!composites[i]) ++count;
			return count;
		}
		public Primes(boolean[] composites)	{
			int count=countPrimes(composites);
			primes=new long[count];
			int index=0;
			for (int i=2;i<composites.length;++i) if (!composites[i])	{
				primes[index]=(long)i;
				++index;
			}
		}
		public boolean isPrime(long l)	{
			for (long p:primes) if ((l%p)==0) return false;
			else if (p*p>=l) return true;
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
		System.out.println("1...");
		boolean[] composites=primeSieve((int)PRIME_SIEVE_LIMIT);
		Primes primeHolder=new Primes(composites);
		System.out.println("2...");
		firstMap.putAll(1l,checkPrime(2l));
		for (int i=3;i<composites.length;i+=2) if (!composites[i]) firstMap.putAll(1l,checkPrime((long)i));
		System.out.println("3...");
		for (Seed s:firstMap.get(1l))	{
			Seed ns=s.getNegativeSeed();
			if (ns.isInsideBounds()) firstMap.put(-1l,ns);
		}
		System.out.println("4...");;
		long total=0;
		firstMap.putAll(combineAll(firstMap));
		for (Map.Entry<Long,Seed> entry:firstMap.entries())	{
			total+=entry.getKey()*entry.getValue().getSumUpToLimit();
		}
		System.out.println("5...");
		for (long l=firstCandidateAfterSieve();l<=MAX;l+=PRIME+PRIME) if (primeHolder.isPrime(l))	{
			total+=Seed.getSumUpToLimit(l);
		}
		System.out.println(total);
	}
}
