package com.euler;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

public class Euler168 {
	private final static BigInteger B105=BigInteger.valueOf(100000);
	private static BigInteger fun(BigInteger e)	{
		Set<BigInteger> nums=new HashSet<>();
		for (BigInteger d=BigInteger.ONE;d.compareTo(BigInteger.TEN)<0;d=d.add(BigInteger.ONE))	{
			for (BigInteger p=BigInteger.ONE;p.compareTo(BigInteger.TEN)<0;p=p.add(BigInteger.ONE))	{
				BigInteger[] nm=(e.subtract(p).multiply(d)).divideAndRemainder(BigInteger.TEN.multiply(p).subtract(BigInteger.ONE));
				if (nm[1].equals(BigInteger.ZERO)&&BigInteger.TEN.multiply(nm[0]).compareTo(e)>=0) nums.add(BigInteger.TEN.multiply(nm[0]).add(d));
			}
		}
		BigInteger sum=BigInteger.ZERO;
		for (BigInteger b:nums) sum=sum.add(b).mod(B105);
		return sum;
	}
	
	public static void main(String[] args)	{
		BigInteger e=BigInteger.ONE;
		BigInteger sum=BigInteger.ZERO;
		for (int i=1;i<=99;++i)	{
			e=e.multiply(BigInteger.TEN);
			sum=sum.add(fun(e)).mod(B105);
		}
		System.out.println(sum.toString());
	}
}
