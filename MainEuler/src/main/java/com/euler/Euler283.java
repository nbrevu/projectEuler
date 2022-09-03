package com.euler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.koloboke.collect.LongCursor;

public class Euler283 {
	private final static int N=1000;
	/*
	 * The paper spells it out, but maybe it won't be very efficient.
	 * 1) Iterate m=1..1000. For each m:
	 * 	1.1) It will be handy to decompose m into factors.
	 *	1.2) Find every factor of 2m. Iterate over them, let's call them u.
	 *		1.2.1) Let u'=2m/u. This will be used in the magic formulas.
	 *		1.2.2) Use a sieve to get every positive number v such that 1<v<floor(sqrt(3)*u) and v is coprime with u. Iterate over v.
	 *			1.2.2.1) Decompose 4m^2(u^2+v^2) in factors, d1 and d2, d1<d2.
	 *			1.2.2.2) v must divide (d1+2mu) and (d2+2mu). Discard the rest. Iterate over the ones that remain. 
	 *				1.2.2.2.1) Use magic formulas to get a, b, c.
	 * It looks complicated but doable. The only lengthy loops are 1) and 1.2.2). The others operate over factors and should be "reasonable".
	 * The order of the algorithm is something like O(n^2*<bunch of logarithms>), which is perfectly fine for n=1000.
	 * The sieve/list of v for each u can be precomputed. Otherwise it's just following steps.
	 * This actually sounds very fun! Maybe it will be slower than it looks and I will need to adjust a little :).
	 */
	
	private final static double S3=Math.sqrt(3);
	
	private static long[] sieveV(long u,DivisorHolder uDecomposition)	{
		int limit=(int)Math.floor(u*S3);
		boolean[] sieve=new boolean[1+limit];
		for (LongCursor cursor=uDecomposition.getFactorMap().keySet().cursor();cursor.moveNext();)	{
			int prime=(int)cursor.elem();
			for (int i=prime;i<sieve.length;i+=prime) sieve[i]=true;
		}
		LongStream.Builder result=LongStream.builder();
		for (int i=1;i<=limit;++i) if (!sieve[i]) result.add(i);
		return result.build().toArray();
	}
	
	private static class Triangle	{
		public final long a;
		public final long b;
		public final long c;
		public Triangle(long a,long b,long c)	{
			this.a=a;
			this.b=b;
			this.c=c;
		}
		@Override
		public String toString()	{
			return String.format("(%d,%d,%d)",a,b,c);
		}
	}
	
	private static class TriangleFinder	{
		private final long[] lastPrimeSieve;
		private final DivisorHolder[] decompositionsCache;
		private final long[][] sievedVsCache;
		public TriangleFinder(int maxSize)	{
			int s2=2*maxSize;
			lastPrimeSieve=Primes.lastPrimeSieve(4l*s2*s2);
			decompositionsCache=new DivisorHolder[1+s2];
			sievedVsCache=new long[1+s2][];
			for (int i=1;i<=s2;++i)	{
				decompositionsCache[i]=DivisorHolder.getFromFirstPrimes(i,lastPrimeSieve);
				sievedVsCache[i]=sieveV(i,decompositionsCache[i]);
			}
		}
		public List<Triangle> getTriangles(int m)	{
			List<Triangle> result=new ArrayList<>();
			int _2m=m+m;
			long _4m2=_2m*_2m;
			DivisorHolder _2mDecomp=decompositionsCache[_2m];
			DivisorHolder _4m2Decomp=_2mDecomp.pow(2);
			for (LongCursor uCursor=_2mDecomp.getUnsortedListOfDivisors().cursor();uCursor.moveNext();)	{
				long u=uCursor.elem();
				long _2mu=_2m*u;
				long qu=_2m/u;
				for (long v:sievedVsCache[(int)u])	{
					long uv=u*u+v*v;
					long _4m2uv=_4m2*uv;
					DivisorHolder uvDivs=DivisorHolder.getFromFirstPrimes(uv,lastPrimeSieve);
					DivisorHolder allDivs=DivisorHolder.combine(_4m2Decomp,uvDivs);
					long vqu=v*qu;
					// Additional check for d1, not present in the paper. Ensures that b<=c. Otherwise, duplicates appear.
					long d1Cap=v*vqu-_2mu;
					for (LongCursor dCursor=allDivs.getUnsortedListOfDivisors().cursor();dCursor.moveNext();)	{
						long d1=dCursor.elem();
						long d2=_4m2uv/d1;
						if ((d1>=d1Cap)&&(d1<=d2)&&(((d1+_2mu)%v)==0)&&(((d2+_2mu)%v)==0))	{
							long q1=(d1+_2mu)/v;
							long q2=(d2+_2mu)/v;
							long a=q1+vqu;
							long b=q2+vqu;
							long c=q1+q2;
							result.add(new Triangle(a,b,c));
						}
					}
				}
				
			}
			return result;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=0l;
		TriangleFinder calculator=new TriangleFinder(N);
		for (int i=1;i<=N;++i)	{
			List<Triangle> ts=calculator.getTriangles(i);
			for (Triangle t:ts) result+=t.a+t.b+t.c;
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
