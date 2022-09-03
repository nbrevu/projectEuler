package com.euler;

import com.euler.common.EulerUtils.CombinatorialNumberCache;

public class Euler792_3 {
	public static void main(String[] args)	{
		CombinatorialNumberCache combis=new CombinatorialNumberCache(43);
		for (int i=1;i<=21;++i)	{
			long sum=0;
			int upper=2*i+1;
			int lower=i+1;
			long factor=1;
			for (int j=lower;j<=upper;++j)	{
				sum+=factor*combis.get(upper,j);
				factor*=-2;
			}
			System.out.println(String.format("i=%d: sum=%d.",i,sum));
		}
	}
}
