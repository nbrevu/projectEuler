package com.euler;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import com.euler.common.Primes.PrimeContainer;

public class Euler58 {
	private final static int CHUNK_SIZE=200000;
	public static void main(String[] args)	{
		PrimeContainer<Set<Integer>> primes=new PrimeContainer<>(new TreeSet<Integer>());
		int currentChunk=CHUNK_SIZE;
		primes.generatePrimesUpTo(currentChunk);
		int howManyNumbers=1;
		int howManyPrimes=0;
		int bladeSize=0;
		int lastNumber=1;
		Collection<Integer> knownPrimes=primes.getPrimes();
		do	{
			++bladeSize;
			int doubleBlade=2*bladeSize;
			while ((doubleBlade+1)*(doubleBlade+1)>currentChunk)	{
				currentChunk+=CHUNK_SIZE;
				primes.generatePrimesUpTo(currentChunk);
				knownPrimes=primes.getPrimes();
			}
			for (int i=0;i<4;++i)	{
				lastNumber+=doubleBlade;
				if (knownPrimes.contains(lastNumber)) ++howManyPrimes;
			}
			howManyNumbers+=4;
			if (bladeSize%1000==0) System.out.println(""+bladeSize+"...");
		}	while (howManyNumbers<10*howManyPrimes);
		System.out.println(2*bladeSize+1);
	}
}
