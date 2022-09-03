package com.euler;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.euler.common.Convergents.Convergent;
import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.FiniteContinuedFraction;
import com.euler.common.Primes;
import com.google.common.math.LongMath;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;
import com.koloboke.collect.set.LongSet;

public class Euler748_3 {
	private final static long LIMIT=LongMath.pow(10l,16);
	private final static long MOD=LongMath.pow(10l,9);
	
	private static class LongPair	{
		public final long a;
		public final long b;
		public LongPair(long a,long b)	{
			this.a=Math.abs(a);
			this.b=Math.abs(b);
		}
		public boolean isValid()	{
			return (a>0)&&(b>0)&&(EulerUtils.gcd(a,b)==1l);
		}
		public static List<LongPair> combine(List<LongPair> a,List<LongPair> b)	{
			List<LongPair> result=new ArrayList<>(2*a.size()*b.size());
			for (LongPair p1:a) for (LongPair p2:b)	{
				LongPair r1=combineA(p1,p2);
				if (r1.isValid()) result.add(r1);
				LongPair r2=combineB(p1,p2);
				if (r2.isValid()) result.add(r2);
			}
			return result;
		}
		private static LongPair combineA(LongPair p1,LongPair p2)	{
			return combine(p1.a,p1.b,p2.a,p2.b);
		}
		private static LongPair combineB(LongPair p1,LongPair p2)	{
			return combine(p1.a,p1.b,p2.b,p2.a);
		}
		private static LongPair combine(long a,long b,long c,long d)	{
			return new LongPair(a*c+b*d,a*d-b*c);
		}
	}
	
	private static long getFirstSqrt(long p)	{
		// Get the smallest square root of p-1 (mod p). Works only if p==1 (mod 4).
		LongSet sqrts=EulerUtils.squareRootModuloPrime(p-1,p);
		LongCursor cursor=sqrts.cursor();
		cursor.moveNext();
		long a=cursor.elem();
		cursor.moveNext();
		long b=cursor.elem();
		return Math.min(a,b);
	}
	
	// Works if p is prime. Otherwise the result is undefined (probably valid but not unique).
	private static LongPair getSumOfSquares(long p)	{
		long x0=getFirstSqrt(p);
		Iterator<Convergent> iterator=FiniteContinuedFraction.getFor(x0,p).iterator();
		/*
		 * In this problem, longs are good enough. However, I happened to have already an implementation of BigInteger convergents, and it's good
		 * enough even if it's somewhat slower.
		 */
		Convergent prev=iterator.next();
		long ak=prev.p.longValueExact();
		long bk=prev.q.longValueExact();
		while (iterator.hasNext())	{
			Convergent curr=iterator.next();
			long bk1=curr.q.longValueExact();
			if (bk1*bk1>p)	{
				long a=x0*bk-p*ak;
				return new LongPair(a,bk);
			}
			bk=bk1;
			ak=curr.p.longValueExact();
		}
		throw new IllegalStateException("Wait, what? Did you skip some convergent?");
	}
	
	private static long sum(long c,List<LongPair> cases,long mod,long limit)	{
		long result=0l;
		for (LongPair pair:cases)	{
			long x=pair.a*c;
			long y=pair.b*c;
			long z=pair.a*pair.b;
			if ((x<=limit)&&(y<=limit)&&(z<=limit)) result+=x+y+z;
		}
		return result%mod;
	}
	
	private static boolean isFull4k1(DivisorHolder decomp)	{
		for (LongCursor cursor=decomp.getFactorMap().keySet().cursor();cursor.moveNext();) if ((cursor.elem()%4)!=1) return false;
		return true;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		List<LongPair> case13=Collections.singletonList(getSumOfSquares(13l));
		long primeLimit=LongMath.sqrt(LIMIT,RoundingMode.UP);
		// Note that this variable already stores the result for N squared.
		LongObjMap<List<LongPair>> storage=HashLongObjMaps.newMutableMap();
		long result=sum(1l,case13,MOD,LIMIT);	// Special case for c=1.
		long[] firstPrimes=Primes.firstPrimeSieve(primeLimit);
		for (int i=5;i<firstPrimes.length;i+=4)	{
			List<LongPair> squareCase;
			if (firstPrimes[i]==0)	{
				// Prime number, we need to create the base result from scratch.
				List<LongPair> baseCase=Collections.singletonList(getSumOfSquares(i));
				squareCase=LongPair.combine(baseCase,baseCase);
			}	else	{
				DivisorHolder decomp=DivisorHolder.getFromFirstPrimes(i,firstPrimes);
				if (!isFull4k1(decomp)) continue;
				List<LongPair> primeCases=storage.get(firstPrimes[i]);
				List<LongPair> otherCases=storage.get(i/firstPrimes[i]);
				squareCase=LongPair.combine(primeCases,otherCases);
			}
			storage.put(i,squareCase);
			List<LongPair> finalCases=LongPair.combine(squareCase,case13);
			result+=sum(i,finalCases,MOD,LIMIT);
		}
		result%=MOD;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
