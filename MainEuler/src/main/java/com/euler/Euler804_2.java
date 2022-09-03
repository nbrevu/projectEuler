package com.euler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes.PrimeDecomposer;
import com.euler.common.Primes.StandardPrimeDecomposer;
import com.euler.common.alpertron.Diophantine2dSolver;

public class Euler804_2 {
	private final static long N=1000000;
	
	public static void main(String[] args)	{
		long count=0;
		SortedMap<Integer,List<Long>> baskets=new TreeMap<>();
		PrimeDecomposer decomposer=new StandardPrimeDecomposer(1_000_000);
		for (long i=1;i<=N;++i)	{
			int sols=Diophantine2dSolver.solve(1,1,41,0,0,-i,decomposer).size();
			if (sols==0) continue;
			baskets.computeIfAbsent(sols,(Integer unused)->new ArrayList<>()).add(i);
			count+=sols;
		}
		for (Map.Entry<Integer,List<Long>> entry:baskets.entrySet())	{
			System.out.println("f(x)="+entry.getKey()+":");
			for (long n:entry.getValue())	{
				DivisorHolder decomp=decomposer.decompose(n);
				System.out.println("\t"+n+" => "+decomp.toString()+".");
			}
		}
		System.out.println(count);
	}
}
