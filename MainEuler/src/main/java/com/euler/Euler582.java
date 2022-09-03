package com.euler;

public class Euler582 {
	private final static long LIMIT=1000;
	
	/*
	 * Ternas de Eisenstein, a=m^2+mk+k^2, b=2mn+n^2, c=m^2-n^2. Redefinimos m=n+k con k>0. Las condiciones del problema se reducen a:
	 * |3n^2-k^2|<=100; 3n^2+3nk+k^2<=LIMIT.
	 * 
	 * Esto se puede hacer con ALPERTRON!!!!! "Sólo" son 201 ecuaciones. ¿Es esto un acicate suficiente como para implementarlo YO de una puta vez?
	 * (Spoiler, probablemente no).
	 * (Re-spoiler: estamos en octubre y he llegado a mi meta de este año. Merece la pena invertir el tiempo extra. ALPERTRON 4EVAH!).
	 * (Bueno, lo primero sería que la fórmula de arriba funcionara y tal. Pero NEIN.
	 */
	public static void main(String[] args)	{
		//*
		long count=0;
		for (long n=1;n*n<LIMIT;++n)	{
			long minK=(long)Math.ceil(Math.sqrt(Math.max(0,3*n*n-100)));
			long maxK=(long)Math.floor(Math.sqrt(3*n*n+100));
			for (long k=minK;k<=maxK;++k)	{
				long c=3*n*(n+k)+k*k;
				if (c<=LIMIT) ++count;
				else break;
			}
		}
		/*/
		long count=0;
		for (long n=1;n*n<LIMIT;++n)	{
			for (long k=1;;++k)	{
				if (3*n*n-k*k<-100) break;
				else if ((3*n*n-k*k<=100)&&(3*n*n+3*n*k+k*k<=LIMIT)) ++count;
			}
		}
		//*/
		System.out.println(count);	// ¿97? :(. Debería salir 235. El desarrollo debe de estar mal.
	}
}
