package com.euler;

import com.euler.common.EulerUtils;

public class Euler797_6 {
	// [0, 1, 3, 7, 5, 31, 3, 127, 17, 73, 11, 2047, 13, 8191, 43, 151, 257, 131071, 57, 524287, 205]
	public static void main(String[] args)	{
		long[] factors=new long[] {1,2,3,4,6,12};
		long[] primitives=new long[] {1,3,7,5,3,13};
		long sum=0;
		for (int i=0;i<64;++i)	{
			long prod=1;
			long lcm=1;
			for (int j=0;j<6;++j) if ((i&(1<<j))!=0)	{
				prod*=primitives[j];
				lcm=EulerUtils.lcm(lcm,factors[j]);
			}
			if (lcm==12) sum+=prod;
		}
		System.out.println(sum);
	}
}
