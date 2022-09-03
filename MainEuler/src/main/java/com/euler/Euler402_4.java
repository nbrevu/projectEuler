package com.euler;

import com.google.common.math.LongMath;

public class Euler402_4 {
	private static class PolynomialAccumulator	{
		private final long[] cubicPortion;
		private final long[] quadraticPortion;
		private final long[] linearPortion;
		private final long fullCube;
		public PolynomialAccumulator()	{
			cubicPortion=new long[25];
			quadraticPortion=new long[25];
			linearPortion=new long[25];
			for (int a=1;a<=24;++a) for (int b=1;b<=24;++b) for (int c=1;c<=24;++c)	{
				long m=getMValue(a,b,c);
				int minABC=Math.max(Math.max(a,b),c);
				for (int i=minABC;i<=24;++i) cubicPortion[i]+=m;
				int minAB=Math.max(a,b);
				for (int i=minAB;i<=24;++i) quadraticPortion[i]+=m;
				int minAC=Math.max(a,c);
				for (int i=minAC;i<=24;++i) quadraticPortion[i]+=m;
				int minBC=Math.max(b,c);
				for (int i=minBC;i<=24;++i) quadraticPortion[i]+=m;
				for (int i=a;i<=24;++i) linearPortion[i]+=m;
				for (int i=b;i<=24;++i) linearPortion[i]+=m;
				for (int i=c;i<=24;++i) linearPortion[i]+=m;
			}
			fullCube=cubicPortion[24];
		}
		public long getSumFor(long n,long mod)	{
			long q=n/24;
			int r=(int)(n%24);
			long result=(q*fullCube)%mod;
			result=(q*(linearPortion[r]+result))%mod;
			result=(q*(quadraticPortion[r]+result));
			return (cubicPortion[r]+result)%mod;
		}
		private static boolean isMultipleOf8(long a,long b,long c)	{
			a%=8;
			b%=8;
			c%=8;
			if (a==2)	{
				if (b==3) return c==2;
				else if (b==7) return c==6;
			}	else if (a==6)	{
				if (b==3) return c==6;
				else if (b==7) return c==2;
			}
			return false;
		}
		private static boolean isMultipleOf4(long a,long b,long c)	{
			a%=4;
			b%=4;
			c%=4;
			if (a==0)	{
				if (b==1) return c==2;
				else if (b==3) return c==0;
			}	else if (a==2)	{
				if (b==1) return c==0;
				else if (b==3) return c==2;
			}
			return false;
		}
		private static boolean isMultipleOf2(long a,long b,long c)	{
			return ((a+b+c)%2)==1;
		}
		private static boolean isMultipleOf3(long a,long b,long c)	{
			return ((b%3)==2)&&((a+c)%3)==0;
		}
		private static long getMValue2(long a,long b,long c)	{
			if (isMultipleOf8(a,b,c)) return 8;
			else if (isMultipleOf4(a,b,c)) return 4;
			else if (isMultipleOf2(a,b,c)) return 2;
			else return 1;
		}
		private static long getMValue3(long a,long b,long c)	{
			return isMultipleOf3(a,b,c)?3:1;
		}
		private static long getMValue(long a,long b,long c)	{
			return getMValue2(a,b,c)*getMValue3(a,b,c);
		}
	}

	/*
	 * First attempt at finding cycles: there is a cycle with the Fibonacci numbers of length 12e9.
	 * Second attempt at finding cycles, this time with fibonaccis: the length is, again, 12e9.
	 */
	public static void main(String[] args)	{
		PolynomialAccumulator mainObject=new PolynomialAccumulator();
		long fiboMod=24l*LongMath.pow(10l,9);
		long realMod=LongMath.pow(10l,9);
		long s1=mainObject.getSumFor(1,fiboMod);
		long s2=mainObject.getSumFor(2,fiboMod);
		long fPrev=1l;
		long fCurr=2l;
		long sPrev=s1;
		long sCurr=s2;
		long index=3;
		long _1e9=1_000_000_000;
		long nextIndex=_1e9;
		for (;;)	{
			long fNext=(fPrev+fCurr)%fiboMod;
			fPrev=fCurr;
			fCurr=fNext;
			sPrev=sCurr;
			sCurr=mainObject.getSumFor(fNext,realMod);
			++index;
			if ((s1==sPrev)&&(s2==sCurr))	{
				System.out.println("DAS CICLEN!!!!! index="+index+".");
				break;
			}	else if (index==nextIndex)	{
				System.out.println(index+"...");
				nextIndex+=_1e9;
			}
		}
	}
}
