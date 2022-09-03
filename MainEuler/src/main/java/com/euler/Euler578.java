package com.euler;

import java.util.ArrayList;
import java.util.List;

import com.euler.common.Primes;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class Euler578 {
	private final static long LIMIT=10000000000000l;
	private final static List<Long> primes=Primes.listLongPrimes((long)Math.sqrt((double)(LIMIT/2)));
	private final static List<Long> pSquares=Lists.transform(primes, new Function<Long,Long>()	{
		@Override
		public Long apply(Long in)	{
			return in*in;
		}
	});
	
	public static void main(String[] args)	{
		 int N=primes.size();
		 System.out.println("Tengo "+N+" primos.");	// 165141
		 List<Long> factors=new ArrayList<>();
		 for (int i=0;i<N;++i)	{
			 long prime=primes.get(i);
			 if (prime*prime*prime>LIMIT) break;
			 for (int j=i+1;j<N;++j)	{
				 long square=pSquares.get(j);
				 if (prime<=LIMIT/square) factors.add(prime*square);
				 else break;
			 }
		 }
		 factors.sort(null);
		 for (int i=0;i<100;++i) System.out.println(factors.get(i));
		 // TODO! Esto no es suficiente. P.ej. 18 cumple, pero no todos sus m�ltiplos lo hacen: GOTO 36.
		 
		 // Otra forma de hacerlo: buscar todas las posibles combinaciones:
		 // p1^e1 * p2^e2 * ...
		 // Y luego rellenar con primos.
		 // Nota: 2*3*5*7*11*13*17*19*23*29*31*37*43 ya pasa de 10^13. Por lo tanto, nunca entran m�s de 12 primos.
	}
}
