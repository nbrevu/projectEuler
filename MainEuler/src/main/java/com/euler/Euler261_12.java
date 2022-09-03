package com.euler;

import java.math.BigInteger;

import com.google.common.math.LongMath;

public class Euler261_12 {
	private final static long LIMIT=LongMath.pow(10l,5);
	
	public static void main(String[] args) {
		BigInteger bigLimit=BigInteger.valueOf(LIMIT);
		BigInteger four=BigInteger.valueOf(4l);
		long result=0;
		for (BigInteger k=BigInteger.ONE;k.compareTo(bigLimit)<=0;k=k.add(BigInteger.ONE))	{
			BigInteger k2=k.multiply(k);
			boolean found=false;
			for (BigInteger m=BigInteger.ONE;m.compareTo(bigLimit)<=0;m=m.add(BigInteger.ONE))	{
				BigInteger m_1=m.add(BigInteger.ONE);
				BigInteger m_m=m_1.multiply(m);
				BigInteger a=m;
				BigInteger b=m_m;
				BigInteger c=m_m.multiply(k).subtract(m_1.multiply(k2));
				BigInteger delta=b.multiply(b).subtract(four.multiply(a).multiply(c));
				if (delta.compareTo(BigInteger.ZERO)<0) continue;
				BigInteger sq=delta.sqrt();
				if (sq.multiply(sq).equals(delta))	{
					BigInteger _2m=m.add(m);
					BigInteger num1=b.negate().add(sq);
					BigInteger[] div1=num1.divideAndRemainder(_2m);
					if (div1[1].equals(BigInteger.ZERO))	{
						BigInteger n=div1[0];
						if (n.compareTo(k)>=0)	{
							System.out.println("AJÁ. Para m="+m+" me encuentro que k="+k+". Y de paso: n="+n+".");
							found=true;
							break;
						}
					}
					BigInteger num2=b.negate().subtract(sq);
					BigInteger[] div2=num2.divideAndRemainder(_2m);
					if (div2[1].equals(BigInteger.ZERO))	{
						BigInteger n=div2[0];
						if (n.compareTo(k)>=0)	{
							found=true;
							System.out.println("AJÁ. Para m="+m+" me encuentro que k="+k+". Y de paso: n="+n+".");
							break;
						}
					}
				}
			}
			if (found) result+=k.longValueExact();
		}
		System.out.println(result);
	}
}
