package com.euler;

public class Euler760_2 {
	public static void main(String[] args)	{
		for (int n=0;n<=100;++n)	{
			long sum=0;
			for (int k=0;k<=n;++k)	{
				int nk=n-k;
				sum+=k^nk;
				sum+=k|nk;
				sum+=k&nk;
			}
			System.out.println("sum(n="+n+")="+sum+".");
		}
	}
}
