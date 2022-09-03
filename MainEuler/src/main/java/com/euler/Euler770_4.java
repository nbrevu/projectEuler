package com.euler;

public class Euler770_4 {
	private final static double LIMIT=1.9999;
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		double quot=LIMIT/(2-LIMIT);
		long result=(long)Math.ceil((quot*quot)/Math.PI);
		//double result=(quot*quot)/Math.PI;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
