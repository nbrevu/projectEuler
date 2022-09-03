package com.euler;

public class Euler692 {
	private static boolean alwaysLoses(long in,long take)	{
		if (take>=in) return false;
		long t2=2*take;
		in-=take;
		for (long i=1;i<=t2;++i) if (canWin(in,i)) return true;
		return false;
	}
	
	private static boolean canWin(long in,long take)	{
		if (take>=in) return true;
		long t2=2*take;
		in-=take;
		for (long i=1;i<=t2;++i) if (!alwaysLoses(in,i)) return false;
		return true;
	}
	
	private static long simulate(long in)	{
		for (long i=1;;++i) if (canWin(in,i)) return i;
	}
	
	public static void main(String[] args)	{
		// H(1)=1, H(4)=1, H(17)=1, H(8)=8 and H(18)=5
		/*
		long[] toTry=new long[] {1,4,17,8,18};
		for (long i:toTry) System.out.println("H("+i+")="+simulate(i)+".");
		*/
		long sum=0l;
		for (long i=1;i<=34;++i)	{
			long h=simulate(i);
			System.out.println("H("+i+")="+h+".");
			sum+=h;
		}
		System.out.println(sum);
	}
}
