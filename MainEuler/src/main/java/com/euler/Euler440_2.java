package com.euler;

import java.math.BigInteger;

import com.euler.common.EulerUtils;

public class Euler440_2 {
	private final static int N=1000;
	
	public static void main(String[] args)	{
		/*
		 * At a first glance, it would seem that gcd(T(i),T(j)) = T(gcd(i+1,j-1)-1).
		 * In other words, gcd(T(i+1),T(j+1))=T(gcd(i+1,j+1)).
		 * 
		 * OK, it works! :D.
		 * BUT: I can't just iterate. Or can I? There are 8e9 cases. That's a LOT, for something that requires BigInteger.
		 * I need to be smart regarding calculations of gcd(c^a+1,c^b+1). Is the result always c^gcd(a,b)+1?
		 * Maybe I should... WRITE ANOTHER PROGRAM TO CHECK IT!
		 */
		BigInteger[] terms=new BigInteger[N+1];
		terms[0]=BigInteger.ONE;
		terms[1]=BigInteger.TEN;
		for (int i=2;i<=N;++i) terms[i]=BigInteger.TEN.multiply(terms[i-1]).add(terms[i-2]);
		int verified=0;
		for (int i=1;i<=N;++i) for (int j=1;j<=i;++j)	{
			int candidate=EulerUtils.gcd(i+1,j+1)-1;
			BigInteger proposed=terms[candidate];
			BigInteger real=EulerUtils.gcd(terms[i],terms[j]);
			if (!proposed.equals(real)) System.out.println("Ooooooh. Not true for (i,j)=("+i+","+j+").");
			else ++verified;
		}
		System.out.println("Verified for "+verified+" cases!");
	}
}
