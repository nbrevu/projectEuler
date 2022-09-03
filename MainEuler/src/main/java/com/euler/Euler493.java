package com.euler;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Locale;

public class Euler493 {
	// Another one at the first try :). This was even easier than I had expected.
	
	private final static int BALLS_OF_EACH_COLOUR=10;
	private final static int COLOURS=7;
	private final static int TOTAL_BALLS=BALLS_OF_EACH_COLOUR*COLOURS;
	private final static int DRAW=20;
	
	public static BigInteger[] getFactorials(int upTo)	{
		BigInteger[] result=new BigInteger[1+upTo];
		result[0]=BigInteger.ONE;
		for (int i=1;i<=upTo;++i) result[i]=result[i-1].multiply(BigInteger.valueOf((long)i));
		return result;
	}
	
	public static BigInteger combinations(int n,int k,BigInteger[] factorials)	{
		return factorials[n].divide(factorials[k].multiply(factorials[n-k]));
	}
	
	public static void main(String[] args)	{
		BigInteger[] cases=new BigInteger[1+COLOURS];
		BigInteger[] factorials=getFactorials(TOTAL_BALLS);
		cases[0]=BigInteger.ZERO;
		for (int i=1;i<=COLOURS;++i)	{
			if (i*BALLS_OF_EACH_COLOUR<DRAW)	{
				cases[i]=BigInteger.ZERO;
				continue;
			}
			cases[i]=combinations(i*BALLS_OF_EACH_COLOUR,DRAW,factorials);
		}
		BigInteger[] exactCases=new BigInteger[1+COLOURS];
		for (int i=0;i<=COLOURS;++i)	{
			if (i*BALLS_OF_EACH_COLOUR<DRAW)	{
				exactCases[i]=BigInteger.ZERO;
				continue;
			}
			exactCases[i]=cases[i];
			for (int j=0;j<i;++j)	{
				BigInteger num=cases[j].multiply(combinations(i,j,factorials));
				if (((i-j)%2)==0) exactCases[i]=exactCases[i].add(num);
				else exactCases[i]=exactCases[i].subtract(num);
			}
		}
		BigInteger sum=BigInteger.ZERO;
		for (int i=1;i<=COLOURS;++i) sum=sum.add(combinations(COLOURS,i,factorials).multiply(exactCases[i]).multiply(BigInteger.valueOf((long)i)));
		BigDecimal result=(new BigDecimal(sum)).divide(new BigDecimal(cases[COLOURS]),MathContext.DECIMAL128);
		System.out.println(String.format(Locale.UK,"%.9f",result.doubleValue()));
	}
}
