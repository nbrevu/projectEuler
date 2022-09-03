package com.euler;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.euler.common.Rational;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;

public class Euler397_7 {
	private final static long K=LongMath.pow(10l,6);
	private final static long X=LongMath.pow(10l,9);
	
	private static long pow(long base,int exp)	{
		long current=base;
		long prod=1;
		while (exp>0)	{
			if ((exp%2)==1) prod=(prod*current);
			current=(current*current);
			exp/=2;
		}
		return prod;
	}
	
	private static class DoubleCounter	{
		private final long maxK;
		private final long maxX;
		private long counter;
		public DoubleCounter(long maxK,long maxX)	{
			this.maxK=maxK;
			this.maxX=maxX;
			counter=maxK;	// Initial special case for K=1 and its multiples.
		}
		public void countTriangles(long numA,long numB,long numC,long denom,long k)	{
			Rational aRat=new Rational(numA,denom);
			Rational bRat=new Rational(numB,denom);
			Rational cRat=new Rational(numC,denom);
			long aNum=aRat.num();
			long bNum=bRat.num();
			long cNum=cRat.num();
			if ((Math.abs(aNum)>maxX)||(Math.abs(bNum)>maxX)||(Math.abs(cNum)>maxX)) return;
			long aDen=aRat.den();
			long bDen=bRat.den();
			long cDen=cRat.den();
			long neededFactor=EulerUtils.lcm(aDen,EulerUtils.lcm(bDen,cDen));
			if (neededFactor>maxK) return;
			long a=aNum*(neededFactor/aDen);
			long b=bNum*(neededFactor/bDen);
			long c=cNum*(neededFactor/cDen);
			long realK=k*neededFactor;
			long kMultiplier=maxK/realK;
			long multiples=0;
			if (kMultiplier>0)	{
				long maxTerm=Math.max(Math.abs(a),Math.max(Math.abs(b),Math.abs(c)));
				long termMultiplier=maxX/maxTerm;
				multiples=Math.min(kMultiplier,termMultiplier);
				counter+=multiples;
			}
		}
		public long getCount()	{
			return counter;
		}
	}

	// 5271614
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long[] firstPrimes=Primes.lastPrimeSieve(K);
		DoubleCounter doubleCounter=new DoubleCounter(K,X);
		for (long k=2l;k<=K;++k)	{
			long k2=k*k;
			long k3=k2*k;
			LongIntMap divs=DivisorHolder.getFromFirstPrimes(k,firstPrimes).getFactorMap();
			int size=divs.size();
			long[] factors=new long[size];
			int index=0;
			for (LongIntCursor cursor=divs.cursor();cursor.moveNext();)	{
				long power=pow(cursor.key(),cursor.value());
				factors[index]=power*power;
				++index;
			}
			int maxBoolean=1<<size;
			for (int i=0;i<maxBoolean;++i)	{
				long p=1;
				int n=i;
				for (int j=0;j<size;++j)	{
					if ((n&1)!=0) p*=factors[j];
					n>>=1;
				}
				long p2=p*p;
				long p3=p2*p;
				long diff3=p3-k3;
				long sum3=p3+k3;
				long pk2=p*k*2;
				long ppk2=p*pk2;
				long pkk2=k*pk2;
				doubleCounter.countTriangles(diff3-ppk2,sum3,-diff3-pkk2,2*p*(k-p),k);
				doubleCounter.countTriangles(-sum3-ppk2,diff3,sum3+pkk2,2*p*(k+p),k);
			}
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(doubleCounter.getCount());
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
