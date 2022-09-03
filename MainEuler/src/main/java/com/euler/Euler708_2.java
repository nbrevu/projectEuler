package com.euler;

import java.math.RoundingMode;

import com.euler.common.Primes;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongLongMap;
import com.koloboke.collect.map.hash.HashLongLongMaps;

public class Euler708_2 {
	/*
	 * ACHTUNG! Lo que quería hacer no funciona. Repensarse la inclusión-exclusión. Quizá almacenar para cada número un par
	 * (primos totales/primos únicos) o el coeficiente de inclusión-exclusión o algo así.
	 */
	private final static long LIMIT=LongMath.pow(10l,13);
	
	private static class PowerOf2Solver	{
		private final int[] firstPrimes;
		private final LongLongMap cache;
		public PowerOf2Solver(int firstPrimeLimit)	{
			firstPrimes=Primes.firstPrimeSieve(firstPrimeLimit);
			cache=HashLongLongMaps.newMutableMap();
		}
		public long solve(long in)	{
			return cache.computeIfAbsent(in,this::calculate);
		}
		private long calculate(long in)	{
			// ZUTUN! TODO! TEHDÄ!!!!!
			return 0;
		}
		private int möbius(int in)	{
			// ZUTUN! Mejor hacer una criba y guardarla. Esto se va a reusar.
			int result=1;
			int pointer=in;
			int lastPrime=0;
			for (;;)	{
				int prime=firstPrimes[pointer];
				if (prime==0) return result;
				else if (prime==lastPrime) return 0;	// This number is not square-free!
				else	{
					result=-result;
					lastPrime=prime;
				}
			}
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		for (int i=1;i<=14;++i)	{
			long result=Primes.countPrimes(LongMath.pow(10l,i),LongMath.pow(10l,Math.min(i,8)));
			System.out.println("π(10^"+i+")="+result+".");
		}
		long result=new PowerOf2Solver((int)LongMath.sqrt(LIMIT,RoundingMode.UP)).calculate(LIMIT);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		//System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
