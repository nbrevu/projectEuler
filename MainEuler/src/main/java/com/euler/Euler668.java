package com.euler;

import java.math.RoundingMode;

import com.euler.common.MeisselLehmerPrimeCounter;
import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler668 {
	private final static long LIMIT=LongMath.pow(10l,10);
	
	// La respuesta es 2811077773 y éste es mi primer programa que usa con éxito el puto Meissel-Lehmer. QUE YA IBA TOCANDO.
	public static void main(String[] args)	{
		int sqrt=(int)(Math.ceil(Math.sqrt(LIMIT)));
		boolean[] composites=Primes.sieve(sqrt);
		long result=LIMIT;
		for (int i=2;i<=sqrt;++i) if (!composites[i]) result-=i;
		MeisselLehmerPrimeCounter counter=new MeisselLehmerPrimeCounter(Math.max(LongMath.sqrt(LIMIT, RoundingMode.DOWN),10000l));
		for (int i=1;i<sqrt;++i)	{
			long limInf=LIMIT/(i+1);
			long limSup=LIMIT/i;
			result-=(counter.pi(limSup)-counter.pi(limInf))*i;
		}
		System.out.println(result);
	}
}
