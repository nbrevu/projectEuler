package com.euler;

import java.math.RoundingMode;

import com.google.common.math.IntMath;

public class Euler611_3 {
	// ZUTUN: repasarse http://mathworld.wolfram.com/SumofSquaresFunction.html y ver todos los patrones.
	
	private static int getAmountOfRepresentations(final int in)	{
		int n=in;
		int a0=0;
		while ((n%2)==0)	{
			n/=2;
			++a0;
		}
		int a3=0;
		while ((n%3)==0)	{
			n/=3;
			++a3;
		}
		if ((a3%2)==1) return 0;
		boolean add4=false;
		int B=1;
		for (int p=5;n>1;p+=(add4?4:2),add4=!add4)	{
			int ai=0;
			if ((n%p)==0)	{
				do	{
					++ai;
					n/=p;
				}	while ((n%p)==0);
				if ((p%4)==3)	{
					if ((ai%2)==1) return 0;
				}	else B*=1+ai;
			}
		}
		if ((B%2)==1)	{
			if ((a0%2)==0) --B;
			else ++B;
		}
		int result=B/2;
		if (isTwoTimesSquare(in)) --result;
		return result;
	}
	
	private static boolean isTwoTimesSquare(int in)	{
		if ((in%2)!=0) return false;
		int n2=in/2;
		int s=IntMath.sqrt(n2,RoundingMode.DOWN);
		return s*s==n2;
	}
	
	public static void main(String[] args)	{
		int limit=2000;
		for (int i=1;i<=limit;++i)	{
			int fine=getAmountOfRepresentations(i);
			System.out.println(""+i+": "+fine+".");
		}
	}
}
