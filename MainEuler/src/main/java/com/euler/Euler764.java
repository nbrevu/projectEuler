package com.euler;

import java.math.RoundingMode;

import com.euler.common.PythagoreanTriples.PrimitiveTriplesIterator;
import com.google.common.math.LongMath;

public class Euler764 {
	private final static long LIMIT=LongMath.pow(10l,4);
	/*
	 * y^4 = z^2 - 16x^2
	 * y^4 = (z+4x)*(z-4x)
	 * 
	 * Primera prueba: iterar sobre y.
	 * 
	 * Segunda prueba: ternas pitagóricas.
	 * ACHTUNG! Éste parece ser el camino.
	 * 	Ternas pitagóricas: m y n coprimos, uno de ellos par; a=m^2-n^2, b=2mn, c=m^2+n^2.
	 *  Como m ó n es par, b=2mn siempre es múltiplo de 4.
	 *  Luego podemos hacer x=(mn)/2.
	 *  Por lo tanto ha de ser y^2=a=m^2-n^2.
	 *  Es decir... y^2+n^2=m^2.
	 *  Podemos usar ternas pitagóricas para generar y. Esto tiene buena pinta :).
	 *  Por las propiedades de las ternas pitagóricas, m es impar. Esto quiere decir que n tiene que ser par para que (m,n) genere una terna primitiva.
	 *  En consecuencia, ¿y es siempre impar? Pero el ejemplo del problema dice que y=4 en un caso...
	 * x=3, y=4, z=20: 16*9+256 = 144+256=400, OK.
	 * La terna pitagórica es (12,16,20) y no es primitiva, claro.
	 * x=10, y=3, z=41: 1600+81=1681. (40,9,41) sí es una terna primitiva.
	 */
	public static void main(String[] args)	{
		PrimitiveTriplesIterator iterator=new PrimitiveTriplesIterator();
		long sum=0;
		for (;;)	{
			iterator.next();
			long a=iterator.a();
			long b=iterator.b();
			long c=iterator.c();
			long y=a;
			long x=(b*c)/2;
			long y2=y*y;
			long z2=16*x*x+y2*y2;
			long z=LongMath.sqrt(z2,RoundingMode.UNNECESSARY);
			if (z<LIMIT)	{
				System.out.println("OH NEIN! ICH HABE DAS TRIPLE GEFUNDEN!!!!! ODER????? x="+x+", y="+y+", z="+z+".");
				sum+=x+y+z;
			}	else if (iterator.isSmallestN()) break;
			long yAlt2=4*b;
			long yAlt=LongMath.sqrt(yAlt2,RoundingMode.DOWN);
			if (yAlt*yAlt==yAlt2)	{
				long xAlt=a;
				long zAlt=4*c;
				System.out.println("WAS IST DAS????? DIE SPEZIELLE TRIPLE!!!!! x="+xAlt+", y="+yAlt+", z="+zAlt+".");
				sum+=xAlt+yAlt+zAlt;
			}
		}
		System.out.println(sum);
	}
}
