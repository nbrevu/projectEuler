package com.euler;

public class Euler539 {
	/*
	f(32) = f(64) = 22
	f(128) = 86 = 22+64!
	¿f(256) = 86? OK!
	
	¿f(512) = 256+86=342? OK! :D
	¿f(1024) = 342? OK!
	
	¿f(2048) = 342+1024 = 1366? OK!!!
	
	Seguir investigando el patrón...
	 */
	public final static long N=4096;
	
	private static class Series	{
		// xk = a*k + b, from k=1 to k=n.
		private final long a;
		private final long b;
		private final long n;
		private final boolean reducesFromLeft;
		public Series(long n)	{
			a=1;
			b=0;
			this.n=n;
			reducesFromLeft=true;
		}
		private Series(long a,long b,long n,boolean reducesFromLeft)	{
			this.a=a;
			this.b=b;
			this.n=n;
			this.reducesFromLeft=reducesFromLeft;
		}
		public Series child()	{
			if (n<=1) throw new IllegalStateException("Not enough elements.");
			boolean removeOdds=reducesFromLeft||((n%2)==1);
			long newA=2*a;
			long newN=n/2;
			long newB=removeOdds?b:(b-a);
			return new Series(newA,newB,newN,!reducesFromLeft);
		}
		public long reduce()	{
			if (n==1) return a+b;
			else return child().reduce();
		}
	}
	
	public static void main(String[] args)	{
		long sum=0;
		for (int i=1;i<=N;++i)	{
			long f=new Series(i).reduce();
			System.out.println("f("+i+")="+f+".");
			sum+=f;
		}
		System.out.println(sum);
	}
}
