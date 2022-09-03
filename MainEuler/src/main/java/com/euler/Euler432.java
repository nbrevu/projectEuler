package com.euler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.euler.common.EulerUtils;
import com.google.common.collect.ImmutableSet;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongIntMap;
import com.koloboke.collect.map.hash.HashLongIntMaps;

public class Euler432 {
	private final static long BASE_NUMBER=510510l;	// Happens to be 2*3*5*7*11*13*17.
	private final static long LIMIT=LongMath.pow(10l,6);
	
	private static long extractPrime(long in,long factor,LongIntMap primes)	{
		if ((in%factor)==0)	{
			do	{
				in/=factor;
				EulerUtils.increaseCounter(primes,factor);
			}	while ((in%factor)==0);
			return in;
		}	else return in;
	}
	
	private static LongIntMap getPrimeFactors(long in)	{
		LongIntMap result=HashLongIntMaps.newMutableMap();
		in=extractPrime(in,2l,result);
		in=extractPrime(in,3l,result);
		boolean add4=false;
		for (long p=5;;p+=add4?4:2,add4=!add4)	{
			in=extractPrime(in,p,result);
			if (in==1) return result;
		}
	}
	
	private static class DivisorList implements Comparable<DivisorList>	{
		private static List<Long> primeList;
		private static long limit;
		private final LongIntMap divisors;
		private final long product;
		private final int growAt;
		public static void setPrimeList(List<Long> theList)	{
			primeList=theList;
		}
		public static void setLimit(long theLimit)	{
			limit=theLimit;
		}
		public DivisorList()	{
			divisors=HashLongIntMaps.newMutableMap();
			product=1l;
			growAt=0;
		}
		private DivisorList(LongIntMap divisors,long product,int growAt)	{
			this.divisors=divisors;
			this.product=product;
			this.growAt=growAt;
		}
		public List<DivisorList> getChildren()	{
			List<DivisorList> result=new ArrayList<>();
			for (int i=growAt;i<primeList.size();++i)	{
				DivisorList child=getChild(i);
				if (child.product<=limit) result.add(child);
			}
			return result;
		}
		private DivisorList getChild(int grow)	{
			long newFactor=primeList.get(grow);
			LongIntMap newDivs=HashLongIntMaps.newMutableMap(divisors);
			EulerUtils.increaseCounter(newDivs,newFactor);
			return new DivisorList(newDivs,product*newFactor,grow);
		}
		public LongIntMap getDivisorMap()	{
			return divisors;
		}
		@Override
		public String toString()	{
			return Long.toString(product);
		}
		@Override
		public int compareTo(DivisorList o) {
			return Long.signum(product-o.product);
		}
		@Override
		public int hashCode()	{
			return Long.hashCode(product);
		}
		@Override
		public boolean equals(Object other)	{
			return product==((DivisorList)other).product;
		}
	}
	
	private static Set<DivisorList> generateNumbers(List<Long> primeList,long limit)	{
		DivisorList.setPrimeList(primeList);
		DivisorList.setLimit(limit);
		Set<DivisorList> result=new TreeSet<>();
		Set<DivisorList> thisGen=ImmutableSet.of(new DivisorList());
		while (!thisGen.isEmpty())	{
			Set<DivisorList> nextGen=new HashSet<>();
			for (DivisorList dl:thisGen) nextGen.addAll(dl.getChildren());
			result.addAll(thisGen);
			thisGen=nextGen;
		}
		return result;
	}
	
	private static LongIntMap multiply(LongIntMap n1,LongIntMap n2)	{
		LongIntMap result=HashLongIntMaps.newMutableMap();
		n2.forEach((long key,int value)->EulerUtils.increaseCounter(result,key,value));
		return result;
	}
	
	public static void main(String[] args)	{
		{
			// MIERDA, esto no funciona. Tengo que ser más cuidadoso. Pero PUEDE que este esquema, en general, funcione tomando las 128 combinaciones.
			LongIntMap baseFactors=getPrimeFactors(BASE_NUMBER);
			List<Long> primesInBase=new ArrayList<>(baseFactors.keySet());
			long baseTotient=EulerUtils.getTotient(baseFactors);
			BigInteger baseSum=EulerUtils.getSumOfTotientsUpTo(LIMIT);
			BigInteger result=baseSum.multiply(BigInteger.valueOf(baseTotient));
			for (DivisorList specialNumber:generateNumbers(primesInBase,LIMIT))	{
				LongIntMap divisors=specialNumber.getDivisorMap();
				long precalculatedTotient=EulerUtils.getTotient(divisors)*baseTotient;
				LongIntMap totalDivisors=multiply(divisors,baseFactors);
				long realTotient=EulerUtils.getTotient(totalDivisors);
				result=result.add(BigInteger.valueOf(realTotient-precalculatedTotient));
			}
			System.out.println(result);
		}
		{
			LongIntMap baseFactors=getPrimeFactors(BASE_NUMBER);
			List<Long> primesInBase=new ArrayList<>(baseFactors.keySet());
			int hm=generateNumbers(primesInBase,LongMath.pow(10l,6)).size();
			System.out.println("Para n=10^6 me salen "+hm+" cosas.");
		}
		{
			LongIntMap baseFactors=getPrimeFactors(BASE_NUMBER);
			List<Long> primesInBase=new ArrayList<>(baseFactors.keySet());
			int hm=generateNumbers(primesInBase,LongMath.pow(10l,11)).size();
			System.out.println("Para n=10^11 me salen "+hm+" cosas.");
		}
	}
}