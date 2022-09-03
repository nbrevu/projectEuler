package com.euler;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.euler.common.SumOfTotientCalculator;

public class Euler432_4 {
	private static int getTotient(int x)	{
		int[] firstPrimes=Primes.firstPrimeSieve(x+1);
		DivisorHolder divs=DivisorHolder.getFromFirstPrimes(x,firstPrimes);
		return (int)divs.getTotient();
	}
	
	public static void main(String[] args)	{
		// ¿CREO que ya lo tengo?
		SumOfTotientCalculator c=SumOfTotientCalculator.getWithoutMod();
		int N=15;
		int tot6_1=getTotient(6*1);
		int tot6_2=getTotient(6*2);
		int tot6_3=getTotient(6*3);
		int tot6_4=getTotient(6*4);
		int tot6_6=getTotient(6*6);
		int tot6_8=getTotient(6*8);
		int tot6_9=getTotient(6*9);
		int tot6_12=getTotient(6*12);
		int f1=tot6_1;
		int f2=tot6_2-f1;
		int f3=tot6_3-2*f1;
		int f4=tot6_4-f2-2*f1;
		int f6=tot6_6-f3-2*f2-2*f1;
		int f8=tot6_8-f4-2*f2-4*f1;
		int f9=tot6_9-2*f3-6*f1;
		int f12=tot6_12-f6-2*f4-2*f3-2*f2-4*f1;
		int sum=0;
		sum+=f1*c.getTotientSum(N/1);
		sum+=f2*c.getTotientSum(N/2);
		sum+=f3*c.getTotientSum(N/3);
		sum+=f4*c.getTotientSum(N/4);
		sum+=f6*c.getTotientSum(N/6);
		sum+=f8*c.getTotientSum(N/8);
		sum+=f9*c.getTotientSum(N/9);
		sum+=f12*c.getTotientSum(N/12);
		System.out.println("Inclusión-exclusión: "+sum+".");
		int[] firstPrimes=Primes.firstPrimeSieve(6*N+1);
		int otherSum=0;
		for (int i=1;i<=N;++i)	{
			DivisorHolder divs=DivisorHolder.getFromFirstPrimes(6*i,firstPrimes);
			otherSum+=divs.getTotient();
		}
		System.out.println("Valor real: "+otherSum+".");
	}
}