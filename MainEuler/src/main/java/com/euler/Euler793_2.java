package com.euler;

public class Euler793_2 {
	private static long calculateWithBruteForce(long n)	{
		long pairs=(n*(n-1))/2;
		long p=(pairs-1)/2;
		long result=0;
		for (int x=0;x<n;++x) for (int y=x+1;y<n;++y)	{
			int lower=-1;
			for (int i=0;i<=x;++i) lower+=y-i;
			int upper=-1;
			for (int i=y;i<n;++i) upper+=i-x;
			if ((lower<=p)&&(upper<=p)) ++result;
		}
		return result;
	}
	
	private static long calculateWithFinesse(long n)	{
		long pairs=(n*(n-1))/2;
		long p=(pairs-1)/2;
		long result=0;
		for (long x=0;x<n;++x)	{
			long b=1+2*x;
			long c=2*p+2-n*n+n+2*x*n;
			long delta=b*b-4*c;
			long firstY=(delta<0)?(x+1):Math.max(x+1,(long)Math.ceil((b+Math.sqrt(delta))*0.5));
			double num=2*p+2+x*x+x;
			double den=2*x+2;
			long lastY=Math.min(n-1,(long)Math.floor(num/den));
			if (firstY<=lastY) result+=1+lastY-firstY;
		}
		return result;
	}
	
	public static void main(String[] args)	{
		for (int i=3;i<=103;i+=4)	{
			long a=calculateWithBruteForce(i);
			long b=calculateWithFinesse(i);
			System.out.println(String.format("%d: ¿%d==%d?",i,a,b));
		}
		System.out.println(calculateWithFinesse(1_000_003));
	}
}
