package com.euler;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import com.euler.common.EulerUtils;
import com.euler.common.EulerUtils.CombinatorialNumberModCache;
import com.euler.common.Primes;

public class Euler365_2 {
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
	
	private static long calculateCombinatorial(long m,long n,long mod,CombinatorialNumberModCache cache)	{
		List<Long> repM=transformToBase(m,mod);
		List<Long> repN=transformToBase(n,mod);
		int S=repN.size();
		repM=repM.subList(0,S);	// The remaining elements will be of the form (C(X,0)=1).
		for (int i=0;i<S;++i) if (repM.get(i)<repN.get(i)) return 0;
		long result=1;
		for (int i=0;i<S;++i)	{
			long comb=cache.get(repM.get(i).intValue(),repN.get(i).intValue());
			result=(result*comb)%mod;
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		NavigableSet<Long> primes=new TreeSet<>(Primes.listLongPrimes(UPPER_BOUND));
		List<Long> primeList=new ArrayList<>(primes.tailSet(LOWER_BOUND,false));
		System.out.println("I got the primes.");
		List<Long> moduliList=new ArrayList<>(primeList.size());
		for (int i=0;i<primeList.size();++i) moduliList.add(calculateCombinatorial(M,N,primeList.get(i),new CombinatorialNumberModCache(0,primeList.get(i))));
		System.out.println("I got the moduli.");
		long sum=0;
		for (int i=0;i<primeList.size()-2;++i)	{
			long p1=primeList.get(i);
			long m1=moduliList.get(i);
			for (int j=i+1;j<primeList.size()-1;++j)	{
				long p2=primeList.get(j);
				long m2=moduliList.get(j);
				long m12=EulerUtils.solveChineseRemainder(m1,p1,m2,p2);
				long p12=p1*p2;
				for (int k=j+1;k<primeList.size();++k)	{
					long p3=primeList.get(k);
					long m3=moduliList.get(k);
					long result=EulerUtils.solveChineseRemainder(m12,p12,m3,p3);
					sum+=result;
				}
			}
		}
		long tac=System.nanoTime();
		System.out.println(sum);
		double seconds=((double)(tac-tic))/1e9;
		System.out.println("Finished in "+seconds+" seconds.");
	}
}
