package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;

public class Euler789 {
	private static long productCost(int[] pairs)	{
		long result=1l;
		for (int p:pairs) result*=p;
		return result;
	}
	
	private static void workWith(int p)	{
		int minimalCost=Integer.MAX_VALUE;
		List<Long> costProducts=new ArrayList<>();
		int[] assignments=new int[p-1];
		for (int i=0;i<assignments.length;++i) assignments[i]=i/2;
		int[] pairs=new int[assignments.length/2];
		do	{
			Arrays.fill(pairs,1);
			for (int i=0;i<assignments.length;++i) pairs[assignments[i]]*=i+1;
			int cost=0;
			for (int i=0;i<pairs.length;++i)	{
				pairs[i]%=p;
				cost+=pairs[i];
			}
			if (cost<minimalCost)	{
				minimalCost=cost;
				costProducts.clear();
				costProducts.add(productCost(pairs));
			}	else if (cost==minimalCost) costProducts.add(productCost(pairs));
		}	while (EulerUtils.nextPermutation(assignments));
		System.out.println(String.format("Minimal cost for p=%d: %d. There are %d separate cases whose cost products are: %s.",p,minimalCost,costProducts.size(),costProducts.toString()));
	}
	
	public static void main(String[] args)	{
		boolean[] composites=Primes.sieve(100);
		for (int i=3;i<=100;i+=2) if (!composites[i]) workWith(i);
	}
}
