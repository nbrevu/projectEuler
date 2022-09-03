package com.euler;

public class Euler327_4 {
	private static long getM(int c,int r)	{
		if (r<c) return r+1;
		long result=4;
		long div=c-2;
		long add=div-1; 
		for (int i=c+1;i<=r;++i)	{
			long q=(result+add)/div;	// Trick to get ceil(q/div) without conditionals.
			result+=2*q+1;
		}
		return result+c-1;
	}
	
	// 34315549139516
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=0;
		for (int c=3;c<=40;++c) result+=getM(c,30);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
