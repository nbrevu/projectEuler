package com.euler;

import java.math.BigInteger;

import com.euler.common.EulerUtils;

public class Euler440_3 {
	private final static int N=200;
	
	public static void main(String[] args)	{
		/*
		 * I need to be smart regarding calculations of gcd(c^a+1,c^b+1). Is the result always c^gcd(a,b)+1?
		 * No, wait, that can't work. gcd(6,26)!=6. This would work if it was gcd(c^a-1,c^b-1), I think.
		 * 
		 * Maybe, except for the case a==b, the only possible prime factor is 2? Like, I can find 2, 4, 8... but not much more.
		 * It's more complicated than that. There is a pattern to be found.
		 * This looks... moderately complicated but very much doable. There seem to be some successions, like the gcd of c^a+1 and c^b+1 depends
		 * on whether a and b belong to certain common arithmetic pattern. For example, 2^(4k+2)+1 is always a multiple of 5.
		 * Of course... this is just 4^(2k+1)+1. I would expect patterns of the form (c^m)^(nx+o)+1 to be multiples of something. Maybe just for
		 * n=2 or n even.
		 */
		int verified=0;
		for (int i=2;i<=N;++i)	{
			BigInteger[] powers=new BigInteger[N+1];
			powers[0]=BigInteger.ONE;
			BigInteger n=BigInteger.valueOf(i);
			for (int j=1;j<=N;++j) powers[j]=powers[j-1].multiply(n);
			for (int j=1;j<=N;++j) for (int k=1;k<j;++k)	{
				BigInteger gcd=EulerUtils.gcd(powers[j].add(BigInteger.ONE),powers[k].add(BigInteger.ONE));
				/*
				 * Ok, I THINK I got it.
				 * Let's suppose we want to calculate gcd(c^a+1,c^b+1).
				 * Then the first thing to do is calculate g=gcd(a,b).
				 * And then, a'=a/g, b'=b/g.
				 * If BOTH a' and b' are odd, then gcd(c^a+1,c^b+1)=c^g+1. Otherwise, gcd(c^a+1,c^b+1)=2 if c is odd, 1 if c is even.
				 * 
				 * WOW, that seems to work :D.
				 */
				int g=EulerUtils.gcd(j,k);
				int a=j/g;
				int b=k/g;
				BigInteger expected;
				if (((a%2)==1)&&((b%2)==1)) expected=powers[g].add(BigInteger.ONE);
				else if ((i%2)==0) expected=BigInteger.ONE;
				else expected=BigInteger.TWO;
				if (gcd.equals(expected)) ++verified;
				else System.out.println("Scheiße, no funciona para i="+i+", (j,k)=("+j+","+k+").");
			}
		}
		System.out.println("Verified for "+verified+" cases!");
	}
}
