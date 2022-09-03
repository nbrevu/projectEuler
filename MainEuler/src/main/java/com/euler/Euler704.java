package com.euler;

import java.math.BigInteger;

import com.euler.common.BigIntegerUtils.BigCombinatorialNumberCache;

public class Euler704 {
	public static void main(String[] args)	{
		int limit=1000;
		BigCombinatorialNumberCache cache=new BigCombinatorialNumberCache(limit);
		for (int i=1;i<=limit;++i)	{
			int max=0;
			for (int j=0;j<=limit/2;++j)	{
				BigInteger combinatorial=cache.get(i,j);
				int g=combinatorial.getLowestSetBit();
				max=Math.max(g,max);
			}
			System.out.println("F("+i+")="+max+".");
		}
	}
}
