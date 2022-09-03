package com.euler;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;

public class Euler699 {
	/*
	 * This is going to be FUN. The problem is complex but not super-complex. It's definitely doable, and the difficulty level is not absurd,
	 * BUUUUUT there is a lot that needs to be considered.
	 * 
	 * 1) σ(n)/n es una función multiplicativa (esto es trivialmente demostrable). Entonces, una forma de proceder es partir de un número
	 * conocido (read: una potencia de 3) y "sustituir". Por ejemplo: para n=9, σ(n)=13. Entonces podemos añadir un 13, en el cual σ(n)/n=14/13.
	 * Esto nos da un número, PERO ADEMÁS podemos seguir y añadir el 14 (¡o todos sus divisores!) -> y así seguimos indefinidamente.
	 * 2) PERO, ESO NO ES NADA, ¿QUÉ TÚ DEJAS PARA MAÑANA? PERO, ESO NO ES NADA, ¿QUÉ TÚ DEJAS PARA MAÑANA? Es gibt ein anderes schanantisches
	 * Ding, darum du musst Denken!!!!! Los números perfectos Y LOS MULTIPERFECTOS los puedo añadir cuando me salga de los cataplines.
	 * 
	 * En resumidas cuentas, esto pide una multilista zipeada. Asumo que no es imposible que los números salgan repetidos, así que:
	 * NAVIGABLEMAP, TE ELIJO A TI.
	 * 
	 * Comentario escrito sin la ayuda de drogas.
	 */
	private final static int LIMIT=1000000;
	
	private static boolean isPowerOf3(long in)	{
		if (in==1) return false;
		while ((in%3)==0) in/=3;
		return in==1;
	}
	
	public static void main(String[] args)	{
		int[] firstPrimes=Primes.firstPrimeSieve(LIMIT);
		long sum=0;
		for (int i=3;i<=LIMIT;++i)	{
			DivisorHolder factors=DivisorHolder.getFromFirstPrimes(i,firstPrimes);
			long sumOfDivisors=factors.getSumOfDivisors();
			long g=EulerUtils.gcd(i,sumOfDivisors);
			long b=i/g;
			if (isPowerOf3(b))	{
				int r=i;
				int p=0;
				while ((r%3)==0)	{
					r/=3;
					++p;
				}
				System.out.println("i="+i+"="+r+"·3^"+p+": s="+sumOfDivisors+"; q="+(sumOfDivisors/g)+"/"+b+".");
				sum+=i;
			}
		}
		System.out.println(sum);
	}
}
