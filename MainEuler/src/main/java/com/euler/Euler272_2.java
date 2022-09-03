package com.euler;

import java.util.HashSet;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import com.euler.common.Primes;

public class Euler272_2 {
	/*
	 * Here are the basic, general facts:
	 * 1) primes of the form p=6k+1 always verify that p^n has 3 cubic roots, no matter how much is n.
	 * 2) primes of the form p=3k+2, i.e., both p=2 and primes of the form p=6k+5 always verify that p^n has 1 cubic root.
	 * 3) for p=3, we have a special case. 3 has one cubic root, but for n>=2, 3^n has three cubic roots.
	 * 4) if A has a cubic roots, B has b cubic roots and gcd(A,B)=1, then A*B has ab cubic roots.
	 * 
	 * Now some other shit related to this problem:
	 *  1) the smallest valid number is 7*9*13*19*31=482391.
	 *  2) therefore the smooth numbers without cubic roots must be up to 1e11/482391 which is a very reasonable 207300.
	 *  3) there is another subset of smooth numbers NOT including 3. These will be multiplied with numbers equal or bigger to
	 *  	7*13*19*31*37=1983163, so we only need to generate up to 50424.
	 *  4) as for the smooth numbers with cubic roots: since 7*9*13*19=15561 we need to generate up to 1e11/15561=6426322. Bigger but doable.
	 */
	private static int addSmooth(NavigableSet<Long> existing,long factor,long limit)	{
		Set<Long> toAdd=new HashSet<>();
		for (long n:existing)	{
			long newNumber=n*factor;
			if (newNumber>limit) break;
			do	{
				toAdd.add(newNumber);
				newNumber*=factor;
			}	while (newNumber<=limit);
		}
		existing.addAll(toAdd);
		return toAdd.size();
	}
	
	private static NavigableSet<Long> getSmooth3kPlus2(long limit,boolean include3)	{
		boolean[] composites=Primes.sieve((int)limit);
		NavigableSet<Long> result=new TreeSet<>();
		result.add(1l);
		if (include3) result.add(3l);
		addSmooth(result,2l,limit);
		for (int i=5;i<composites.length;i+=6) if (!composites[i])	{
			if (addSmooth(result,i,limit)==0) break;
		}
		return result;
	}
	
	public static void main(String[] args)	{
		System.out.println(getSmooth3kPlus2(207300,true).size());
		System.out.println(getSmooth3kPlus2(207300,false).size());
	}
}
