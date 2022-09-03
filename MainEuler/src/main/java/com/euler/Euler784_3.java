package com.euler;

import java.math.BigInteger;

import com.euler.common.EulerUtils.Pair;
import com.euler.common.Primes.PrimeDecomposer;
import com.euler.common.Primes.StandardPrimeDecomposer;
import com.euler.common.alpertron.Diophantine2dSolver;
import com.euler.common.alpertron.DiophantineSolution;
import com.google.common.math.IntMath;

public class Euler784_3 {
	private final static int LIMIT=2*IntMath.pow(10,6);
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		PrimeDecomposer decomposer=new StandardPrimeDecomposer(IntMath.pow(10,9));
		long sum=0;
		for (int p=2;p<=LIMIT;++p) for (DiophantineSolution solSet:Diophantine2dSolver.solve(0,-1,0,p,-p,1,decomposer)) for (Pair<BigInteger,BigInteger> sol:solSet)	{
			long q=sol.first.longValue();
			if (q<=p) continue;
			sum+=p+q;
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(sum);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
