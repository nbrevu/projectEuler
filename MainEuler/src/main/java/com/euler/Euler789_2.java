package com.euler;

import java.util.Arrays;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;

public class Euler789_2 {
	private static long productCost(int[] pairs)	{
		long result=1l;
		for (int p:pairs) result*=p;
		return result;
	}
	
	private static void displayInfo(int cost,int p,int[] assignments,long productCost)	{
		int n=(p-1)/2;
		int[][] actualPairs=new int[n][2];
		for (int i=0;i<assignments.length;++i)	{
			int index=assignments[i];
			if (actualPairs[index][0]==0) actualPairs[index][0]=1+i;
			else actualPairs[index][1]=1+i;
		}
		StringBuilder sb=new StringBuilder();
		sb.append("Found case for p=").append(p).append(" and cost=").append(cost).append(": {");
		boolean isFirst=true;
		for (int i=0;i<actualPairs.length;++i)	{
			if (isFirst) isFirst=false;
			else sb.append(',');
			sb.append('(').append(actualPairs[i][0]).append(',').append(actualPairs[i][1]).append(')');
		}
		sb.append('}');
		System.out.println(sb.toString());
	}
	
	private static void workWith(int p)	{
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
			if (cost<=p-1)	{
				displayInfo(cost,p,assignments,productCost(pairs));
				// break;
			}
		}	while (EulerUtils.nextPermutation(assignments));
	}
	
	public static void main(String[] args)	{
		boolean[] composites=Primes.sieve(100);
		for (int i=3;i<=100;i+=2) if (!composites[i]) workWith(i);
	}
}
