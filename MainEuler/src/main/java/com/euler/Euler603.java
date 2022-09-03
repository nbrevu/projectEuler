package com.euler;

import com.euler.common.LongMatrix;

public class Euler603 {
	/*
	 * El estado se compone de tres variables: (x0,x1,x2).
	 * x0 = suma total hasta ahora.
	 * x1 = suma de todos los números que pueden ser concatenados.
	 * x2 = cantidad total de números que pueden ser encadenados.
	 * 
	 * Evolución al añadir un dígito más, D:
	 * x2(n+1) = x2(n)+1.
	 * x1(n+1) = 10*x1(n) + D*x2(n+1) = 10*x1(n) + D*x2(n) + D.
	 * x0(n+1) = x0(n) + x1(n+1) = x0(n) + 10*x1(n) + D*x2(n) + D.
	 * 
	 * Podemos simular las constantes añadiendo una variable falsa, x3(n) = 1.
	 */
	public static void main(String[] args)	{
		LongMatrix toMultiply=new LongMatrix(4);
		toMultiply.assign(0,0,1l);
		toMultiply.assign(0,1,10l);
		toMultiply.assign(1,1,10l);
		toMultiply.assign(2,2,1l);
		toMultiply.assign(2,3,1l);
		toMultiply.assign(3,3,1l);
		long[] digits=new long[] {2,0,2,4};
		LongMatrix matrix=new LongMatrix(4);
		for (int i=0;i<4;++i) matrix.assign(i,i,1l);
		for (long l:digits)	{
			toMultiply.assign(0,2,l);
			toMultiply.assign(0,3,l);
			toMultiply.assign(1,2,l);
			toMultiply.assign(1,3,l);
			matrix=toMultiply.multiply(matrix);
			System.out.println(matrix);
		}
		System.out.println(matrix.get(0,0));
	}
}
