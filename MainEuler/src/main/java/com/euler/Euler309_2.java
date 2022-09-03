package com.euler;

import com.euler.common.PythagoreanTriples;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler309_2 {
	private final static int LIMIT=1_000_000;
	
	private static void add(LongObjMap<LongSet> container,long a,long b)	{
		LongSet subContainer=container.computeIfAbsent(a,(long unused)->HashLongSets.newMutableSet());
		subContainer.add(b);
	}
	
	// This scheme is slightly wrong. Review the conditions!
	private static LongObjMap<LongSet> getSortedCatheti(int maxValue)	{
		LongObjMap<LongSet> result=HashLongObjMaps.newMutableMap();
		PythagoreanTriples.PrimitiveTriplesIterator iterator=new PythagoreanTriples.PrimitiveTriplesIterator();
		boolean jump=false;
		for (;;)	{
			if (jump)	{
				iterator.nextM();
				jump=false;
			}	else iterator.next();
			if (iterator.c()>=maxValue)	{
				if (iterator.isSmallestN()) break;
				else jump=true;
			}	else	{
				long maxIter=(maxValue-1)/iterator.c();
				long a=iterator.a();
				long b=iterator.b();
				long ia=0;
				long ib=0;
				for (int i=1;i<=maxIter;++i)	{
					ia+=a;
					ib+=b;
					add(result,ia,ib);
					add(result,ib,ia);
				}
			}
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		LongObjMap<LongSet> triples=getSortedCatheti(LIMIT);
		int result=0;
		for (LongSet s:triples.values())	{
			long[] values=s.toLongArray();
			for (int i=0;i<values.length;++i) for (int j=i+1;j<values.length;++j)	{
				long a=values[i];
				long b=values[j];
				if ((a*b)%(a+b)==0) ++result;
			}
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
