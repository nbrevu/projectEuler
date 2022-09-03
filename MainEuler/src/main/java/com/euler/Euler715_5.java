package com.euler;

import java.math.RoundingMode;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeSet;

import com.euler.common.MeisselLehmerPrimeCounter;
import com.euler.common.PowerfulNumberGenerator;
import com.euler.common.PowerfulNumberGenerator.PowerfulNumber;
import com.google.common.math.LongMath;

public class Euler715_5 {
	public final static long LIMIT=LongMath.pow(10l,12);
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		NavigableSet<Long> powerfulNumbers=new TreeSet<>();
		PowerfulNumberGenerator generator=new PowerfulNumberGenerator(LIMIT);
		generator.generatePowerfulNumbers(LIMIT,(PowerfulNumber num)->powerfulNumbers.add(num.n));
		MeisselLehmerPrimeCounter counter=new MeisselLehmerPrimeCounter(LongMath.sqrt(LIMIT,RoundingMode.UP));
		MeisselLehmerPrimeCounter.CACHE_LIMIT=10_000_000;
		long dummy=0;
		for (Iterator<Long> it=powerfulNumbers.descendingIterator();it.hasNext();)	{
			long x=it.next();
			dummy+=counter.piWithCacheClearing(it.next());
			System.out.println(x);
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(dummy+", JAJA SI.");
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
