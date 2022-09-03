package com.euler.experiments;

import java.math.BigInteger;

public class RepeatedSquare4 {
	public static void main(String[] args)	{
		BigInteger mod=BigInteger.valueOf(121l);
		BigInteger eleven=BigInteger.valueOf(11l);
		BigInteger four=BigInteger.valueOf(4l);
		BigInteger n=BigInteger.ONE;
		for (int i=1;i<=1001;++i)	{
			n=n.multiply(BigInteger.TEN);
			BigInteger candidate=n.add(BigInteger.ONE);
			if (candidate.mod(mod).signum()==0)	{
				BigInteger found=candidate.divide(eleven).multiply(four);
				System.out.println(String.format("For i=%d we find that %d^2=%d.",i,found,found.multiply(found)));
			}
		}
	}
}
