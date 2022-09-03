package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import com.euler.common.DecompositionFinder;
import com.euler.common.Primes;
import com.google.common.collect.Multiset;
import com.google.common.math.LongMath;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Euler354 {
	// MAL. La solución real es 58065134 y me están saliendo 52232544...
	
	private final static int N=450;
	private final static long L=5*LongMath.pow(10l,11);
	
	private final static int[] FIRST_3KPLUS1_PRIMES=new int[] {7,13,19,31,37,43};	// I actually don't need more than the first three.
	
	private static void addFactor(NavigableSet<Double> set,double factor,double limit)	{
		List<Double> toAdd=new ArrayList<>();
		for (double d:set)	{
			double newLog=d+factor;
			if (newLog>limit) break;
			while (newLog<=limit)	{
				toAdd.add(newLog);
				newLog+=factor;
			}
		}
		set.addAll(toAdd);
	}

	private static int[] extractPrimes3kPlus1(boolean[] composites) {
		int n=0;
		for (int i=7;i<composites.length;i+=6) if (!composites[i]) ++n;
		int[] result=new int[n];
		int index=0;
		for (int i=7;i<composites.length;i+=6) if (!composites[i])	{
			result[index]=i;
			++index;
		}
		return result;
	}

	private static long addResults(int[] powers,int[] primes3kPlus1,NavigableSet<Double> additionalFactors,double logL2) {
		IntSet present=HashIntSets.newMutableSet();
		return addResultsRecursive(powers,primes3kPlus1,additionalFactors,logL2,present,0,0);
	}

	private static long addResultsRecursive(int[] powers, int[] primes3kPlus1,NavigableSet<Double> additionalFactors,double limitLog,IntSet present,int powersIndex,int addingFrom) {
		int power=powers[powersIndex]-1;
		int firstPrime=primes3kPlus1[addingFrom];
		if (power*Math.log(firstPrime)>limitLog) return 0;
		long result=0;
		for (int i=addingFrom;i<primes3kPlus1.length;++i)	{
			int p=primes3kPlus1[i];
			if (present.contains(p)) continue;
			double primePowerLog=power*Math.log(p);
			if (primePowerLog>limitLog) return result;
			if (powersIndex>=powers.length-1)	{
				double maxLog=limitLog-primePowerLog;
				result+=additionalFactors.headSet(maxLog).size();
			}	else	{
				present.add(p);
				int nextStartPoint=(powers[powersIndex+1]==powers[powersIndex])?(1+i):0;
				result+=addResultsRecursive(powers,primes3kPlus1,additionalFactors,limitLog-primePowerLog,present,powersIndex+1,nextStartPoint);
				present.removeInt(p);
			}
		}
		return result;
	}

	public static void main(String[] args)	{
		 /*
		  * Decompositions of 75:
		  * [5, 15],
		  * [3, 5, 5],
		  * [75],
		  * [3, 25]
		  */
		long tic=System.nanoTime();
		int n6=N/6;
		DecompositionFinder decompFinder=new DecompositionFinder(n6);
		Set<Multiset<Integer>> decomps=decompFinder.getDecomposition(n6);
		double minValue=Double.MAX_VALUE;
		Comparator<Integer> reverse=Comparator.reverseOrder();
		for (Multiset<Integer> decomp:decomps)	{
			List<Integer> powers=new ArrayList<>(decomp);
			powers.sort(reverse);
			double value=0;
			for (int i=0;i<powers.size();++i) value+=(powers.get(i)-1)*Math.log(FIRST_3KPLUS1_PRIMES[i]);
			minValue=Math.min(minValue,value);
		}
		double logL2=2*Math.log(L)-Math.log(3);
		double maxLogInSearch=logL2-minValue;
		System.out.println("Max log: "+Math.exp(maxLogInSearch)+".");
		int maxPrime=(int)Math.ceil(Math.exp(maxLogInSearch/2));
		System.out.println("maxPrime: "+maxPrime+".");
		boolean[] composites=Primes.sieve(maxPrime);
		NavigableSet<Double> additionalFactors=new TreeSet<>();
		additionalFactors.add(0d);	// Should it be 0?
		addFactor(additionalFactors,Math.log(3),maxLogInSearch);
		addFactor(additionalFactors,Math.log(4),maxLogInSearch);
		for (int i=5;i<composites.length;i+=6) if (!composites[i]) addFactor(additionalFactors,2*Math.log(i),maxLogInSearch);
		System.out.println("Extra factors: "+additionalFactors.size()+".");
		int[] primes3kPlus1=extractPrimes3kPlus1(composites);
		System.out.println("Primes to consider: "+primes3kPlus1.length+".");
		long result=0;
		for (Multiset<Integer> decomp:decomps)	{
			List<Integer> lPowers=new ArrayList<>(decomp);
			lPowers.sort(reverse);
			int[] powers=lPowers.stream().mapToInt(Integer::intValue).toArray();
			long tmpResult=addResults(powers,primes3kPlus1,additionalFactors,logL2);
			System.out.println(Arrays.toString(powers)+" => "+tmpResult+".");
			result+=tmpResult;
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
