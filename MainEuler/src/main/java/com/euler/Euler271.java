package com.euler;

import static com.euler.common.EulerUtils.sum;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class Euler271 {
	private final static List<Long> primes=Arrays.asList(2l,3l,5l,7l,11l,13l,17l,19l,23l,29l,31l,37l,41l,43l);
	
	private static class NonTrivialBase	{
		public final long prime;
		public final List<Long> bases;
		public NonTrivialBase(long prime,List<Long> bases)	{
			this.prime=prime;
			this.bases=Collections.unmodifiableList(bases);
		}
		public Remainder getRemainder(int index)	{
			return new Remainder(bases.get(index),prime);
		}
	}
	
	private static class IndexGenerator implements Iterator<int[]>	{
		private int[] limits;
		private int[] indices;
		private boolean isNextCalculated;
		public IndexGenerator(int[] limits)	{
			this.limits=limits;
			indices=new int[limits.length];
			isNextCalculated=true;
		}
		@Override
		public boolean hasNext() {
			if (!isNextCalculated) calculateNext();
			return indices[0]>=0;
		}
		@Override
		public int[] next() {
			if (!isNextCalculated) calculateNext();
			isNextCalculated=false;
			return indices;
		}
		private void calculateNext()	{
			boolean isFinished=false;
			for (int i=0;i<indices.length;++i)	{
				++indices[i];
				if (indices[i]<limits[i])	{
					isFinished=true;
					break;
				}
				indices[i]=0;
			}
			if (!isFinished) for (int i=0;i<indices.length;++i) indices[i]=-1;
			isNextCalculated=true;
		}
	}
	
	private static class Remainder	{
		public final long remainder;
		public final long mod;
		public Remainder(long remainder,long mod)	{
			if (remainder>=mod) throw new IllegalArgumentException("Reverse, please.");
			this.remainder=remainder;
			this.mod=mod;
		}
		public Remainder combineWith(Remainder other)	{
			BigInteger newMod=BigInteger.valueOf(this.mod).multiply(BigInteger.valueOf(other.mod));
			BigInteger a=BigInteger.valueOf(this.mod);
			BigInteger b=BigInteger.valueOf(other.mod);
			BigInteger A=a.modInverse(b);
			BigInteger B=b.modInverse(a);
			BigInteger x=BigInteger.valueOf(this.remainder);
			BigInteger y=BigInteger.valueOf(other.remainder);
			BigInteger newRemainder=y.multiply(a).multiply(A).add(x.multiply(b).multiply(B)).mod(newMod);
			return new Remainder(newRemainder.longValue(),newMod.longValue());
		}
	}
	
	public static List<Long> getBasesForPrime(long prime)	{
		List<Long> bases=new ArrayList<>();
		for (long i=1;i<prime;++i)	{
			if (((i*i*i)%prime)==1l) bases.add(i);
		}
		return bases;
	}
	
	private static Remainder getRemainder(Remainder baseRemainder,int[] indices,List<NonTrivialBase> relevantBases)	{
		Remainder result=baseRemainder;
		for (int i=0;i<indices.length;++i)	{
			result=result.combineWith(relevantBases.get(i).getRemainder(indices[i]));
		}
		return result;
	}
	
	public static void main(String[] args)	{
		List<NonTrivialBase> relevantBases=new ArrayList<>();
		long simpleProduct=1l;
		for (long p:primes)	{
			List<Long> basesForP=getBasesForPrime(p);
			/*
			if (basesForP.size()==1) simpleProduct*=p;
			else relevantBases.add(new NonTrivialBase(p,basesForP));
			*/
			if (basesForP.size()==1)	{
				simpleProduct*=p;
				System.out.println(""+p+" es una mierdecilla.");
			}	else	{
				relevantBases.add(new NonTrivialBase(p,basesForP));
				System.out.println(""+p+" es guay.");
			}

		}
		Remainder baseRemainder=new Remainder(1l,simpleProduct);
		SortedSet<Long> results=new TreeSet<>();
		int[] limits=new int[relevantBases.size()];
		for (int i=0;i<relevantBases.size();++i) limits[i]=relevantBases.get(i).bases.size();
		IndexGenerator generator=new IndexGenerator(limits);
		while (generator.hasNext())	{
			int[] indices=generator.next();
			Remainder remainder=getRemainder(baseRemainder,indices,relevantBases);
			if (remainder.remainder!=1l) results.add(remainder.remainder);
		}
		System.out.println(sum(results));
	}
}
