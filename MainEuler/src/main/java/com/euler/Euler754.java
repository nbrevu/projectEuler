package com.euler;

import com.euler.common.CoprimeCounter;
import com.euler.common.EulerUtils;

public class Euler754 {
	private final static int LIMIT=100_000_000;
	private final static long MOD=1_000_000_007l;
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		CoprimeCounter counter=new CoprimeCounter(LIMIT);
		long result=1l;
		for (int i=2;i<LIMIT;++i)	{
			long exp=counter.countCoprimes(i,i,LIMIT);
			result=result*EulerUtils.expMod(i,exp,MOD);
			result%=MOD;
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
