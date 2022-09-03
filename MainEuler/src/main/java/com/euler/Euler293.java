package com.euler;

import static com.euler.common.EulerUtils.product;
import static com.euler.common.EulerUtils.sum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.euler.common.Primes;

public class Euler293 {
	private final static int N=1000000000;
	private final static int SIEVED=N+1000;
	private final static int MAX_PRIMES=32000;
	
	private static class AdmissibleGenerator implements Iterator<Long>	{
		private List<Long> factors;
		private SortedSet<Long> available;
		public AdmissibleGenerator(List<Long> factors)	{
			this.factors=factors;
			available=new TreeSet<>();
			available.add(product(factors));
		}
		@Override
		public boolean hasNext() {
			return !available.isEmpty();
		}
		@Override
		public Long next() {
			long result=available.first();
			available.remove(result);
			for (long f:factors)	{
				long candidate=result*f;
				if (candidate<=N) available.add(candidate);
			}
			return result;
		}
	}
	
	private static class AdmissibleGeneratorGenerator implements Iterator<AdmissibleGenerator>	{
		private List<Long> primes;
		private int lastIndex;
		private boolean isNextCalculated;
		public AdmissibleGeneratorGenerator(List<Long> primes)	{
			this.primes=primes;
			lastIndex=2;
			isNextCalculated=true;
		}
		@Override
		public boolean hasNext() {
			if (!isNextCalculated) calculateNext();
			return lastIndex>=0;
		}
		@Override
		public AdmissibleGenerator next() {
			if (!isNextCalculated) calculateNext();
			if (lastIndex<0) throw new NoSuchElementException();
			AdmissibleGenerator res=new AdmissibleGenerator(primes.subList(0,lastIndex));
			isNextCalculated=false;
			return res;
		}
		private void calculateNext()	{
			++lastIndex;
			if (product(primes.subList(0,lastIndex))>=(long)N) lastIndex=-1;
			isNextCalculated=true;
		}
	}
	
	private static long firstPrimeAfterMark(boolean[] composites,long mark) throws ArrayIndexOutOfBoundsException	{
		for (int i=2+(int)mark;;++i) if (!composites[i]) return (long)i;
	}
	
	private static List<Long> getPrimesUpTo(boolean[] composites,long mark)	{
		List<Long> res=new ArrayList<>();
		for (int i=2;i<(int)mark;++i) if (!composites[i]) res.add((long)i);
		return res;
	}
	
	private static Collection<Long> allAdmissible=new TreeSet<Long>();
	
	private static void addFortunateNumbers(AdmissibleGenerator gen,boolean[] composites,Set<Long> fortunate)	{
		while (gen.hasNext())	{
			long admissible=gen.next();
			// DEBUG!
			allAdmissible.add(admissible);
			// /DEBUG!
			long nextPrime=firstPrimeAfterMark(composites,admissible);
			fortunate.add(nextPrime-admissible);
		}
	}
	
	public static void main(String[] args)	{
		boolean[] composites=Primes.sieve(SIEVED);
		Set<Long> fortunate=new TreeSet<>();
		List<Long> smallPrimes=getPrimesUpTo(composites,MAX_PRIMES);
		AdmissibleGenerator g1=new AdmissibleGenerator(Arrays.asList(2l));
		addFortunateNumbers(g1,composites,fortunate);
		AdmissibleGeneratorGenerator gen=new AdmissibleGeneratorGenerator(smallPrimes);
		while (gen.hasNext()) addFortunateNumbers(gen.next(),composites,fortunate);
		// DEBUG!
		System.out.println(sum(fortunate));
	}
}
