package com.euler;

import java.math.BigInteger;

public class Euler570 {
	private final static int ORDER=10000000;
	
	private final static BigInteger times2(BigInteger in)	{
		return in.add(in);
	}
	
	private final static BigInteger times3(BigInteger in)	{
		return in.add(in).add(in);
	}
	
	private final static BigInteger times6(BigInteger in)	{
		return in.add(in).add(in).add(in).add(in).add(in);
	}
	
	private static BigInteger[] iterate(BigInteger[] positions)	{
		BigInteger[] res=new BigInteger[]{BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO};
		// 1[0,0,0] generates 6*1[0,0,2]
		// 1[0,0,2] generates 3*1[0,0,2] + 2*1[0,2,2] + 1*3[2,2,2]
		// 1[0,2,2] generates 1*1[0,0,2] + 2*1[0,2,2] + 1*1[2,2,2] + 2*3[2,2,2]
		// 1[2,2,2] generates 3*1[2,2,2] + 3*3[2,2,2]
		// 3[2,2,2] generates 6*3[2,2,4]
		// 3[2,2,4] generates 3*3[2,2,4] + 2*3[2,4,4] + 1*5[4,4,4]
		// 3[2,4,4] generates 1*3[2,2,4] + 2*3[2,4,4] + 1*3[4,4,4] + 2*5[4,4,4]
		// 3[4,4,4] generates 3*3[4,4,4] + 3*5[4,4,4]
		res[1]=times6(positions[0]).add(times3(positions[1])).add(positions[2]);
		res[2]=times2(positions[1]).add(times2(positions[2]));
		res[3]=positions[2].add(times3(positions[3]));
		res[4]=positions[1].add(times2(positions[2])).add(times3(positions[3]));
		res[5]=times6(positions[4]).add(times3(positions[5])).add(positions[6]);
		res[6]=times2(positions[5]).add(times2(positions[6]));
		res[7]=positions[6].add(times3(positions[7]));
		return res;
	}
	
	private static BigInteger[] initial()	{
		return new BigInteger[]{BigInteger.ONE,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO};
	}
	
	private static BigInteger getGcd(BigInteger[] triangles)	{
		BigInteger t1=triangles[0].add(triangles[1]).add(triangles[2]).add(triangles[3]);
		BigInteger t3=triangles[4].add(triangles[5]).add(triangles[6]).add(triangles[7]);
		return t1.gcd(t3);
	}
	
	public static void main(String[] args)	{
		BigInteger res=BigInteger.ZERO;
		BigInteger[] triangles=iterate(initial());
		for (int i=3;i<=ORDER;++i)	{
			//if ((i%1000)==0) System.out.println(""+i+"...");
			triangles=iterate(triangles);
			BigInteger value=getGcd(triangles);
			System.out.println("i="+i+": "+value+".");
			res=res.add(value);
		}
		System.out.println(res.toString());
	}
}
