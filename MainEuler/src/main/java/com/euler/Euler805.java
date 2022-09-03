package com.euler;

import com.euler.common.EulerUtils;

public class Euler805 {
	private final static int N=200;
	
	// There are 24463 cases.
	public static void main(String[] args)	{
		int counter=0;
		for (int i=1;i<=N;++i) for (int j=1;j<=N;++j) if (EulerUtils.areCoprime(i,j)) ++counter;
		System.out.println(counter);
	}
}
