package com.euler;

public class Euler396 {
	// Brute force.
	private static long nextTerm(long in,long base)	{
		long base1=1+base;
		long result=0;
		long term=1;
		while (in>0)	{
			long mod=(in%base);
			result+=mod*term;
			in/=base;
			term*=base1;
		}
		return result-1;
	}
	
	private static long getSeqLength(long n)	{
		long result=1;
		long num=n;
		long base=2;
		for (;;)	{
			num=nextTerm(num,base);
			if (num==0) return result;
			++result;
			++base;
		}
	}
	
	public static void main(String[] args)	{
		for (long n=1;n<=7;++n)	{
			long res=getSeqLength(n);
			System.out.println("G("+n+")="+res);
		}
	}
}
