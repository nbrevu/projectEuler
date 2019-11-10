package com.euler;

import com.euler.common.Timing;

public class Euler163 {
	private final static int N=36;
	
	private static long solve()	{
		long n2=N*N;
		long n3=n2*N;
		long a1=(2*n3+5*n2+2*N)/8;
		long a2=2*((3*n3-N)/6);
		long a3=n3+3*n2+2*N;
		long a4=6*((2*n3+5*n2+2*N)/8);
		long tmp56=2*n3+3*n2-3*N;
		long a5=6*(tmp56/18);
		long a6=6*(tmp56/10);
		long a7=3*((22*n3+45*n2-4*N)/48);
		return a1+a2+a3+a4+a5+a6+a7;
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler163::solve);
	}
}
