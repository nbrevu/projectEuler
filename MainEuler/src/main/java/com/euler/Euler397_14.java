package com.euler;

import java.math.BigInteger;

import com.euler.common.BigRational;
import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.IntMath;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;

public class Euler397_14 {
	private final static int K=IntMath.pow(10,6);
	private final static int X=IntMath.pow(10,9);
	
	private static BigInteger pow(BigInteger base,int exp)	{
		BigInteger current=base;
		BigInteger prod=BigInteger.ONE;
		while (exp>0)	{
			if ((exp%2)==1) prod=prod.multiply(current);
			current=current.multiply(current);
			exp/=2;
		}
		return prod;
	}
	
	private static BigInteger max(BigInteger a,BigInteger b)	{
		return (a.compareTo(b)>0)?a:b;
	}
	
	private static BigInteger min(BigInteger a,BigInteger b)	{
		return (a.compareTo(b)<0)?a:b;
	}
	
	private static BigInteger maxAbs(BigInteger a,BigInteger b,BigInteger c)	{
		a=a.abs();
		b=b.abs();
		c=c.abs();
		return max(a,max(b,c));
	}
	
	private static class DoubleCounter	{
		private final BigInteger maxK;
		private final BigInteger maxX;
		private BigInteger counter;
		public DoubleCounter(int maxK,int maxX)	{
			this.maxK=BigInteger.valueOf(maxK);
			this.maxX=BigInteger.valueOf(maxX);
			counter=BigInteger.valueOf(maxK);	// Initial special case for K=1 and its multiples.
		}
		public void countTriangles(BigInteger numA,BigInteger numB,BigInteger numC,BigInteger denom,BigInteger k)	{
			BigRational aRat=new BigRational(numA,denom);
			BigRational bRat=new BigRational(numB,denom);
			BigRational cRat=new BigRational(numC,denom);
			BigInteger aNum=aRat.num;
			BigInteger bNum=bRat.num;
			BigInteger cNum=cRat.num;
			if ((aNum.abs().compareTo(maxX)>0)||(bNum.abs().compareTo(maxX)>0)||(cNum.abs().compareTo(maxX)>0)) return;
			BigInteger aDen=aRat.den;
			BigInteger bDen=bRat.den;
			BigInteger cDen=cRat.den;
			BigInteger neededFactor=EulerUtils.lcm(aDen,EulerUtils.lcm(bDen,cDen));
			if (neededFactor.compareTo(maxK)>0) return;
			BigInteger a=aNum.multiply(neededFactor.divide(aDen));
			BigInteger b=bNum.multiply(neededFactor.divide(bDen));
			BigInteger c=cNum.multiply(neededFactor.divide(cDen));
			BigInteger realK=k.multiply(neededFactor);
			BigInteger kMultiplier=maxK.divide(realK);
			if (kMultiplier.compareTo(BigInteger.ZERO)>0)	{
				BigInteger maxTerm=maxAbs(a,b,c);
				BigInteger termMultiplier=maxX.divide(maxTerm);
				counter=counter.add(min(kMultiplier,termMultiplier));
			}
		}
		public BigInteger getCount()	{
			return counter;
		}
	}

	// 5271572. FUCK.
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int[] firstPrimes=Primes.lastPrimeSieve(K);
		DoubleCounter doubleCounter=new DoubleCounter(K,X);
		for (int ik=2;ik<=K;++ik)	{
			BigInteger k=BigInteger.valueOf(ik);
			BigInteger k2=k.multiply(k);
			BigInteger k3=k2.multiply(k);
			LongIntMap divs=DivisorHolder.getFromFirstPrimes(ik,firstPrimes).getFactorMap();
			int size=divs.size();
			BigInteger[] factors=new BigInteger[size];
			int index=0;
			for (LongIntCursor cursor=divs.cursor();cursor.moveNext();)	{
				BigInteger power=pow(BigInteger.valueOf(cursor.key()),cursor.value());
				factors[index]=power.multiply(power);
				++index;
			}
			int maxBoolean=1<<size;
			for (int i=0;i<maxBoolean;++i)	{
				BigInteger p=BigInteger.ONE;
				int n=i;
				for (int j=0;j<size;++j)	{
					if ((n&1)!=0) p=p.multiply(factors[j]);
					n>>=1;
				}
				BigInteger p2=p.multiply(p);
				BigInteger p3=p2.multiply(p);
				BigInteger diff3=p3.subtract(k3);
				BigInteger sum3=p3.add(k3);
				BigInteger pk2=p.multiply(k.add(k));
				BigInteger ppk2=p.multiply(pk2);
				BigInteger pkk2=k.multiply(pk2);
				doubleCounter.countTriangles(diff3.subtract(ppk2),sum3,diff3.negate().subtract(pkk2),BigInteger.TWO.multiply(p.multiply(k.subtract(p))),k);
				doubleCounter.countTriangles(sum3.negate().subtract(ppk2),diff3,sum3.add(pkk2),BigInteger.TWO.multiply(p).multiply(k.add(p)),k);
			}
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(doubleCounter.getCount());
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
