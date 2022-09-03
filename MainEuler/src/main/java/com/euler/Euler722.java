package com.euler;

public class Euler722 {
	private final static int K=7;
	private final static int P=15;
	
	private final static double Q=1-Math.pow(0.5,P);
	
	private final static int INTERVALS=10000000;
	
	public static void main(String[] args)	{
		// Me sale 3.3767927662443386E132, pero no es el resultado :(.
		/*
		 * Para k=7, P=15, el resultado que me sale es 6.725803486727e39, y el resultado correcto es 6.725803486744e39.
		 * Esos dos últimos decimales están mal.
		 * La solución, claramente, es GOTO MATHEMATICA.
		 * (La solución buena de verdad es usar polilogaritmos).
		 */
		int next=INTERVALS;
		double result=0.0;
		for (long d=1;;++d)	{
			double dK=Math.pow(d,K);
			double qD=Math.pow(Q,d);
			double addend=dK*qD/(1-qD);
			if (addend==0.0)	{
				System.out.println("Paro en d="+d+".");
				break;
			}
			result+=addend;
			if (d==next)	{
				System.out.println("Para d="+d+" el sumando es "+addend+" y la suma es "+result+".");
				next+=INTERVALS;
			}
		}
		System.out.println(result);
	}
}
