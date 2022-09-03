package com.euler;

import java.util.Arrays;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.euler.common.SumOfTotientCalculator;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;

public class Euler432_5 {
	private static int getTotient(int x)	{
		int[] firstPrimes=Primes.firstPrimeSieve(x+1);
		DivisorHolder divs=DivisorHolder.getFromFirstPrimes(x,firstPrimes);
		return (int)divs.getTotient();
	}
	
	private static boolean areAllFactorsIncluded(int n,int[] primes)	{
		for (int p:primes) while ((n%p)==0) n/=p;
		return n==1;
	}
	
	public static void main(String[] args)	{
		// Funciona :D:D:D.
		int[] primes=new int[] {2,3,5};
		int composite=Arrays.stream(primes).reduce(1,(int a,int b)->a*b);
		int N=80;
		IntIntMap fs=HashIntIntMaps.newMutableMap();
		SumOfTotientCalculator c=SumOfTotientCalculator.getWithoutMod();
		int sum=0;
		for (int i=1;i<=N;++i) if (areAllFactorsIncluded(i,primes))	{
			int factor=getTotient(i*composite);
			for (int j=1;j<i;++j) if ((i%j)==0) factor-=fs.get(j)*getTotient(i/j);
			fs.put(i,factor);
			sum+=factor*c.getTotientSum(N/i);
		}
		System.out.println("Inclusión-exclusión: "+sum+".");
		int[] firstPrimes=Primes.firstPrimeSieve(composite*N+1);
		int otherSum=0;
		for (int i=1;i<=N;++i)	{
			DivisorHolder divs=DivisorHolder.getFromFirstPrimes(composite*i,firstPrimes);
			otherSum+=divs.getTotient();
		}
		System.out.println("Valor real: "+otherSum+".");
		System.out.println("Esto es rarunesco, oder?");
		System.out.println(fs);
	}
}