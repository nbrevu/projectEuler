package com.euler;

import com.euler.common.LongMatrix;
import com.google.common.math.LongMath;

public class Euler377 {
	private final static long MOD=LongMath.pow(10l,9);
	private final static long BASE=13l;
	private final static int MAX_EXP=17;
	
	/*-
	 * Esto es similar a lo que estoy haciendo para los números estrobogramáticos :O.
	 * Sea C(x) la cantidad de números cuya suma es x, y S(x) la suma de todos ellos.
	 * Entonces:
	 * C(x)=C(x-1)+C(x-2)+...+C(x-9).
	 * S(x)=10*S(x-1)+1*C(x-1) + 10*S(x-2)+2*C(x-2) + ... + 10*S(x-9)+9*C(x-9).
	 * Para x=1, C(1)=1, S(x)=1.
	 * Para x=0, C(0)=1, S(x)=0 (esto se hace para poder hacer transiciones como 0->2 y tal).
	 * Para x<0, C(x)=0, S(x)=0.
	 * 
	 * Necesito un vector de 18 elementos: [C(x) S(x) C(x-1) S(x-1) ... C(x-8) S(x-8)].
	 * Sea M la matriz de transición de este estado. Multiplicamos M^N por el estado inicial para x=0 (un vector de 18 elementos, todos nulos salvo
	 * el primero, que es igual a 1, correspondiente a C(0)) y el SEGUNDO elemento del resultado es el S(x) que buscamos. Podemos simplificar
	 * calculando la potencia de la matriz y obteniendo el elemento M_{2,1} (primer elemento de la segunda fila).
	 */
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		LongMatrix m=new LongMatrix(18);
		for (int i=1;i<=9;++i)	{
			int prevIdx=2*(i-1);
			m.assign(0,prevIdx,1l);
			m.assign(1,prevIdx,i);
			m.assign(1,1+prevIdx,10l);
		}
		for (int i=1;i<9;++i)	{
			m.assign(2*i,2*(i-1),1l);
			m.assign(2*i+1,2*i-1,1l);
		}
		long result=0l;
		long tac=System.nanoTime();
		for (int i=1;i<=MAX_EXP;++i)	{
			m=m.pow(BASE,MOD);
			result+=m.get(1,0);
		}
		result%=MOD;
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
