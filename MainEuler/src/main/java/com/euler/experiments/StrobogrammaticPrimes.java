package com.euler.experiments;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.euler.common.Primes.RabinMiller;
import com.koloboke.collect.map.IntIntCursor;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;

public class StrobogrammaticPrimes {
	// "Encontrados 71 estroboprimos de 10 cifras que empiezan por 1. El menor es 1001961001 y el mayor es 1989886861".
	private final static IntIntMap STROBOGRAMMATIC_COUNTERPARTS=HashIntIntMaps.newImmutableMap(new int[] {0,1,9,8,6},new int[] {0,1,6,8,9});
	private final static List<Integer> WITNESSES=Arrays.asList(2,3,5,7);
	
	public static void main(String[] args)	{
		long base=1_000_000_001l;
		RabinMiller tester=new RabinMiller();
		SortedSet<Long> stroboPrimes=new TreeSet<>();
		for (IntIntCursor cursor18=STROBOGRAMMATIC_COUNTERPARTS.cursor();cursor18.moveNext();)	{
			long base18=cursor18.key()*100_000_000+cursor18.value()*10;
			long tmp18=base+base18;
			for (IntIntCursor cursor27=STROBOGRAMMATIC_COUNTERPARTS.cursor();cursor27.moveNext();)	{
				long base27=cursor27.key()*10_000_000+cursor27.value()*100;
				long tmp27=tmp18+base27;
				for (IntIntCursor cursor36=STROBOGRAMMATIC_COUNTERPARTS.cursor();cursor36.moveNext();)	{
					long base36=cursor36.key()*1_000_000+cursor36.value()*1000;
					long tmp36=tmp27+base36;
					for (IntIntCursor cursor45=STROBOGRAMMATIC_COUNTERPARTS.cursor();cursor45.moveNext();)	{
						long base45=cursor45.key()*100_000+cursor45.value()*10_000;
						long candidate=tmp36+base45;
						BigInteger bigCandidate=BigInteger.valueOf(candidate);
						if (tester.isPrime(bigCandidate,WITNESSES)) stroboPrimes.add(candidate);
					}
				}
			}
		}
		System.out.println(String.format("Encontrados %d estroboprimos de 10 cifras que empiezan por 1. El menor es %d y el mayor es %d.",stroboPrimes.size(),stroboPrimes.first(),stroboPrimes.last()));
		System.out.println(stroboPrimes);
	}
}
