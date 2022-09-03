package com.euler.experiments;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.LongSupplier;
import java.util.stream.LongStream;

import com.euler.common.Primes;
import com.google.common.math.LongMath;

// ZUTUN! Con truquitos, esto podría ir mucho más rápido.
public class Iannucci {
	private static class CustomSieve implements LongSupplier	{
		private final long[] subValues;
		private final long modulus;
		private int currentSubValue;
		private long currentValue;
		
		private CustomSieve(long[] subValues,long modulus)	{
			this.subValues=subValues;
			this.modulus=modulus;
			// Skip 1!
			getAsLong();
		}
		
		public static CustomSieve getFromPrimeList(List<Long> primes)	{
			if (primes.isEmpty()) return new CustomSieve(new long[] {0l},1l);
			long product=primes.stream().mapToLong(Long::longValue).reduce(1l,(long a,long b)->a*b);
			if (product>Integer.MAX_VALUE) throw new IllegalArgumentException();
			boolean[] sieve=new boolean[(int)product];
			sieve[0]=true;
			for (long p:primes) for (long q=p;q<product;q+=p) sieve[(int)q]=true;
			List<Long> longValues=new ArrayList<>();
			for (int i=0;i<(int)product;++i) if (!sieve[i]) longValues.add(Long.valueOf(i));
			long[] subValues=longValues.stream().mapToLong(Long::longValue).toArray();
			return new CustomSieve(subValues,product);
		}
		
		@Override
		public long getAsLong() {
			long result=subValues[currentSubValue]+currentValue;
			if (currentSubValue>=subValues.length-1)	{
				currentSubValue=0;
				currentValue+=modulus;
			}	else ++currentSubValue;
			return result;
		}
	}
	
	private final static long MAX_PRIME=10000;
	private final static double LOG2=Math.log(2.0);
	
	private static List<Long> getU(List<Long> primes, int n)	{
		double log=0.0;
		for (int k=n;k<primes.size();++k) {
			long p=primes.get(k);
			log+=Math.log(p);
			log-=Math.log(p-1);
			if (log>LOG2) return primes.subList(n,k+1);
		}
		throw new NoSuchElementException("T'has pasao, macho t'has pasao.");
	}
	
	private static Map<Long,Integer> decomposeAndAdd(long m,long[] primes,List<Long> u)	{
		Map<Long,Integer> result=new HashMap<>();
		for (long p:primes)	{
			if (m==1) break;
			if (p*p>m)	{
				result.put(m,1);
				break;
			}
			int d=0;
			while ((m%p)==0)	{
				m/=p;
				++d;
			}
			if (d>0) result.put(p,d);
		}
		for (Long p:u) result.compute(p,(Long k,Integer v)->1+((v==null)?0:v.intValue()));
		return result;
	}
	
	private static double sigmaQuotLog(Map<Long,Integer> factors)	{
		return factors.entrySet().stream().mapToDouble((Map.Entry<Long,Integer> entry)->{
			long prime=entry.getKey();
			int power=entry.getValue();
			long bigPower=LongMath.pow(prime,power+1);
			try	{
				long sigma=(bigPower-1)/(prime-1);
				return Math.log(sigma)-power*Math.log(prime);
			}	catch (ArithmeticException exc)	{
				throw exc;
			}
		}).sum();
	}
	
	private static BigInteger toBigInteger(Map<Long,Integer> factors)	{
		BigInteger result=BigInteger.ONE;
		for (Map.Entry<Long,Integer> factor:factors.entrySet()) result=result.multiply(BigInteger.valueOf(LongMath.pow(factor.getKey(),factor.getValue())));
		return result;
	}
	
	private static BigInteger getFirstAbundantWithoutNPrimes(int n)	{
		List<Long> primes=Primes.listLongPrimes(MAX_PRIME);
		List<Long> u=getU(primes,n);
		LongStream sieved=LongStream.generate(CustomSieve.getFromPrimeList(primes.subList(0,n)));
		long[] possibleFactors=primes.stream().skip(n).mapToLong(Long::longValue).toArray();
		Map<Long,Integer> factors=sieved.mapToObj((long m)->decomposeAndAdd(m,possibleFactors,u)).filter((Map<Long,Integer> decomp)->sigmaQuotLog(decomp)>LOG2).findFirst().get();
		return toBigInteger(factors);
	}
	
	public static void main(String[] args)	{
		System.out.println(getFirstAbundantWithoutNPrimes(0));
		System.out.println(getFirstAbundantWithoutNPrimes(1));
		System.out.println(getFirstAbundantWithoutNPrimes(2));
		System.out.println(getFirstAbundantWithoutNPrimes(3));
		System.out.println(getFirstAbundantWithoutNPrimes(4));
		System.out.println(getFirstAbundantWithoutNPrimes(5));
	}
}
