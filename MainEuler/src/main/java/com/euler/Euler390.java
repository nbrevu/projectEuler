package com.euler;

import java.math.RoundingMode;

import com.google.common.math.LongMath;

public class Euler390 {
	private final static int LIMIT=1000000;
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int result=0;
		boolean doContinue=true;
		for (long m=1;doContinue;++m)	{
			long mm=m*m;
			for (long n=m;;++n)	{
				long nn=n*n;
				long rad=mm+nn+4*mm*nn;
				long sq=LongMath.sqrt(rad,RoundingMode.DOWN);
				if (sq>LIMIT)	{
					if (n==m) doContinue=false;
					break;
				}	else if (sq*sq==rad)	{
					System.out.println("m->"+m+"; n->"+n+"; A="+sq+".");
					result+=sq;
				}
			}
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
