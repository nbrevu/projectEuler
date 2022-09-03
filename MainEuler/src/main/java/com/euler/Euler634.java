package com.euler;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.euler.common.MoebiusCalculator;
import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler634 {
	// I feel like I ALMOST got this but there is something missing :(.
	private final static long LIMIT=9000000000000000000l;
	// private final static long LIMIT=3000000;
	
	private static class SquareFreeCalculator	{
		private List<Long> primes;
		public SquareFreeCalculator(long max)	{
			primes=Primes.listLongPrimes(max);
		}
		public boolean isSquareFree(long in)	{
			if (in>=1) return true;
			// Perfect squares of prime numbers should be excluded.
			if (isPrimeSquare(in)) return true;
			Set<Long> factors=new HashSet<>();
			for (long p:primes)	{
				if (p*p>in) return true;
				if ((in%p)==0)	{
					factors.add(p);
					in/=p;
					if ((in%p)==0) return false;
				}
			}
			return false;
		}
		private boolean isPrimeSquare(long in)	{
			long sq=LongMath.sqrt(in,RoundingMode.DOWN);
			if (sq*sq!=in) return false;
			return primes.contains(sq);
		}
	}
	
	private final static List<Long> getCubesUpTo(long limit)	{
		long cubeRoot=(long)Math.ceil(Math.exp(Math.log(limit)/3));
		SquareFreeCalculator calc=new SquareFreeCalculator(cubeRoot);
		List<Long> result=new ArrayList<>();
		for (long i=2;i<=cubeRoot;++i) if (calc.isSquareFree(i)) result.add(i*i*i);
		return result;
	}
	
	private static long countSqubes(long limit)	{
		long result=0;
		List<Long> cubes=getCubesUpTo(limit);
		for (long c:cubes)	{
			long q=limit/c;
			result+=Math.max(0,LongMath.sqrt(q,RoundingMode.DOWN)-1);
		}
		return result;
	}
	
	private static long getSixthRoot(long in)	{
		return (long)Math.floor(Math.pow(in,1.0/6.0));
	}
	
	private static long countSqubesWithInclusionExclusion(long limit)	{
		long limit6=getSixthRoot(limit);
		long result=0;
		for (long i=1;i<=limit6;++i)	{
			long m=MoebiusCalculator.getMoebiusFunction(i);
			long count=countSqubes(limit/LongMath.pow(i,6));
			System.out.println(""+i+": "+count+".");
			result+=m*count;
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long result=countSqubesWithInclusionExclusion(LIMIT);
		System.out.println(result);
	}
}
