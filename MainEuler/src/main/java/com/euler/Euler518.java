package com.euler;

import java.math.RoundingMode;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.IntMath;

public class Euler518 {
	private final static int LIMIT=100000000;
	private final static boolean[] COMPOSITES=Primes.sieve(LIMIT);
	
	private static boolean isPrime(long N)	{
		return !COMPOSITES[(int)N];
	}
	
	private static long getSumWith(long num,long den)	{
		if (EulerUtils.gcd(num,den)>1l) return 0l;
		long den2=den*den;
		long numDen=num*den;
		long num2=num*num;
		long curDen2=den2;
		long curNumDen=numDen;
		long curNum2=num2;
		long sum=0l;
		while (curNum2<LIMIT)	{
			if (isPrime(curDen2-1)&&isPrime(curNumDen-1)&&isPrime(curNum2-1)) sum+=curDen2+curNumDen+curNum2-3;
			curDen2+=den2;
			curNumDen+=numDen;
			curNum2+=num2;
		}
		return sum;
	}
	
	public static void main(String[] args)	{
		long sum=0l;
		int sqLimit=IntMath.sqrt(LIMIT,RoundingMode.FLOOR);
		for (int i=1;i<sqLimit;++i)	{
			int limit2=LIMIT/i;
			for (int j=i+1;j<limit2;++j) sum+=getSumWith(j,i);
		}
		System.out.println(sum);
	}
	
	/*
	public static long getNextInGeoSeq(long a,long b)	{
		long bb=b*b;
		if ((bb%a)!=0) return -1;
		return (bb/a)-1;
	}
	
	public static void main(String[] args)	{
		long sum=0l;
		for (int i=0;i<PRIMES.size();++i)	{
			long a=PRIMES.get(i);
			System.out.println(""+a+"...");
			long a_=a+1;
			long limit=LongMath.sqrt(a*LIMIT,RoundingMode.FLOOR);
			for (int j=i+1;j<PRIMES.size();++j)	{
				long b=PRIMES.get(j);
				if (b>limit) break;
				long c=getNextInGeoSeq(a_,b+1);
				if ((c>0)&&!COMPOSITES[(int)c]) sum+=a+b+c;
			}
		}
		System.out.println(sum);
	}
	*/
}
