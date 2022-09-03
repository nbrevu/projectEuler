package com.euler;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.euler.common.DivisorHolder;
import com.euler.common.DivisorHolder.TwoFactorDecomposer;
import com.euler.common.DivisorHolder.TwoFactorDecomposition;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;

public class Euler563_2 {
	private final static long LIMIT=9*LongMath.pow(10l,18);
	private final static int GOAL=100;
	
	private final static int[] PRIMES=new int[] {2,3,5,7,11,13,17,19,23};
	
	private static class PrimePowers	{
		private final int[] powers;
		public PrimePowers()	{
			this.powers=new int[PRIMES.length];
		}
		private PrimePowers(int[] powers)	{
			this.powers=powers;
		}
		public PrimePowers addPrime(int index,int amount)	{
			int[] newPowers=Arrays.copyOf(powers,powers.length);
			newPowers[index]+=amount;
			return new PrimePowers(newPowers);
		}
		public TwoFactorDecomposer getDecomposer()	{
			DivisorHolder divisors=new DivisorHolder();
			for (int i=0;i<PRIMES.length;++i) divisors.addFactor(PRIMES[i],powers[i]);
			return divisors.getDecomposer();
		}
	}
	
	private static void addPrime(SortedMap<Long,PrimePowers> result,int primeIndex,long limit)	{
		long prime=PRIMES[primeIndex];
		LongObjMap<PrimePowers> toAdd=HashLongObjMaps.newMutableMap();
		long limitBefore=limit/prime;
		for (Map.Entry<Long,PrimePowers> entry:result.entrySet())	{
			long value=entry.getKey();
			int amount=0;
			PrimePowers current=entry.getValue();
			while (value<=limitBefore)	{
				value*=prime;
				++amount;
				toAdd.put(value,current.addPrime(primeIndex,amount));
			}
		}
		result.putAll(toAdd);
	}
	
	private static SortedMap<Long,PrimePowers> getSmoothNumbers(long limit)	{
		SortedMap<Long,PrimePowers> result=new TreeMap<>();
		result.put(1l,new PrimePowers());
		for (int i=0;i<PRIMES.length;++i) addPrime(result,i,limit);
		return result;
	}
	
	private static long sumMinimal(SortedMap<Long,PrimePowers> smoothNumbers,int goal)	{
		DivisorHolder.PowerCache powers=new DivisorHolder.PowerCache(LIMIT,PRIMES);
		BitSet alreadyFound=new BitSet(1+goal);
		alreadyFound.set(0);
		alreadyFound.set(1);
		int pending=goal-1;
		long result=0;
		for (Map.Entry<Long,PrimePowers> entry:smoothNumbers.entrySet())	{
			int counter=0;
			TwoFactorDecomposer decomposer=entry.getValue().getDecomposer();
			for (TwoFactorDecomposition decomp:decomposer)	{
				long val1=powers.getValue(decomposer.factors,decomp.factor1);
				long val2=powers.getValue(decomposer.factors,decomp.factor2);
				if (val1>val2)	{
					long swap=val1;
					val1=val2;
					val2=swap;
				}
				if (val2<=(long)(Math.floor(val1*1.1))) ++counter;
			}
			if ((counter<=goal)&&!alreadyFound.get(counter))	{
				result+=entry.getKey();
				alreadyFound.set(counter);
				--pending;
				System.out.println("M("+counter+")="+entry.getKey()+".");
				if (pending==0) return result;
			}
		}
		throw new RuntimeException("Se han quedado "+pending+" cosas sin salir.");
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		SortedMap<Long,PrimePowers> smoothNumbers=getSmoothNumbers(LIMIT);
		long result=sumMinimal(smoothNumbers,GOAL);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
