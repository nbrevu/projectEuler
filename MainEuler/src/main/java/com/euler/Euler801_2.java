package com.euler;

import java.util.Arrays;

public class Euler801_2 {
	private final static int P=7;
	
	public static void main(String[] args)	{
		int l=P*(P-1);
		int[][] mods=new int[l][l];
		for (int i=1;i<=l;++i)	{
			int idx=i-1;
			mods[idx][0]=i%P;
			for (int j=1;j<l;++j) mods[idx][j]=(mods[idx][j-1]*i)%P;
			System.out.println(String.format("%02d: %s.",i,Arrays.toString(mods[idx])));
		}
	}
}
