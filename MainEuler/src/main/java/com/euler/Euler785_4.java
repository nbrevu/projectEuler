package com.euler;

import java.math.BigInteger;
import java.util.List;

import com.euler.common.EulerUtils;
import com.euler.common.EulerUtils.Pair;
import com.euler.common.Primes;
import com.euler.common.Primes.PrimeDecomposer;
import com.euler.common.Primes.StandardPrimeDecomposer;
import com.euler.common.alpertron.Diophantine2dSolver;
import com.euler.common.alpertron.DiophantineSolution;
import com.google.common.math.IntMath;

public class Euler785_4 {
	private final static int N=IntMath.pow(10,9);
	
	private static class Checker	{
		private final int[] primes;
		public Checker(int n)	{
			primes=Primes.lastPrimeSieve(n);
		}
		public boolean isValid(int x)	{
			if ((x%3)==2) return false;
			while (x>1)	{
				int prime=primes[x];
				if (prime==0) return x%3!=2;
				else if ((prime%3)==2) return false;
				x/=prime;
			}
			return true;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		Checker checker=new Checker(N);
		PrimeDecomposer decomp=new StandardPrimeDecomposer(N);
		long sum=0;
		for (int q=67108863;q<N;q+=2) if (checker.isValid(q))	{
			// System.out.println(q+"...");
			List<DiophantineSolution> sols=Diophantine2dSolver.solve(3,0,1,-16l*q,0,-4l*q*q,decomp);
			for (DiophantineSolution d:sols) for (Pair<BigInteger,BigInteger> pair:d)	{
				if ((pair.first.signum()<=0)||(pair.second.signum()<0)) continue;
				long x=pair.first.longValueExact();
				long s=8*q-x;
				long sdelta=pair.second.longValueExact();
				long y=(s-sdelta)/2;
				if (x>y) continue;
				long z=(s+sdelta)/2;
				if ((z<=N)&&(EulerUtils.gcd(x,EulerUtils.gcd(y,z))==1))	{
					// System.out.println(String.format("q=%d: x=%d, y=%d, z=%d.",q,x,y,z));
					sum+=8*q;
				}
			}
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(sum);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
