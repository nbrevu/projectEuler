package com.euler;

import java.math.BigInteger;
import java.util.Arrays;

import com.google.common.math.LongMath;

public class Euler402_3 {
	private static class PolynomialAccumulator	{
		private final static BigInteger BIG_24=BigInteger.valueOf(24);
		private final BigInteger[] cubicPortion;
		private final BigInteger[] quadraticPortion;
		private final BigInteger[] linearPortion;
		private final BigInteger fullCube;
		public PolynomialAccumulator()	{
			cubicPortion=new BigInteger[25];
			quadraticPortion=new BigInteger[25];
			linearPortion=new BigInteger[25];
			Arrays.fill(cubicPortion,BigInteger.ZERO);
			Arrays.fill(quadraticPortion,BigInteger.ZERO);
			Arrays.fill(linearPortion,BigInteger.ZERO);
			for (int a=1;a<=24;++a) for (int b=1;b<=24;++b) for (int c=1;c<=24;++c)	{
				BigInteger m=BigInteger.valueOf(getMValue(a,b,c));
				int minABC=Math.max(Math.max(a,b),c);
				for (int i=minABC;i<=24;++i) cubicPortion[i]=cubicPortion[i].add(m);
				int minAB=Math.max(a,b);
				for (int i=minAB;i<=24;++i) quadraticPortion[i]=quadraticPortion[i].add(m);
				int minAC=Math.max(a,c);
				for (int i=minAC;i<=24;++i) quadraticPortion[i]=quadraticPortion[i].add(m);
				int minBC=Math.max(b,c);
				for (int i=minBC;i<=24;++i) quadraticPortion[i]=quadraticPortion[i].add(m);
				for (int i=a;i<=24;++i) linearPortion[i]=linearPortion[i].add(m);
				for (int i=b;i<=24;++i) linearPortion[i]=linearPortion[i].add(m);
				for (int i=c;i<=24;++i) linearPortion[i]=linearPortion[i].add(m);
			}
			fullCube=cubicPortion[24];
		}
		public BigInteger getSumFor(BigInteger n)	{
			BigInteger[] division=n.divideAndRemainder(BIG_24);
			BigInteger q=division[0];
			int r=division[1].intValueExact();
			// cubicPortion[r]+q*(quadraticPortion[r]+q*(linearPortion[r]+q*fullCube))
			return cubicPortion[r].add(q.multiply(quadraticPortion[r].add(q.multiply(linearPortion[r].add(q.multiply(fullCube))))));
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
	 */
	public static void main(String[] args)	{
		PolynomialAccumulator mainObject=new PolynomialAccumulator();
		BigInteger mod=BigInteger.valueOf(24l*LongMath.pow(10l,9));
		BigInteger s1=mainObject.getSumFor(BigInteger.ONE);
		BigInteger s2=mainObject.getSumFor(BigInteger.TWO);
		BigInteger fPrev=BigInteger.ONE;
		BigInteger fCurr=BigInteger.TWO;
		BigInteger sPrev=s1;
		BigInteger sCurr=s2;
		long index=3;
		long _1e9=1_000_000_000;
		long nextIndex=_1e9;
		for (;;)	{
			BigInteger fNext=fPrev.add(fCurr).mod(mod);
			fPrev=fCurr;
			fCurr=fNext;
			sPrev=sCurr;
			sCurr=mainObject.getSumFor(fCurr).mod(mod);
			++index;
			if (s1.equals(sPrev)&&s2.equals(sCurr))	{
				System.out.println("DAS CICLEN!!!!! index="+index+".");
				break;
			}	else if (index==nextIndex)	{
				System.out.println(index+"...");
				nextIndex+=_1e9;
			}
		}
	}
}
