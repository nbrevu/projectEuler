package com.euler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.euler.common.Primes;

public class Euler549 {
	private final static int N=100000000;
	private static class Divisors	{
		private final Map<Integer,Integer> divisors;
		public Divisors()	{
			divisors=new HashMap<>();
		}
		public Divisors(Map<Integer,Integer> previous,Integer newFactor)	{
			divisors=new HashMap<>(previous);
			addFactorToMap(previous,newFactor);
		}
		public void addFactor(Integer factor)	{
			addFactorToMap(divisors,factor);
		}
		public Map<Integer,Integer> getDivisors()	{
			return divisors;
		}
		private static int getMinFactorialForPrime(int prime,int power)	{
			// Returns the smallest X such that (prime^power) divides X!.
			if (prime<=7) return powerCache.get(prime)[power];
			else return prime*power;
		}
		public long getMinFactorial()	{
			long result=0;
			for (Map.Entry<Integer,Integer> entry:divisors.entrySet())	{
				long fForP=getMinFactorialForPrime(entry.getKey(),entry.getValue());
				result=Math.max(result,fForP);
			}
			return result;
		}
		public static void addFactorToMap(Map<Integer,Integer> divisors,Integer newFactor)	{
			Integer power=divisors.get(newFactor);
			Integer newPower=(power==null)?1:(1+power);
			divisors.put(newFactor,newPower);
		}
		private static Map<Integer,int[]> powerCache;
		static	{
			powerCache=new HashMap<>();
			int[] powersOfTwo={0,2,4,4,6,8,8,8,10,12,12,14,16,16,16,16,18,20,20,22,24,24,24,26,28,28,30,32,32,32,32,32};
			powerCache.put(2,powersOfTwo);
			int[] powersOfThree={0,3,6,9,9,12,15,18,18,21,24,27,27,27,30,33,36,36,39,42,45,45};
			powerCache.put(3,powersOfThree);
			int[] powersOfFive={0,5,10,15,20,25,25,30,35,40,45,50,50,55,60};
			powerCache.put(5,powersOfFive);
			int[] powersOfSeven={0,7,14,21,28,35,42,49,49,56,63,70};
			powerCache.put(7,powersOfSeven);
		}
	}
	
	public static void oldMain(String[] args)	{
		Map<Integer,Integer> empty=Collections.emptyMap();
		Divisors[] allDivisors=new Divisors[N+1];
		int[] firstPrimes=Primes.firstPrimeSieve(N);
		allDivisors[1]=new Divisors();
		int result=0;
		for (int i=2;i<=N;++i)	{
			int p=firstPrimes[i];
			if (p==0) allDivisors[i]=new Divisors(empty,i);
			else allDivisors[i]=new Divisors(allDivisors[i/p].getDivisors(),p);
			result+=allDivisors[i].getMinFactorial();
		}
		System.out.println(result);
	}
	
	public static void main(String[] args)	{
		int[] firstPrimes=Primes.firstPrimeSieve(N);
		long result=0;
		for (int i=2;i<=N;++i)	{
			Divisors divs=new Divisors();
			int j=i;
			for (;;)	{
				if (j==1) break;
				int factor=firstPrimes[j];
				if (factor==0)	{
					divs.addFactor(j);
					break;
				}	else	{
					divs.addFactor(factor);
					j=j/factor;
				}
			}
			result+=divs.getMinFactorial();
		}
		System.out.println(result);
	}
}
