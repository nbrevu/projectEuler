package com.euler;

import com.euler.common.EulerUtils;

public class Euler659_3 {
	public static void main(String[] args)	{
		int k=4;
		int residue=k*k;
		int prevSq=residue;
		for (int i=1;i<10000;++i)	{
			int sq=i*i+residue;
			System.out.println("i="+i+" => "+EulerUtils.gcd(sq,prevSq)+".");
			prevSq=sq;
		}
	}
}
