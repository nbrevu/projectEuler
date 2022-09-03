package com.euler;

public class Euler724 {
	public static void main(String[] args)	{
		long n=100;
		long n2=n*n;
		long n3=n2*n;
		long n4=n2*n2;
		double term1=(n+1)*n4;
		double termI=2*(n2-n4);
		double termI2=n3-n2;
		double combi=1;
		double result=0;
		boolean sign=((n%2)==0);
		for (long i=1;i<=n-1;++i)	{
			combi=(combi*(n-i))/i;
			double frac1=i/(double)n;
			double num2=term1+i*(termI+i*termI2);
			double den2=2*i*Math.pow(n-i,3);
			double term=combi*Math.pow(frac1,n)*num2/den2;
			if (!sign) term=-term;
			System.out.println("Term for i="+i+": "+term+".");
			result+=term;
			sign=!sign;
		}
		System.out.println(result);
	}
}
