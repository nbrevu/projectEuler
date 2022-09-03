package com.euler;

import java.util.Arrays;

import com.euler.common.EulerUtils.CombinatorialNumberCache;

public class Euler792_2 {
	public static void main(String[] args)	{
		CombinatorialNumberCache combis=new CombinatorialNumberCache(40);
		for (int i=1;i<=21;++i)	{
			long[] values=new long[i+1];
			long sum=0;
			for (int j=0;j<=i;++j)	{
				values[j]=combis.get(2*i-j,i);
				if ((j&1)==0) sum+=values[j];
				else sum-=values[j];
			}
			System.out.println(String.format("i=%d: sum=%d, values=%s.",i,sum,Arrays.toString(values)));
		}
	}
}
