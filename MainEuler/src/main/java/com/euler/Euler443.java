package com.euler;

import java.math.RoundingMode;
import java.util.List;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler443 {
	/*
	 * Sorprendentemente fácil. En todo momento se verifica que o bien el MCD es 1 o bien el valor es igual
	 * a 3*posición. En este caso, sea N la posición. Entonces, obtenemos el menor factor primo de N, p.
	 * El siguiente número que verifica valor==3*posición está en N+1+((p-3)/2). Entre estas dos posiciones,
	 * la diferencia de dos valores consecutivos siempre es 1. Por tanto, podemos ir pegando saltos, y en
	 * cuanto nos pasemos del límite, hallar el resultado final sumando al último valor la diferencia entre
	 * el límite buscado y la última posición en la que valor==3*posición.
	 */
	private final static long LIMIT=LongMath.pow(10,15);
	
	private static class Factorizer	{
		private final List<Long> primes;
		public Factorizer(long limit)	{
			primes=Primes.listLongPrimes(LongMath.sqrt(limit,RoundingMode.DOWN));
		}
		public long getNextNumber(long in)	{
			// Assumes in is odd.
			for (long p:primes) if ((in%p)==0) return (p-3)/2;
			return (in-3)/2;
		}
	}
	
	private static class State	{
		public final Factorizer factorizer;
		public long position;
		public long value;
		public State(Factorizer factorizer)	{
			this.factorizer=factorizer;
			position=4;
			value=13;
		}
		public boolean next()	{
			// I thought that this would work, but it doesn't.
			if (((position%3)==0)&&(value==3*position))	{
				long k=factorizer.getNextNumber(2*position-1);
				position+=k+1;
				value=3*position;
				return true;
			}	else	{
				++position;
				value+=EulerUtils.gcd(position,value);
				return false;
			}
		}
		@Override
		public String toString()	{
			return "g("+position+")="+value;
		}
	}
	
	public static void main(String[] args)	{
		Factorizer factorizer=new Factorizer(LIMIT);
		State state=new State(factorizer);
		long tentative=0;
		for (;;)	{
			state.next();
			if (state.position>=LIMIT) break;
			else tentative=LIMIT-state.position+state.value;
		}
		System.out.println(tentative);
	}
}
