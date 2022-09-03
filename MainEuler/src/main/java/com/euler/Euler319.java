package com.euler;

import com.google.common.math.LongMath;

public class Euler319 {
	public static void main(String[] args)	{
		// https://www.tapatalk.com/groups/eulersolutionsfr/problem-319-t136-s50.html
		/*
		 * t(1) = 1.
		 * t(n) = (3^(n+1)+1)/2 - 2^(n+1) - sum(k,2,n-1,t(floor(n/k))), forall n>1.
		 * This is not (directly) reasonably doable when n is above 10^6 or so.
		 * My first attempt would be trying to count how many times does each factor of the form (3^(n+1)+1)/2 - 2^(n+1) appear.
		 */
		long[] t=new long[21];
		t[1]=1l;
		for (int i=2;i<=20;++i)	{
			long res=(LongMath.pow(3l,i+1)-1)/2-(1<<(i+1));
			for (int j=2;j<i;++j) res-=t[i/j];
			t[i]=res;
			System.out.println(String.format("t(%d)=%d.",i,res));
		}
	}
}
