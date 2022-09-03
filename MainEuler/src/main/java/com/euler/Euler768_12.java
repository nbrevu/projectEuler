package com.euler;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Euler768_12 {
	private final static int HOLES=60;
	private final static int CANDLES=20;
	
	private static void addAll(Set<BitSet> source,int size,Set<BitSet> target)	{
		if ((HOLES%size)!=0) return;
		int q=HOLES/size;
		for (BitSet b:source)	{
			for (int i=0;i<q;++i)	{
				boolean isValid=true;
				for (int j=0;j<size;++j) if (b.get(i+q*j))	{
					isValid=false;
					break;
				}
				if (!isValid) continue;
				BitSet newSet=(BitSet)(b.clone());
				for (int j=0;j<size;++j) newSet.set(i+q*j);
				target.add(newSet);
			}
		}
	}
	
	/*
	 * 122400675
	 * Elapsed 466.38601450000004 seconds.
	 * 
	 * Definitely the incl.-excl. must be correct... the problem, if any, lies in the core logic.
	 * Some of my assumptions are very wrong, but I don't know which ones.
	 * It seems likely, again, that there ARE additional solutions aside from the ones based on cycles. And I'm stumped.
	 */
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		List<Set<BitSet>> results=new ArrayList<>();
		results.add(Set.of(new BitSet(HOLES)));
		results.add(Set.of());
		for (int i=2;i<=CANDLES;++i)	{
			System.out.println(i+"...");
			Set<BitSet> newSet=new HashSet<>();
			for (int j=2;j<=i;++j)	{
				if ((HOLES%j)!=0) continue;
				System.out.println("\t"+j+"...");
				Set<BitSet> oldSet=results.get(i-j);
				addAll(oldSet,j,newSet);
			}
			results.add(newSet);
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(results.get(CANDLES).size());
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
