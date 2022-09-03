package com.euler;

import java.math.BigInteger;

public class Euler80 {
	public static boolean isSquare(int i)	{
		int sq=(int)Math.sqrt((double)i);
		return sq*sq==i;
	}
	
	public static void main(String args[])	{
		int sum=0;
		StringBuilder numberBuilder=new StringBuilder("1");
		for (int j=0;j<100;++j) numberBuilder.append("0");
		BigInteger huge=new BigInteger(numberBuilder.toString());
		for (int i=2;i<100;++i)	{
			if (isSquare(i)) continue;
			int sq=(int)Math.sqrt((double)i);
			BigInteger n=BigInteger.valueOf(sq);
			BigInteger d=BigInteger.ONE;
			BigInteger A=BigInteger.valueOf(i);
			if (sq*sq+sq<i) ++sq;
			for (int j=0;j<9;++j)	{
				BigInteger tmp=n.multiply(n).add(d.multiply(d).multiply(A));
				d=d.multiply(n.add(n));
				n=tmp;
			}
			n=n.multiply(huge);
			String number=n.divide(d).toString();
			for (int j=0;j<number.length()-1;++j) sum+=(int)(number.charAt(j)-'0');
		}
		System.out.println(sum);
	}
}
