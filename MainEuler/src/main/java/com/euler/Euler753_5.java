package com.euler;

import java.util.BitSet;

import com.euler.common.Primes;
import com.google.common.math.IntMath;

public class Euler753_5 {
	/*
	4714126766770661630
	Elapsed 13649.812470488001 seconds.
	JAJA SI.
	In the problem thread there are some interesting solutions.
	 */
	private final static int LIMIT=6_000_000;

	private static class ResidueCalculator	{
		private final static int SAFE_LIMIT=IntMath.pow(2,21);
		private final BitSet counters;
		private final long[] safeCubes;
		private static long cube(long i)	{
			return i*i*i;
		}
		private int cubeMod(long x,long p)	{
			long x2=(x*x)%p;
			return (int)((x2*x)%p);
		}
		public ResidueCalculator(int limit)	{
			counters=new BitSet(limit);
			safeCubes=new long[limit];
			for (int i=1;i<Math.min(limit,SAFE_LIMIT);++i) safeCubes[i]=cube(i);
		}
		private void setResidues(long p)	{
			counters.clear(0,(int)p);
			if (p<=SAFE_LIMIT) for (int i=1;i<p;++i)	{
				int residue=(int)(safeCubes[i]%p);
				counters.set(residue);
			}	else	{
				for (int i=1;i<SAFE_LIMIT;++i)	{
					int residue=(int)(safeCubes[i]%p);
					counters.set(residue);
				}
				for (int i=SAFE_LIMIT;i<p;++i)	{
					int residue=cubeMod(i,p);
					counters.set(residue);
				}
			}
		}
		public long calculateResidue(long p)	{
			if ((p%3)==1)	{
				long residues=(p-1)/3;
				setResidues(p);
				int sums=0;
				for (int i=2;i<p;++i) if (counters.get(i)&&counters.get(i-1)) ++sums;
				return 27*residues*sums;
			}	else return (p-1)*(p-2);
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		ResidueCalculator calculator=new ResidueCalculator(LIMIT);
		long result=0l;
		for (long p:Primes.listLongPrimesAsArray(LIMIT)) result+=calculator.calculateResidue(p);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
