package com.euler;

import java.util.Locale;

public class Euler394 {
	private final static double X=40d;
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		double result=(7d/9d)+(2d/9d)/(X*X*X)+(2d/3d)*Math.log(X);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(String.format(Locale.UK,"%.10f",result));
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
