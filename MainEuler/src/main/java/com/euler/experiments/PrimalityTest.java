package com.euler.experiments;

import java.math.BigInteger;

import com.euler.common.Primes;
import com.euler.common.Primes.RabinMiller;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class PrimalityTest	{
	private final LongSet smallPrimes;
	private final RabinMiller rabinMiller;
	private final int maxWitnesses;
	private final BigInteger maxSmallPrime;
	public PrimalityTest(int maxSmallPrime,int maxWitnesses)	{
		boolean[] composites=Primes.sieve(maxSmallPrime);
		smallPrimes=HashLongSets.newMutableSet();
		smallPrimes.add(2l);
		smallPrimes.add(3l);
		boolean add4=false;
		for (int i=5;i<composites.length;i+=(add4?4:2),add4=!add4) if (!composites[i]) smallPrimes.add(i);
		rabinMiller=new RabinMiller();
		this.maxWitnesses=maxWitnesses;
		this.maxSmallPrime=BigInteger.valueOf(maxSmallPrime);
	}
	public PrimalityTest()	{
		this(10000,5);
	}
	public boolean isPrime(BigInteger n)	{
		if (n.compareTo(maxSmallPrime)<=0) return smallPrimes.contains(n.longValueExact());
		else return rabinMiller.isPrimeUsingRandomWitnesses(n,maxWitnesses);
	}
}