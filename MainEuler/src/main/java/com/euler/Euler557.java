package com.euler;

import java.math.RoundingMode;

import com.google.common.math.LongMath;

public class Euler557 {
	private final static long N=10000;
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=0;
		long maxA=N-3;
		long maxAB=N-2;
		long maxABC=N-1;
		for (long a=1;a<=maxA;++a)	{
			long a2=a*a;
			long a3=a2*a;
			long maxB=maxAB-a;
			for (long b=1;b<=maxB;++b)	{
				long ab=a+b;
				long maxC=LongMath.divide(a2,b,RoundingMode.UP)-1;
				for (long c=b;c<=maxC;++c)	{
					long abc=ab+c;
					if (abc>maxABC) break;
					long num=a3+a2*(b+c)+a*b*c;
					long den=a2-b*c;
					if (num>den*N) break;
					else if ((num%den)==0)	{
						long s=num/den;
						long d=s-(a+b+c);
						if (d>0) result+=s;
					}
				}
			}
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
