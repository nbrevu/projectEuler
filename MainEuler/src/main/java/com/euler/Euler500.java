package com.euler;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import com.euler.common.Primes;

public class Euler500 {
	// Another one at the first try :).
	private final static long GOAL=500500;
	private final static long MOD=500500507;
	
	private final static List<Long> primes=Primes.listLongPrimes(MOD);
	
	private static class NextFactorCandidate implements Comparable<NextFactorCandidate>	{
		public final long prime;
		public final int power;
		private double log;
		public NextFactorCandidate(long prime)	{
			this.prime=prime;
			this.power=1;
			this.log=Math.log((double)prime);
		}
		private NextFactorCandidate(NextFactorCandidate previous)	{
			this.prime=previous.prime;
			this.power=2*previous.power;
			this.log=2*previous.log;
		}
		public NextFactorCandidate getNext()	{
			return new NextFactorCandidate(this);
		}
		@Override
		public int hashCode()	{
			return Long.valueOf(prime).hashCode()+Integer.valueOf(power).hashCode();
		}
		@Override
		public boolean equals(Object other)	{
			NextFactorCandidate nfcOther=(NextFactorCandidate)other;
			return ((prime==nfcOther.prime)&&(power==nfcOther.power));
		}
		@Override
		public int compareTo(NextFactorCandidate other)	{
			return Double.valueOf(log).compareTo(Double.valueOf(other.log));
		}
	}
	
	private static class PrimeFactors	{
		private SortedMap<Long,Integer> factors;
		public PrimeFactors()	{
			factors=new TreeMap<>();
		}
		public void addFactor(Long prime,int power)	{
			Integer oldPow=factors.get(prime);
			int newPow=power+((oldPow==null)?0:oldPow.intValue());
			factors.put(prime,newPow);
		}
		public void addFactor(NextFactorCandidate nfc)	{
			addFactor(nfc.prime,nfc.power);
		}
		public long compose(long mod)	{
			long product=1l;
			for (Map.Entry<Long,Integer> entry:factors.entrySet())	{
				long prime=entry.getKey();
				int power=entry.getValue();
				for (int i=0;i<power;++i) product=(product*prime)%mod;
			}
			return product;
		}
		public BigInteger getFullNumber()	{
			BigInteger product=BigInteger.ONE;
			for (Map.Entry<Long,Integer> entry:factors.entrySet())	{
				BigInteger prime=BigInteger.valueOf(entry.getKey());
				int power=entry.getValue();
				BigInteger factor=prime.pow(power);
				product=product.multiply(factor);
			}
			return product;
		}
	}
	
	public static void main(String[] args)	{
		NavigableSet<NextFactorCandidate> candidates=new TreeSet<>();
		for (long prime:primes) candidates.add(new NextFactorCandidate(prime));
		PrimeFactors holder=new PrimeFactors();
		for (int i=0;i<GOAL;++i)	{
			NextFactorCandidate candidate=candidates.pollFirst();
			holder.addFactor(candidate);
			candidates.add(candidate.getNext());
		}
		System.out.println(holder.compose(MOD));
		BigInteger theNumber=holder.getFullNumber();
		try (PrintStream out=new PrintStream(new File("D:\\out500.txt")))	{
			out.println(theNumber.toString());
		}	catch (IOException exc)	{
			System.out.println("Ay, ay. No me gusta.");
		}
	}
}
