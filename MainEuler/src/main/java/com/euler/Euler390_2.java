package com.euler;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;

import com.euler.common.EulerUtils.Pair;
import com.euler.common.Primes.PrimeDecomposer;
import com.euler.common.Primes.StandardPrimeDecomposer;
import com.euler.common.alpertron.Diophantine2dSolver;
import com.euler.common.alpertron.DiophantineSolution;
import com.google.common.math.LongMath;

public class Euler390_2 {
	private final static long LIMIT=LongMath.pow(10l,10);

	/*
	 * 2919133642971
	 * Elapsed 58.1773249 seconds.
	 * 
	 * I CAN'T BELIVE (sic) THIS!
	 * Maybe I will dare finally fighting with 261.
	 */
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		BigInteger bigLimit=BigInteger.valueOf(LIMIT);
		PrimeDecomposer decomposer=new StandardPrimeDecomposer(100_000_000);
		long maxM=LongMath.sqrt(LIMIT/2,RoundingMode.UP);
		long result=0l;
		for (long m=1;m<=maxM;++m)	{
			long mm=m*m;
			BigInteger bigM=BigInteger.valueOf(m);
			List<DiophantineSolution> sols=Diophantine2dSolver.solve(4*mm+1,0,-1,0,0,mm,decomposer);
			for (DiophantineSolution sol:sols)	{
				for (Pair<BigInteger,BigInteger> tmpSol:sol)	{
					BigInteger n=tmpSol.first;
					BigInteger x=tmpSol.second;
					if ((n.signum()<=0)||(x.signum()<=0)) continue;
					else if (x.compareTo(bigLimit)>=0) break;
					else if (n.compareTo(bigM)<0) continue;
					else result+=x.longValueExact();
				}
			}
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
