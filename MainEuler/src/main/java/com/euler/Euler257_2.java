package com.euler;

import java.math.RoundingMode;

import com.euler.common.EulerUtils;
import com.google.common.math.DoubleMath;
import com.google.common.math.LongMath;

public class Euler257_2 {
	private final static long N=LongMath.pow(10l,11);
	
	private static long getEquilaterals(long n)	{
		return n/3;	// Well, duh.
	}
	
	/*
	 * Case A: Q=2, q=1. k above m.
	 */
	private static long getForCaseA(long n)	{
		long maxM=DoubleMath.roundToInt(Math.sqrt(n/6.0),RoundingMode.DOWN);
		long count=0;
		for (long m=3;m<=maxM;++m)	{
			long maxK=DoubleMath.roundToInt(Math.sqrt(2.0)*m,RoundingMode.DOWN);
			long minK=m+1;
			if ((minK%2)==0) ++minK;
			for (long k=minK;k<=maxK;k+=2) if (EulerUtils.areCoprime(m,k))	{
				long a=k*m;
				long b=k*(k+m);
				long c=m*(2*m+k);
				long P=a+b+c;
				if (P>n) break;
				else count+=n/P;
			}
		}
		return count;
	}
	
	/*
	 * Case B: Q=2, q=2. k below m.
	 */
	private static long getForCaseB(long n)	{
		long maxM=DoubleMath.roundToInt(Math.sqrt(n/3.0),RoundingMode.DOWN);
		long count=0;
		for (long m=3;m<=maxM;m+=2)	{
			long minK=LongMath.divide(m,2,RoundingMode.UP);
			long maxK=DoubleMath.roundToInt(m/Math.sqrt(2.0),RoundingMode.DOWN);
			for (long k=minK;k<=maxK;++k) if (EulerUtils.areCoprime(m,k))	{
				long a=k*m;
				long b=k*(2*k+m);
				long c=m*(k+m);
				long P=a+b+c;
				if (P>n) break;
				else count+=n/P;
			}
		}
		return count;
	}
	
	/*
	 * Case C: Q=3, k=2n-m.
	 */
	private static long getForCaseC(long n)	{
		long maxM=DoubleMath.roundToInt(Math.sqrt(N/4),RoundingMode.DOWN);
		long count=0;
		for (long m=3;m<=maxM;m+=2)	{
			long maxK=DoubleMath.roundToInt(Math.sqrt(3.0)*m,RoundingMode.DOWN);
			long minK=1+m;
			if ((minK%2)==0) ++minK;	// m and k always odd.
			for (long k=minK;k<=maxK;k+=2) if (EulerUtils.areCoprime(k,m))	{
				long nsmall=(k+m)/2;
				long a=k*m;
				long b=k*nsmall;
				long c=m*((3*m+k)/2);
				if (EulerUtils.gcd(a,EulerUtils.gcd(b,c))>1) continue;
				long P=a+b+c;
				if (P>n) break;
				else count+=n/P;
			}
		}
		return count;
	}
	
	/*
	 * Case D: Q=3, k=(2n-m)/2.
	 */
	private static long getForCaseD(long n)	{
		long maxM=DoubleMath.roundToInt(Math.sqrt(N/2),RoundingMode.DOWN);
		long count=0;
		for (long m=4;m<=maxM;m+=2)	{
			long maxK=DoubleMath.roundToInt(Math.sqrt(0.75)*m,RoundingMode.DOWN);
			long minK=1+LongMath.divide(m,2,RoundingMode.DOWN);
			for (long k=minK;k<=maxK;k+=2) if (EulerUtils.areCoprime(k,m/2))	{
				long nsmall=k+(m/2);
				long a=k*m;
				long b=k*nsmall;
				long c=(m/2)*(3*(m/2)+k);
				if (EulerUtils.gcd(a,EulerUtils.gcd(b,c))>1) continue;
				long P=a+b+c;
				if (P>n) break;
				else count+=n/P;
			}
		}
		return count;
	}
	
	/*
	 * Case E: Q=3, k=(2n-m)/3.
	 */
	private static long getForCaseE(long n)	{
		long maxM=DoubleMath.roundToInt(Math.sqrt(3*N/4),RoundingMode.DOWN);
		long count=0;
		for (long m=3;m<=maxM;m+=2)	{
			long maxK=DoubleMath.roundToInt(m/Math.sqrt(3.0),RoundingMode.DOWN);
			long minK=1+LongMath.divide(m,3,RoundingMode.DOWN);
			if ((minK%2)==0) ++minK;
			for (long k=minK;k<=maxK;k+=2) if (EulerUtils.areCoprime(k,m))	{
				long nsmall=(3*k+m)/2;
				long a=k*m;
				long b=k*nsmall;
				long c=m*((k+m)/2);
				if (EulerUtils.gcd(a,EulerUtils.gcd(b,c))>1) continue;
				long P=a+b+c;
				if (P>n) break;
				else count+=n/P;
			}
		}
		return count;
	}
	
	/*
	 * Case F: Q=3, k=(2n-m)/6.
	 */
	private static long getForCaseF(long n)	{
		long maxM=DoubleMath.roundToInt(Math.sqrt(3*N/2),RoundingMode.DOWN);
		long count=0;
		for (long m=4;m<=maxM;m+=2)	{
			long maxK=DoubleMath.roundToInt(m/Math.sqrt(12.0),RoundingMode.DOWN);
			long minK=1+LongMath.divide(m,6,RoundingMode.DOWN);
			for (long k=minK;k<=maxK;++k) if (EulerUtils.areCoprime(k,m/2))	{
				long nsmall=3*k+(m/2);
				long a=k*m;
				long b=k*nsmall;
				long c=(m/2)*((m/2)+k);
				if (EulerUtils.gcd(a,EulerUtils.gcd(b,c))>1) continue;
				long P=a+b+c;
				if (P>n) break;
				else count+=n/P;
			}
		}
		return count;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long count=getEquilaterals(N);
		count+=getForCaseA(N);
		count+=getForCaseB(N);
		count+=getForCaseC(N);
		count+=getForCaseD(N);
		count+=getForCaseE(N);
		count+=getForCaseF(N);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
		System.out.println(count);
	}
}
