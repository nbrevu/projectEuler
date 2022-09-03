package com.euler;

public class Euler318 {
	private final static long LIMIT=2011;
	
	private final static double LOG10_2011=-LIMIT*Math.log(10.0);
	
	public static void main(String[] args)	{
		long sum=0;
		/*
		for (int i=3;i<=4021;i+=2)	{
			int i2_1=(i*i)-1;
			double fractionalPart=(double)i-Math.sqrt((double)i2_1);
			sum+=(long)(Math.ceil(LOG10_2011/Math.log(fractionalPart)));
			System.out.println(""+((i-1)/2)+"*"+((i+1)/2)+" => "+(long)Math.ceil(LOG10_2011/Math.log(fractionalPart)));
		}
		*/
		for (int p=1;p<=LIMIT-1;++p) for (int q=p+1;q<=LIMIT-p;++q)	{
			double spsq2=(double)(p+q)-2*Math.sqrt((double)(p*q));
			if ((spsq2<=0)||(1<=spsq2)) break;
			sum+=(long)(Math.ceil(LOG10_2011/Math.log(spsq2)));
		}
		System.out.println(sum);
	}
}
