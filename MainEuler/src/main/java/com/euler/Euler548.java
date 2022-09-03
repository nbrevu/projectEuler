package com.euler;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;

public class Euler548 {
	//*
	private final static long LIMIT=10000000000000000l;
	private final static long PRIME_LIMIT=100000000l;
	
	private final static int MAX_PRIMES=7;
	private final static int HASH_BUCKETS=4096;
	
	private static List<Long> primes;
	
	private static Comparator<Integer> REVERSE_COMPARATOR=new Comparator<Integer>()	{
		@Override
		public int compare(Integer i1, Integer i2) {
			return i2-i1;
		}
	};
	
	private static Comparator<List<Integer>> LIST_COMPARATOR=new Comparator<List<Integer>>()	{
		@Override
		public int compare(List<Integer> l1,List<Integer> l2)	{
			if (l1.size()!=l2.size()) return l1.size()-l2.size();
			int diff=EulerUtils.intSum(l1)-EulerUtils.intSum(l2);
			if (diff!=0) return diff;
			for (int i=0;i<l1.size();++i)	{
				diff=l1.get(i)-l2.get(i);
				if (diff!=0) return diff;
			}
			return 0;
		}
	};
	
	private static class PrimeProduct	{
		private static Map<PrimeProduct,Set<PrimeProduct>> divisors=new HashMap<>(HASH_BUCKETS);
		private static Map<PrimeProduct,Long> gozintas=new HashMap<>(HASH_BUCKETS);
		private static Map<List<Integer>,PrimeProduct> cache=new HashMap<>(HASH_BUCKETS);
		private final List<Integer> exponents;
		private PrimeProduct(List<Integer> exponents)	{
			int lastNonZero=getTrimmingIndex(exponents);
			this.exponents=exponents.subList(0,lastNonZero);
			List<Integer> canonicalList=new ArrayList<>(this.exponents);
			canonicalList.sort(REVERSE_COMPARATOR);
			if (!divisors.containsKey(this))	{
				divisors.put(this,calculateDivisors());
				gozintas.put(this,calculateGozinta());
			}
		}
		public static boolean isCached(List<Integer> exponents)	{
			return cache.containsKey(exponents);
		}
		public static PrimeProduct getPrimeProduct(List<Integer> exponents)	{
			PrimeProduct cached=cache.get(exponents);
			if (cached==null)	{
				cached=new PrimeProduct(exponents);
				cache.put(exponents,cached);
			}
			return cached;
		}
		private static int getTrimmingIndex(List<Integer> list) {
			for (int i=list.size()-1;i>=0;--i) if (list.get(i)!=0) return i+1;
			return 0;
		}
		private Set<PrimeProduct> calculateDivisors()	{
			Set<PrimeProduct> result=new HashSet<>();
			if (exponents.isEmpty()) return Collections.singleton(this);
			for (int i=0;i<exponents.size();++i) if (exponents.get(i)>0)	{
				List<Integer> newList=new ArrayList<>(exponents);
				newList.set(i,exponents.get(i)-1);
				PrimeProduct divisor=getPrimeProduct(newList);
				result.addAll(divisor.getDivisors());
				result.add(divisor);
			}
			return result;
		}
		private long calculateGozinta()	{
			if (exponents.isEmpty()) return 1l;
			long result=0l;
			for (PrimeProduct divisor:getDivisors()) result+=divisor.getGozinta();
			return result;
		}
		public Set<PrimeProduct> getDivisors()	{
			return divisors.get(this);
		}
		public long getGozinta()	{
			return gozintas.get(this);
		}
		public boolean surpassesLimit()	{
			return gozintas.get(this)>LIMIT;
		}
		public boolean matches()	{
			long gozinta=getGozinta();
			List<Integer> newExponents=factorize(gozinta);
			return newExponents.equals(exponents);
		}
		public List<Integer> getFactors()	{
			return exponents;
		}
		@Override
		public boolean equals(Object other)	{
			PrimeProduct ppOther=(PrimeProduct)other;
			return exponents.equals(ppOther.exponents);
		}
		@Override
		public int hashCode()	{
			return exponents.hashCode();
		}
		@Override
		public String toString()	{
			return exponents.toString();
		}
	}
	
	private static List<Integer> factorize(long in)	{
		Map<Long,Integer> factors=new HashMap<>();
		for (long p:primes) if (p*p>in) break;
		else if ((in%p)==0)	{
			int times=0;
			do	{
				++times;
				in/=p;
			}	while ((in%p)==0l);
			factors.put(p,times);
		}
		if (in!=1l) factors.put(in,1);
		List<Integer> powers=new ArrayList<>(factors.values());
		powers.sort(REVERSE_COMPARATOR);
		return powers;
	}
	
	public static List<List<Integer>> getChildren(List<Integer> list)	{
		List<List<Integer>> result=new ArrayList<>();
		List<Integer> toAdd;
		if (list.size()<MAX_PRIMES)	{
			toAdd=new ArrayList<>(list);
			toAdd.set(0,toAdd.get(0)+1);
			if (!PrimeProduct.isCached(toAdd)) result.add(toAdd);
		}
		for (int i=1;i<list.size();++i) if (list.get(i)<list.get(i-1))	{
			toAdd=new ArrayList<>(list);
			toAdd.set(i,toAdd.get(i)+1);
			if (!PrimeProduct.isCached(toAdd)) result.add(toAdd);
		}
		toAdd=new ArrayList<>(list);
		toAdd.add(1);
		if (!PrimeProduct.isCached(toAdd)) result.add(toAdd);
		return result;
	}
	
	public static void main(String[] args)	{
		primes=Primes.listLongPrimes(PRIME_LIMIT);
		SortedSet<List<Integer>> queue=new TreeSet<>(LIST_COMPARATOR);
		queue.add(Arrays.asList(1));
		SortedSet<Long> results=new TreeSet<>();
		try(PrintStream ps=new PrintStream(new File("D:\\Output548.txt")))	{
			while (!queue.isEmpty())	{
				List<Integer> factors=queue.first();
				PrimeProduct pp=PrimeProduct.getPrimeProduct(factors);
				ps.println(pp.getFactors()+" => "+pp.getGozinta());
				if (!pp.surpassesLimit())	{
					if (pp.matches())	{
						results.add(pp.getGozinta());
						System.out.println(""+pp.getGozinta()+" => "+EulerUtils.sum(results)+" ["+pp.getFactors()+"]");
					}
					queue.addAll(getChildren(factors));
				}
				queue.remove(factors);
			}
		}	catch (IOException exc)	{
			System.out.println("D'oh!");
		}
		System.out.println(results);
		System.out.println(EulerUtils.sum(results));
	}
	/*/
	private final static int LIMIT=1000;
	
	private static int getGozintaChains(List<Integer> divisors)	{
		int size=divisors.size();
		int[] gozinta=new int[size];
		gozinta[0]=1;
		for (int i=1;i<size;++i) for (int j=0;j<i;++j) if ((divisors.get(i)%divisors.get(j))==0) gozinta[i]+=gozinta[j];
		return gozinta[size-1];
	}
	
	private static int[] firstPrimes;
	
	private static void addDivisor(Set<Integer> divs,int divisor)	{
		Set<Integer> newSet=new TreeSet<>(divs);
		for (int i:newSet) divs.add(i*divisor);
	}
	
	private static List<Integer> getDivisors(int num)	{
		Set<Integer> divisors=new TreeSet<>();
		divisors.add(1);
		for (;;)	{
			int prime=firstPrimes[num];
			if (prime==0)	{
				addDivisor(divisors,num);
				return new ArrayList<>(divisors);
			}	else	{
				addDivisor(divisors,prime);
				num/=prime;
			}
		}
	}
	
	public static void main(String[] args)	{
		firstPrimes=Primes.firstPrimeSieve(LIMIT);
		Set<Integer> acceptable=new TreeSet<>();
		for (int i=2;i<=LIMIT;++i)	{
			int gozinta=getGozintaChains(getDivisors(i));
			System.out.println(""+i+": "+gozinta);
			if (i==gozinta) acceptable.add(i);
		}
		System.out.println(acceptable);
	}
	//*/
}
