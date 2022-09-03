package com.euler;

import com.euler.common.EulerUtils;

public class Euler402 {
	private static long getMaxCommonFactor(long a,long b,long c)	{
		long result=a+b+c+1;
		for (long n=2;;++n)	{
			long polyval=n*(c+n*(b+n*(a+n)));
			result=EulerUtils.gcd(result,polyval);
			if (result<=n) return result;
		}
	}
	
	public static void main(String[] args)	{
		/*
		long sum=0;
		for (long a=1;a<=10;++a) for (long b=1;b<=10;++b) for (long c=1;c<=10;++c)	{
			long result=getMaxCommonFactor(a,b,c);
			System.out.println("n^4+"+((a==1)?"":(""+a))+"n^3+"+((b==1)?"":(""+b))+"n^2+"+((c==1)?"":(""+c))+"n: "+result);
			sum+=result;
		}
		System.out.println(sum);
		*/
		long max=0;
		long maxA=0,maxB=0,maxC=0;
		long L=1000;
		for (long a=1;a<=L;++a) for (long b=1;b<=L;++b) for (long c=1;c<=L;++c)	{
			long result=getMaxCommonFactor(a,b,c);
			//System.out.println("n^4+"+((a==1)?"":(""+a))+"n^3+"+((b==1)?"":(""+b))+"n^2+"+((c==1)?"":(""+c))+"n: "+result);
			if (result>max)	{
				max=result;
				maxA=a;
				maxB=b;
				maxC=c;
			}
		}
		System.out.println("Max: "+max+" ("+maxA+", "+maxB+", "+maxC+")");
	}
}
