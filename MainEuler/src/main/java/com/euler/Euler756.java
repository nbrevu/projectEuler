package com.euler;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import com.euler.common.BigIntegerUtils.BigCombinatorialNumberCache;

public class Euler756 {
	private final static int SIZE=100;
	private final static int LENGTH=50;
	
	public static void main(String[] args)	{
		// Es funktioniert :). Now let's do some MATH!
		BigCombinatorialNumberCache cache=new BigCombinatorialNumberCache(SIZE);
		BigInteger denominator=cache.get(SIZE,LENGTH);
		BigInteger numerator=BigInteger.ZERO;
		BigInteger otherNumerator=BigInteger.ZERO;
		BigInteger realSum=BigInteger.valueOf((SIZE*(SIZE+1))/2);
		int distribution=LENGTH-2;
		for (int i=1;i<=SIZE;++i)	{
			BigInteger fI=BigInteger.valueOf(i);
			for (int j=0;j<i;++j)	{
				BigInteger diff=BigInteger.valueOf(i-j);
				int otherPoints=SIZE-1-(i-j);
				int realDistr=distribution;
				if (j==0)	{
					++otherPoints;
					++realDistr;
				}
				if (otherPoints<realDistr) continue;	// Yes, there are better ways to do this.
				numerator=numerator.add(diff.multiply(fI).multiply(cache.get(otherPoints,realDistr)));
				otherNumerator=otherNumerator.add(cache.get(otherPoints,realDistr));
			}
		}
		BigDecimal dDenom=new BigDecimal(denominator);
		BigDecimal dNum=new BigDecimal(numerator);
		BigDecimal result=new BigDecimal(realSum).subtract(dNum.divide(dDenom,7,RoundingMode.HALF_UP));
		System.out.println(new BigDecimal(otherNumerator).divide(dDenom,7,RoundingMode.HALF_UP));
		System.out.println(result);
	}
}
