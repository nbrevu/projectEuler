package com.euler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;

// TODO: echarle un ojo a ESTO: http://stackoverflow.com/questions/16402854/number-of-increasing-subsequences-of-length-k.
public class Euler378 {
	private final static int UP_TO=60000000;
	
	private final static long LIMIT=1000000000000000000l;
	
	private static int[] triangulars(int[] firstPrimes)	{
		int[] dT=new int[1+UP_TO];
		dT[0]=1;
		DivisorHolder prev=new DivisorHolder();
		for (int i=1;i<=UP_TO;++i)	{
			DivisorHolder current=DivisorHolder.getFromFirstPrimes(i+1,firstPrimes);
			DivisorHolder triangular=DivisorHolder.combine(prev,current);
			triangular.removeFactor(2);
			dT[i]=(int)triangular.getAmountOfDivisors();
			prev=current;
		}
		return dT;
	}
	
	private static NavigableMap<Integer,SortedSet<Integer>> sortByDivisors(int[] dT)	{
		NavigableMap<Integer,SortedSet<Integer>> sortedByDivisors=new TreeMap<>();
		for (int i=1;i<=UP_TO;++i)	{
			int howManyDivs=dT[i];
			SortedSet<Integer> indices=sortedByDivisors.get(howManyDivs);
			if (indices==null)	{
				indices=new TreeSet<>();
				sortedByDivisors.put(howManyDivs,indices);
			}
			indices.add(i);
			if ((i%100000)==0) System.out.println(""+i+"...");
		}
		return sortedByDivisors;
	}
	
	private static long sumAcceptableTriples(int[] dT,NavigableMap<Integer,SortedSet<Integer>> sortedByDivisors)	{
		int[] minors=new int[1+UP_TO];
		minors[UP_TO]=0;
		long sum=0l;
		for (int i=UP_TO-1;i>0;--i)	{
			int thisDT=dT[i];
			List<Integer> immediate=new ArrayList<>();
			for (Map.Entry<Integer,SortedSet<Integer>> sets:sortedByDivisors.headMap(thisDT,false).entrySet()) immediate.addAll(sets.getValue().tailSet(i));
			minors[i]=immediate.size();
			System.out.println(""+i+": "+minors[i]);
			for (int secondElement:immediate) sum=(sum+minors[secondElement])%LIMIT;
			if ((i%100000)==0) System.out.println(""+i+"...");
		}
		return sum;
	}
	
	public static void main(String[] args)	{
		int[] firstPrimes=Primes.firstPrimeSieve(2+UP_TO);
		System.out.println("Primos calculados.");
		int[] dT=triangulars(firstPrimes);
		System.out.println("dT calculado.");
		NavigableMap<Integer,SortedSet<Integer>> sortedByDivisors=sortByDivisors(dT);
		System.out.println("¿Hasta aquí va rápido o qué?");
		System.out.println(sumAcceptableTriples(dT,sortedByDivisors));
	}
}
