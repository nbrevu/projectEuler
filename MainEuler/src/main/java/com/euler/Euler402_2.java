package com.euler;

public class Euler402_2 {
	private final static long N=10000;
	
	private static boolean isMultipleOf8(long a,long b,long c)	{
		a%=8;
		b%=8;
		c%=8;
		if (a==2)	{
			if (b==3) return c==2;
			else if (b==7) return c==6;
		}	else if (a==6)	{
			if (b==3) return c==6;
			else if (b==7) return c==2;
		}
		return false;
	}
	private static boolean isMultipleOf4(long a,long b,long c)	{
		a%=4;
		b%=4;
		c%=4;
		if (a==0)	{
			if (b==1) return c==2;
			else if (b==3) return c==0;
		}	else if (a==2)	{
			if (b==1) return c==0;
			else if (b==3) return c==2;
		}
		return false;
	}
	private static boolean isMultipleOf2(long a,long b,long c)	{
		return ((a+b+c)%2)==1;
	}
	private static boolean isMultipleOf3(long a,long b,long c)	{
		return ((b%3)==2)&&((a+c)%3)==0;
	}
	private static long getMValue2(long a,long b,long c)	{
		if (isMultipleOf8(a,b,c)) return 8;
		else if (isMultipleOf4(a,b,c)) return 4;
		else if (isMultipleOf2(a,b,c)) return 2;
		else return 1;
	}
	private static long getMValue3(long a,long b,long c)	{
		return isMultipleOf3(a,b,c)?3:1;
	}
	private static long getMValue(long a,long b,long c)	{
		return getMValue2(a,b,c)*getMValue3(a,b,c);
	}

	// Es gefällt mir!!!!!
	public static void main(String[] args)	{
		long[] cubic=new long[25];
		long[] quadratic=new long[25];
		long[] linear=new long[25];
		for (int a=1;a<=24;++a) for (int b=1;b<=24;++b) for (int c=1;c<=24;++c)	{
			long m=getMValue(a,b,c);
			int minABC=Math.max(Math.max(a,b),c);
			for (int i=minABC;i<=24;++i) cubic[i]+=m;
			int minAB=Math.max(a,b);
			for (int i=minAB;i<=24;++i) quadratic[i]+=m;
			int minAC=Math.max(a,c);
			for (int i=minAC;i<=24;++i) quadratic[i]+=m;
			int minBC=Math.max(b,c);
			for (int i=minBC;i<=24;++i) quadratic[i]+=m;
			for (int i=a;i<=24;++i) linear[i]+=m;
			for (int i=b;i<=24;++i) linear[i]+=m;
			for (int i=c;i<=24;++i) linear[i]+=m;
		}
		long main=cubic[24];
		long k=N/24;
		int r=(int)(N%24);
		long result=cubic[r]+k*(quadratic[r]+k*(linear[r]+k*main));
		System.out.println(result);
	}
}
