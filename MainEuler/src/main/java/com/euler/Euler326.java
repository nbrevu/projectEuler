package com.euler;

import java.util.Arrays;

public class Euler326 {
	public static void main(String[] args)	{
		int n=200;
		long[] result=new long[n];
		long[] result10=new long[n];
		result[0]=1;
		result10[0]=1;
		long sum=0;
		for (int i=1;i<n;++i)	{
			sum+=result[i-1]*i;
			result[i]=sum%(i+1);
			result10[i]=result[i]%10;
		}
		System.out.println(Arrays.toString(result));
		System.out.println(Arrays.toString(result10));
	}
}
