package com.euler;

import java.util.NavigableMap;
import java.util.TreeMap;

import com.euler.common.PrimePowerCombination;
import com.euler.common.Primes;
import com.euler.common.Timing;
import com.google.common.math.IntMath;

public class Euler108 {
	private static long N=1000;
	
	private final static int MAX_PRIME=1000;
	
	private static double[] getLogs(int[] in)	{
		double[] result=new double[in.length];
		for (int i=0;i<in.length;++i) result[i]=Math.log(in[i]);
		return result;
	}
	
	private static long solve()	{
		int[] primes=Primes.listIntPrimesAsArray(MAX_PRIME);
		double[] primeLogs=getLogs(primes);
		NavigableMap<Double,PrimePowerCombination> pending=new TreeMap<>();
		PrimePowerCombination initial=new PrimePowerCombination();
		pending.put(initial.getRepresentativeLogarithm(primeLogs),initial);
		for (;;)	{
			PrimePowerCombination current=pending.pollFirstEntry().getValue();
			if (current.howManyDivisorsInSquare()>=2*N-1) return current.getRepresentative(primes);
			for (PrimePowerCombination child:current.getChildren()) pending.put(child.getRepresentativeLogarithm(primeLogs),child);
		}
	}
	
	public static void main(String[] args)	{
		if (args.length>0) N=4*IntMath.pow(10,6);
		Timing.time(Euler108::solve);
	}
}
