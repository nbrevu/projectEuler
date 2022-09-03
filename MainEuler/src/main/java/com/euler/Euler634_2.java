package com.euler;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler634_2 {
	// private final static long LIMIT=9000000000000000000l;
	private final static long LIMIT=3000000l;
	
	private static class SquareFreeCalculator	{
		private List<Long> primes;
		public SquareFreeCalculator(long max)	{
			primes=Primes.listLongPrimes(max);
		}
		public boolean isSquareFree(long in)	{
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
	
	public static void main(String[] args)	{
		List<Long> cubes=getCubesUpTo(LIMIT);
		Set<Long> result=new HashSet<>();
		for (long c:cubes)	{
			long tmpLimit=LongMath.sqrt(LIMIT/c,RoundingMode.DOWN);
			// System.out.println(""+c+" - "+result.size()+" - "+tmpLimit+"...");
			for (long s=2;;++s)	{
				long s2=s*s;
				if (s2>LIMIT/c) break;
				long sqube=c*s2;
				if (result.contains(sqube)) System.out.println("Repetido: "+sqube);
				result.add(sqube);
			}
		}
		System.out.println(result.size());
	}
}
