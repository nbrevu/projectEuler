package com.euler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import com.euler.common.BigIntegerUtils.BigCombinatorialNumberCache;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;

public class Euler365 {
	private final static long M=1000000000000000000l;
	private final static long N=1000000000l;
	
	private final static long UPPER_BOUND=5000l;
	private final static long LOWER_BOUND=1000l;
	
	private static List<Long> transformToBase(long in,long base)	{
		List<Long> representation=new ArrayList<>();
		while (in>0)	{
			representation.add(in%base);
			in/=base;
		}
		return representation;
	}
	
	private static long calculateCombinatorial(long m,long n,long mod,BigCombinatorialNumberCache cache)	{
		List<Long> repM=transformToBase(m,mod);
		List<Long> repN=transformToBase(n,mod);
		int S=repN.size();
		repM=repM.subList(0,S);	// The remaining elements will be of the form (C(X,0)=1).
		for (int i=0;i<S;++i) if (repM.get(i)<repN.get(i)) return 0;
		BigInteger bigMod=BigInteger.valueOf(mod);
		long result=1;
		for (int i=0;i<S;++i)	{
			BigInteger comb=cache.get(repM.get(i).intValue(),repN.get(i).intValue());
			BigInteger divided=comb.mod(bigMod);
			result=(result*divided.longValue())%mod;
		}
		return result;
	}
	
	private static long calculateCombinatorial(long m,long n,long p1,long p2,long p3,BigCombinatorialNumberCache cache)	{
		try	{
			long n1=calculateCombinatorial(m,n,p1,cache);
			long n2=calculateCombinatorial(m,n,p2,cache);
			long n3=calculateCombinatorial(m,n,p3,cache);
			long n12=EulerUtils.solveChineseRemainder(n1,p1,n2,p2);
			return EulerUtils.solveChineseRemainder(n12,p1*p2,n3,p3);
		}	catch (AssertionError ass)	{
			System.out.println("Error con los primos "+p1+", "+p2+", "+p3+".");
			throw ass;
		}
	}
	
	public static void main(String[] args)	{
		double tic=System.nanoTime();
		NavigableSet<Long> primes=new TreeSet<>(Primes.listLongPrimes(UPPER_BOUND));
		List<Long> primeList=new ArrayList<>(primes.tailSet(LOWER_BOUND,false));
		System.out.println("I got the primes.");
		BigCombinatorialNumberCache cache=new BigCombinatorialNumberCache((int)UPPER_BOUND);
		System.out.println("I got the cache.");
		long result=0;
		for (int i=0;i<primeList.size()-2;++i)	{
			long p1=primeList.get(i);
			for (int j=i+1;j<primeList.size()-1;++j)	{
				long p2=primeList.get(j);
				for (int k=j+1;k<primeList.size();++k)	{
					long p3=primeList.get(k);
					result+=calculateCombinatorial(M,N,p1,p2,p3,cache);
				}
			}
		}
		double tac=System.nanoTime();
		System.out.println(result);
		double seconds=((double)(tac-tic))/1e9;
		System.out.println("Finished in "+seconds+" seconds.");
	}
}
