package com.euler;

import java.util.HashSet;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import com.euler.common.AlternatePrimeCounter;
import com.euler.common.AlternatePrimeCounter.AlternatePrimeCounts;
import com.euler.common.PowerfulNumberGenerator;
import com.euler.common.PowerfulNumberGenerator.PowerfulNumber;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;

public class Euler715_4 {
	public final static long LIMIT=LongMath.pow(10l,12);
	
	// This is VERY DEFINITELY not feasible.
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		NavigableSet<Long> powerfulNumbers=new TreeSet<>();
		PowerfulNumberGenerator generator=new PowerfulNumberGenerator(LIMIT);
		generator.generatePowerfulNumbers(LIMIT,(PowerfulNumber num)->powerfulNumbers.add(num.n));
		LongObjMap<AlternatePrimeCounts> allCounters=HashLongObjMaps.newMutableMap();
		while (!powerfulNumbers.isEmpty())	{
			long last=powerfulNumbers.last();
			AlternatePrimeCounter counter=new AlternatePrimeCounter(last);
			Set<Long> toRemove=new HashSet<>();
			for (long l:powerfulNumbers)	{
				AlternatePrimeCounts counts=counter.sumF(l);
				if (counts!=null)	{
					toRemove.add(l);
					allCounters.put(l,counts);
				}
			}
			System.out.println("Haciendo el truqui con "+last+" he quitado "+toRemove.size()+" números.");
			powerfulNumbers.removeAll(toRemove);
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
