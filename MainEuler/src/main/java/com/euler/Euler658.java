package com.euler;

import com.euler.common.EulerUtils.CombinatorialNumberCache;

public class Euler658 {
	private static long getF(CombinatorialNumberCache combis,int k,int i)	{
		long result=0;
		int firstTerm;
		if (((k-i)%2)==1)	{
			++result;
			firstTerm=i;
		}	else firstTerm=i+1;
		for (int j=firstTerm;j<k;j+=2) result+=combis.get(j,i-1);
		return result;
	}
	
	public static void main(String[] args)	{
		int N=14;
		CombinatorialNumberCache combis=new CombinatorialNumberCache(N-1);
		for (int i=1;i<N;++i)	{
			System.out.println(String.format("f(%d,%d)=%d.",i,3,getF(combis,i,3)));
		}
	}
}
