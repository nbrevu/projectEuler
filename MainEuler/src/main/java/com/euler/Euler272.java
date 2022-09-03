package com.euler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.euler.common.Primes;

public class Euler272 {
	private final static long LIMIT=100000000000l;
	private final static int SIEVE_LIMIT=200000000;
	
	private static class NumberGenerator implements Iterator<Long>	{
		private List<Long> primes;
		private SortedSet<Long> generated;
		
		public NumberGenerator(long number,Collection<Long> primes)	{
			this.primes=new ArrayList<>(primes);
			generated=new TreeSet<>();
			generated.add(number);
		}
		@Override
		public boolean hasNext() {
			return !generated.isEmpty();
		}
		@Override
		public Long next() {
			long result=generated.first();
			generated.remove(result);
			for (long p:primes)	{
				long candidate=result*p;
				if (candidate>LIMIT) break;
				generated.add(candidate);
			}
			return result;
		}
	}
	
	private static class CombinationGenerator implements Iterator<NumberGenerator>	{
		private SortedSet<Long> primes1;
		private List<Long> primes3;
		private int[] indices;
		private boolean isNextCalculated;
		
		public CombinationGenerator(SortedSet<Long> primes1,List<Long> primes3)	{
			this.primes1=primes1;
			this.primes3=primes3;
			indices=new int[5];
			for (int i=1;i<5;++i) indices[i]=i;
			isNextCalculated=false;
		}
		@Override
		public boolean hasNext() {
			if (!isNextCalculated) calculate();
			return indices[0]>=0;
		}
		@Override
		public NumberGenerator next() {
			if (!isNextCalculated) calculate();
			long product=1l;
			for (int i:indices)	{
				long newPrime=primes3.get(i);
				product*=newPrime;
				primes1.add(newPrime);
			}
			NumberGenerator result=new NumberGenerator(product,primes1);
			for (int i:indices) primes1.remove(primes3.get(i));
			isNextCalculated=false;
			return result;
		}
		private void calculate()	{
			boolean isFinished=false;
			for (int index=4;index>=0;--index)	{
				++indices[index];
				for (int j=1+index;j<5;++j) indices[j]=1+indices[j-1];
				if (isAcceptable())	{
					isFinished=true;
					break;
				}
			}
			if (!isFinished)	{
				indices[0]=-1;
				return;
			}
			isNextCalculated=true;
		}
		private boolean isAcceptable()	{
			long product=1l;
			for (int i:indices)	{
				if (i>=primes3.size()) return false;
				product*=primes3.get(i);
			}
			return product<LIMIT;
		}
	}
	
	public static void main(String[] args)	{
		boolean composites[]=Primes.sieve(SIEVE_LIMIT);
		SortedSet<Long> primes1=new TreeSet<>();
		List<Long> primes3=new ArrayList<>();
		primes1.add(2l);
		primes1.add(3l);
		for (int p=5;p<composites.length;p+=2) if (!composites[p])	{
			if ((p%6)==1) primes3.add((long)p);
			else primes1.add((long)p);
		}
		CombinationGenerator baseGenerator=new CombinationGenerator(primes1,primes3);
		long sum=0l;
		while (baseGenerator.hasNext())	{
			NumberGenerator numberGenerator=baseGenerator.next();
			while (numberGenerator.hasNext()) sum+=numberGenerator.next();
		}
		System.out.println(sum);
	}
}
