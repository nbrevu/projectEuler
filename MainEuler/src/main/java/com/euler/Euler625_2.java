package com.euler;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.euler.common.Primes;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.math.LongMath;

public class Euler625_2 {
	private final static long START=500000000l;
	private final static long END=100000000000l;
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		// Así no es factible, se queda pillado tras "Después de añadir el 353, tengo 345563815 números.".
		List<Long> primes=Primes.listLongPrimes(LongMath.sqrt(END,RoundingMode.DOWN));
		System.out.println(primes);
		System.out.println(primes.size());
		List<Long> allTheNumbers=new ArrayList<>();
		allTheNumbers.add(1l);
		for (long p:primes)	{
			List<Long> newNumbers=new ArrayList<>();
			for (long n:allTheNumbers) for (;;)	{
				n*=p;
				if (n>END) break;
				newNumbers.add(n);
			}
			allTheNumbers.addAll(newNumbers);
			newNumbers.clear();
			System.out.println("Despu�s de a�adir el "+p+", tengo "+allTheNumbers.size()+" n�meros.");
		}
		Collection<Long> finalSet=Collections2.filter(allTheNumbers, new Predicate<Long>()	{
			@Override
			public boolean apply(Long arg) {
				return arg>START;
			}
		});
		System.out.println("Al final me queda una lista con "+finalSet.size()+" cosicas.");
		long tac=System.nanoTime();
		double seconds=(tac-tic)/1e9;
		System.out.println("Pues parece que no, pero entre una cosa y otra me he tirado "+seconds+" segundos con la tonter�a �sta.");
	}
}
