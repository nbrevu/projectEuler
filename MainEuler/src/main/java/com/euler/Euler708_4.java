package com.euler;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongIntCursor;

public class Euler708_4 {
	private final static long N=LongMath.pow(10l,2);
	
	public static void main(String[] args)	{
		/*
		 * IDIOTEN, SEHR IDIOTEN, ÜBER-IDIOTEN.
		 * Claro que no funciona. Estoy calculando una suma y necesito productos.
		 * Voy a tener que volver a intentar lo de la variante tocha del Meissel-Lehmer. Esto me puede llevar como un mes :|.
		 * Y, así y todo, probablemente me pete por memoria.
		 */
		// This doesn't work. I'm missing something.
		long result=0;
		for (long p:Primes.listLongPrimes(N)) for (long power=p;power<N;power*=p)	{
			System.out.println(power+"...");
			result+=N/power;
		}
		System.out.println(2*result+1);
		/*
		MeisselLehmerPrimeCounter counter=new MeisselLehmerPrimeCounter(LongMath.sqrt(N,RoundingMode.UP));
		List<Long> primesBelowSqrt=Primes.listLongPr
		int 
		*/
		{
			SortedMap<Long,Integer> factors=new TreeMap<>();
			int[] firstPrimes=Primes.firstPrimeSieve(100);
			for (int i=2;i<=100;++i)	{
				DivisorHolder divisors=DivisorHolder.getFromFirstPrimes(i,firstPrimes);
				for (LongIntCursor cursor=divisors.getFactorMap().cursor();cursor.moveNext();) factors.compute(cursor.key(),(Long unused,Integer prevSum)->cursor.value()+((prevSum==null)?0:prevSum.intValue()));
			}
			for (Map.Entry<Long,Integer> factor:factors.entrySet()) System.out.println(factor.getKey()+": "+factor.getValue()+".");
		}
	}
}
