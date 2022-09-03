package com.euler;

import com.euler.common.CoprimeCounter;

public class Euler787_3 {
	private final static int N=1_000_000_000;
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=1;	// This is (1,1). The other cases are symmetrical.
		int half=N/2;
		CoprimeCounter counter=new CoprimeCounter(half);
		for (int i=1;i<half;i+=2) result+=2*counter.countCoprimes(i,i,N-i);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
